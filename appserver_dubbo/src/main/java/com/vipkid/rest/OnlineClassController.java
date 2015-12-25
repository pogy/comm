package com.vipkid.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.vipkid.handler.OnlineClassHandler;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.Course.Type;
import com.vipkid.model.FloatWrapper;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.Student;
import com.vipkid.repository.OpenClassRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.rest.vo.query.OnlineClassVO;
import com.vipkid.rest.vo.query.TeacherEvaluationVO;
import com.vipkid.security.SecurityService;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.PeakTimeService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.BooleanWrapper;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.DateWrapper;
import com.vipkid.service.pojo.OnlineClassFinishCountView;
import com.vipkid.service.pojo.OnlineClassPeakViewPreWeek;
import com.vipkid.service.pojo.OnlineClassView;
import com.vipkid.service.pojo.PeakTimePerWeek;
import com.vipkid.service.pojo.leads.OnlineClassVo;
import com.vipkid.util.DateTimeUtils;

@RestController
@RequestMapping("/api/service/private/onlineClasses")
public class OnlineClassController {
    private Logger logger = LoggerFactory.getLogger(OnlineClassController.class.getSimpleName());

    @Resource
    private OnlineClassService onlineClassService;
    
    @Resource
	private SecurityService securityService;
    
	@Resource
	private PeakTimeService peakTimeService;
	
	@Resource
	private OpenClassRepository openClassRepository;
	

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public OnlineClass find(@RequestParam("id") long id) {
        logger.info("find, param={}", id);
        return onlineClassService.find(id);
    }

    @RequestMapping(value = "/countBackupDutyByTeacherIdAndDate", method = RequestMethod.GET)
    public Count countBackupDutyByTeacherIdAndDate(@RequestParam("teacherId") long teacherId, @RequestParam("date") DateParam date) {
        logger.info("countBackupDutyByTeacherIdAndDate, params: teacherId = {}, dateParam = {}", teacherId, date);

        return onlineClassService.countBackupDutyByTeacherIdAndDate(teacherId, date);
    }

    @RequestMapping(value = "/countInterviewEnrolledByTeacherIdAndDate", method = RequestMethod.GET)
    public Count countInterviewEnrolledByTeacherIdAndDate(@RequestParam("teacherId") long teacherId, @RequestParam("date") DateParam date) {
        logger.info("countInterviewEnrolledByTeacherIdAndDate, params: teacherId = {}, dateParam = {}", teacherId, date);

        return onlineClassService.countInterviewEnrolledByTeacherIdAndDate(teacherId, date);
    }

    @RequestMapping(value = "/findTestClassAndITByStudentId", method = RequestMethod.GET)
    public List<OnlineClass> findTestClassAndITByStudentId(@RequestParam("studentId") long studentId) {
        logger.info("findTestClassAndITByStudentId, params: studentId={}", studentId);

        return onlineClassService.findTestClassAndITByStudentId(studentId);
    }

    @RequestMapping(value = "/findByStudentIdAndFinishType", method = RequestMethod.GET)
    public List<OnlineClass> findByStudentIdAndFinishType(@RequestParam("studentId") long studentId, @RequestParam("finishType") String finishType, @RequestParam("start") int start, @RequestParam("length") int length) {
        logger.info("findTestClassAndITByStudentId, params: studentId={}", studentId);

        return onlineClassService.findByStudentIdAndFinishType(studentId, finishType, start, length);
    }

    @RequestMapping(value = "/countByStudentIdAndFinishType", method = RequestMethod.GET)
    public Count countByStudentIdAndFinishType(@RequestParam("studentId") long studentId, @RequestParam("finishType") String finishType) {
        logger.info("countByStudentIdAndFinishType, params: studentId={}, finishType={}", studentId, finishType);

        return onlineClassService.countByStudentIdAndFinishType(studentId, finishType);
    }

    @RequestMapping(value = "/countForFireman", method = RequestMethod.GET)
    public Count countForFireman(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, @RequestParam("statusList") Status[] statusList) {
        logger.info("list course with params: scheduleFrom = {}, scheduleTo = {}, status = {}, hasClassroom = {}.", scheduledDateTimeFrom, scheduledDateTimeTo);

        return onlineClassService.countForFireman(Arrays.asList(courseIds == null ? new Long[0] : courseIds), scheduledDateTimeFrom, scheduledDateTimeTo, Arrays.asList(statusList == null ? new Status[0] : statusList));
    }

    @RequestMapping(value = "/findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId", method = RequestMethod.GET)
    public List<OnlineClass> findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId(@RequestParam("learningProgressCourseId") long learningProgressCourseId, @RequestParam("studentId") long studentId) {
        logger.info("findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId, params: learningProgressCourseId={}, studentId={}");

        return onlineClassService.findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId(learningProgressCourseId, studentId);
    }

    @RequestMapping(value = "/findNextWeekAvailableOnlineClassesByLearningProgressCourseId", method = RequestMethod.GET)
    public List<OnlineClass> findNextWeekAvailableOnlineClassesByLearningProgressCourseId(@RequestParam("learningProgressCourseId") long learningProgressCourseId) {
        logger.info("findNextWeekAvailableOnlineClassesByLearningProgressCourseId, params: learningProgressCourseId={}", learningProgressCourseId);

        return onlineClassService.findNextWeekAvailableOnlineClassesByLearningProgressCourseId(learningProgressCourseId);
    }

    @RequestMapping(value = "/findByStudentIdAndCourseIdAndStatus", method = RequestMethod.GET)
    public List<OnlineClass> findByStudentIdAndCourseIdAndStatus(@RequestParam("studentId") long studentId, @RequestParam("courseId") long courseId, @RequestParam("status") Status status, @RequestParam("start") int start, @RequestParam("length") int length) {
        logger.info("find onlineClass for studentId = {}, courseId = {}, status = {}", studentId, courseId, status);

        return onlineClassService.findByStudentIdAndCourseIdAndStatus(studentId, courseId, status, start, length);
    }

    @RequestMapping(value = "/findByStudentIdAndCourseIdAndFinishType", method = RequestMethod.GET)
    public List<OnlineClass> findByStudentIdAndCourseIdAndFinishType(@RequestParam("studentId") long studentId, @RequestParam("courseId") long courseId, @RequestParam("type") FinishType type) {
        logger.info("find onlineClass for studentId = {}, courseId = {}, FinishType = {}", studentId, courseId, type);

        return onlineClassService.findByStudentIdAndCourseIdAndFinishType(studentId, courseId, type);
    }

    @RequestMapping(value = "/findByTeacherIdAndStudentIdAndScheduledDateTime", method = RequestMethod.GET)
    public List<OnlineClass> findByTeacherIdAndStudentIdAndScheduledDateTime(@RequestParam("teacherId") long teacherId, @RequestParam("studentId") long studentId, @RequestParam("scheduledDateTime") DateParam scheduledDateParam) {
        logger.info("find onlineClasses for teacherId = {}, studentId = {}, scheduleDateTime = {}", teacherId, DateTimeUtils.format(scheduledDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findByTeacherIdAndStudentIdAndScheduledDateTime(teacherId, studentId, scheduledDateParam);
    }
    
    @RequestMapping(value = "/findByStudentIdAndScheduledDateTime", method = RequestMethod.GET)
    public List<OnlineClass> findByStudentIdAndScheduledDateTime(@RequestParam("studentId") long studentId, @RequestParam("scheduledDateTime") DateParam scheduledDateParam) {
        logger.info("find onlineClasses for studentId = {}, scheduleDateTime = {}", studentId, DateTimeUtils.format(scheduledDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findByStudentIdAndScheduledDateTime(studentId, scheduledDateParam);
    }
    
    @RequestMapping(value = "/findByTeacherIdAndStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClass> findByTeacherIdAndStartDateAndEndDate(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
        logger.info("find onlineClasses for teacherId = {}, startDate = {}, endDate = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam, endDateParam);
    }
    
    @RequestMapping(value = "/findExccedMaxParallelTrialCountByStartDateAndEndDate", method = RequestMethod.GET)
	public List<DateWrapper> findExccedMaxParallelTrialCountByStartDateAndEndDate(@RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
		logger.info("find excced max parallel trial booking count date for startDate = {}, endDate = {}",DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassService.findExccedMaxParallelTrialCountByStartDateAndEndDate(startDateParam, endDateParam);
	}

    @RequestMapping(value = "/findOnlineClassViewByTeacherIdAndStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClassView> findOnlineClassViewByTeacherIdAndStartDateAndEndDate(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
        logger.info("find onlineClasses for teacherId = {}, startDate = {}, endDate = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
        List<OnlineClassView> rtnList =  onlineClassService.findOnlineClassViewByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam, endDateParam);
        return rtnList;
    }

    @RequestMapping(value = "/findByTeacherIdAndStartDateAndEndDateAndStatus", method = RequestMethod.GET)
    public List<OnlineClass> findByTeacherIdAndStartDateAndEndDateAndStatus(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam, @RequestParam("status") String statuses) {
        logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

        return onlineClassService.findByTeacherIdAndStartDateAndEndDateAndStatus(teacherId, startDateParam, endDateParam, statuses);
    }

    @RequestMapping(value = "/findByTeacherIdAndStartDateAndEndDateAndStatusAndPage", method = RequestMethod.GET)
    public List<OnlineClass> findByTeacherIdAndStartDateAndEndDateAndStatusAndPage(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam, @RequestParam("status") String statuses, @RequestParam("page") int page, @RequestParam("size") int size) {
        logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

        return onlineClassService.findByTeacherIdAndStartDateAndEndDateAndStatusAndPage(teacherId, startDateParam, endDateParam, statuses, page, size);
    }

    @RequestMapping(value = "/findNextLessonPositionByTeacherIdAndStartDateAndEndDateAndStatus", method = RequestMethod.GET)
    public Count findNextLessonPositionByTeacherIdAndStartDateAndEndDateAndStatus(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam, @RequestParam("status") String statuses) {
        logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

        return onlineClassService.findNextLessonPositionByTeacherIdAndStartDateAndEndDateAndStatus(teacherId, startDateParam, endDateParam, statuses);
    }

    @RequestMapping(value = "/countByTeacherIdAndStartDateAndEndDateAndStatus", method = RequestMethod.GET)
    public Count countByTeacherIdAndStartDateAndEndDateAndStatus(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam, @RequestParam("status") String statuses) {
        logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

        return onlineClassService.countByTeacherIdAndStartDateAndEndDateAndStatus(teacherId, startDateParam, endDateParam, statuses);
    }

    @RequestMapping(value = "/countByTeacherIdAndEndDateAndStatus", method = RequestMethod.GET)
    public Count countByTeacherIdAndEndDateAndStatus(@RequestParam("teacherId") long teacherId, @RequestParam("endDate") DateParam endDateParam, @RequestParam("status") String statuses) {
        logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

        return onlineClassService.countByTeacherIdAndEndDateAndStatus(teacherId, endDateParam, statuses);
    }

    @RequestMapping(value = "/countByTeacherIdAndStartDateAndStatus", method = RequestMethod.GET)
    public Count countByTeacherIdAndStartDateAndStatus(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("status") String statuses) {
        logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

        return onlineClassService.countByTeacherIdAndStartDateAndStatus(teacherId, startDateParam, statuses);
    }

    @RequestMapping(value = "/findByStudentIdAndStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClass> findByStudentIdAndStartDateAndEndDate(@RequestParam("studentId") long studentId, @RequestParam(value = "startDate", required = false) DateParam startDateParam, @RequestParam(value = "endDate", required = false) DateParam endDateParam) {
        logger.info("find onlineClasses for studentId = {}, startDate = {}, endDate = {}", studentId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findByStudentIdAndStartDateAndEndDate(studentId, startDateParam, endDateParam);
    }

    @RequestMapping(value = "/countClassByStudentIdAndStartDateOrEndDate", method = RequestMethod.GET)
    public Count countClassByStudentIdAndStartDateOrEndDate(@RequestParam("studentId") long studentId, @RequestParam(value = "startDate", required = false) DateParam startDateParam, @RequestParam(value = "endDate", required = false) DateParam endDateParam) {
        logger.info("countClassByStudentIdAndStartDateOrEndDate, params: studentId={}", studentId);
        return onlineClassService.countClassByStudentIdAndStartDateOrEndDate(studentId, startDateParam, endDateParam);
    }

    @RequestMapping(value = "/findAvailableByTeacherId", method = RequestMethod.GET)
    public List<OnlineClass> findAvailableByTeacherId(@RequestParam("teacherId") long teacherId) {
        logger.info("find classes for teacherId = {}", teacherId);
        return onlineClassService.findAvailableByTeacherId(teacherId);
    }
    
    @RequestMapping(value = "/findAvailableByTeacherIdAndLimitParallelByCourseType", method = RequestMethod.GET)
	public List<OnlineClass> findAvailableByTeacherIdAndLimitParallelByCourseName(@RequestParam("teacherId") long teacherId, @RequestParam("type") Type type) {
		logger.info("find classes for teacherId = {} and limit trail parallel", teacherId);
		return onlineClassService.findAvailableByTeacherIdAndLimitParallelByCourseType(teacherId, type);
	}

    @RequestMapping(value = "/findAvailableByScheduledDateTime", method = RequestMethod.GET)
    public List<OnlineClass> findAvailableByScheduledDateTime(@RequestParam("scheduledDateTime") DateParam scheduledDateTime) {
        logger.info("find classes for scheduled date time = {}", DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findAvailableByScheduledDateTime(scheduledDateTime);
    }

    @RequestMapping(value = "/findAvailableByByTeacherCertificatedCourseIdScheduledDateTime", method = RequestMethod.GET)
    public List<OnlineClass> findAvailableByByTeacherCertificatedCourseIdScheduledDateTime(@RequestParam("courseId") long courseId, @RequestParam("scheduledDateTime") DateParam scheduledDateTime) {
        logger.info("find classes for courseId = {}, scheduled date time = {}", courseId, DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findAvailableByByTeacherCertificatedCourseIdScheduledDateTime(courseId, scheduledDateTime);
    }
    
    @RequestMapping(value = "/findIfExceedParallelTrialByScheduledDateTime", method = RequestMethod.GET)
	public BooleanWrapper findIfExceedParallelTrialByScheduledDateTime(@RequestParam("scheduledDateTime") DateParam scheduledDateTime) {
		logger.info("count parallel trial course, scheduled date time = {}", DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassService.findIfExceedParallelTrialByScheduledDateTime(scheduledDateTime);
	}

    @RequestMapping(value = "/findAvailableByStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClass> findAvailableByStartDateAndEndDate(@RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
        logger.info("find available classes for startDate = {}, endDate = {}", DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findAvailableByStartDateAndEndDate(startDateParam, endDateParam);
    }

    @RequestMapping(value = "/findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClass> findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate(@RequestParam("courseId") long courseId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
    	// 记录请求和返回时间差
    	long requestStartTime = System.currentTimeMillis();
    	logger.info("find available classes for courseId = {}, startDate = {}, endDate = {}", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
    	
    	List<OnlineClass> onlineClasses = onlineClassService.findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate(courseId, startDateParam, endDateParam);
        
    	long requestEndTime = System.currentTimeMillis();
    	logger.error("the requeset took " + (requestEndTime - requestStartTime) + " millisecond");
    	return onlineClasses;
    }
    
    @RequestMapping(value = "/findAvailableByTeacherCertificatedCourseIdStartDateAndEndDateAndLimitParallelByCourseType", method = RequestMethod.GET)
    public List<OnlineClass> findAvailableByTeacherCertificatedCourseIdStartDateAndEndDateAndLimitParallelByCourseType(@RequestParam("courseId") long courseId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam, @RequestParam("type") Type type) {
		logger.info("find available classes for courseId = {}, startDate = {}, endDate = {} and limit trail parallel", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassService.findAvailableByTeacherCertificatedCourseIdStartDateAndEndDateAndLimitParallelByCourseType(courseId, startDateParam, endDateParam, type);
    }
    
    @RequestMapping(value = "/findAvailablePracticumByStartDateAndEndDate", method = RequestMethod.GET)
	public List<OnlineClass> findAvailablePracticumByStartDateAndEndDate(@RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
		logger.info("find available practicum classes for startDate = {}, endDate = {}", DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassService.findAvailablePracticumByStartDateAndEndDate(startDateParam, endDateParam);
	}

    @RequestMapping(value = "/findOpenTeacherRecruitmentByStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClass> findOpenTeacherRecruitmentByStartDateAndEndDate(@RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam, @RequestParam("type") String type) {
        logger.info("find open teacher recruitment classes for startDate = {}, endDate = {}", DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findOpenTeacherRecruitmentByStartDateAndEndDate(startDateParam, endDateParam, type);
    }

    /**
     * 获取teacher的available time-slot (online-class). -- web front design： 获取所有的online-class,在front-end使用js进行处理。--名称不太符合
     *
     * @param teacherId
     * @param startDateParam
     * @param endDateParam
     * @return
     */

    @RequestMapping(value = "/findAvailableByTeacherIdAndStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClass> findAvailableByTeacherIdAndStartDateAndEndDate(@RequestParam(value = "teacherId", required = false, defaultValue = "0") Long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
        logger.info("find available classes for teacher={}, startDate = {}, endDate = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        List<OnlineClass> onlineClasses = onlineClassService.findAvailableByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam, endDateParam);
        if(CollectionUtils.isEmpty(onlineClasses)){
        	return onlineClasses;
        }
        List<OnlineClass> list = getNewOnlineClassList(onlineClasses);
        return list;
    }

    @RequestMapping(value = "/findOpenByCourseIdAndStartDateAndEndDate", method = RequestMethod.GET)
    public List<OnlineClass> findOpenByCourseIdAndStartDateAndEndDate(@RequestParam("courseId") long courseId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
        logger.info("find open classes for courseId = {}, startDate = {}, endDate = {}", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findOpenByCourseIdAndStartDateAndEndDate(courseId, startDateParam, endDateParam);
    }

    @RequestMapping(value = "/findOpenByCourseIdAndScheduledDateTime", method = RequestMethod.GET)
    public List<OnlineClass> findOpenByCourseIdAndScheduledDateTime(@RequestParam("courseId") long courseId, @RequestParam("scheduledDateTime") DateParam scheduledDateTime) {
        logger.info("find open classes for courseId = {}, scheduledDateTime = {}", courseId, DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findOpenByCourseIdAndScheduledDateTime(courseId, scheduledDateTime);
    }

    @RequestMapping(value = "/findByStudentIdAndLessonId", method = RequestMethod.GET)
    public OnlineClass findByStudentIdAndLessonId(@RequestParam("studentId") long studentId, @RequestParam("lessonId") long lessonId) {
        logger.info("find onlineclass for studentId = {}, lessonId = {}", studentId, lessonId);

        return onlineClassService.findByStudentIdAndLessonId(studentId, lessonId);
    }

    @RequestMapping(value = "/findByStudentIdAndCourseType", method = RequestMethod.GET)
    public List<OnlineClass> findByStudentIdAndCourseType(@RequestParam("studentId") long studentId) {
        logger.info("find onlineclass for studentId = {}, type = {}", studentId);

        return onlineClassService.findByStudentIdAndCourseType(studentId);
    }

    @RequestMapping(value = "/findBookedLessonNumberThisWeek", method = RequestMethod.GET)
    public Count findBookedLessonNumberThisWeek(@RequestParam("studentId") long studentId) {
        logger.info("find onlineclass for studentId = {}", studentId);

        return onlineClassService.findBookedLessonNumberThisWeek(studentId);
    }

    @RequestMapping(value = "/findBookedLessonNumberNextWeek", method = RequestMethod.GET)
    public Count findBookedLessonNumberNextWeek(@RequestParam("studentId") long studentId) {
        logger.info("find onlineclass for studentId = {}", studentId);

        return onlineClassService.findBookedLessonNumberNextWeek(studentId);
    }

    @RequestMapping(value = "/book", method = RequestMethod.PUT)
    public OnlineClass book(@RequestBody final OnlineClass onlineClass) {
        logger.info("book --> doBook:  New request from student={}, to book onlineClass.id={} ", onlineClass.getStudentEnglishNames(), onlineClass.getId());
        OnlineClass bookedOnlineClass = onlineClassService.doBook(onlineClass);
        List<Student> students = onlineClass.getStudents();
		Student student = students.get(0);
		Course course = getCourse(onlineClass);
		if (needCreateDBYClassroom(bookedOnlineClass)) {
			logger.info("needCreateDBYClassroom -- ");
			onlineClassService.doReScheduleDby(student, course, bookedOnlineClass, true);
		}


        return bookedOnlineClass;
    }
    
	private boolean needCreateDBYClassroom(final OnlineClass onlineClass){
		if (onlineClass.getStatus() == Status.BOOKED && 
				(onlineClass.getCourse().getMode() == Mode.ONE_ON_ONE
				|| onlineClass.getCourse().getType() == Type.TEACHER_RECRUITMENT
				|| onlineClass.getCourse().getType() == Type.PRACTICUM)){
			return true;
		}
		return false;
	}
	
//TODO: It will be used later - Leon
//	@PUT
//	@Path("/book")
//	public OnlineClass book(final OnlineClass onlineClass) {
//		logger.debug("New request from student={}, to book onlineClass.id={} ",onlineClass.getStudentEnglishNames(), onlineClass.getId());
//		if (!securityController.isRoleAllowed(Role.STAFF_ADMIN, Role.STAFF_OPERATION, Role.PARENT, Role.TEACHER)){
//			throw new AccessDeniedServiceException("Pemission Dennied");
//		}
//		
//		OnlineClass bookedOnlineClass = null;
//		Course course = onlineClass.getCourse();
//		
//		if(onlineClass.getSerialNumber() == null){
//			//onlineClass.setSerialNumber(Long.toString((new Date()).getTime()));
//			User booker = securityController.getCurrentUser();
//			if(booker == null) {
//				booker = staffAccessor.findByUsername(Configurations.System.SYSTEM_USERNAME);
//			}
//			onlineClass.setBooker(booker);
//			onlineClass.setLastEditor(booker);
//			onlineClassAccessor.create(onlineClass);
//		}
//		
//		boolean needReleaseLocks = true;
//		try{
//			if (canGetBookingLocks(onlineClass)){
//				checkDateTimeAlreadyBooked(onlineClass);
//				checkClassAlreadyBooked(onlineClass);
//				checkNoMoreClassHours(onlineClass);
//				
//				if (course.isSequential()){
//					bookedOnlineClass = doBookInorder(onlineClass);
//				} else{
//					bookedOnlineClass = doBookDisorder(onlineClass);
//				}
//				needReleaseLocks = false;
//			}else{
//				throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
//			}
//		}finally{
//			if (needReleaseLocks){
//				releaseLocks(onlineClass);
//			}
//		}
//		
//		updateBackupDuty(onlineClass);
//		
//		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_BOOK);
//		
//		String operation = "Booked the " + onlineClass.getOnlineClassName();
//		securityController.logAudit(Level.INFO, Category.ONLINE_CLASS_BOOK, operation);
//		
//		// 创建各种评语
//		createComments(bookedOnlineClass);
//		
//		// 对于一对一课程，创建多贝云教室, 对于一对多课程，需教务手工添加Zoom教室id
//		if (onlineClass.getStatus() == Status.BOOKED && onlineClass.getCourse().getMode() == Course.Mode.ONE_ON_ONE) {
//			User user = ((CustomizedPrincipal) securityContext.getUserPrincipal()).getUser();
//			if(user == null) {
//				user = (User) staffAccessor.findByUsername(Configurations.System.SYSTEM_USERNAME);
//			}
//			final User operator = user;
//			
//			ExecutorService executorService = Executors.newSingleThreadExecutor();
//			FutureTask<OnlineClass> futureTask = new FutureTask<OnlineClass>(new Callable<OnlineClass>(){
//				@Override
//				public OnlineClass call() throws Exception {
//					return createDBYClassroom(onlineClass, operator);
//				}
//			});
//			executorService.submit(futureTask);
//		}
//		
//		sendEmailAndSMSWhenOnlineClassIsBooked(course, bookedOnlineClass);
//		
//		return bookedOnlineClass;	
//	}

    @RequestMapping(value = "/cancel", method = RequestMethod.PUT)
    public OnlineClass cancel(@RequestBody OnlineClass onlineClass) {
        logger.info("New cancel request from operator={}, to cancel the onlineClass.id={} for the student={}, cancelledStudent={} ", securityService.getCurrentUser(), onlineClass.getId(), onlineClass.getStudentEnglishNames(), onlineClass.getCancelledStudentEnglishNames());

        OnlineClass cancelOnlineClass = onlineClassService.doCancel(onlineClass);
        List<Student> cancelledStudents = onlineClass.getCancelledStudents();
        Course course = getCourse(onlineClass);

		if (course.isSequential()) {
			for (Student tStudent : cancelledStudents) {
				onlineClassService.doReScheduleDby(tStudent, course, cancelOnlineClass, false);
			}
		}
       
		return cancelOnlineClass;
    }
    
    private Course getCourse(final OnlineClass onlineClass){
    	try {
    		return onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
    	} catch (NullPointerException e) {
    		OnlineClass inDbOnlineClass = onlineClassService.find(onlineClass.getId());
    		return inDbOnlineClass.getLesson().getLearningCycle().getUnit().getCourse();
    	}
	}

    @RequestMapping(method = RequestMethod.POST)
    public OnlineClass create(@RequestBody OnlineClass onlineClass) {
        logger.info("New create request from student={}, to book onlineClass.id={} ", onlineClass.getStudentEnglishNames(), onlineClass.getId());

        return onlineClassService.create(onlineClass);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public OnlineClass update(@RequestBody OnlineClass onlineClass) {
        logger.info("Update onlineclass from student={}, to onlineclass={}", onlineClass.getStudentEnglishNames(), onlineClass.getId());

        return onlineClassService.update(onlineClass);
    }

    @RequestMapping(value = "/changeTeacher", method = RequestMethod.PUT)
    public Response changeTeacher(@RequestBody OnlineClass onlineClass, HttpServletResponse response) {
        logger.info("changeTeacher onlineclass from student={}, to onlineclass={}", onlineClass.getStudentEnglishNames(), onlineClass.getId());

        Response res = onlineClassService.changeTeacher(onlineClass);
        if (null != res) {
            response.setStatus(res.getStatus());
        }
        return res;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public Response delete(@RequestParam("id") long id, HttpServletResponse response) {
        logger.info("changeTeacher params: id={}", id);

        Response res = onlineClassService.delete(id);
        if (null != res) {
            response.setStatus(res.getStatus());
        }
        return res;
    }

    @RequestMapping(value = "/undoFinish", method = RequestMethod.PUT)
    public Response undoFinish(@RequestBody OnlineClass onlineClass, HttpServletResponse response) {
        Response res = onlineClassService.doUndoFinish(onlineClass);
        if (null != res) {
            response.setStatus(res.getStatus());
        }
        return res;
    }

    @RequestMapping(value = "/finish", method = RequestMethod.PUT)
	public OnlineClass finish(@RequestBody OnlineClass onlineClass) {

    	// 2015-07-17 公开课的结束直接修改online. 无其他操作email
    	if (Course.Mode.ONE_TO_MANY == onlineClass.getCourseMode()) {
    		//onlineClass.setStatus(Status.FINISHED);
    		return onlineClassService.doFinishOpenClass(onlineClass);
    	}
    	
		OnlineClass onlineClassUpdate = onlineClassService.doFinish(onlineClass);
		Course course = getCourse(onlineClassUpdate);
		switch (onlineClassUpdate.getFinishType()) {
		case AS_SCHEDULED:
			if (course.getMode().equals(Course.Mode.ONE_TO_MANY)) {
				List<Student> itProblemStudents = onlineClassUpdate.getItProblemStudents();
				List<Student> noShowStudents = onlineClassUpdate.getNoShowStudents();
				for (Student tmpStudent : itProblemStudents) {
					if (course.isSequential()) {
						onlineClassService.doReScheduleDby(tmpStudent, course, onlineClassUpdate, false);
					}
				}

				for (Student tmpStudent : noShowStudents) {
					if (course.isSequential()) {
						onlineClassService.doReScheduleDby(tmpStudent, course, onlineClassUpdate, false);
					}
				}
			}
			break;
		case STUDENT_NO_SHOW:
		case STUDENT_IT_PROBLEM:
		case TEACHER_NO_SHOW:
		case TEACHER_IT_PROBLEM:
			List<Student> students = onlineClassUpdate.getStudents();

			if (course.isSequential()) {
				for (Student tStudent : students) {
					onlineClassService.doReScheduleDby(tStudent, course, onlineClassUpdate, false);
				}
			}
			break;
		default:
			break;
		}

		return onlineClassUpdate;

	}

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<OnlineClass> list(
            @RequestParam(value = "courseIds", required = false) Long[] courseIds,
            @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText,
            @RequestParam(value = "searchStudentText", required = false) String searchStudentText,
            @RequestParam(value = "searchSalesText", required = false) String searchSalesText,
            @RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom,
            @RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "finishType", required = false) String finishType,
            @RequestParam(value = "shortNotice", required = false) Boolean shortNotice,
            @RequestParam(value = "hasClassroom", required = false) Boolean hasClassroom,
            @RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "length", required = false) Integer length) {
        logger.debug("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.", searchTeacherText, searchStudentText, searchSalesText, scheduledDateTimeFrom, scheduledDateTimeTo, status, finishType, hasClassroom, start, length);

        List<OnlineClass>onlineClasses =  onlineClassService.list(courseIds == null ? new ArrayList<Long>() : Arrays.asList(courseIds), searchTeacherText,
                searchStudentText, searchSalesText,
                scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom),
                scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo),
                status == null ? null : Status.valueOf(status),
                finishType == null ? null : FinishType.valueOf(finishType), shortNotice,
                hasClassroom, start, length);
        if(CollectionUtils.isEmpty(onlineClasses)){
        	return onlineClasses;
        }
        List<OnlineClass> list = getNewOnlineClassList(onlineClasses);
        return list;
    }

	private List<OnlineClass> getNewOnlineClassList(
			List<OnlineClass> onlineClasses) {
		List<OnlineClass> list = new ArrayList<OnlineClass>();
        for(OnlineClass os : onlineClasses){
        	OnlineClass onlineClass = new OnlineClass();
        	if(null != os.getLesson()){
        		if(os.getLesson().getLearningCycle().getUnit().getCourse().getMode()==Mode.ONE_TO_MANY){
        			if(os.getLesson().getLearningCycle().getUnit().getCourse().getType()==Type.OPEN1){
        				copyTo(os, onlineClass);
        			}else{
        				onlineClass = os;
        			}
        			long count = openClassRepository.countOpenClassStudentById(onlineClass.getId());
        			onlineClass.setStudentCount(count);
        		}else{
        			onlineClass = os;
        		}
        	}else{
        		onlineClass = os;
        	}
        	list.add(onlineClass);
        }
		return list;
	}

	private void copyTo(OnlineClass os, OnlineClass onlineClass) {
		onlineClass.setAbleToEnterClassroomDateTime(os.getAbleToEnterClassroomDateTime());
		onlineClass.setArchived(os.isArchived());
		onlineClass.setAsScheduledStudents(os.getAsScheduledStudents());
		onlineClass.setAttatchDocumentSucess(os.isAttatchDocumentSucess());
		onlineClass.setBookDateTime(os.getBookDateTime());
		onlineClass.setBooker(os.getBooker());
		onlineClass.setCanUndoFinish(os.isCanUndoFinish());
		onlineClass.setClassroom(os.getClassroom());
		onlineClass.setComments(os.getComments());
		onlineClass.setConsumeClassHour(os.isConsumeClassHour());
		onlineClass.setCourse(os.getCourse());
		onlineClass.setCourseMode(os.getCourseMode());
		onlineClass.setDbyDocument(os.getDbyDocument());
		onlineClass.setFinishType(os.getFinishType());
		onlineClass.setId(os.getId());
		onlineClass.setItProblemStudents(os.getItProblemStudents());
		onlineClass.setLastEditDateTime(os.getLastEditDateTime());
		onlineClass.setLastEditor(os.getLastEditor());
		onlineClass.setLesson(os.getLesson());
		onlineClass.setMaxStudentNumber(os.getMaxStudentNumber());
		onlineClass.setMinStudentNumber(os.getMinStudentNumber());
		onlineClass.setPayrollItem(os.getPayrollItem());
		onlineClass.setScheduledDateTime(os.getScheduledDateTime());
		onlineClass.setSerialNumber(os.getSerialNumber());
		onlineClass.setShortNotice(os.isShortNotice());
		onlineClass.setStatus(os.getStatus());
		onlineClass.setStudentEnterClassroomDateTime(os.getStudentEnterClassroomDateTime());
		onlineClass.setTeacher(os.getTeacher());
		onlineClass.setTeacherEnterClassroomDateTime(os.getTeacherEnterClassroomDateTime());
		onlineClass.setUnitPrice(os.getUnitPrice());
		onlineClass.setWxtCourseId(os.getWxtCourseId());
		onlineClass.setDemoReport(os.getDemoReport());
		/*onlineClass.setFiremanToStudentComments(os.getFiremanToStudentComments());
		onlineClass.setBackup(os.isBackup());
		onlineClass.setBackupTeachers(os.getBackupTeachers());
		onlineClass.setFiremanToTeacherComment(os.getFiremanToTeacherComment());
		onlineClass.setNoShowStudents(onlineClass.getNoShowStudents());
		onlineClass.setTeacherComments(os.getTeacherComments());
		onlineClass.setStudents(os.getStudents());*/
	}

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Count count(
            @RequestParam(value = "courseIds", required = false) Long[] courseIds,
            @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText,
            @RequestParam(value = "searchStudentText", required = false) String searchStudentText,
            @RequestParam(value = "searchSalesText", required = false) String searchSalesText,
            @RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom,
            @RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "finishType", required = false) String finishType,
            @RequestParam(value = "shortNotice", required = false) Boolean shortNotice,
            @RequestParam(value = "hasClassroom", required = false) Boolean hasClassroom) {
        logger.debug("list course with params: searchTeacherText = {}, searchStudentText = {}, searchSalesText={}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}.", searchTeacherText, searchStudentText, searchSalesText, scheduledDateTimeFrom, scheduledDateTimeTo, status, finishType, hasClassroom);

        return onlineClassService.count(courseIds == null ? new ArrayList<Long>() : Arrays.asList(courseIds),
                searchTeacherText, searchStudentText, searchSalesText,
                scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom),
                scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo),
                status == null ? null : Status.valueOf(status),
                finishType == null ? null : FinishType.valueOf(finishType),
                shortNotice,
                hasClassroom);
    }

    @RequestMapping(value = "/listForComments", method = RequestMethod.GET)
    public List<OnlineClass> listForComments(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, @RequestParam(value = "searchStudentText", required = false) String searchStudentText, @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, @RequestParam(value = "finishType", required = false) String finishType,
                                             @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "length", required = false) Integer length, @RequestParam(value = "isTeacherCommentsEmpty", required = false) Boolean isTeacherCommentsEmpty, @RequestParam(value = "isFiremanCommentsEmpty", required = false) Boolean isFiremanCommentsEmpty, @RequestParam(value = "searchOnlineClassText", required = false) String searchOnlineClassText) {
        logger.debug("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, finishType = {}, start = {}, length = {}, searchOnlineClassText = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, searchOnlineClassText, start, length);

        return onlineClassService.listForComments(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, start, length, isTeacherCommentsEmpty, isFiremanCommentsEmpty, searchOnlineClassText);
    }

    @RequestMapping(value = "/listForFireman", method = RequestMethod.GET)
    public List<OnlineClass> listForFireman(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, @RequestParam(value = "statusList", required = false) Status[] statusList, @RequestParam("start") int start, @RequestParam("length") int length) {
        logger.debug("list course with params: scheduleFrom = {}, scheduleTo = {}, start = {}, length = {}.", scheduledDateTimeFrom, scheduledDateTimeTo, start, length);

        return onlineClassService.listForFireman(Arrays.asList(courseIds == null ? new Long[0] : courseIds), scheduledDateTimeFrom, scheduledDateTimeTo, Arrays.asList(statusList == null ? new Status[0] : statusList), start, length);
    }

    @RequestMapping(value = "/listOnlineClassesAndPayrollItems", method = RequestMethod.GET)
    public List<OnlineClass> listOnlineClassesAndPayrollItems(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, @RequestParam(value = "searchStudentText", required = false) String searchStudentText, @RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo, @RequestParam("start") int start, @RequestParam("length") int length) {
        logger.debug("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);

        return onlineClassService.listOnlineClassesAndPayrollItems(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom), scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo), start, length);
    }

    @RequestMapping(value = "/countOnlineClassesAndPayrollItems", method = RequestMethod.GET)
    public Count countOnlineClassesAndPayrollItems(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, @RequestParam(value = "searchStudentText", required = false) String searchStudentText, @RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo, @RequestParam("start") int start, @RequestParam("length") int length) {
        logger.debug("court course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);

        return onlineClassService.countOnlineClassesAndPayrollItems(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom), scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo), start, length);
    }

    @RequestMapping(value = "/getSalary", method = RequestMethod.GET)
    public FloatWrapper getSalary(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText,
                                  @RequestParam(value = "searchStudentText", required = false) String searchStudentText, @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom,
                                  @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "length", required = false) Integer length) {
        logger.info(
                "list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.",
                searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
        if (null == start) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
        return onlineClassService.getSalary(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
    }

    @RequestMapping(value = "/countForComments", method = RequestMethod.GET)
    public Count countForComments(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, @RequestParam(value = "searchStudentText", required = false) String searchStudentText, @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, @RequestParam(value = "finishType", required = false) String finishType,
                                  @RequestParam(value = "isTeacherCommentsEmpty", required = false) Boolean isTeacherCommentsEmpty, @RequestParam(value = "isFiremanCommentsEmpty", required = false) Boolean isFiremanCommentsEmpty, @RequestParam(value = "searchOnlineClassText", required = false) String searchOnlineClassText) {
        logger.debug("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, finishType = {}, searchOnlineClassText = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, searchOnlineClassText);

        return onlineClassService.countForComments(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, isTeacherCommentsEmpty, isFiremanCommentsEmpty, searchOnlineClassText);
    }

    @RequestMapping(value = "/listForDemoReport", method = RequestMethod.GET)
    public List<OnlineClassVO> listForDemoReport(@RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, @RequestParam(value = "searchStudentText", required = false) String searchStudentText, @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, @RequestParam(value = "lifeCycle", required = false) String lifeCycle,
                                               @RequestParam(value = "salesId", required = false) Long salesId,@RequestParam(value = "searchStatus", required = false)String searchStatus,@RequestParam(value = "finishType", required = false)String finishType, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "length", required = false) Integer length) {
        logger.debug("list online class with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, lifeCycle = {}, salesId = {}, searchStatus = {},  finishType = {}, start = {}, length = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, lifeCycle, salesId, searchStatus,finishType,start, length);
        long startTime1 = System.currentTimeMillis();
        List<OnlineClass> onlineClassList = onlineClassService.listForDemoReport(searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, lifeCycle, salesId,searchStatus,finishType, start, length);
        long endTime1 = System.currentTimeMillis();
        logger.error("+++++++++++++++++++get OnlineClass from db,expired：{}",endTime1 - startTime1);
        long startTime2 = System.currentTimeMillis();
        List<OnlineClassVO> onlineClassVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(onlineClassList)) {
            onlineClassVOList =  OnlineClassHandler.convert2VOList(onlineClassList);
        }
        long endTime2 = System.currentTimeMillis();
        logger.error("+++++++++++++++++++transfer data,expired：{}",endTime2 - startTime2);
        return onlineClassVOList;
    }

    @RequestMapping(value = "/countForDemoReport", method = RequestMethod.GET)
    public Count countForDemoReport(@RequestParam(value = "false", required = false) List<Long> courseIds, @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, @RequestParam(value = "searchStudentText", required = false) String searchStudentText,
                                    @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo,
                                    @RequestParam(value = "lifeCycle", required = false) String lifeCycle,
                                    @RequestParam(value = "salesId", required = false) Long salesId,@RequestParam(value = "searchStatus", required = false)String searchStatus,@RequestParam(value = "finishType", required = false)String finishType) {
        logger.debug("list online class with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, lifeCycle = {}, salesId = {}, searchStatus = {},  finishType = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, lifeCycle, salesId, searchStatus,finishType);
        return onlineClassService.countForDemoReport(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo,lifeCycle, salesId,searchStatus,finishType);
    }

    @RequestMapping(value = "/classroom/create", method = RequestMethod.PUT)
    public OnlineClass createClassroom(@RequestBody OnlineClass onlineClass) {
        return onlineClassService.createClassroom(onlineClass);
    }

    @RequestMapping(value = "/findNextShouldTakeClass", method = RequestMethod.GET)
    public OnlineClass findNextShouldTakeClass(@RequestParam("studentId") long studentId) {
        logger.debug("find lessons for studentId = {}", studentId);

        return onlineClassService.findNextShouldTakeClass(studentId);
    }

    @RequestMapping(value = "/findLastedFinishedClassByStudentId", method = RequestMethod.GET)
    public OnlineClass findLastedFinishedClassByStudentId(@RequestParam("studentId") long studentId) {
        logger.debug("find lasted finished class for studentId = {}", studentId);

        return onlineClassService.findLastedFinishedClassByStudentId(studentId);
    }

    @RequestMapping(value = "/findFirstFinishedClassByStudentId", method = RequestMethod.GET)
    public OnlineClass findFirstFinishedClassByStudentId(@RequestParam("studentId") long studentId) {
        logger.debug("find first finished class for studentId = {}", studentId);

        return onlineClassService.findFirstFinishedClassByStudentId(studentId);
    }

    @RequestMapping(value = "/findByUnitId", method = RequestMethod.GET)
    public List<OnlineClass> findByUnitId(@RequestParam("studentId") long studentId, @RequestParam("unitId") long unitId) {
        logger.debug("find classes for unitId = {}", unitId);

        return onlineClassService.findByUnitId(studentId, unitId);
    }

    @RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
    public List<OnlineClass> findByStudentId(@RequestParam("studentId") long studentId) {
        logger.debug("find classes for studentId = {}", studentId);

        return onlineClassService.findByStudentId(studentId);
    }

    @RequestMapping(value = "/countByStudentOrTeacherIdAndStartDateEndDateForAttendance", method = RequestMethod.GET)
    public OnlineClassFinishCountView countByStudentOrTeacherIdAndStartDateEndDateForAttendance(@RequestParam("teacherId") String teacherId, @RequestParam("studentId") String studentId, @RequestParam("scheduledDateTimeFrom") DateTimeParam scheduledDateTimeFrom, @RequestParam("scheduledDateTimeTo") DateTimeParam scheduledDateTimeTo, @RequestParam("finishType") FinishType finishType) {
        return onlineClassService.countByStudentOrTeacherIdAndStartDateEndDateForAttendance(teacherId, studentId, scheduledDateTimeFrom, scheduledDateTimeTo, finishType);
    }

    @RequestMapping(value = "/listByStudentOrTeacherIdAndStartDateEndDateForAttendance", method = RequestMethod.GET)
    public List<OnlineClass> listByStudentOrTeacherIdAndStartDateEndDateForAttendance(@RequestParam("teacherId") String teacherId,
                                                                                      @RequestParam("studentId") String studentId, @RequestParam("scheduledDateTimeFrom") DateTimeParam scheduledDateTimeFrom,
                                                                                      @RequestParam("scheduledDateTimeTo") DateTimeParam scheduledDateTimeTo, @RequestParam("finishType") FinishType finishType,
                                                                                      @RequestParam("start") int start, @RequestParam("length") int length) {

        return onlineClassService.listByStudentOrTeacherIdAndStartDateEndDateForAttendance(teacherId, studentId, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, start, length);
    }

    @RequestMapping(value = "/sendStarById", method = RequestMethod.GET)
    public OnlineClass sendStarById(@RequestParam("id") long onlineClassId) {
        logger.debug("send star to on line class = {}", onlineClassId);

        return onlineClassService.sendStarById(onlineClassId);
    }

//	private void updateBackupDuty(final OnlineClass onlineClass){
//		if (onlineClass.isBackup()){
//			List<OnlineClass> hasbackupOnlineClasses = onlineClassAccessor.findByBackupTeacherIdAndScheduledDateTime(onlineClass.getTeacher().getId(), onlineClass.getScheduledDateTime());
//			for (OnlineClass hasbackupOnlineClass : hasbackupOnlineClasses){
//				List<Teacher> backupTeachers = hasbackupOnlineClass.getBackupTeachers();
//				backupTeachers.remove(onlineClass.getTeacher());
//				User lastEditor = securityController.getCurrentUser();
//				onlineClass.setLastEditor(lastEditor);
//				onlineClassAccessor.update(hasbackupOnlineClass);
//			}
//			securityController.logAudit(Level.INFO, Category.BACKUP_TEACHER_REMOVE, "Removed backup duty [" + onlineClass.getTeacher().getName() + "] "
//					+ "for [" + DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) +"]" );
//			onlineClass.setBackup(false);
//		}
//	}

    @RequestMapping(value = "/findLatestBookedClassByStudentId", method = RequestMethod.GET)
    public OnlineClass findLatestBookedClassByStudentId(@RequestParam("studentId") long studentId) {
        logger.debug("findLatestBookedClassByStudentId studentId = {}", studentId);
        return onlineClassService.findLatestBookedClassByStudentId(studentId);
    }

    @RequestMapping(value = "/countBackupDutyByTeacherIdAndStartDateAndEndDate", method = RequestMethod.GET)
    public Count countBackupDutyByTeacherIdAndStartDateAndEndDate(@RequestParam("teacherId") long teacherId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam) {
        logger.debug("countBackupDutyByTeacherIdAndStartDateAndEndDate, teacherId: {}, startDate: {}, endDate: {}", teacherId, startDateParam.getValue(), endDateParam.getValue());

        return onlineClassService.countBackupDutyByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam, endDateParam);
    }
    
    @RequestMapping(value = "/filterForComments", method = RequestMethod.GET)
    public List<OnlineClassVO> filterForComments(@RequestParam(value = "courseIds", required = false) Long[] courseIds, @RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, @RequestParam(value = "searchStudentText", required = false) String searchStudentText, @RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, @RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, @RequestParam(value = "finishType", required = false) String finishType,
                                             @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "length", required = false) Integer length, @RequestParam(value = "isTeacherCommentsEmpty", required = false) Boolean isTeacherCommentsEmpty, @RequestParam(value = "isFiremanCommentsEmpty", required = false) Boolean isFiremanCommentsEmpty, @RequestParam(value = "searchOnlineClassText", required = false) String searchOnlineClassText) {
        logger.debug("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, finishType = {}, start = {}, length = {}, searchOnlineClassText = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, searchOnlineClassText, start, length);

        List<OnlineClass> onlineClassList =  onlineClassService.listForComments(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, start, length, isTeacherCommentsEmpty, isFiremanCommentsEmpty, searchOnlineClassText);
        
        return OnlineClassHandler.convert2VOForCommentsList(onlineClassList);
    }
    
    @RequestMapping(value = "/findOnlineClassPeakViewPreWeek", method = RequestMethod.GET)   
    public OnlineClassPeakViewPreWeek findOnlineClassPeakViewPreWeek(@RequestParam("startDate") DateTimeParam startDateParam, @RequestParam("endDate") DateTimeParam endDateParam,@RequestParam(value = "teacherId", required = false) Long teacherId,@RequestParam(value = "courseId", required = false) Long courseId){
    	return onlineClassService.findOnlineClassPeakViewPreWeek(startDateParam, endDateParam,teacherId,courseId);
    }
    
    
	@RequestMapping(value="/findDefaultPeakTime",method = RequestMethod.GET)
	public PeakTimePerWeek findDefaultPeakTime() {
		logger.info("find peakTime ");
		return peakTimeService.getDefaultPeak();		
	}
	
	@RequestMapping(value="/listOnlineClassForCLT",method = RequestMethod.GET)
	public List<OnlineClassVo> listOnlineClassForCLT(
			@RequestParam(value = "scheduledTimeFrom", required = false) Long scheduledTimeFrom,
			@RequestParam(value = "scheduledTimeTo", required = false) Long scheduledTimeTo,
			@RequestParam(value = "courseIds", required = false) Long[] courseIds,
			@RequestParam(value = "status", required = false) Status status,
			@RequestParam(value = "finishType", required = false) String finishType,
			@RequestParam(value = "cltId", required = false) Long cltId,
			@RequestParam(value = "teacherName", required = false) String teacherName,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false,defaultValue = "80") Integer length) {
		
		logger.info("listOnlineClass, params: scheduledTimeFrom = {},scheduledTimeTo = {},courseIds = {},status = {},finishType = {},cltId = {},teacherName = {},searchText = {},start = {},length = {}",
				scheduledTimeFrom,scheduledTimeTo,courseIds,status,finishType,cltId,teacherName,searchText,start,length);
	
		return onlineClassService.listOnlineClassForCLT(
				scheduledTimeFrom != null ? new Date(scheduledTimeFrom) : null,
				scheduledTimeTo != null ? new Date(scheduledTimeTo) : null,
				courseIds != null ? Lists.newArrayList(courseIds) : null,
				status, finishType, cltId,teacherName, searchText, start, length);
	}
	
	@RequestMapping(value="/countOnlineClassForCLT",method = RequestMethod.GET)
	public Count countOnlineClassForCLT(
			@RequestParam(value = "scheduledTimeFrom", required = false) Long scheduledTimeFrom,
			@RequestParam(value = "scheduledTimeTo", required = false) Long scheduledTimeTo,
			@RequestParam(value = "courseIds", required = false) Long[] courseIds,
			@RequestParam(value = "status", required = false) Status status,
			@RequestParam(value = "finishType", required = false) String finishType,
			@RequestParam(value = "cltId", required = false) Long cltId,
			@RequestParam(value = "teacherName", required = false) String teacherName,
			@RequestParam(value = "searchText", required = false) String searchText) {
		
		logger.info("listOnlineClass, params: scheduledTimeFrom = {},scheduledTimeTo = {},courseIds = {},status = {},finishType = {},cltId = {},teacherName = {},searchText = {}",
				scheduledTimeFrom,scheduledTimeTo,courseIds,status,finishType,cltId,teacherName,searchText);
	
		long count =  onlineClassService.countOnlineClassForCLT(
				scheduledTimeFrom != null ? new Date(scheduledTimeFrom) : null,
				scheduledTimeTo != null ? new Date(scheduledTimeTo) : null,
				courseIds != null ? Lists.newArrayList(courseIds) : null,
				status, finishType, cltId,teacherName, searchText);
		return new Count(count);
	}
	
//	@GET
//	@Path("/listForFireman")
//	public List<OnlineClass> listForFireman(@RequestParam("courseIds") List<Long> courseIds, @RequestParam("scheduledDateTimeFrom") DateTimeParam scheduledDateTimeFrom, @RequestParam("scheduledDateTimeTo") DateTimeParam scheduledDateTimeTo, @RequestParam("statusList") List<Status> statusList, @RequestParam("start") int start, @RequestParam("length") int length) {
//		logger.debug("list course with params: scheduleFrom = {}, scheduleTo = {}, start = {}, length = {}.", scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
//		
//		List<OnlineClass> listForFireman = new ArrayList<OnlineClass>();
//		listForFireman = onlineClassAccessor.listForFireman(courseIds, scheduledDateTimeFrom, scheduledDateTimeTo, statusList, start, length);
//		
//		List<OnlineClass> simplifiedListForFireman = new ArrayList<OnlineClass>();
//		if(listForFireman.isEmpty() == false){
//			for(OnlineClass onlineClass : listForFireman){
//				OnlineClass simplifiedOnlineClass = new OnlineClass();
//				
//				simplifiedOnlineClass.setStatus(onlineClass.getStatus());
//				simplifiedOnlineClass.setScheduledDateTime(onlineClass.getScheduledDateTime());
//				simplifiedOnlineClass.setSerialNumber(onlineClass.getSerialNumber());
//				simplifiedOnlineClass.setStudents(onlineClass.getStudents());
//				simplifiedOnlineClass.setTeacher(onlineClass.getTeacher());
//				simplifiedOnlineClass.setBackupTeacherNames(onlineClass.getBackupTeacherNames());
//				simplifiedOnlineClass.setClassroom(onlineClass.getClassroom());
//				simplifiedOnlineClass.setId(onlineClass.getId());
//				simplifiedOnlineClass.setAttatchDocumentSucess(onlineClass.isAttatchDocumentSucess());
//				simplifiedOnlineClass.setTeacherEnterClassroomDateTime(onlineClass.getTeacherEnterClassroomDateTime());
//				simplifiedOnlineClass.setStudentEnterClassroomDateTime(onlineClass.getStudentEnterClassroomDateTime());
//				simplifiedOnlineClass.setCanUndoFinish(onlineClass.isCanUndoFinish());
//				simplifiedOnlineClass.setAttatchDocumentSucess(onlineClass.isAttatchDocumentSucess());
//				
//				Course course = onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
//				Course simplifiedCourse = new Course();
//				simplifiedCourse.setId(course.getId());
//				simplifiedCourse.setName(course.getName());
//				simplifiedOnlineClass.setCourse(simplifiedCourse);
//				
//				simplifiedListForFireman.add(simplifiedOnlineClass);
//			}
//			
//		}	
//		return simplifiedListForFireman;
//	}

//	@GET
//	@Path("/countForFireman")
//	public Count countForFireman (@RequestParam("courseIds") List<Long> courseIds, @RequestParam("scheduledDateTimeFrom") DateTimeParam scheduledDateTimeFrom, @RequestParam("scheduledDateTimeTo") DateTimeParam scheduledDateTimeTo, @RequestParam("statusList") List<Status> statusList) {
//		logger.debug("list course with params: scheduleFrom = {}, scheduleTo = {}, status = {}, hasClassroom = {}.", scheduledDateTimeFrom, scheduledDateTimeTo);
//		Count count = new Count(onlineClassAccessor.countForFireman(courseIds, scheduledDateTimeFrom, scheduledDateTimeTo, statusList));
//		return count;
//	}
	
    
    @RequestMapping(value = "/filterForStudentComments", method = RequestMethod.GET)
    public List<OnlineClassVO> filterForStudentComments(
    		@RequestParam(value = "courseIds", required = false) Long[] courseIds, 
    		@RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, 
    		@RequestParam(value = "searchStudentText", required = false) String searchStudentText, 
    		@RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, 
    		@RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, 
    		@RequestParam(value = "scores", required = false) Integer scores,
    		@RequestParam(value = "existStudentComment", required = false) Boolean existStudentComment,
    		@RequestParam(value = "finishType", required = false) String finishType,
    		@RequestParam(value = "cltId", required = false) String cltId,
            @RequestParam(value = "start", required = false) Integer start, 
            @RequestParam(value = "length", required = false) Integer length) {
    	
        List<OnlineClass> onlineClassList =  onlineClassService.listForStudentComments(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, scores, existStudentComment, finishType, cltId, start, length);
        
        return OnlineClassHandler.convert2VOForCommentsList(onlineClassList);
    }

    @RequestMapping(value = "/countForStudentComments", method = RequestMethod.GET)
    public Count countForStudentComments(
    		@RequestParam(value = "courseIds", required = false) Long[] courseIds, 
    		@RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, 
    		@RequestParam(value = "searchStudentText", required = false) String searchStudentText, 
    		@RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, 
    		@RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo, 
    		@RequestParam(value = "scores", required = false) Integer scores,
    		@RequestParam(value = "existStudentComment", required = false) Boolean existStudentComment,
    		@RequestParam(value = "finishType", required = false) String finishType,
    		@RequestParam(value = "cltId", required = false) String cltId) {
    	
        return onlineClassService.countForStudentComments(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, scores, existStudentComment, finishType, cltId);
    }
    
    @RequestMapping(value = "/listForTeacherEvaluation", method = RequestMethod.GET)
    public TeacherEvaluationVO listForTeacherEvaluation(
    		@RequestParam(value = "courseIds", required = false) Long[] courseIds, 
    		@RequestParam(value = "searchTeacherText", required = false) String searchTeacherText, 
    		@RequestParam(value = "scheduledDateTimeFrom", required = false) DateTimeParam scheduledDateTimeFrom, 
    		@RequestParam(value = "scheduledDateTimeTo", required = false) DateTimeParam scheduledDateTimeTo,
    		@RequestParam(value = "finishType", required = false) String finishType) {
    	return onlineClassService.listForTeacherEvaluation(Arrays.asList(courseIds == null ? new Long[0] : courseIds), searchTeacherText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType);
    }
    
    @RequestMapping(value = "/findAvailableTimeSlotsWithParallelLimit", method = RequestMethod.GET)
    public List<Date> findAvailableTimeSlotsWithParallelLimit(@RequestParam("courseId") long courseId, @RequestParam("startDate") DateParam startDateParam, @RequestParam("endDate") DateParam endDateParam, @RequestParam("type") Type type) {
		logger.info("find available classes for courseId = {}, startDate = {}, endDate = {} and limit trail parallel", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassService.findAvailableTimeSlotsWithParallelLimit(courseId, startDateParam, endDateParam, type);
    }
    
    @RequestMapping(value = "/findByStudentIdAndDateTime", method = RequestMethod.GET)
    public List<OnlineClassVO> findByStudentIdAndDateTime(@RequestParam("studentId") long studentId, @RequestParam(value = "startDate", required = false) DateParam startDateParam, @RequestParam(value = "endDate", required = false) DateParam endDateParam) {
        logger.info("find onlineClasses for studentId = {}, startDate = {}, endDate = {}", studentId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));

        return onlineClassService.findByStudentIdAndDateTime(studentId, startDateParam, endDateParam);
    }
    
    @RequestMapping(value = "/findBookedInterviewByStartDateAndEndDateAndCourseId", method = RequestMethod.GET)
    public List<OnlineClassVO> findBookedInterviewByStartDateAndEndDateAndCourseId(@RequestParam(value = "startDate", required = false) DateParam startDateParam, @RequestParam(value = "endDate", required = false) DateParam endDateParam) {
        logger.info("findBookedInterviewByStartDateAndEndDateAndCourseId, params: startDateParam = {}, endDateParam = {}", startDateParam, endDateParam);
        return onlineClassService.findBookedInterviewByStartDateAndEndDateAndCourseId(startDateParam, endDateParam);
    }
    
//    @RequestMapping(value = "/findBookedPracticumByStartDateAndEndDateAndCourseId", method = RequestMethod.GET)
//    public List<OnlineClassVO> findBookedPracticumByStartDateAndEndDateAndCourseId(@RequestParam(value = "startDate", required = false) DateParam startDateParam, @RequestParam(value = "endDate", required = false) DateParam endDateParam) {
//        logger.info("findBookedInterviewByStartDateAndEndDateAndCourseId, params: startDateParam = {}, endDateParam = {}", startDateParam, endDateParam);
//        return onlineClassService.findBookedPracticumByStartDateAndEndDateAndCourseId(startDateParam, endDateParam);
//    }
}
