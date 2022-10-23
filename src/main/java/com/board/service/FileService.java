package com.board.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public class FileService {
	
	//파일 업로드 기능
	public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
		
		UUID uuid = UUID.randomUUID();//중복된 파일명 없이 서로 다른개체를 부여하기위해 적용
		
		//확장자 명
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		//UUID받은값과 확장자명을 조합하기위해 확장자 명을 만든다.
		String savedFileName = uuid.toString()+extension;
		
		String fileUploadFullUrl = uploadPath + "/" + savedFileName;
		//생성자로 파일이 저장될 위치와 파일의 이름을 넘겨 파일에 쓸 파일 출력 스트림을 만든다.
		FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
		
		fos.write(fileData);
		fos.close();
		return savedFileName;
		
	}
	
	//파일 삭제 기능
	public void deleteFile(String filePath) throws Exception{
		
		File deleteFile = new File(filePath);
		
		if(deleteFile.exists()) {
			deleteFile.delete();
			log.info("********업로드된 파일 삭제********");
		}else {
			log.info("********업로드된 파일이 존재X********");
		}
		
	}
	
}
