package com.board.entity;

import java.lang.reflect.Member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.board.constant.Role;
import com.board.dto.BoardFormDTO;
import com.board.dto.UserFormDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_tbl")
@Getter @Setter
@ToString
public class User extends BaseEntity {
	
	@Id
	@Column(name = "uno")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long uno;
	
	private String name;
	
	@Column(unique = true) //아이디는 고유한 값이므로 다른값이 들어오지 못하도록 unique를 선언한다.
	private String userId;
	
	private String password;
	
	private String phone;
	
	@Enumerated(EnumType.STRING)
	private Role role; //회원유형을 구분하기위한 ENUM
	
	//BcryptPasswordEncoder를 이용하여 암호화 한 비밀번호를 테이블에 저장하기 위한 메소드
	public static User joinUser(UserFormDTO userFormDto, PasswordEncoder passwordEncoder) {
		
		//userFormDto에 데이터를 담을 User엔티티 생성자 생성
		User user = new User();
		
		user.setName(userFormDto.getName());
		user.setUserId(userFormDto.getUserId());
		user.setRole(userFormDto.getRole());
		user.setPhone(userFormDto.getPhone());

		//비밀번호 암호화 해서 리턴
		String password = passwordEncoder.encode(userFormDto.getPassword());
		user.setPassword(password);
		
		return user;
		
	}
	
	//변경감지를 위한 게시판 수정
	public void modifyUser(UserFormDTO userFormDto, PasswordEncoder passwordEncoder) {
		String password = passwordEncoder.encode(userFormDto.getPassword());
		this.password =  password;
		this.phone = userFormDto.getPhone();
	}
	
}
