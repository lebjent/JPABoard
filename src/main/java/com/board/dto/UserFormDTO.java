package com.board.dto;

import java.time.LocalDateTime;

import com.board.constant.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFormDTO {
	
	private Long uno; //회원번호
	
	private String name;//유저 이름
	
	private String userId;//유저 아이디
	
	private String password;//유저 패스워드
	
	private String phone;//유저 휴대폰 번호
	
	private Role role;//유저 회원유형
	
	private LocalDateTime joinDate;//회원가입일
}
