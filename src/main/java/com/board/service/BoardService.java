package com.board.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.board.dto.BoardFileDTO;
import com.board.dto.BoardFormDTO;
import com.board.dto.BoardSearchDTO;
import com.board.dto.UserFormDTO;
import com.board.entity.Board;
import com.board.entity.BoardFile;
import com.board.repository.BoardFileRepository;
import com.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardFileRepository boardFileRepository;
	
	private final BoardRepository boardRepository;
	
	private final BoardFileService boardFileService;
	
	
	public Long boardWrite(Board board, List<MultipartFile> boardFileList)throws Exception{
		
		//게시물 등록
		boardRepository.save(board);
		
		
		//파일리스트가 없는경우에는 실행하지 않는다.
		//첨부파일은 3개까지 등록이 가능하므로 리스트로 받아왔다.
		if(!boardFileList.isEmpty()) {
			//파일등록
			for(int i=0; i<boardFileList.size();i++) {
				BoardFile boardFile =  new BoardFile();
				boardFile.setBoard(board);
				
				
				boardFileService.saveBoardFile(boardFile, boardFileList.get(i));
				
			}
		}
		
		return board.getId();
		
	}
	
	@Transactional(readOnly = true)//데이터의 수정이 없으므로 최적화를 위해 어노테이션을 설정
	public Page<Board> getBoardListPage(BoardSearchDTO boardSearchDTO, Pageable pageable){
		return boardRepository.getBoardListPage(boardSearchDTO, pageable);
	}
	
	//게시판 상세보기
	@Transactional(readOnly = true)
	public BoardFormDTO getBoardDetail(Long bno,String type) {
		
		//게시판 첨부파일 리스트 가져오기
		List<BoardFile> boardFileList = boardFileRepository.findByBoardIdOrderByIdAsc(bno);
		
		List<BoardFileDTO> boardFileDTOList = new ArrayList<>();
		for(BoardFile boardFile : boardFileList) {
			BoardFileDTO boardFileDTO = new BoardFileDTO();
			boardFileDTO.setFileName(boardFile.getFileName());
			boardFileDTO.setFileUrl(boardFile.getFileUrl());
			boardFileDTO.setFno(boardFile.getId());
			boardFileDTO.setOriginalFileName(boardFile.getOriginalFileName());
			
			boardFileDTOList.add(boardFileDTO);
			
		}
		
		//게시판 내용가져오기(게시판 아이디를 통해 상품엔티티를 조회 존재하지 않을때 EntityNotFoundException 발생 )
		Board board = boardRepository.findById(bno).orElseThrow(EntityNotFoundException::new);
		
		//상세보기시에만 조회수 증가
		if(type.equals("D")) {
			//조회수 증가
			boardRepository.updateCnt(board.getId());
		}else if(type.equals("M")) {
			
		}
		
		//게시물을 담을 DTO 생성
		BoardFormDTO boardFormDTO = new BoardFormDTO();
		boardFormDTO.setBno(board.getId());
		boardFormDTO.setTitle(board.getTitle());
		boardFormDTO.setContent(board.getContent());
		boardFormDTO.setViewCnt(board.getViewCnt()+1);
		boardFormDTO.setWriter(board.getWriter());
		boardFormDTO.setViewStatus(board.getViewStatus());
		boardFormDTO.setBoardFileList(boardFileDTOList);
		
		return boardFormDTO;
	}
	
	public Long updateBoard(BoardFormDTO boardFormDTO,List<MultipartFile> boardFileList) throws Exception{
		
		
		//게시물 수정
		Board board = boardRepository.findById(boardFormDTO.getBno()).orElseThrow(EntityNotFoundException::new);
		board.updateBoard(boardFormDTO);
		List<Long> fno = boardFormDTO.getBoardFileFno();
		//이미지 수정
		for(int i = 0; i < boardFileList.size(); i++) {
			boardFileService.updateBoardFile(fno.get(i), boardFileList.get(i));
		}
		
		return board.getId(); 
		
	}
	
	public Long boardDelete(BoardFormDTO boardFormDTO) throws Exception{
		
		List<Long> fno = boardFormDTO.getBoardFileFno();
		for(int i = 0; i < fno.size(); i++) {
			boardFileService.deleteBoardFile(fno.get(i));
		}
		
		//게시물 삭제
		Board board = boardRepository.findById(boardFormDTO.getBno()).orElseThrow(EntityNotFoundException::new);
		//게시물 테이블 삭제
		boardRepository.deleteById(board.getId());
		
		
		return board.getId();
	}
	
	public void boardWithDrawDelete(String userId)throws Exception{
		
		List<Board> boardList = boardRepository.findByWriter(userId);
		for(Board board : boardList) {
			List<BoardFile> boardFileList = boardFileRepository.findByBoardIdOrderByIdAsc(board.getId());
			for(BoardFile boardFile : boardFileList) {
				boardFileService.deleteBoardFile(boardFile.getId());
			}
			boardRepository.deleteById(board.getId());
		}
		
	}
	
}


