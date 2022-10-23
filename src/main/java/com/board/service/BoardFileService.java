package com.board.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.board.entity.BoardFile;
import com.board.repository.BoardFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardFileService {
	
	@Value("${boardFileLocation}")
	private String boardFileLocation;
	
	private final BoardFileRepository boardFileRepository;
	
	private final FileService fileService;
	
	//게시판 첨부파일 저장
	public void saveBoardFile(BoardFile boardFile, MultipartFile attachFile) throws Exception {
		
		String originalFileName = attachFile.getOriginalFilename();
		String fileName = "";
		String fileUrl = "";
		
		//파일 업로드
		if(!StringUtils.isEmpty(originalFileName)) {
			fileName = fileService.uploadFile(boardFileLocation, originalFileName, attachFile.getBytes());
			fileUrl = "/board/file/"+ fileName;
		}
		
		//상품 이미지 정보 저장
		boardFile.updateFile(originalFileName, fileName, fileUrl);
		boardFileRepository.save(boardFile);
	}
	
	//게시판 수정하기	
	public void updateBoardFile(Long fno,MultipartFile boardFile)throws Exception{
		
		
		//파일정보를 불러오기
		BoardFile savedBoardFile = boardFileRepository.findById(fno).orElseThrow(EntityNotFoundException::new);
		
		//기존파일 삭제
		if(!StringUtils.isEmpty(savedBoardFile.getFileName())) {
			fileService.deleteFile(boardFileLocation+"/"+savedBoardFile.getFileName());
		}
		
		//boardFile이 비어있지 않은경우
		if(!boardFile.isEmpty()) {
			
			String originalFileName = boardFile.getOriginalFilename();
			String fileName = fileService.uploadFile(boardFileLocation, originalFileName,boardFile.getBytes());
			String fileUrl = "/board/file/"+ fileName;
			//변경감지기능을 사용하여 트랜잭션이 끝날때 업데이트 처리 .save()를 사용하지 않는다.
			savedBoardFile.updateFile(originalFileName, fileName, fileUrl);
			
		}else {//첨부파일이 없는경우
			String originalFileName = "";
			String fileName = "";
			String fileUrl = "";
			savedBoardFile.updateFile(originalFileName, fileName, fileUrl);
		}
	}
	
	
	//게시판 삭제하기	
	public void deleteBoardFile(Long fno)throws Exception{
		
		
		//파일정보를 불러오기
		BoardFile boardFile = boardFileRepository.findById(fno).orElseThrow(EntityNotFoundException::new);
		
		//기존파일 삭제
		if(!StringUtils.isEmpty(boardFile.getFileName())) {
			fileService.deleteFile(boardFileLocation+"/"+boardFile.getFileName());
		}
		
		boardFileRepository.deleteById(boardFile.getId());
		
	}
	
}
