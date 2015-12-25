package com.vipkid.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.dby.AttachDocumentResult;
import com.vipkid.ext.dby.DBYAPI;
import com.vipkid.ext.dby.Document;
import com.vipkid.ext.dby.ListDocumentsResult;
import com.vipkid.model.Lesson;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.security.SecurityService;

@Service
public class DBYTrialAttachDocsServiceUtil {
	private Logger logger = LoggerFactory
			.getLogger(DBYTrialLessonUploadServiceUtil.class.getSimpleName());

	@Resource
    private SecurityService securityService;

    @Resource
    private OnlineClassRepository onlineClassRepository;

    @Resource
    private LessonRepository lessonRepository;
    
    /**
	 * 去除trial课所有的课件，否则，attach课件后，顺序会出现问题。
	 * @param onlineClass
	 * @return
	 */
	public Boolean removeTrialAttachedDocs(OnlineClass onlineClass) {
		//
		ListDocumentsResult results = DBYAPI.listDocuments(onlineClass.getClassroom());
		if (false == results.isSuccess()) {
			return false;
		}

		for (int i = 0; i < results.getDocuments().size(); i++) {
			Document doc = results.getDocuments().get(i);
			if (doc != null && doc.getDocumentId() != null) {
				DBYAPI.removeDocument(onlineClass.getClassroom(), doc.getDocumentId());
				securityService.logAudit(Level.WARNING, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "reAttatch for trial Document..............remove trial document! ,teacher name" + onlineClass.getTeacher().getName()
						+ "serial number:" + onlineClass.getClassroom());
			}
		}
		
		return true;
	}

	/**
	 * 2015-09-16
	 * 
	 * @param onlineClass
	 * @param lesson
	 * @return
	 */
	public boolean attacheTrailDocForClassroom(OnlineClass onlineClass, Lesson lesson) {
		boolean bAttachSuccess = false;
		//
		String docSetting = lesson.getDbyDocument();
		String[] docs = docSetting.split(",");
		// 反序attach --
		int nLen = docs.length;
		if (nLen < 1) {
			return false;
		}
		
		Boolean bRemoveResult = removeTrialAttachedDocs(onlineClass);
		if (!bRemoveResult) {
			//
			logger.warn("removeTrialAttachDoc return false");
		}
		
		for (int nIndex = nLen - 1; nIndex >= 0; nIndex--) {

			String doc = docs[nIndex];
			AttachDocumentResult attachDocumentResult = DBYAPI.attachDocument(onlineClass.getClassroom(), doc);
			if (attachDocumentResult.isSuccess()) {
				bAttachSuccess = true;
				onlineClass.setDbyDocument(onlineClass.getLesson().getDbyDocument());
				securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_CREATE, "attach trial doc for classroom: " + onlineClass.getClassroom());
				onlineClass.setAttatchDocumentSucess(true);
			} else {
				securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, "Fail to attach DuoBeiYun classroom document for trial classroom id: " + onlineClass.getId()
						+ ", the error code is: " + attachDocumentResult.getError());
				if (!"repeat_arrange_to_course_error".equals(attachDocumentResult.getError())) {
				}
			}
		}

		if (bAttachSuccess) {
			onlineClassRepository.update(onlineClass);
		}

		return bAttachSuccess;
	}

	public boolean needAttachForTrial(OnlineClass onlineClass, Lesson lesson){
		
		ListDocumentsResult results = DBYAPI.listDocuments(onlineClass.getClassroom());
		if (false == results.isSuccess()) {
			return false;
		}
		String strLessonDocs = null;
		if (null != onlineClass.getLesson()) {
			strLessonDocs = onlineClass.getLesson().getDbyDocument();
		}
		
		String [] docs = strLessonDocs.split(",");
		for (String docInLesson : docs) {
			Boolean b = false;
			for (int i = 0; i < results.getDocuments().size(); i++) {
				Document doc = results.getDocuments().get(i);
				if (doc != null && doc.getDocumentId() != null) {
					if(docInLesson.indexOf(doc.getDocumentId())>=0){
						b = true;
						continue;
					}
				}
			}
			if (!b) {
				return true;
			}			
		}			
		return false ;		
	}
}
