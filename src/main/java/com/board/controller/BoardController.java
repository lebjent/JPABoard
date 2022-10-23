package com.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.board.config.AuditorAwareImpl;
import com.board.dto.BoardFileDTO;
import com.board.dto.BoardFormDTO;
import com.board.dto.BoardSearchDTO;
import com.board.entity.Board;
import com.board.entity.User;
import com.board.service.BoardService;
import com.querydsl.core.util.FileUtils;

import lombok.RequiredArgsConstructor;

@RequestMapping("/board")
@Controller
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
	
	@GetMapping(value = "/boardWrite")
	public String boardWrite(Model model)throws Exception{
		
		//작성자를 로그인한 아이디로 값을 불러오기 위함
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = "";
		if(authentication != null) {
			userId = authentication.getName();
		}
		
		model.addAttribute("boardFormDTO", new BoardFormDTO());
		model.addAttribute("userId", userId);//로그인한 아이디값을 모델객체에 저장
		
		return "/board/boardWrite";
	}
	
	@PostMapping(value = "/boardWrite")
	public String boardWrite(@Valid BoardFormDTO boardFormDTO, Model model, @RequestParam("boardFile") List<MultipartFile> boardFileList) {
		
		//받아온 게시물정보 파라미터를 boardEntity에 저장
		Board board = Board.writeBoard(boardFormDTO);
		
		try {
			boardService.boardWrite(board, boardFileList);
		}catch (Exception e) {
			model.addAttribute("errorMessage","게시판 등록중 에러가 발생하였습니다.");
			return "/board/boardWrite";
		}
		
		return "redirect:/";
	}
	
	//게시판 리스트 불러오기
	//value에 게시판 리스트 화면 진입 시 URL에 페이지 번호가 없는 경우와 페이지번호가 있는경우 2가지를 매핑한다.
	@GetMapping(value = {"/boardList","/boardList/{page}"})
	public String boardList(BoardSearchDTO boardSearchDTO,@PathVariable("page") Optional<Integer> page, Model model)throws Exception{
		
		//로그인한 아이디로 값을 불러오기 위함
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = "";
		if(authentication != null) {
			userId = authentication.getName();
		}
		
		//로그인한 유저가 ADMIN인지 확인하기 위함
		Boolean roleChk = AuditorAwareImpl.hasAdminRole();
		if(roleChk) {
			model.addAttribute("role","ADMIN");
		}else {
			model.addAttribute("role","USER");
		}
		
		//페이징을 위해 PageRequest.of 메소드를 통해 Pageable객체를 생성합니다.
		//페이지 번호가 없으면 0페이지를 조회하도록 한다.(첫번째 파라미터: 페이지,2번째 파라미터:노출할 데이터 개수)
		Pageable pageable = PageRequest.of(page.isPresent()? page.get():0, 5);
		
		Page<Board> boardList = boardService.getBoardListPage(boardSearchDTO, pageable);
		
		//유저이름 저장
		model.addAttribute("userId", userId);
		//게시판 리스트를 저장
		model.addAttribute("boardList",boardList);
		//게시판 검색정보를 저장(검색조건을 유지하기위하여)
		model.addAttribute("boardSearchDTO", boardSearchDTO);
		//메뉴하단에 보여줄 페이지 번호의 최대개수
		model.addAttribute("maxPage", 5);
		
		
		return "/board/boardList";
	}
	
	@GetMapping(value = "/boardDetail/{bno}")
	public String boardDetail(@PathVariable("bno")Long bno, Model model,@RequestParam("page")int page) {
		
		//작성자를 로그인한 아이디로 값을 불러오기 위함
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = "";
		if(authentication != null) {
			userId = authentication.getName();
		}
		
		try {
			BoardFormDTO boardFormDTO = boardService.getBoardDetail(bno,"D");
			model.addAttribute("boardFormDTO", boardFormDTO);
			model.addAttribute("userId", userId);
			model.addAttribute("page", page);
		}catch (EntityNotFoundException e) {
			model.addAttribute("errorMessage", "존재하지 않는 게시물 입니다.");
			model.addAttribute("boardFormDTO",new BoardFormDTO());
		}
		
		return "/board/boardDetail";
		
	}
	
	//첨부파일 다운로드
	@GetMapping(value="/fileDownload")
	public ResponseEntity<Object> fileDownload(@RequestParam Map<String,Object> param,HttpServletResponse response) throws Exception{
		
		//기본첨부파일 저장된 폴더
		String path = "C:/board/file/";
		
		//폴더에 저장된 파일명 가져오기
		String fileName = (String)param.get("fileName");
		//오리지널 파일명 가져오기
		String originalFileName = (String)param.get("originalFileName");
		
		try {
			Path filePath = Paths.get(path+fileName);
			// 파일 resource 얻기
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			
			File file = new File(path);
			
			HttpHeaders headers = new HttpHeaders();
			
			String downloadName = URLEncoder.encode(originalFileName,"UTF-8").replaceAll("\\+", "");

			
			//저장할 파일이름을 원래 파일명으로 저장한다.
			headers.add("Content-Disposition", "attachment; filename="+downloadName);
			
			return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
		}
		
	}
	
	//게시물 수정페이지
	@GetMapping(value = "/boardModify/{bno}")
	public String boardModify(@PathVariable("bno")Long bno, Model model,@RequestParam("page")int page) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = "";
		if(authentication != null) {
			userId = authentication.getName();
		}
		
		try {
			BoardFormDTO boardFormDTO = boardService.getBoardDetail(bno,"M");
			model.addAttribute("boardFormDTO", boardFormDTO);
			model.addAttribute("userId", userId);
			model.addAttribute("page", page);
		}catch (EntityNotFoundException e) {
			model.addAttribute("errorMessage", "존재하지 않는 게시물 입니다.");
			model.addAttribute("boardFormDTO",new BoardFormDTO());
		}
		
		return "/board/boardModify";
		
	}
	
	//게시판 수정하기
	@PostMapping(value = "/boardModify/{bno}")
	public String boardModify(@Valid BoardFormDTO boardFormDTO, Model model, @RequestParam("boardFile") List<MultipartFile> boardFileList) {
		
		try {
			boardService.updateBoard(boardFormDTO, boardFileList);
		}catch (Exception e) {
			model.addAttribute("errorMessage","게시판 수정중 에러가 발생하였습니다.");
			return "/board/boardModify";
		}
		
		return "redirect:/";
	}
	
	//게시판 삭제하기
	@PostMapping(value = "/boardDelete/{bno}")
	public String boardDelete(@Valid BoardFormDTO boardFormDTO, Model model) {
		
		try {
			boardService.boardDelete(boardFormDTO);
		}catch (Exception e) {
			model.addAttribute("errorMessage","게시판 삭제중 에러가 발생하였습니다.");
			return "/board/boardDetail";
		}
		
		return "redirect:/";
	}
	
}
