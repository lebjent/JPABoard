package com.board.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "board_file")
@Getter
@Setter
public class BoardFile extends BaseEntity {
	
	@Id
	@Column(name = "file_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; //파일 기본키
	
	private String fileName; //파일이름
	
	private String originalFileName; //원본파일이름
	
	private String fileUrl; //파일경로
	
	@ManyToOne(fetch = FetchType.LAZY) //다대일 단방향 관계로 매핑
	@JoinColumn(name = "board_id")
	private Board board;
	
	//파일명, 업데이트할 파일명, 파일경로를 파라미터로 받아서 파일정보를 업데이트 하는 메소드
	public void updateFile(String originalFileName, String fileName, String fileUrl) {
		this.originalFileName = originalFileName;
		this.fileName = fileName;
		this.fileUrl = fileUrl;
	}
}
