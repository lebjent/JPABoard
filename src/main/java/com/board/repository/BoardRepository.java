package com.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.board.dto.BoardSearchDTO;
import com.board.entity.Board;

/*
  QuerydslPredicateExecutor를 상속하면 인터페이스를 상속하면 Querydsl을 사용하기위해 
  EntityManager를 주입하여 JPAQueryFactory를 생성하고 
  기본 쿼리도 직접 작성해야하는 수고를 덜어줄 수 있다.
*/
public interface BoardRepository extends JpaRepository<Board, Long>,
				 QuerydslPredicateExecutor<Board>,BoardCustomRepository{
	
	//조회수 증가
	@Modifying
	@Query("update Board p set p.viewCnt = p.viewCnt+1 where p.id = :board_id")
	public void updateCnt(@Param("board_id") Long boardId);
	
	List<Board> findByWriter(String userId);
}

