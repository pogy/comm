package com.vipkid.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.vipkid.ext.dby.DBYAPI;
import com.vipkid.ext.dby.UploadDocumentResult;
import com.vipkid.model.Lesson;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.SecurityService;

@Service
public class DBYTrialLessonUploadServiceUtil {

	private Logger logger = LoggerFactory
			.getLogger(DBYTrialLessonUploadServiceUtil.class.getSimpleName());

	@Resource
    private SecurityService securityService;

    @Resource
    private OnlineClassRepository onlineClassRepository;

    @Resource
    private LessonRepository lessonRepository;
    
    // 
	final static String kStrFoundTrialNamePrefix = "T1-U1-LC1-L1";
	final static String kStrLevel1TrialNamePrefix = "T1-U1-LC1-L2";
	final static String kStrTravelTrialNamePrefix = "T1-U1-LC1-L3";

	String[] strTrialLevel1FileNames = { 
			"T1-U1-LC1-L1.pptx",
			"T1-U1-LC1-L1 Level 0 Unit 00.pdf",
			"T1-U1-LC1-L1 Level 1 Unit 01.pdf",
			"T1-U1-LC1-L1 Level 1 Unit 04.pdf",
			"T1-U1-LC1-L1 Level 1 Unit 09.pdf",
			"T1-U1-LC1-L1 Level 2 Unit 01.pdf",
			"T1-U1-LC1-L1 Level 2 Unit 04.pdf",
			"T1-U1-LC1-L1 Level 2 Unit 07.pdf",
			"T1-U1-LC1-L1 Level 2 Unit 10.pdf",
			"T1-U1-LC1-L1 Level 3 Unit 01.pdf",
			"T1-U1-LC1-L1 Level 3 Unit 04.pdf",
			"T1-U1-LC1-L1 Level 4 Unit 01.pdf" };

	String[] strTrialLevel2FileNames = { 
			"T1-U1-LC1-L2.pptx",
			"T1-U1-LC1-L2 Level 0 Unit 00.pdf",
			"T1-U1-LC1-L2 Level 1 Unit 01.pdf",
			"T1-U1-LC1-L2 Level 1 Unit 04.pdf",
			"T1-U1-LC1-L2 Level 1 Unit 09.pdf",
			"T1-U1-LC1-L2 Level 2 Unit 01.pdf",
			"T1-U1-LC1-L2 Level 2 Unit 04.pdf",
			"T1-U1-LC1-L2 Level 2 Unit 07.pdf",
			"T1-U1-LC1-L2 Level 2 Unit 10.pdf",
			"T1-U1-LC1-L2 Level 3 Unit 01.pdf",
			"T1-U1-LC1-L2 Level 3 Unit 04.pdf",
			"T1-U1-LC1-L2 Level 4 Unit 01.pdf" };

	String[] strTrialLevel3FileNames = { 
			"T1-U1-LC1-L3.pptx",
			"T1-U1-LC1-L3 Level 0 Unit 00.pdf",
			"T1-U1-LC1-L3 Level 1 Unit 01.pdf",
			"T1-U1-LC1-L3 Level 1 Unit 04.pdf",
			"T1-U1-LC1-L3 Level 1 Unit 09.pdf",
			"T1-U1-LC1-L3 Level 2 Unit 01.pdf",
			"T1-U1-LC1-L3 Level 2 Unit 04.pdf",
			"T1-U1-LC1-L3 Level 2 Unit 07.pdf",
			"T1-U1-LC1-L3 Level 2 Unit 10.pdf",
			"T1-U1-LC1-L3 Level 3 Unit 01.pdf",
			"T1-U1-LC1-L3 Level 3 Unit 04.pdf",
			"T1-U1-LC1-L3 Level 4 Unit 01.pdf" };

	private String getSerialNumberForTrial(String strTrialType) {
		if (strTrialType.equals("FoundationTrial")) {
			return kStrFoundTrialNamePrefix;
		}
		if (strTrialType.equals("Level1Trial")) {
			return kStrLevel1TrialNamePrefix;
		}
		if (strTrialType.equals("TravelTrial")) {
			return kStrTravelTrialNamePrefix;
		}
		logger.error("Error Trial Type for Trial SerialNumber name..");
		return "";
	}

	/**
	 * 2015-09-01 trial foundation课件--多个文档上传
	 * 
	 * @param pptFilePath
	 * @return
	 */
	public Response doUploadTrialDocuments(String pptFilePath,
			String strTrialType) {
		Preconditions.checkArgument(StringUtils.isNotBlank(pptFilePath),
				"PPT file path should not be null");
		logger.info("FoundationTrialDocument pptFilepath={}", pptFilePath);

		// 获取type获取serial number
		String strTrialSerialNumber = getSerialNumberForTrial(strTrialType);
		if (StringUtils.isEmpty(strTrialSerialNumber)) {
			String operation = "Error trial type:" + strTrialType;
			logger.error(operation);
			securityService.logSystemAudit(Level.ERROR,
					Category.DBY_UPLOAD_PPT_FAIL, operation);

			return new Response(HttpStatus.BAD_REQUEST.value(), operation);
		}

		File fileDir = new File(pptFilePath);
		if (!fileDir.isDirectory()) {
			return new Response(HttpStatus.BAD_REQUEST.value(),
					"not found directory for ppt");
		}

		//
		Lesson lesson = lessonRepository
				.findBySerialNumber(strTrialSerialNumber);
		if (lesson == null) {
			String operation = "Fail to upload ppt for Trial Lesson: "
					+ strTrialSerialNumber
					+ ", can not find the relevant lesson in DB.";
			logger.error(operation);
			securityService.logSystemAudit(Level.ERROR,
					Category.DBY_UPLOAD_PPT_FAIL, operation);
			return new Response(HttpStatus.BAD_REQUEST.value(), operation);
		}

		// 开始处理各个文件
		File[] files = fileDir.listFiles();
		if (files.length < 1) {
			// // 先清理
			// lesson.setDbyDocument("");
			// lessonRepository.update(lesson);
			String operation = "upload ppt for Trial Lesson: "
					+ strTrialSerialNumber + ", But no file in the directory.";
			logger.error(operation);
			securityService.logSystemAudit(Level.ERROR,
					Category.DBY_UPLOAD_PPT_FAIL, operation);
			return new Response(HttpStatus.BAD_REQUEST.value(), operation);
		}

		List<File> fileList = new ArrayList<File>();
		for (File f : files) {
		    fileList.add(f);
		}
		
		Collections.sort(fileList, new Comparator<File>() {
		    @Override
		    public int compare(File o1, File o2) {
		        if (o1.isDirectory() && o2.isFile())
		            return -1;
		        if (o1.isFile() && o2.isDirectory())
		            return 1;
		        return o1.getName().compareTo(o2.getName());
		    }
		});
		
		// 再排序 
		List<File> sortedFileList = new ArrayList<File>();
		// 最后的
		int nLen = fileList.size();
		sortedFileList.add(fileList.get(nLen-1));
		for (int i=0;i<nLen-1;i++) {
			//
			sortedFileList.add(fileList.get(i));
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		// 上传
		FutureTask<Boolean> futureTask = new FutureTask<Boolean>(
				new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {

						StringBuffer dbyDocs = new StringBuffer();

						for (final File file : sortedFileList) {
					
							final String fileName = file.getName();
							int dot = fileName.lastIndexOf(".");
							if (dot < 0 || dot == (fileName.length() - 1)) {
								logger.info(
										"check fileName {}, don't handle it",
										fileName);
								// 文件名不对
								continue;
							}

							// 2015-09-01
							// 名字为lesson的serailNumber,进行查找T1-U1-LC1-L1-***
							String fileNameWithoutExtension = fileName
									.substring(0, dot);
							logger.info("fileNameWithoutExtension={}",
									fileNameWithoutExtension);

							//
							boolean bFileNameOK = fileName
									.startsWith(strTrialSerialNumber);
							if (!bFileNameOK) {
								// 目录中会包含其他的meta文件。过滤掉。
								logger.error(
										"fileNameWithoutExtension: this name not start with {}",
										strTrialSerialNumber);
								continue;
							}

							UploadDocumentResult uploadDocumentResult = DBYAPI
									.uploadDocument(file);

							if (uploadDocumentResult.isSuccess()) {

								if (dbyDocs.length() > 0) {
									dbyDocs.append(",");
									dbyDocs.append(uploadDocumentResult
											.getUuid());
								} else {
									dbyDocs.append(uploadDocumentResult
											.getUuid());
								}

								// 删除文件
								// file.delete();

							} else {
								String operation = "Fail to upload ppt for lesson "
										+ fileName
										+ ", the error is: "
										+ uploadDocumentResult.getError();
								logger.error(operation);
								securityService
										.logSystemAudit(Level.ERROR,
												Category.DBY_UPLOAD_PPT_FAIL,
												operation);
							}

						}

						// 查询获取lesson，防止多线程中重复覆盖
						Lesson lesson = lessonRepository
								.findBySerialNumber(strTrialSerialNumber);
						// attach新的文档
						// lesson.addTrialDBYDocument(uploadDocumentResult.getUuid());
						lesson.setDbyDocument(dbyDocs.toString());
						lessonRepository.update(lesson);

						// String operation =
						// "Success to upload ppt and update for trial lesson: "
						// + strTrialSerialNumber;
						// logger.info(operation);
						// securityService.logSystemAudit(Level.INFO,
						// Category.DBY_UPLOAD_PPT_SUCCESS, operation);
						//
						//
						String finishOperation = "Finished: Success to upload ppt and update for trial lesson "
								+ strTrialSerialNumber;
						logger.info(finishOperation);
						securityService.logSystemAudit(Level.INFO,
								Category.DBY_UPLOAD_PPT_SUCCESS,
								finishOperation);

						return true;
					}
				});

		executorService.submit(futureTask);

		return new Response(HttpStatus.OK.value(),
				"PPT are uploading, please see result in audit later.");

	}

}
