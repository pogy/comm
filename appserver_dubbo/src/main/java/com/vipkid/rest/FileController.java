package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vipkid.model.FileType;
import com.vipkid.security.SecurityService;
import com.vipkid.service.FileService;
import com.vipkid.service.pojo.StringWrapper;
import com.vipkid.service.pojo.UploadedFile;

@RestController
@RequestMapping(value="/api/service/private/file")
public class FileController {
	private static Logger logger = LoggerFactory.getLogger(FileController.class.getSimpleName());

	@Resource
	private SecurityService securityService;
	
	@Resource
	private FileService fileService;

	@RequestMapping(value="/upload",method = RequestMethod.POST)
	public UploadedFile upload(@RequestParam("fileType") final FileType fileType, @RequestParam("file") MultipartFile file, @RequestParam(value="fileSize",required=false) String fileSize) {
		logger.info("upload file for filename = {}",file.getOriginalFilename());
		return fileService.upload(fileType, file, fileSize);
	}
	
	@RequestMapping(value="/findTeacherFilesRootPath",method = RequestMethod.GET)
	public StringWrapper getTeacherFilesRootPath() {
		logger.info("get file root path}");
		return fileService.getTeacherFilesRootPath();
	}

}
