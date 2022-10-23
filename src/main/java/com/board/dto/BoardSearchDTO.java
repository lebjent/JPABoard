package com.board.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSearchDTO {
	
	private String searchBy;//검색유형
	private String keyword = "";//검색 키워드
	
}
