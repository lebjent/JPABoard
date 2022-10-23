package com.board.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.board.constant.ViewStatus;
import com.board.dto.BoardFormDTO;
import com.board.dto.UserFormDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name="board_tbl")//테이블 명
public class Board extends BaseEntity{
	
	@Id
	@Column(name = "board_id")//컬럼명
	@GeneratedValue(strategy =  GenerationType.AUTO)//기본키를 Auto로 설정
	private Long id; //게시물 번호
	
	@Column(nullable = false, length=50) //50자이내
	private String title;//제목
	
	@Column(nullable = false, length=10) //10자이내
	private String writer;//작성자명
	
	@Lob//게시판 내용은 용량이 크므로 Lob을 설정
	@Column(nullable = false)
	private String content;//작성내용
	
	@Enumerated(EnumType.STRING)
	private ViewStatus viewStatus; //공개-비공개
	
	@Column(columnDefinition = "number(10) default '0'")
	private int viewCnt; //조회수
	
	public static Board writeBoard(BoardFormDTO boardFormDTO) {
		
		//boardFormDTO에 데이터를 담을 Board엔티티 생성자 생성
		Board board = new Board();
		
		board.setViewStatus(boardFormDTO.getViewStatus());
		board.setContent(boardFormDTO.getContent());
		board.setTitle(boardFormDTO.getTitle());
		board.setWriter(boardFormDTO.getWriter());

		
		return board;
		
	}
	
	
	//변경감지를 위한 게시판 수정
	public void updateBoard(BoardFormDTO boardFormDTO) {
		this.title =  boardFormDTO.getTitle();
		this.content = boardFormDTO.getContent();
		this.viewStatus = boardFormDTO.getViewStatus();
	}
	
	
}
