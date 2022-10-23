package com.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.BoardFile;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
	
	List<BoardFile> findByBoardIdOrderByIdAsc(Long bno);
	
}
