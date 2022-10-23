package com.board.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.board.dto.UserFormDTO;
import com.board.entity.User;
import com.board.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	private final PasswordEncoder passwordEncoder;
	
	//로그인 뷰
	@GetMapping(value = "/login")
	public String loginView() {
		return "user/loginView";
	}
	
	//로그인 에러시 
	@GetMapping(value = "/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요.");
		return "/user/loginView";
	}
	
	//회원유형 선택 뷰
	@GetMapping(value = "/userType")
	public String userTypeView() {
		return "user/userType";
	}
	//관리자 회원가입 뷰
	@GetMapping(value = "/adminJoinView")
	public String adminJoinView(Model model) {
		
		model.addAttribute("userFormDTO", new UserFormDTO());
		
		return "user/adminJoinView";
	}
	
	//관리자 회원가입 뷰
	@GetMapping(value = "/userJoinView")
	public String userJoinView(Model model) {
		
		model.addAttribute("userFormDTO", new UserFormDTO());
		
		return "user/userJoinView";
	}
	
	//회원중복확인
	@ResponseBody
	@PostMapping(value = "/dupeChk")
	public Map<String,Object> dupChk(@RequestParam String userId) throws Exception{
		Map<String,Object> resultObject = new HashMap<>();
		resultObject = userService.validateDuplicateUser(userId);
		
		return resultObject;
	}
	
	//실질적인 관리자 회원가입
	@PostMapping(value = "/join")
	public String UserJoin(UserFormDTO userFormDTO) {
		User user = User.joinUser(userFormDTO, passwordEncoder);
		userService.joinUser(user);
		
		return "redirect:/";
	}
	
	//회원수정시 회원정보 불러오기
	@ResponseBody
	@PostMapping(value = "/getUser")
	public UserFormDTO getUser(@RequestParam("uno") Long uno) throws Exception {
		
		UserFormDTO userFormDTO = userService.getUser(uno);
		
		return userFormDTO;
	}
	
	//회원정보수정
	@PostMapping(value = "/userModify")
	public String userModify(@Valid UserFormDTO userFormDTO)throws Exception{
		try {
			userService.userModify(userFormDTO,passwordEncoder);
		} catch (Exception e) {
			return "redirect:/admin/userMng";
		}
		
		return "redirect:/admin/userMng";
	}
	
}
