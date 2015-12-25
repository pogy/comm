package com.vipkid.rest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Student;
import com.vipkid.model.StudentExam;
import com.vipkid.security.SecurityService;
import com.vipkid.service.StudentExamService;
import com.vipkid.service.StudentService;
import com.vipkid.service.exception.BadRequestServiceException;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

/**
 * 学生测试服务
 * 
 * @author VIPKID
 *
 */

@RestController
@RequestMapping("/api/service/public/studentExam")
public class StudentExamController {

	private Logger logger = LoggerFactory.getLogger(StudentExamController.class.getSimpleName());

	@Context
	private ServletContext servletContext;

	@Resource
	private SecurityService securityService;

	@Resource
	private StudentExamService studentExamService;
	
	@Resource
	private StudentService studentService;
	
/*
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public StudentExam find(@RequestParam("examid") long examid) {
		return studentExamService.find(examid);
	}

	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<StudentExam> findByStudentId(@RequestParam("stduentId") long stduentId) {
		return studentExamService.findByStudentId(stduentId);
	}

	@RequestMapping(value = "/findByUuidForStudentId", method = RequestMethod.GET)
	public StudentExam findByUuidForStudentId(@RequestParam("uuid") String uuid, @RequestParam("userId") long userId) {

		if (null == uuid) {
			throw new BadRequestServiceException("requet uuid is null");
		}
		return studentExamService.findByUuidForStudentId(uuid, userId);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public StudentExam update(@RequestBody StudentExam studentExam) {
		studentExamService.update(studentExam);
		return studentExam;
	}

	@RequestMapping(value = "/create", method = RequestMethod.PUT)
	public StudentExam createNew(@RequestBody StudentExam studentExam) {
		logger.info("create a student exam.");
		return studentExamService.createNew(studentExam);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public StudentExam createNew(@RequestParam("stduentId") long studentId, @RequestParam("level") String strLevel) {
		logger.info("another create a student exam.");

		return studentExamService.createNew(studentId, strLevel);
	}
	*/
	
	//=====2015-06-15  port from feature/StudentExam
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public StudentExam find(@RequestParam("examid") long examid){
		return studentExamService.find(examid);
//		return studentExamService.findById(examid);
	}

	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<StudentExam> findByStudentId(@RequestParam("studentId") long studentId){
			return studentExamService.findByStudentId(studentId);
	//	return studentExamService.findByStudentId(studentId);
	}

	/**
	 * 在未登录状态下完成的测试，在登录后，要对结果进行更新。
	 * @param uuid
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/findByUuidForStudentId", method = RequestMethod.GET)
	public StudentExam findByUuidForStudentId(@RequestParam("uuid") String uuid, @RequestParam(value = "userId", required = false ) long userId){
		
		if (null == uuid) {
			throw new BadRequestServiceException("requet uuid is null");
		}
		
		// TODO: 在service中，find而不要update
		StudentExam studentExam = studentExamService.findByUuidForStudentId(uuid, userId) ; //studentExamService.findByUUId(uuid);
		// 已经正确地设置过id了。
		if (studentExam.getStudent().getId()>0) {
			throw new BadRequestServiceException("Bad request！");
		}
		
		//  student_id -- 
		if (studentExam.getStudent().getId()<1  && userId>0 ) {
			//
			Student student = studentService.find(userId);
			studentExam.setStudent(student);
			//update
			studentExam = studentExamService.update(studentExam);
		}
		
		/** 如果在不允许未登录情况下，进行测试，则 exec here
		if (studentExam.getStudentId()<1) {
			throw new UserNotExistServiceException("You haven't logined.");
		}
		*/
		return studentExam;
	}


	@RequestMapping(value = "/findByFamilyId", method = RequestMethod.GET)
	public List<StudentExam> findByFamilyId(@RequestParam("familyId") long familyId) {
		//
		List<StudentExam> studentList = studentExamService.findByFamilyId(familyId);
		return studentList;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public StudentExam update(StudentExam studentExam){
		// 设置接收时间 -- 根据uuid获取记录
		Date createDT = new Date();
		studentExam.setEndDatetime(createDT);
		
		studentExamService.update(studentExam);
		return studentExam;
	}

	/**
	 * 获取指定学生的最新的测试记录
	 * @param studentId
	 * @return
	 */
	@RequestMapping(value = "/getLastExam", method = RequestMethod.GET) 
	public StudentExam getLastExam(@RequestParam("studentId")long studentId) {
		//
		return studentExamService.getLastExam(studentId);
	}

	//--
	@RequestMapping(value = "/create", method=RequestMethod.POST)
	public StudentExam createNew(StudentExam studentExam){
		logger.debug("create a student exam.");
		return studentExamService.create(studentExam);
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
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public StudentExam createNew(@RequestParam(value="studentId",required=true)long studentId,@RequestParam("familyId")long familyId,  @RequestParam(value="level",required=false) String strLevel){
		logger.debug("createNew a student exam for http add.");
		
		// 邮件发送 -- 需要student及相关信息。
		// notes:  针对注册用户，具有student ID，发送邮件。 针对未登陆（未注册）用户，无student数据，另行处理. 
		Student student = studentService.find(studentId);
		if (null == student) {
			throw new BadRequestServiceException("Thx but don't hack");
		}
		
		// clean un-finished data.
		studentExamService.clearForStudent(studentId);
		
		StudentExam studentExam = new StudentExam();
	//	String strCommnents = getCommentsForLevel(strLevel);
	//	if (null == strCommnents) {
	//		throw new BadRequestServiceException("error level value.");
	//	}
		
		String uuid = UUID.randomUUID().toString();
		studentExam.setRecordUuid(uuid);
		
		Date createDT = new Date();
		studentExam.setCreateDatetime(createDT);
		
	//	studentExam.setStudentId(studentId);
		studentExam.setStudent(student);
		studentExam.setFamilyId(familyId);

		studentExam.setExamComment("");
		studentExam.setExamLevel("");
		studentExam.setExamScore(0);

		
		StudentExam newStudentExam = studentExamService.create(studentExam);
		
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
	@RequestMapping(value="/submitStudentExamResult", method=RequestMethod.POST)
	public StudentExam updateStudentExam(@RequestParam("studentId")long studentId,@RequestParam("familyId")long familyId,  @RequestParam("level") String strLevel, @RequestParam("studentexam_uuid") String csrfToken){
		logger.debug("submitStudentExamResult on controller: token:{}",csrfToken);
		
		if (null == strLevel) {
			throw new BadRequestServiceException("There isn't level given.");
		}
		
		StudentExam newStudentExam = null;
		
		try {
			newStudentExam = studentExamService.updateStudentExam(studentId, familyId, strLevel, csrfToken);
		} catch (Exception e) {
			throw e;
		}
		
		return newStudentExam;
	}
	
	
	@RequestMapping(value = "/count", method=RequestMethod.GET)
	public Count count(@RequestParam("studentId")long studentId) {
		//
		Count count = studentExamService.count(studentId);
		return count;
	}

	
	@RequestMapping(value="/countByFamily", method=RequestMethod.GET)
	public Count countByFamily(@RequestParam("familyId")long familyId) {
		//
		Count nCnt = studentExamService.countByFamily(familyId);
		return nCnt;
	}


	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<StudentExam> list(@RequestParam("studentId") Long studentId,  @RequestParam(value = "start", required=false, defaultValue = "0") int start, @RequestParam(value="length",required= false, defaultValue="15") int length) {
		logger.debug("list StudentExam with params:  studentId = {}, start = {}, length = {}.", studentId, start, length);
		return studentExamService.list(studentId, start, length);
	}
	
	//listByFamily
	@RequestMapping(value="/listByFamily", method=RequestMethod.GET)
	public List<StudentExam> listByFamily(@RequestParam("familyId") Long familyId,  @RequestParam(value="start", required=false, defaultValue="0") int start, @RequestParam(value="length",required=false,defaultValue="15") int length) {
		logger.debug("list StudentExam with params:  studentId = {}, start = {}, length = {}.", familyId, start, length);
		return studentExamService.listByFamily(familyId, start, length);
	}

	// search method for system =================
	@RequestMapping(value="/countBySearch", method=RequestMethod.GET)
	public Count countBySearch(@RequestParam(value="search", required=false) String search, 
			@RequestParam(value="executeDateTimeFrom", required=false) DateTimeParam executeDateTimeFrom, 
			@RequestParam(value="executeDateTimeTo", required=false) DateTimeParam executeDateTimeTo,
			@RequestParam(value="sales", required=false, defaultValue="0") long salesId,
			@RequestParam(value="clt", required=false, defaultValue="0") long cltId) {
		//
		Count nCnt = studentExamService.countBySearch(search, executeDateTimeFrom, executeDateTimeTo,salesId,cltId);
		return nCnt;
	}

	@RequestMapping(value="/listBySearch", method=RequestMethod.GET)
	public List<StudentExam> listBySearch(@RequestParam(value="search",required=false) String search, 
			@RequestParam(value="executeDateTimeFrom", required=false) DateTimeParam executeDateTimeFrom, 
			@RequestParam(value="executeDateTimeTo", required=false) DateTimeParam executeDateTimeTo,
			@RequestParam(value="sales", required=false, defaultValue="0") long salesId,
			@RequestParam(value="clt", required=false, defaultValue="0") long cltId,
					@RequestParam(value="start",required=false,defaultValue="0") int start,
					@RequestParam(value="length",required=false, defaultValue="15") int length) {
		logger.debug("list StudentExam with params:  search = {}, start = {}, length = {}.", search, start, length);
		return studentExamService.listBySearch(search,executeDateTimeFrom, executeDateTimeTo, salesId, cltId, start, length);
	}
	
}
