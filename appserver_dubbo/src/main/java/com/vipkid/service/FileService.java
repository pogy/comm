package com.vipkid.service;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.FileType;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.FileUploadServiceException;
import com.vipkid.service.pojo.StringWrapper;
import com.vipkid.service.pojo.UploadedFile;
import com.vipkid.util.Configurations;

@Service
public class FileService {
	private static Logger logger = LoggerFactory.getLogger(FileService.class.getSimpleName());
	
	@Value("#{configProperties['teachar_recruitment.url']}")
	private String TEACHER_RECRUITMENT_URL;

	@Resource
	private SecurityService securityService;

	private String getContentType(String strFileType)
    {

		String contentType = "text/plain";
//		String contentType = "application/octet-stream";
		
		if (null == strFileType) {
			return contentType;
		}
		  
        switch (strFileType.toLowerCase())
        {
        	// image
            case "gif":
                contentType = "image/gif";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "jpg":
                contentType = "image/jpg";
                break;
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "bmp":
            	contentType = "image/bmp";
            	break;
            // 
            case "rar":
                contentType = "application/octet-stream";
                break;
            case "zip":
                contentType = "application/zip";
                break;
            // doc
            case "doc":
            	contentType = "application/msword";
            	break;
            case "docx":
            	contentType = "application/msword";
            	break;
            case "rtf":
            	contentType = "application/rtf";
            	break;
            case "pdf":
            	contentType = "application/pdf";
            	break;
            case "txt":
            	contentType = "text/plain";
            	break;
            case "xml":
            	contentType = "text/xml";
            	break;
            // audio
            case "aiff":
            	contentType = "audio/aiff";
            	break;
            case "mp3":
            	contentType = "audio/mpeg";
            	break;
            case "wav":
            	contentType = "audio/x-wav";
            	break;
            // video
            case "mp4":
            	contentType = "video/mp4";
            	break;
            case "avi":
            	contentType = "video/x-msvideo";
            	break;
            case "mov":
            	contentType = "video/quicktime";
            	break;
            default:
                break;
        }
        
        return contentType;
    }
	
	public UploadedFile upload(final FileType fileType, MultipartFile file, String fileSize) {
		OSSClient ossClient = new OSSClient(Configurations.OSS.ENDPOINT, Configurations.OSS.KEY_ID, Configurations.OSS.KEY_SECRET);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		if (!(fileSize == null || "".equals(fileSize))) {
			objectMetadata.setContentLength(Long.parseLong((fileSize)));
		} else {
			objectMetadata.setContentLength(file.getSize());
		}
		
		String realName = file.getOriginalFilename();
		//In spring don't need converting
//		try{
//			realName = new String(realName.getBytes("iso8859-1"),"utf-8");
//		}catch(Exception e){
//			logger.warn("Convert name encoding failed");
//		}
		int index = realName.lastIndexOf(".");
		String postFix = realName.substring(index);

		// String fileShortName = formDataContentDisposition.getFileName();
		String ossUrl = getPrefix(fileType) + UUID.randomUUID().toString() + postFix;
		if (fileType == FileType.FILE || fileType == FileType.UNIT_TEST){
			ossUrl = getPrefix(fileType) + UUID.randomUUID().toString() + '/' + realName;
		}
		
		// 设置content type
		String strContentType = getContentType(postFix.substring(1));
		objectMetadata.setContentType(strContentType);
		
		final String url = ossUrl;

		PutObjectResult result=null;
		try {
			result = ossClient.putObject(Configurations.OSS.BUCKET, url, file.getInputStream(), objectMetadata);
		} catch (OSSException e) {
            logger.error("OSSException,e={}",e);
		} catch (ClientException e) {
            logger.error("ClientException,e={}",e);
        } catch (IOException e) {
            logger.error("IOException,e={}",e);
        }

		logger.info("uploading file = {} with type = {} to AliYun OSS", file.getOriginalFilename(), fileType.name());

		securityService.logAudit(Level.INFO, Category.FILE_UPLOAD, "Uploaded file: " + url);
		switch (fileType) {
		case AVATAR:
			//Shrink avatar
			//Shrink avatar 
			Runnable imgProcess = new Runnable() {
				@Override
				public void run() {
					String avatarLargeStyle = "avatar-large";
					if (!ImageProcessAPI.shrink(Configurations.OSS.shrinkURl + "/" + url, avatarLargeStyle)) {
						logger.error("Failed to process the image[" + url + "] with style" + avatarLargeStyle + " to AliYun.");
					}
				}

			};
			new Thread(imgProcess).start();
			break;
		case IMAGE:

		default:
			break;
		}

		if (result == null) {
			throw new FileUploadServiceException("Upload file fail.", url);
		} else {
			UploadedFile uf = new UploadedFile();
			if (fileType == FileType.FILE || fileType == FileType.UNIT_TEST){
				uf.setName(realName);
			} else{
				uf.setName(UUID.randomUUID().toString() + postFix);
			}
			
			uf.setUrl(Configurations.OSS.URL_FIX + "/" + url);

			return uf;
		}

	}

	private String getPrefix(FileType fileType) {
		String dir = null;

		switch (fileType) {
		case AVATAR:
			dir = Configurations.OSS.AVATAR + "/";
			break;
		case IMAGE:
			dir = Configurations.OSS.IMAGE + "/";
			break;
		case VIDEO:
			dir = Configurations.OSS.VIDEO + "/";
			break;
		case AUDIO:
			dir = Configurations.OSS.AUDIO + "/";
			break;
		case REPORT:
			dir = Configurations.OSS.REPORT + "/";
			break;
		case FILE:
			dir = Configurations.OSS.FILE + "/";
			break;
		case UNIT_TEST:
			dir = Configurations.OSS.UNIT_TEST + "/";
			break;
		default:
				
		}
		return dir;
	}

	public StringWrapper getTeacherFilesRootPath() {
		StringWrapper stringWrapper = new StringWrapper();
		stringWrapper.setWord(TEACHER_RECRUITMENT_URL);
		return stringWrapper;
	}	

}
