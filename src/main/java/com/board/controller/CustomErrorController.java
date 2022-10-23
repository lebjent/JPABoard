package com.board.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller 
public class CustomErrorController implements ErrorController {
  
  @RequestMapping(value = "/error") 
  public String errorHandler(HttpServletRequest request) { 
	  Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE); 
	  //오류가 있을경우
	  if(status!=null) {
		  	int statusCode = Integer.valueOf(status.toString());
		  	//오류코드를 가져온다.
			if(statusCode == HttpStatus.NOT_FOUND.value()) { 
				//404오류 발생시
				return "/error/404";
			}else if(statusCode == HttpStatus.FORBIDDEN.value()) { 
				//500오류 발생시
				return "/error/500"; 
			}else if(statusCode == HttpStatus.UNAUTHORIZED.value()) { 
				//401오류 발생시
				return "/error/401"; 
			}else { 
				//그외 오류 발생시
				return "/error/basic"; 
			}
  
	  } 
	  
	  return "/error/basic"; 
  }
 
}
 
