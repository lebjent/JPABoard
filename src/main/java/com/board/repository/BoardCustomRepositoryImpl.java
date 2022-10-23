package com.board.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.util.StringUtils;

import com.board.config.AuditorAwareImpl;
import com.board.dto.BoardSearchDTO;
import com.board.entity.Board;
import com.board.entity.QBoard;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class BoardCustomRepositoryImpl implements BoardCustomRepository{
	
	//동적으로 쿼리를 생성하기 위하여 JPAQueryFactory를 생성
	private JPAQueryFactory queryFactory;
	
	//JPAQuery생성자로 EntityMageger 객체를 넣어준다.
	public BoardCustomRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
	
	//검색유형에 따른 결과값
	//제목 : T - 작성자 : W - 내용 : C
	private BooleanExpression searchByLike(String searchBy, String keyword) {
		
		if(StringUtils.equals("T", searchBy)) {
			return QBoard.board.title.like("%"+keyword+"%");
		}else if(StringUtils.equals("W", searchBy)) {
			return QBoard.board.writer.like("%"+keyword+"%");
		}else if(StringUtils.equals("C", searchBy)) {
			return QBoard.board.content.like("%"+keyword+"%");
		}else if(StringUtils.equals("TW", searchBy)) {
			return QBoard.board.title.like("%"+keyword+"%").or(QBoard.board.writer.like("%"+keyword+"%"));
		}
		
		return null;
		
	}
	
	@Override
	public Page<Board> getBoardListPage(BoardSearchDTO boardSearchDTO, Pageable pageable) {
		
		QueryResults<Board> results = queryFactory
						.selectFrom(QBoard.board)// SELECT * FROM BOARD_TBL로 지정
						.where(searchByLike(boardSearchDTO.getSearchBy(), boardSearchDTO.getKeyword()))
						.orderBy(QBoard.board.regTime.desc())//등록일 기준
						.offset(pageable.getOffset())
						.limit(pageable.getPageSize())
						.fetchResults();
		
		//게시판 리스트 조회결과를 List를 저장한다.
		List<Board> boardList = results.getResults();
		long total = results.getTotal();//총 게시물 결과 수
		
		return new PageImpl<>(boardList, pageable, total);
		
	}


}
