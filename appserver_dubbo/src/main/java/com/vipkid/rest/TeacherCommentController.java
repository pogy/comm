package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import com.vipkid.rest.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.TeacherComment;
import com.vipkid.service.TeacherCommentService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.TeacherCommentView;

@RestController
@RequestMapping("/api/service/private/teacherComments")
public class TeacherCommentController {
	private Logger logger = LoggerFactory.getLogger(TeacherCommentController.class.getSimpleName());

	@Resource
	private TeacherCommentService teacherCommentService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public TeacherComment find(@RequestParam("id") long id) {
		logger.info("find teacherComment with params: id = {}", id);
		return teacherCommentService.find(id);
	}
	
	@RequestMapping(value = "/findByTeacherIdAndTimeRange", method = RequestMethod.GET)
	public List<TeacherComment> findByTeacherIdAndTimeRange(@RequestParam("teacherId") long teacherId, @RequestParam("beginDate") DateParam startDate, @RequestParam("endDate") DateParam endDate) {
		logger.info("find TeacherComments with params: teacherId = {}, startDate = {}, endDate = {}", teacherId, startDate, endDate);
		return teacherCommentService.findByTeacherIdAndTimeRange(teacherId, startDate.getValue(), endDate.getValue());
	}
	
	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<TeacherComment> findByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find TeacherComments with params: studentId = {}", studentId);
		return teacherCommentService.findByStudentId(studentId);
	}

	@RequestMapping(value = "/findRecentByStudentIdAndAmount", method = RequestMethod.GET)
	public List<TeacherComment> findRecentByStudentIdAndAmount(@RequestParam("studentId") long studentId, @RequestParam("amount") long amount) {
		logger.info("find TeacherComments with params: studentId = {}, amount = {}", studentId, amount);
		return teacherCommentService.findRecentByStudentIdAndAmount(studentId, amount);
	}
	
	@RequestMapping(value = "/findRecentByStudentIdAndClassIdAndAmount", method = RequestMethod.GET)
	public List<TeacherComment> findRecentByStudentIdAndClassIdAndAmount(@RequestParam("studentId") long studentId, @RequestParam("onlineClassId") long onlineClassId,  @RequestParam("amount") long amount) {
		logger.info("list TeacherComments with params: studentId = {}, onlineClassId={}, amount = {}", studentId, onlineClassId, amount);
		return teacherCommentService.findRecentByStudentIdAndClassIdAndAmount(studentId, onlineClassId, amount);
	}
	
	@RequestMapping(value = "/findByOnlineClassIdAndStudentId", method = RequestMethod.GET)
	public TeacherComment findByOnlineClassIdAndStudentId(@RequestParam("onlineClassId") long onlineClassId, @RequestParam("studentId") long studentId) {
		logger.info("find TeacherComments with params: studentId = {}, amount = {}", onlineClassId);
		return teacherCommentService.findByOnlineClassIdAndStudentId(onlineClassId, studentId);
	}
	
	@RequestMapping(value = "/findTeacherCommentViewByOnlineClassIdAndStudentId", method = RequestMethod.GET)
	public TeacherCommentView findTeacherCommentViewByOnlineClassIdAndStudentId(@RequestParam("onlineClassId") long onlineClassId, @RequestParam("studentId") long studentId) {
		logger.info("find TeacherCommentView with params: studentId = {}, amount = {}", onlineClassId);
		return teacherCommentService.findTeacherCommentViewByOnlineClassIdAndStudentId(onlineClassId, studentId);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<TeacherComment> list(@RequestParam(value="empty", required=false) Boolean empty, @RequestParam(value="courseId", required=false) Long courseId, @RequestParam(value="teacherId", required=false) Long teacherId, @RequestParam(value="studentId", required=false) Long studentId, @RequestParam(value="start", required=false) Integer start, @RequestParam(value="length", required=false) Integer length) {
		logger.info("list teacherComment with params: empty = {}, courseId = {}, teacherId = {}, studentId = {}, start = {}, length = {}.", empty, courseId, teacherId, studentId, start, length);
		return teacherCommentService.list(empty, courseId, teacherId, studentId, start, length);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@RequestParam(value="empty", required=false) Boolean empty, @RequestParam(value="courseId", required=false) Long courseId, @RequestParam(value="teacherId", required=false) Long teacherId, @RequestParam(value="studentId", required=false) Long studentId) {
		logger.info("list teacherComment with params: empty = {}, courseId = {}, teacherId = {}, studentId = {}.", empty, courseId, teacherId, studentId);
		return teacherCommentService.count(empty, courseId, teacherId, studentId);
	}
	
	@RequestMapping(value = "/updateStar", method = RequestMethod.PUT)
	public TeacherComment updateStar(@RequestBody TeacherComment teacherComment) {
		logger.info("update star with teacher comment id = {}.", teacherComment.getId());
		return teacherCommentService.updateStar(teacherComment);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Response update(@RequestBody TeacherComment teacherComment) {
		logger.info("update teacherComment with id = {}", teacherComment.getId());
		return teacherCommentService.update(teacherComment);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public Response create(@RequestBody TeacherComment teacherComment) {
		logger.info("create teacherComment: {}", teacherComment);
		return teacherCommentService.create(teacherComment);
	}
	
}
