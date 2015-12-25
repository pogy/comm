package com.vipkid.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import com.vipkid.ext.dby.*;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.fusesource.hawtdispatch.internal.util.StringSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.vipkid.ext.dby.AttachDocumentResult;
import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.Lesson;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Role;
import com.vipkid.model.Staff;
import com.vipkid.model.User;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.BadRequestServiceException;
import com.vipkid.service.pojo.Room;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;
import com.vipkid.service.DBYTrialLessonUploadServiceUtil;

@Service
public class DBYService {
	private Logger logger = LoggerFactory.getLogger(DBYService.class.getSimpleName());

	@Resource
	private SecurityService securityService;

	@Resource
	private OnlineClassRepository onlineClassRepository;

	@Resource
	private LessonRepository lessonRepository;

	@Resource
	private StaffRepository staffRepository;

	@Resource
	private DBYTrialLessonUploadServiceUtil trialLessonUploadUtil;

	@Resource 
	private DBYTrialAttachDocsServiceUtil trialAttachDocsUtil;
	
	public Room getDBYRoomURL(String userId, String name, String roomId, Role role) {
		logger.info("get DBY room URL for userId = {}, name = {}, roomId = {}, role = {}", userId, name, roomId, role);

		if (TextUtils.isEmpty(userId)) {
			throw new BadRequestServiceException("userId should not be empty");
		} else if (TextUtils.isEmpty(name)) {
			throw new BadRequestServiceException("name should not be empty");
		} else if (TextUtils.isEmpty(roomId)) {
			throw new BadRequestServiceException("roomId should not be empty");
		} else if (role == null) {
			throw new BadRequestServiceException("role should not be empty");
		}
		name = name.trim();
		String url = DBYAPI.getRoomURL(userId, name, roomId, role);
		Room room = new Room(url);
		return room;
	}

	public AttachDocumentResult doReAttatchDocument(long onlineClassId, long userId) {
		AttachDocumentResult attachDocumentResult = null;
		Staff user = null;
		OnlineClass onlineClass = null;
		try {
			onlineClass = onlineClassRepository.find(onlineClassId);

			attachDocumentResult = new AttachDocumentResult();

			Boolean bResult = false;
			Course course = onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
			if (null != course && Course.Type.TRIAL == course.getType()) {
				Lesson lesson = onlineClass.getLesson();
				if (null != lesson) {
					// trial reset doc
					bResult = trialAttachDocsUtil.attacheTrailDocForClassroom(onlineClass, lesson);
				}

				if (bResult) {
					attachDocumentResult.setSuccess(true);

				} else {
					attachDocumentResult.setSuccess(false);
				}
				return attachDocumentResult;
			}

			user = staffRepository.find(userId);
			UpdateRoomTitleResult updateRoomTitleResult = DBYAPI.updateRoomTitle(onlineClass.getClassroom(), onlineClass.getLesson().getName());
			if (updateRoomTitleResult.isSuccess()) {
				boolean gotDocument = false;
				ListDocumentsResult results = DBYAPI.listDocuments(onlineClass.getClassroom());
				if (results.isSuccess()) {
					for (int i = 0; i < results.getDocuments().size(); i++) {
						Document doc = results.getDocuments().get(i);
						if (doc != null && doc.getDocumentId() != null) {
							if (doc.getDocumentId().equals(onlineClass.getLesson().getDbyDocument())) {
								gotDocument = true;
								onlineClass.setDbyDocument(onlineClass.getLesson().getDbyDocument());
							} else {
								DBYAPI.removeDocument(onlineClass.getClassroom(), doc.getDocumentId());
								securityService.logAudit(Level.WARNING, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "reAttatchDocument..............remove document! ,teacher name"
										+ onlineClass.getTeacher().getName() + "serial number:" + onlineClass.getClassroom(), user);
							}
						}
					}
				}
				if (!gotDocument) {
					attachDocumentResult = DBYAPI.attachDocument(onlineClass.getClassroom(), onlineClass.getLesson().getDbyDocument());
					securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "Update classroom for onlineClass: coming ......................" + onlineClass.getSerialNumber(),
							user);

					if (attachDocumentResult.isSuccess()) {
						onlineClass.setDbyDocument(attachDocumentResult.getDocumentId());
						securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "Update classroom for onlineClass: " + onlineClass.getSerialNumber(), user);
						onlineClass.setAttatchDocumentSucess(true);
					} else {
						onlineClass.setAttatchDocumentSucess(false);
						securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "Fail to update DuoBeiYun classroom document for online class id: " + onlineClass.getId()
								+ "reAttatchDocument.............., the error code is: " + attachDocumentResult.getError(), user);
						EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.FIRE_MAN, onlineClass, attachDocumentResult, user, "update");
						EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.TESTERHOU, onlineClass, attachDocumentResult, user, "update");
						EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.DEVDENG, onlineClass, attachDocumentResult, user, "update");
					}

				} else {
					attachDocumentResult = new AttachDocumentResult();
					attachDocumentResult.setSuccess(true);
				}
				onlineClassRepository.update(onlineClass);
			}
		} catch (Exception e) {
			if (onlineClass != null && user != null) {
				securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "reAttatchDocument..............got errors " + e.getMessage(), user);
			}
			logger.error("Server exception,msg={}", e);
		}
		return attachDocumentResult;

	}

	public Response doUploadDocuments(String pptFile) {
		Preconditions.checkArgument(StringUtils.isNotBlank(pptFile), "PPT file path should not be null");
		logger.info("pptFile={}", pptFile);
		File fileDir = new File(pptFile);
		if (fileDir.isDirectory()) {
			File[] files = fileDir.listFiles();

			ExecutorService executorService = Executors.newFixedThreadPool(10);

			for (final File file : files) {
				final String fileName = file.getName();
				int dot = fileName.lastIndexOf(".");
				if ((dot > -1) && (dot < (fileName.length()))) {
					String fileNameWithoutExtension = fileName.substring(0, dot);
					logger.info("fileNameWithoutExtension={}", fileNameWithoutExtension);
					final Lesson lesson = lessonRepository.findBySerialNumber(fileNameWithoutExtension);
					if (lesson == null) {
						String operation = "Fail to upload ppt for lesson " + fileName + ", can not find the relevant lesson in DB.";
						logger.error(operation);
						securityService.logSystemAudit(Level.ERROR, Category.DBY_UPLOAD_PPT_FAIL, operation);
					} else {
						FutureTask<Lesson> futureTask = new FutureTask<Lesson>(new Callable<Lesson>() {
							@Override
							public Lesson call() throws Exception {
								UploadDocumentResult uploadDocumentResult = DBYAPI.uploadDocument(file);
								if (uploadDocumentResult.isSuccess()) {
									lesson.setDbyDocument(uploadDocumentResult.getUuid());
									lessonRepository.update(lesson);

									String operation = "Success to upload ppt for lesson " + fileName;
									logger.info(operation);
									securityService.logSystemAudit(Level.INFO, Category.DBY_UPLOAD_PPT_SUCCESS, operation);

								} else {
									String operation = "Fail to upload ppt for lesson " + fileName + ", the error is: " + uploadDocumentResult.getError();
									logger.error(operation);
									securityService.logSystemAudit(Level.ERROR, Category.DBY_UPLOAD_PPT_FAIL, operation);
								}
								return lesson;
							}
						});

						executorService.submit(futureTask);
					}
				}
			}

			return new Response(HttpStatus.OK.value(), "PPT are uploading, please see result in audit later.");
		} else {
			return new Response(HttpStatus.BAD_REQUEST.value());
		}
	}

	public ListDocumentsResult listDocument(long onlineClassId) {
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassId);
		ListDocumentsResult listDocumentResult = DBYAPI.listDocuments(onlineClass.getClassroom());
		return listDocumentResult;
	}

	public RemoveDocumentResult removeDocument(long onlineClassId, String documentId) {
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassId);
		RemoveDocumentResult removeDocumentResult = DBYAPI.removeDocument(onlineClass.getClassroom(), documentId);
		return removeDocumentResult;
	}

	
	public com.vipkid.rest.vo.Response doReScheduleDbyDocuments() {

		try {
			Date startDate = DateTimeUtils.getToday(0);
			Date endDate = DateTimeUtils.getTomorrow(0);
			securityService.logSystemAudit(Level.INFO, Category.PPT_UPDATE, "reScheduleDbyDocuments.........begin..........");

			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedOnlineClassByStartDateAndEndDate(startDate, endDate);
			for (OnlineClass onlineClass : onlineClasses) {

				Course course = onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
				if (null != course && Course.Type.TRIAL == course.getType()) {
					Lesson lesson = onlineClass.getLesson();
					if (null != lesson) {
						if (trialAttachDocsUtil.needAttachForTrial(onlineClass, lesson)) {
							// trial reset doc -- 覆盖式操作
							trialAttachDocsUtil.attacheTrailDocForClassroom(onlineClass, lesson);
						}
					}

				} else {
					// 非Trial课处理
					try {

						UpdateRoomTitleResult updateRoomTitleResult = DBYAPI.updateRoomTitle(onlineClass.getClassroom(), onlineClass.getLesson().getName());
						if (updateRoomTitleResult.isSuccess()) {

							boolean gotDocument = false;
							ListDocumentsResult results = DBYAPI.listDocuments(onlineClass.getClassroom());
							if (onlineClass.getClassroom() == null) {
								break;
							}
							if (results != null && results.isSuccess()) {
								for (int i = 0; i < results.getDocuments().size(); i++) {
									Document doc = results.getDocuments().get(i);
									if (doc != null && doc.getDocumentId() != null) {
										if (doc.getDocumentId().equals(onlineClass.getLesson().getDbyDocument())) {
											gotDocument = true;
											onlineClass.setDbyDocument(onlineClass.getLesson().getDbyDocument());
										} else {
											DBYAPI.removeDocument(onlineClass.getClassroom(), doc.getDocumentId());
											securityService.logSystemAudit(Level.WARNING, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "reScheduleDbyDocuments.........,remove document! ,teacher name"
													+ onlineClass.getTeacher().getName() + "class room:" + onlineClass.getClassroom());
										}
									}
								}
							} else {
								securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "reScheduleDbyDocuments.........,list documents error");
							}

							if (!gotDocument) {

								AttachDocumentResult attachDocumentResult = DBYAPI.attachDocument(onlineClass.getClassroom(), onlineClass.getLesson().getDbyDocument());
								if (attachDocumentResult.isSuccess()) {
									onlineClass.setDbyDocument(attachDocumentResult.getDocumentId());
									securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "Update classroom for onlineClass: " + onlineClass.getSerialNumber());
									onlineClass.setAttatchDocumentSucess(true);
								} else {
									// String error =
									// attatchDocumentResult.getError();
									// if
									// (!"repeat_arrange_to_course_error".equals(error))
									{
										onlineClass.setAttatchDocumentSucess(false);
										securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE,
												"Fail to update DuoBeiYun classroom document for online class id: " + onlineClass.getId() + ", the error code is: " + attachDocumentResult.getError());
										EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.FIRE_MAN, onlineClass, attachDocumentResult, null, "schedule");
										EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.TESTERHOU, onlineClass, attachDocumentResult, null, "schedule");
										EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.DEVDENG, onlineClass, attachDocumentResult, null, "schedule");
									}
								}
							}
							onlineClassRepository.update(onlineClass);
						}
					} catch (Exception e) {
						logger.error("doReScheduleDbyDocuments error", e);
						securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "reScheduleDbyDocuments.........,got errors    :" + e.getMessage());
					}

				} // if else
			} // ---- for
			securityService.logSystemAudit(Level.INFO, Category.PPT_UPDATE, "reScheduleDbyDocuments.........begin..........");

			return new Response(HttpStatus.OK.value(), "PPT are uploaed, please see result in audit later.");
		} catch (Exception e) {
			securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "reScheduleDbyDocuments.........,got errors    :" + e.getMessage());

			logger.error("doReScheduleDbyDocuments error", e);
			return new Response(HttpStatus.BAD_REQUEST.value(), "PPT are uploaed error, please see result in audit later.");

		}

	}

	/**
	 * 2015-09-01 trial foundation课件--多个文档上传
	 * 
	 * @param pptFilePath
	 * @return
	 */
	public Response doUploadTrialDocuments(String pptFilePath, String strTrialType) {
		Preconditions.checkArgument(StringUtils.isNotBlank(pptFilePath), "PPT file path should not be null");
		logger.info("FoundationTrialDocument pptFilepath={}", pptFilePath);
		return trialLessonUploadUtil.doUploadTrialDocuments(pptFilePath, strTrialType);

	}

}
