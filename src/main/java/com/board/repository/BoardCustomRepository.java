package com.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.board.dto.BoardSearchDTO;
import com.board.entity.Board;

public interface BoardCustomRepository {

	//게시판 조건을 담고있는 BoardSearchDTO 객체와 페이징 정보를 담고있는 pageable객체를 파라미터로 받는 반환데이터로 페이지객체를 반환
	Page<Board> getBoardListPage(BoardSearchDTO boardSearchDTO, Pageable pageable);
	
}
