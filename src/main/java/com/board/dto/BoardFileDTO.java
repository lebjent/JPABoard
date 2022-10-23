package com.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardFileDTO {
	
	private Long fno;
	
	private String fileName; //파일이름
	
	private String originalFileName; //원본파일이름
	
	private String fileUrl; //파일경로
	
}
