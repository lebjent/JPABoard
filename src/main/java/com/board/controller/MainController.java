package com.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	//메인 페이지로 이동
	@GetMapping(value = "/")
	public String mainPage(Model model)throws Exception{
		
		return "main";
		
	}
}
