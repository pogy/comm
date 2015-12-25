package com.vipkid.rest;

import java.io.IOException;

import javax.annotation.Resource;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vipkid.model.FileType;
import com.vipkid.model.Audit.Category;
import com.vipkid.security.SecurityService;
import com.vipkid.service.FileService;
import com.vipkid.service.ImportCourseService;

@RestController
@RequestMapping(value = "/api/service/private/importCourse")
public class ImportCourseController {
	
	@Resource
	private ImportCourseService importCourseService;
	
	@Resource
	private SecurityService securityService;
	
	private static Logger logger = LoggerFactory.getLogger(FileService.class.getSimpleName());
	
	@RequestMapping(value = "/upload",method = RequestMethod.POST)
	public void upload(@RequestParam("fileType")final FileType fileType,@RequestParam("file")MultipartFile file,@RequestParam("fileSize")String fileSize){
		logger.info("import xml file = {} with type = {}",file.getOriginalFilename(),fileType.name());
		
		try {
			importCourseService.readCourse(file);
			StringBuffer strbuf = new StringBuffer(file.getOriginalFilename());
			securityService.logAudit(com.vipkid.model.Audit.Level.INFO, Category.IMPORT_COURSE_DATA, "Upload: The "+strbuf.toString()+" has been uploaded successfully" );
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}
	

}
