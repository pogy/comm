package com.vipkid.task;

import com.vipkid.ext.dby.*;
import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.Lesson;
import com.vipkid.model.OnlineClass;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.DBYTrialAttachDocsServiceUtil;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;


@Component
public class AttachDocumentTask {
    private Logger logger = LoggerFactory.getLogger(AttachDocumentTask.class.getSimpleName());

    @Resource
    private OnlineClassRepository onlineClassRepostiory;

    @Resource
    private SecurityService securityService;

    @Resource 
	private DBYTrialAttachDocsServiceUtil trialAttachDocsUtil;
    @Scheduled(cron = "0 0 1 * * ?")
    //@Schedule(hour = "0", minute = "0", second = "0")
    // @Schedule(hour = "*", minute = "*", second = "*/2")//两秒发一次
//     @Scheduled(fixedRate = 50000)  //5 秒触发一次
	public void updateAttachDocumentScheduler() {
    	logger.info("Enter updateAttachDocumentScheduler()");
		try {	
			if (Configurations.Deploy.ENABLE_ATTACH_DBY) {
				updateAttachDocuments();
			}
			
		} catch (Throwable e) {
			// audit log
			securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, e.getMessage());
			logger.error("Exception found when attachDocumentScheduler:" + e.getMessage(), e);
		}
		logger.info("Leave updateAttachDocumentScheduler()");
	}

    private void updateAttachDocuments() {

        Date startDate = DateTimeUtils.getToday(0);
        Date endDate = DateTimeUtils.getTomorrow();
        List<OnlineClass> onlineClasses = onlineClassRepostiory.findBookedOnlineClassByStartDateAndEndDate(startDate, endDate);
        for (OnlineClass onlineClass : onlineClasses) {
            try {
                boolean gotDocument = false;
                if (onlineClass.getClassroom() == null) {
                    break;
                }

                // trial 课多课件的处理
                Course course =  onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
				if (null != course && Course.Type.TRIAL == course.getType()) {
					Lesson lesson = onlineClass.getLesson();
					
					if (null != lesson) {
						if (trialAttachDocsUtil.needAttachForTrial(onlineClass, lesson)) {
							// trial reset doc -- 覆盖式操作
							trialAttachDocsUtil.attacheTrailDocForClassroom(onlineClass, lesson);
						}
					}
					
					continue;
				}
				
                UpdateRoomTitleResult updateRoomTitleResult = DBYAPI.updateRoomTitle(onlineClass.getClassroom(), onlineClass.getLesson().getName());
                if (updateRoomTitleResult.isSuccess()) {
                    ListDocumentsResult results = DBYAPI.listDocuments(onlineClass.getClassroom());
                    if (results != null && results.isSuccess()) {
                        for (int i = 0; i < results.getDocuments().size(); i++) {
                            Document doc = results.getDocuments().get(i);
                            if (doc != null && doc.getDocumentId() != null) {
                                if (doc.getDocumentId().equals(onlineClass.getLesson().getDbyDocument())) {
                                    gotDocument = true;
                                    onlineClass.setDbyDocument(onlineClass.getLesson().getDbyDocument());
                                } else {
                                    DBYAPI.removeDocument(onlineClass.getClassroom(), doc.getDocumentId());
                                    securityService.logAudit(Level.WARNING, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "AttachDocumentScheduler........remove document! ,teacher name"
                                            + onlineClass.getTeacher().getName() + "class room:" + onlineClass.getClassroom());
                                    logger.info("AttachDocumentScheduler........remove document! ,teacher name" + onlineClass.getTeacher().getName() + "class room:"
                                            + onlineClass.getClassroom());
                                }
                            }
                        }
                    } else {
                        logger.info("AttachDocumentScheduler........list  document! ,teacher name" + onlineClass.getTeacher().getName() + "calss room:"
                                + onlineClass.getClassroom());
                        securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "get document error");
                    }

                    if (!gotDocument) {
                        AttachDocumentResult attachDocumentResult = DBYAPI.attachDocument(onlineClass.getClassroom(), onlineClass.getLesson()
                                .getDbyDocument());
                        if (attachDocumentResult.isSuccess()) {
                            onlineClass.setDbyDocument(attachDocumentResult.getDocumentId());
                            securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "AttachDocumentScheduler........attach document! ,teacher name" + onlineClass.getTeacher().getName() + "class room  :"
                                    + onlineClass.getClassroom());
                            logger.info("AttachDocumentScheduler........attach document! ,teacher name" + onlineClass.getTeacher().getName() + "class room  :"
                                    + onlineClass.getClassroom());
                            onlineClass.setAttatchDocumentSucess(true);
                        } else {
                            // String error = attatchDocumentResult.getError();
                            // if
                            // (!"repeat_arrange_to_course_error".equals(error))
                            {
                                onlineClass.setAttatchDocumentSucess(false);
                                securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE,
                                        "Fail to update DuoBeiYun classroom document for online class id: " + onlineClass.getId()
                                                + ", the error code is: " + attachDocumentResult.getError());
                                logger.error("AttachDocumentScheduler........attach document! ,teacher name" + onlineClass.getTeacher().getName() + "class room  :"
                                        + onlineClass.getClassroom());
                                EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.FIRE_MAN, onlineClass,
                                        attachDocumentResult, null, "schedule");
                                EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.TESTERHOU, onlineClass,
                                        attachDocumentResult, null, "schedule");
                                EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.DEVDENG, onlineClass,
                                        attachDocumentResult, null, "schedule");
                            }
                        }
                    }
                    onlineClassRepostiory.update(onlineClass);
                }
            } catch (Exception e) {
                //securityController.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, e.getMessage());
                logger.error("Exception found when attachDocumentScheduler.............:" + e.getMessage(), e);
            } catch (Throwable t) {
                logger.error("Throwable found when attachDocumentScheduler.............:" + t.getMessage(), t);
            }
        }

    }
        
}
