package com.vipkid.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Student;
import com.vipkid.model.StudentExam;
import com.vipkid.repository.StudentExamRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.BadRequestServiceException;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

/**
 * 学生测试服务
 * @author VIPKID
 *
 */

@Service
public class StudentExamService {

	private Logger logger = LoggerFactory.getLogger(StudentExamService.class.getSimpleName());
	
	private static String kStrFoundationKey = "foundation";
	// 2015-09-28 把L1U0修改为L0U0 !
	private static String kStrLevel0Key = "l1u0";		// l1u0 --> l0u0  (?)
	
	private static HashMap<String, String>  kExamComments = new HashMap<String,String>(){ 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			// 2015-08-03 -- 英文部分使用（）包括，和原有数据保持一致。新显示时，解析出来，换行显示。
			// 2015-09-10 version 5 级别标准
			put(kStrFoundationKey,"英语学习基础级别 (ESL Skill Building)"); 
			
			put(kStrLevel0Key,"英语学习基础级别 (ESL Skill Building)"); 
			put("l1u1","美国小学K年级 (Grade K)");
			put("l1u4","美国小学K年级 (Grade K)");
			put("l1u9","美国小学K年级 (Grade K)");
			
			put("l2u1","美国小学K年级 (Grade K)");
			put("l2u4","美国小学K年级 (Grade K)");
			put("l2u7","美国小学1年级 (Grade 1)");
			put("l2u10","美国小学1年级 (Grade 1)");
			
			put("l3u1","美国小学1年级 (Grade 1)");
			put("l3u4","美国小学1年级 (Grade 1)");
			put("l3u7","美国小学2年级 (Grade 2)");
			put("l3u10","美国小学2年级 (Grade 2)");
			
			put("l4u1","美国小学2年级 (Grade 2)");
			put("l4u4","美国小学2年级 (Grade 2)");
			put("l4u7","美国小学2年级 (Grade 2)");
			put("l4u10","美国小学2年级 (Grade 2)");	
			
		} 
	};
	
	
	@Context
	private ServletContext servletContext;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private StudentExamRepository studentExamRepository;

	@Resource
	private StudentRepository studentRepository;
	
	public StudentExam find(long examid){
		return studentExamRepository.findById(examid);
	}
	
	public List<StudentExam> findByStudentId(long stduentId){
		return studentExamRepository.findByStudentId(stduentId);
	}

	/**
	 * 在未登录状态下完成的测试，在登录后，要对结果进行更新。
	 * @param uuid
	 * @param userId
	 * @return
	 */
	public StudentExam findByUuidForStudentId(String uuid, long userId){
		
		if (null == uuid) {
			throw new BadRequestServiceException("requet uuid is null");
		}
		
		StudentExam studentExam = studentExamRepository.findByUUId(uuid);
//		// 已经正确地设置过id了。
//		if (studentExam.getStudentId()>0) {
//			throw new BadRequestServiceException("Bad request！");
//		}
		
//		if (studentExam.getStudentId()<1  && userId>0 ) {
//			studentExam.setStudentId(userId);
//			//update
//			studentExam = studentExamRepository.update(studentExam);
//		}
//		
//		if (studentExam.getStudentId()<1) {
//			throw new UserNotExistServiceException("You haven't logined.");
//		}
		return studentExam;
	}

	public List<StudentExam> findByFamilyId(long familyId) {
		// 
		List<StudentExam> studentExamList = studentExamRepository.findByFamilyId(familyId);
		return studentExamList;
	}
	
	public StudentExam update(StudentExam studentExam){
		studentExamRepository.update(studentExam);
		return studentExam;
	}
	
	//--
	public StudentExam create(StudentExam studentExam){
		logger.debug("create a student exam.");
		return studentExamRepository.create(studentExam);
	}

	public StudentExam getLastExam( long studentId) {
		//
		return studentExamRepository.findLastByStudentId(studentId);
	}

	/**
	 * 在create时，可能没有studentId和family Id，在update时，需要这两个值
	 * 2015-06-10 添加csrf的处理：
	 * 	在用户【开始测试】时，生成带uuid作为token的studentExam记录。并同时设置到cookies中。
	 * 	在用户提交时，根据token，检查是否正确的请求，排除csrf。
	 * @param studentId
	 * @param familyId
	 * @param strLevel -- no use
	 * @return 
	 */
	public StudentExam createNew( long studentId, long familyId, String strLevel){
		logger.debug("createNew a student exam.");
		
		// 邮件发送 -- 需要student及相关信息。
		// notes:  针对注册用户，具有student ID，发送邮件。 针对未登陆（未注册）用户，无student数据，另行处理.
		Student student = studentRepository.find(studentId);
		if (null == student) {
			throw new BadRequestServiceException("Thx but don't hack");
		}
		
		// clean un-finished data.
		studentExamRepository.clearForStudent(studentId);
		
		StudentExam studentExam = new StudentExam();
//		String strCommnents = getCommentsForLevel(strLevel);
//		if (null == strCommnents) {
//			throw new BadRequestServiceException("error level value.");
//		}
		
		String uuid = UUID.randomUUID().toString();
		studentExam.setRecordUuid(uuid);
		
		Date createDT = new Date();
		studentExam.setCreateDatetime(createDT);
		
//		studentExam.setStudentId(studentId);
		studentExam.setStudent(student);
		studentExam.setFamilyId(familyId);
		
		StudentExam newStudentExam = studentExamRepository.create(studentExam);
		
		return newStudentExam;
	}

	/**
	 * 在create时，可能没有studentId和family Id，在update时，需要这两个值
	 * 2015-06-10 添加csrf的处理：
	 * 	在用户【开始测试】时，生成带uuid作为token的studentExam记录。并同时设置到cookies中。
	 * 	在用户提交时，根据token，检查是否正确的请求，排除csrf。
	 * @param studentId
	 * @param familyId
	 * @param strLevel
	 * @return 
	 */
	public StudentExam updateStudentExam(long studentId, long familyId, String strLevel, String csrfToken){
		logger.debug("another create a student exam. -- token:{}",csrfToken);
		
		if (null == strLevel) {
			throw new BadRequestServiceException("There isn't level given.");
		}
		
		// 2015-09-12 foundation --> l1u0  2015-09-28 l0u0;
		if (strLevel.equalsIgnoreCase(kStrFoundationKey)) {
			strLevel = kStrLevel0Key;
		}
		
		// 邮件发送 -- 需要student及相关信息。
		// notes:  针对注册用户，具有student ID，发送邮件。 针对未登陆（未注册）用户，无student数据，另行处理.
		Student student = null;
		StudentExam studentExam = null;
		
		try {
			student = studentRepository.find(studentId);
			// token check 
			studentExam = studentExamRepository.findByUUId(csrfToken);
		} catch (Exception e) {
			throw new BadRequestServiceException("Thx but don't hack1");
		}
		
		if (null == student) {
			throw new BadRequestServiceException("Thx but don't hack2");
		}
		
//		if (null == studentExam || studentExam.getEndDatetime() != null) { // && studentExam.getEndDatetime() != null ) {
//			return studentExam;
//			//throw new BadRequestServiceException("Thx but don't hack3");
//		}
//		// not null in db
//		if ( studentExam.getEndDatetime() != null ) {
//			throw new BadRequestServiceException("please start your test from home.");
//		}
		
		String strCommnents = getCommentsForLevel(strLevel);
		if (null == strCommnents) {
			throw new BadRequestServiceException("error level value.");
		}
		
		String strUpperLevle = strLevel.toUpperCase();
		studentExam.setExamLevel(strUpperLevle);
		
		Date createDT = new Date();
		studentExam.setEndDatetime(createDT);
		studentExam.setExamComment(strCommnents);
		studentExam.setStatus(1);
		studentExam.setStudent(student);
		
		StudentExam newStudentExam = studentExamRepository.update(studentExam);
		
		EMail.sendStudentLevelExamEmail(studentExam, student);
		
		return newStudentExam;
	}


	// level --> comments
	private String getCommentsForLevel(String strLevelContent) {
		
		if (null == strLevelContent) {
			return "";
		}
		
		String strLevel = strLevelContent.toLowerCase();
		String strComment = kExamComments.get(strLevel);	
		if (null == strComment) {
			logger.error("错误的levle");
			
		}
		return strComment;
	}
	
	public Count count( long studentId) {
		//
		long nCnt = studentExamRepository.count(studentId);
		return new Count(nCnt);
	}

	public Count countByFamily( long familyId) {
		//
		long nCnt = studentExamRepository.countByFamily(familyId);
		return new Count(nCnt);
	}

	public List<StudentExam> list( Long studentId, int start, int length) {
		logger.debug("list StudentExam with params:  studentId = {}, start = {}, length = {}.", studentId, start, length);
		return studentExamRepository.list(studentId, start, length);
	}

	//listByFamily
	public List<StudentExam> listByFamily( Long familyId, int start, int length) {
		logger.debug("list StudentExam with params:  studentId = {}, start = {}, length = {}.", familyId, start, length);
		return studentExamRepository.listByFamily(familyId, start, length);
	}

	// search method for system =================
	public Count countBySearch( String search, DateTimeParam executeDateTimeFrom, DateTimeParam executeDateTimeTo,long salesId, long cltId) {
		//
		long nCnt = studentExamRepository.countBySearch(search, executeDateTimeFrom, executeDateTimeTo, salesId, cltId);
		return new Count(nCnt);
	}

	
	public List<StudentExam> listBySearch( String search, DateTimeParam executeDateTimeFrom, DateTimeParam executeDateTimeTo,long salesId, long cltId, int start, int length) {
		logger.debug("list StudentExam with params:  search = {}, start = {}, length = {}.", search, start, length);
		return studentExamRepository.listBySearch(search,executeDateTimeFrom, executeDateTimeTo, salesId, cltId, start, length);
	}

	//===================
	/**
	 * 清理指定学生的未完成记录
	 * @param studentId
	 */
	public void clearForStudent(long studentId) {
		return;
	}

	public StudentExam findByRecordUuid(String csrfToken) {
		// 
		StudentExam studentExam = studentExamRepository.findByUUId(csrfToken);
		return studentExam;
	}
	
}
