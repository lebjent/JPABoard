package com.board.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.board.constant.Role;
import com.board.dto.BoardFormDTO;
import com.board.dto.UserFormDTO;
import com.board.dto.UserSearchDTO;
import com.board.service.BoardService;
import com.board.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	
	private final UserService userService;
	private final BoardService boardService;
	
	@GetMapping(value = {"/userMng", "/userMng/{page}"})
	public String userMng(UserSearchDTO userSearchDTO, @PathVariable("page")Optional<Integer> page,Model model)throws Exception{
		
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
		
		Role user = Role.USER;
		
		//일반회원만 리스트 불러오기
		Page<UserFormDTO> userList = userService.getUserList(userSearchDTO,user, pageable);
		
		model.addAttribute("userList", userList);
		//게시판 검색정보를 저장(검색조건을 유지하기위하여)
		model.addAttribute("userSearchDTO", userSearchDTO);
		model.addAttribute("page", pageable.getPageNumber());
		model.addAttribute("maxPage", 5);
		
		return "/admin/userMng";
	}
	
	//회원탈퇴
	@GetMapping(value = "/userWithDraw")
	public String userWithDraw(@Valid UserFormDTO userFormDTO,Model model) throws Exception {
		try {
			boardService.boardWithDrawDelete(userFormDTO.getUserId());
			userService.userWithDraw(userFormDTO);
		}catch (Exception e) {
			model.addAttribute("errorMessage","회원탈퇴 중 에러가 발생하였습니다.");
			return "redirect:/";
		}
		
		return "redirect:/admin/userMng";
	}
}
