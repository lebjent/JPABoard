package com.board.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.constant.Role;
import com.board.dto.UserFormDTO;
import com.board.dto.UserSearchDTO;
import com.board.entity.Board;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
//lombok에서 제공하는 기능으로 빈에 생성자가 하나고 final이나 @NonNull이 붙은 경우 @Autowired 어노테이션 없이 의존성 주입이 가능하다
@RequiredArgsConstructor 
public class UserService implements UserDetailsService {
	
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;
	
	//회원가입을 진행
	public User joinUser(User user) {
		return userRepository.save(user);
	}
	
	//중복확인을 진행
	public Map<String,Object> validateDuplicateUser(String userId) {
		
		Map<String, Object> returnObject = new HashMap<>();
		
		//엔티티에 회원정보를 넣어준다.
		User joinChk = userRepository.findByUserId(userId);
		
		//해당되는 아이디가 있는지 체크
		if(joinChk != null) {
			returnObject.put("status", "FAIL");
		}else {
			returnObject.put("status","OK");
		}
		
		return returnObject;
		
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		
		//userId로 우선 회원정보에대한 값을 체크
		User user = userRepository.findByUserId(userId);
		
		//유저 값이 있는지 없는지 null체크
		if(user == null) {
			throw new UsernameNotFoundException(userId);
		}
		
		//엔티티이름을 User로 만들어서 스프링 시큐리티에서 제공하는 User 객체를 생성하기 위해서 생성자로 파라미터를 넘겨준다.
		return org.springframework.security.core.userdetails.User
															.builder()
															.username(user.getUserId())
															.password(user.getPassword())
															.roles(user.getRole().toString())
															.build();
	}
	
	//일반유저 리스트 가져오기
	@Transactional(readOnly = true)
	public Page<UserFormDTO> getUserList(UserSearchDTO userSearchDTO,Role role, Pageable pageable){
		
		//검색조건 체크 - 이름:N | 아이디:I | 번호:P | 이름+아이디:NI
		String searchType = "";
		//키워드
		String keyword = "";
		
		if(userSearchDTO.getSearchType() == null) {//검색조건이 없을시
			searchType = "T";
			keyword = "";
		}else {//검색조건이 있을경우
			searchType = userSearchDTO.getSearchType();
			keyword = userSearchDTO.getKeyword();
		}
		
		
		List<User> userList = new ArrayList<>();
		Long userCount = 0L;
		
		//검색조건에 따른 레파지토리 리스트 가져오기
		if(searchType.equals("N")) {//이름
			userList = userRepository.findUserName(role, keyword, pageable);
			userCount = userRepository.countUserName(role, keyword);
		}else if(searchType.equals("I")) {//아이디
			userList = userRepository.findUserId(role, keyword, pageable);
			userCount = userRepository.countUserId(role, keyword);
		}else if(searchType.equals("P")) {//전화번호
			userList = userRepository.findUserPhone(role, keyword, pageable);
			userCount = userRepository.countUserPhone(role, keyword);
		}else if(searchType.equals("NI")) {//이름+아이디
			userList = userRepository.findUserNameId(role, keyword, pageable);
			userCount = userRepository.countUserNameId(role, keyword);
		}else if(searchType.equals("T")) {//검색조건이 없을경우
			userList = userRepository.findUser(role, pageable);
			userCount = userRepository.countUser(role);
		}
		
		
		List<UserFormDTO> userFormList = new ArrayList<>();
		
		for(User user:userList) {
			UserFormDTO userFormDTO = new UserFormDTO();
			userFormDTO.setUno(user.getUno());
			userFormDTO.setName(user.getName());
			userFormDTO.setPhone(user.getPhone());
			userFormDTO.setUserId(user.getUserId());
			userFormDTO.setJoinDate(user.getRegTime());
			
			userFormList.add(userFormDTO);
		}
		
		return new PageImpl<UserFormDTO>(userFormList, pageable, userCount);
	}
	
	//회원탈퇴
	public Long userWithDraw(UserFormDTO userFormDTO) {
		
		User user = userRepository.findById(userFormDTO.getUno()).orElseThrow(EntityNotFoundException::new);
		userRepository.delete(user);
		
		return user.getUno();
	}
	
	//회원정보 가져오기
	public UserFormDTO getUser(Long uno) throws Exception {
		
		//유저번호로 유저정보 가져오기
		User user = userRepository.findById(uno).orElseThrow(EntityNotFoundException::new);
		//반환한 DTO
		UserFormDTO userFormDTO = new UserFormDTO();
		
		userFormDTO.setName(user.getName());
		userFormDTO.setPhone(user.getPhone());
		userFormDTO.setUserId(user.getUserId());
		userFormDTO.setUno(user.getUno());
		
		return userFormDTO;
	}
	//회원정보 수정하기
	public Long userModify(UserFormDTO userFormDTO,PasswordEncoder passwordEncoder) throws Exception {
		System.out.println(userFormDTO.getUno());
		User user = userRepository.findById(userFormDTO.getUno()).orElseThrow(EntityNotFoundException::new);
		user.modifyUser(userFormDTO, passwordEncoder);
		
		return user.getUno();
		
	}
}
