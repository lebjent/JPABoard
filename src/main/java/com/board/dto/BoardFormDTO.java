package com.board.dto;

import java.util.ArrayList;
import java.util.List;

import com.board.constant.ViewStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardFormDTO {
	
	private Long bno;
	
	private String title; //제목
	
	private String writer;//작성자명
	
	private String content;//작성내용
	
	private ViewStatus viewStatus; //공개범위
	
	private int viewCnt; //조회수
	
	//게시물 저장 후 수정할때 게시물 파일정보를 저장하는 리스트
	private List<BoardFileDTO> boardFileList = new ArrayList<>();
	
	//게시물 파일 아이디를 저장하는 리스트
	private List<Long> boardFileFno = new ArrayList<>();
}
