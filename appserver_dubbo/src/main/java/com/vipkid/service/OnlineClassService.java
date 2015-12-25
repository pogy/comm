package com.vipkid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.duobeiyun.DuobeiYunClient;
import com.vipkid.context.AppContext;
import com.vipkid.ext.dby.AttachDocumentResult;
import com.vipkid.ext.dby.CreateRoomResult;
import com.vipkid.ext.dby.DBYAPI;
import com.vipkid.ext.dby.Document;
import com.vipkid.ext.dby.ListDocumentsResult;
import com.vipkid.ext.dby.UpdateRoomTitleResult;
import com.vipkid.ext.email.EMail;
import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.handler.OnlineClassHandler;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.Course.Type;
import com.vipkid.model.DemoReport;
import com.vipkid.model.DemoReport.Answer;
import com.vipkid.model.DemoReport.LifeCycle;
import com.vipkid.model.EducationalComment;
import com.vipkid.model.FiremanToStudentComment;
import com.vipkid.model.FiremanToTeacherComment;
import com.vipkid.model.FloatWrapper;
import com.vipkid.model.Leads;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.Lesson;
import com.vipkid.model.Medal;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.OnlineClassOperation;
import com.vipkid.model.OpenClassDesc;
import com.vipkid.model.OpenClassDesc.OpenClassType;
import com.vipkid.model.OrderItem;
import com.vipkid.model.Parent;
import com.vipkid.model.Payroll;
import com.vipkid.model.PayrollItem;
import com.vipkid.model.Product;
import com.vipkid.model.Student;
import com.vipkid.model.Teacher;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.TrialThreshold;
import com.vipkid.model.Unit;
import com.vipkid.model.User;
import com.vipkid.redis.DistributedLock;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.AuditRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.DemoReportRepository;
import com.vipkid.repository.EducationalCommentRepository;
import com.vipkid.repository.FiremanToStudentCommentRepository;
import com.vipkid.repository.FiremanToTeacherCommentRepository;
import com.vipkid.repository.LeadsRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.MedalRepository;
import com.vipkid.repository.OnlineClassOperationRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OpenClassRepository;
import com.vipkid.repository.OrderItemRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.repository.PayrollItemRepository;
import com.vipkid.repository.PayrollRepository;
import com.vipkid.repository.ProductRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherCommentRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.repository.TrialThresholdRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.rest.vo.query.OnlineClassVO;
import com.vipkid.rest.vo.query.TeacherEvaluationVO;
import com.vipkid.security.CustomizedPrincipal;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.BadRequestServiceException;
import com.vipkid.service.exception.DateTimeAlreadyScheduledServiceException;
import com.vipkid.service.exception.ExceedMaxParallelCountServiceException;
import com.vipkid.service.exception.NoMoreClassHourForBookingServiceException;
import com.vipkid.service.exception.NoMoreLessonForBookingServiceException;
import com.vipkid.service.exception.OnlineClassAlreadyBookedByOthersServiceException;
import com.vipkid.service.exception.OnlineClassAlreadyExistServiceException;
import com.vipkid.service.exception.OnlineClassAlreadyRequestedServiceException;
import com.vipkid.service.exception.OnlineClassNotExistServiceException;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.BooleanWrapper;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.DateWrapper;
import com.vipkid.service.pojo.OnlineClassFinishCountView;
import com.vipkid.service.pojo.OnlineClassPeakTimeView;
import com.vipkid.service.pojo.OnlineClassPeakViewPreWeek;
import com.vipkid.service.pojo.OnlineClassView;
import com.vipkid.service.pojo.PeakTimePerWeek;
import com.vipkid.service.pojo.leads.OnlineClassVo;
import com.vipkid.service.pojo.parent.LessonsView;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;
import com.vipkid.util.Redis;
import com.vipkid.util.TextUtils;

@Service
public class OnlineClassService {
	private Logger logger = LoggerFactory.getLogger(OnlineClassService.class.getSimpleName());
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private OnlineClassOperationRepository onlineClassOperationRepository;
	
	@Resource
	private LearningProgressRepository learningProgressRepository;
	
	@Resource
	private LessonRepository lessonRepository;
	
	@Resource
	private AuditRepository auditRepository;
	
	@Resource
	private StaffRepository staffRepository;
	
	@Resource
	private CourseRepository courseRepository;
	
	@Resource
	private PayrollRepository payrollRepository;
	
	@Resource
	private PayrollItemRepository payrollItemRepository;
	
	@Resource
	private MedalRepository medalRepository;
	
	@Resource
	private TeacherRepository teacherRepository;
	
	@Resource
	private TeacherCommentRepository teacherCommentRepository;
	
	@Resource
	private EducationalCommentRepository educationalCommentRepository;
	
	@Resource
	private FiremanToTeacherCommentRepository firemanToTeacherCommentRepository;
	
	@Resource
	private FiremanToStudentCommentRepository firemanToStudentCommentRepository;
	
	@Resource
	private DemoReportRepository demoReportCommentRepository;
	
	@Resource
	private StudentRepository studentRepository;
	
	@Resource
	private MoxtraUserService moxtraUserService;
	
	@Resource
	private OrderItemRepository orderItemRepository;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private LeadsRepository leadsRepository;
	@Resource
	private LeadsManageService leadsManageService;
	
	@Resource
	private OrderRepository orderRepository;
	
	@Resource
	private StudentLifeCycleLogService studentLifeCycleLogService;
	
	@Resource
	private FiremanOnlineClassSupportingStatusRedisService firemanOnlineClassSupportingStatusRedisService;
	
	@Resource
    private PeakTimeService peakTimeService;

	@Resource
	private OpenClassRepository openClassRepository;
	
	@Resource
	private ProductRepository productRepository;
	
	@Resource
	private TrialThresholdRepository trialThresholdRepository;
	
	public OnlineClass find(long id) {
		return onlineClassRepository.find(id);
	}
	
	public Count countBackupDutyByTeacherIdAndDate (long teacherId, DateParam date) {
		Count resultCount = new Count();
		resultCount.setTotal(onlineClassRepository.countBackupDutyByTeacherIdAndDate(teacherId, date.getValue()));
		
		return resultCount;
	}
	
	public Count countInterviewEnrolledByTeacherIdAndDate (long teacherId, DateParam date) {
		Count resultCount = new Count();
		resultCount.setTotal(onlineClassRepository.countInterviewEnrolledByTeacherIdAndDate(teacherId, date.getValue()));
		
		return resultCount;
	}
	
	public List<OnlineClass> findTestClassAndITByStudentId(long studentId) {
		return onlineClassRepository.findTestClassAndITByStudentId(studentId);
	}
	
	public List<OnlineClass> findByStudentIdAndFinishType(long studentId, String finishType, int start, int length) {
		List<OnlineClass> onlineClasses = onlineClassRepository.findByStudentIdAndFinishType(studentId, finishType, start, length);
	
		for (OnlineClass onlineClass : onlineClasses) {
			List<TeacherComment> comments = onlineClass.getTeacherComments();
			List<TeacherComment> commentForThisStudent = new ArrayList<TeacherComment>();
			for (TeacherComment c : comments) {
				if (c.getStudent().getId() == studentId) {
					commentForThisStudent.add(c);
				}
			}
			onlineClass.setTeacherComments(commentForThisStudent);
		}
		
		return onlineClasses;
	}
	
	public Count countByStudentIdAndFinishType(long studentId, String finishType) {
		return new Count(onlineClassRepository.countByStudentIdAndFinishType(studentId, finishType));
	}
	
	public OnlineClassPeakViewPreWeek findOnlineClassPeakViewPreWeek(DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Long teacherId, Long courseId) {
		List<OnlineClassPeakTimeView> availPeakTimeViewList = onlineClassRepository.listPeakTimeView(scheduledDateTimeFrom, scheduledDateTimeTo, teacherId, courseId, true);
		List<OnlineClassPeakTimeView> peakTimeViewList = onlineClassRepository.listPeakTimeView(scheduledDateTimeFrom, scheduledDateTimeTo, teacherId, courseId, false);

		
		PeakTimePerWeek peakTime = peakTimeService.getDefaultPeak();
		Map<String, String> periodMap = peakTime.getPeriodMap();
		if (peakTimeViewList.size() > 0 || availPeakTimeViewList.size() > 0) {
			OnlineClassPeakViewPreWeek view = new OnlineClassPeakViewPreWeek();
			view.setPeriodMap(periodMap);
			for (OnlineClassPeakTimeView onlineClassPeakTimeView : peakTimeViewList) {
				//booked count for searching result.
				
				if (onlineClassPeakTimeView.getStatus() == Status.BOOKED) {
					//booked count for every day .
					view.countAvailableOrBooked(onlineClassPeakTimeView,false);	
					view.countTotalBookedAndTotalTimeSolts(onlineClassPeakTimeView);
				} else if (onlineClassPeakTimeView.getStatus() == Status.FINISHED) {
					view.countTotalBookedAndTotalTimeSolts(onlineClassPeakTimeView);
					if (onlineClassPeakTimeView.getFinishType() == FinishType.TEACHER_NO_SHOW) {
						view.countTeacherNoShow();						
					} else if (onlineClassPeakTimeView.getFinishType() == FinishType.STUDENT_NO_SHOW) {
						view.countStudentNoShow();
					} else if (onlineClassPeakTimeView.getFinishType() == FinishType.STUDENT_IT_PROBLEM || onlineClassPeakTimeView.getFinishType() == FinishType.TEACHER_IT_PROBLEM) {
						view.countItProblem();
					}
				}
			}
			for (OnlineClassPeakTimeView onlineClassPeakTimeView : availPeakTimeViewList) {
				if (onlineClassPeakTimeView.getStatus() == Status.AVAILABLE) {
					//available count for every day .
					view.countAvailableOrBooked(onlineClassPeakTimeView,true);					
				}
				//avail count for searching result  .
				if (onlineClassPeakTimeView.getStatus() == Status.AVAILABLE || onlineClassPeakTimeView.getStatus() == Status.EXPIRED) {
					view.countTotalBookedAndTotalTimeSolts(onlineClassPeakTimeView);
				}
			}
			return view;
		}
		return null;
	}
	
	public Count countForFireman (List<Long> courseIds, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, List<Status> statusList) {
		logger.info("list course with params: scheduleFrom = {}, scheduleTo = {}, status = {}, hasClassroom = {}.", scheduledDateTimeFrom, scheduledDateTimeTo);
		Count count = new Count(onlineClassRepository.countForFireman(courseIds, scheduledDateTimeFrom, scheduledDateTimeTo, statusList));
		return count;
	}
	
	public List<OnlineClass> findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId(long learningProgressCourseId, long studentId) {
		return onlineClassRepository.findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId(learningProgressCourseId, studentId);
	}
	
	public List<OnlineClass> findNextWeekAvailableOnlineClassesByLearningProgressCourseId(long learningProgressCourseId) {
		return onlineClassRepository.findNextWeekOpenOnlineClassesByLearningProgressCourseId(learningProgressCourseId);
	}
	
	public List<OnlineClass> findByStudentIdAndCourseIdAndStatus(long studentId, long courseId, Status status, int start, int length) {
		logger.info("find onlineClass for studentId = {}, courseId = {}, status = {}", studentId, courseId, status);
		return onlineClassRepository.findByStudentIdAndCourseIdAndStatus(studentId, courseId, status, start, length);
	}
	
	public List<OnlineClass> findByStudentIdAndCourseIdAndFinishType(long studentId, long courseId, FinishType type) {
		logger.info("find onlineClass for studentId = {}, courseId = {}, FinishType = {}", studentId, courseId, type);
		return onlineClassRepository.findByStudentIdAndCourseIdAndFinishType(studentId, courseId, type);
	}
	
	public List<OnlineClass> findByTeacherIdAndStudentIdAndScheduledDateTime(long teacherId, long studentId, DateParam scheduledDateParam) {
		logger.info("find onlineClasses for teacherId = {}, studentId = {}, scheduledDateTime = {}", teacherId, studentId, DateTimeUtils.format(scheduledDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass> onlineClasses = onlineClassRepository.findByTeacherIdAndStudentIdAndScheduledDateTime(teacherId, studentId, scheduledDateParam.getValue());
		return simplifyOnlineClasses(onlineClasses);
	}
	
	public List<OnlineClass> findByStudentIdAndScheduledDateTime(long studentId, DateParam scheduledDateParam) {
		logger.info("find onlineClasses for studentId = {}, scheduledDateTime = {}", studentId, DateTimeUtils.format(scheduledDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass> onlineClasses = onlineClassRepository.findByStudentIdAndScheduledDateTime(studentId, scheduledDateParam.getValue());
		return simplifyOnlineClasses(onlineClasses);
	}
	
	public List<OnlineClass> findByTeacherIdAndStartDateAndEndDate(long teacherId, DateParam startDateParam, DateParam endDateParam) {
		logger.info("find onlineClasses for teacherId = {}, startDate = {}, endDate = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass> onlineClasses = onlineClassRepository.findByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam.getValue(), endDateParam.getValue());
		return simplifyOnlineClasses(onlineClasses);
	}
	
	public List<DateWrapper> findExccedMaxParallelTrialCountByStartDateAndEndDate(DateParam startDateParam, DateParam endDateParam) {
		logger.info("find excced max parallel trial booking count date for startDate = {}, endDate = {}",DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		int parallelCount = getMaxParallelCountFromRedis();
		return onlineClassRepository.findExccedMaxParallelTrialCountByStartDateAndEndDate(startDateParam.getValue(), endDateParam.getValue(), parallelCount);
	}
	
	public List<OnlineClassView> findOnlineClassViewByTeacherIdAndStartDateAndEndDate(long teacherId, DateParam startDateParam, DateParam endDateParam) {
		logger.info("find onlineClasses for teacherId = {}, startDate = {}, endDate = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass> onlineClasses = onlineClassRepository.findByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam.getValue(), endDateParam.getValue());
        List<OnlineClassView> onlineClassViews = com.google.common.collect.Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(onlineClasses)) {
            for (OnlineClass onlineClass : onlineClasses) {
                OnlineClassView view = new OnlineClassView();
                view.setFinishType(onlineClass.getFinishType());
                view.setId(onlineClass.getId());
                Lesson onlineClassLesson = onlineClass.getLesson();
                if (onlineClassLesson != null) {
                    Lesson newLesson = new Lesson();
                    newLesson.setName(onlineClassLesson.getName());
                    newLesson.setSerialNumber(onlineClassLesson.getSerialNumber());
                    view.setLesson(newLesson);
                }
                view.setScheduledDateTime(onlineClass.getScheduledDateTime());
                view.setSerialNumber(onlineClass.getSerialNumber());
                view.setStatus(onlineClass.getStatus());
                view.setClassroom(onlineClass.getClassroom());
                view.setStudents(onlineClass.getStudents());
                view.setStudentEnglishNames(onlineClass.getStudentEnglishNames());
                view.setShortNotice(onlineClass.isShortNotice());
                view.setBackup(onlineClass.isBackup());
                view.setCourseMode(onlineClass.getCourseMode());
                onlineClassViews.add(view);
            }
        }		
		return onlineClassViews;
	}
	
	public List<OnlineClass> findByTeacherIdAndStartDateAndEndDateAndStatus(long teacherId, DateParam startDateParam, DateParam endDateParam, String statuses) {
		logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

		List<Status> statueList = new LinkedList<Status>();
		for (String status : statuses.split("\\|")) {
			statueList.add(convertStringToStatus(status));
		}

		List<OnlineClass> onlineClasses = onlineClassRepository.findByTeacherIdAndStartDateAndEndDateAndStatus(teacherId, startDateParam.getValue(), endDateParam.getValue(), statueList);     
		ArrayList<OnlineClass> simplifiedOnlineClasses = new ArrayList<OnlineClass>();
		for (OnlineClass onlineClass : onlineClasses) {
			OnlineClass simplifiedOnlineClass = new OnlineClass();
			simplifiedOnlineClass.setFinishType(onlineClass.getFinishType());
			simplifiedOnlineClass.setId(onlineClass.getId());
			Lesson lesson = new Lesson();
			lesson.setId(onlineClass.getLesson().getId());
			lesson.setName(onlineClass.getLesson().getName());
			lesson.setSerialNumber(onlineClass.getLesson().getSerialNumber());
			simplifiedOnlineClass.setLesson(lesson);
			simplifiedOnlineClass.setScheduledDateTime(onlineClass.getScheduledDateTime());
			simplifiedOnlineClass.setStatus(onlineClass.getStatus());
			simplifiedOnlineClass.setStudents(onlineClass.getStudents());
			simplifiedOnlineClass.setTeacherComments(onlineClass.getTeacherComments());
			simplifiedOnlineClass.setStudentEnglishNames(onlineClass.getStudentEnglishNames());
			simplifiedOnlineClasses.add(simplifiedOnlineClass);
		}
		
		return simplifiedOnlineClasses;
	}
	
	public List<OnlineClass> findByTeacherIdAndStartDateAndEndDateAndStatusAndPage(long teacherId, DateParam startDateParam, DateParam endDateParam, String statuses, int page, int size) {
		logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

		List<Status> statueList = new LinkedList<Status>();
		for (String status : statuses.split("\\|")) {
			statueList.add(convertStringToStatus(status));
		}

		List<OnlineClass> onlineClasses = onlineClassRepository.findByTeacherIdAndStartDateAndEndDateAndStatusAndPage(teacherId, startDateParam.getValue(), endDateParam.getValue(), statueList, page, size);     
		ArrayList<OnlineClass> simplifiedOnlineClasses = new ArrayList<OnlineClass>();
		for (OnlineClass onlineClass : onlineClasses) {
			OnlineClass simplifiedOnlineClass = new OnlineClass();
			simplifiedOnlineClass.setFinishType(onlineClass.getFinishType());
			simplifiedOnlineClass.setId(onlineClass.getId());
			Lesson lesson = new Lesson();
			lesson.setId(onlineClass.getLesson().getId());
			lesson.setSequence(onlineClass.getLesson().getSequence());
			lesson.setName(onlineClass.getLesson().getName());
			lesson.setSerialNumber(onlineClass.getLesson().getSerialNumber());
//			uncomment these to fix the material issue.
//			Course course = new Course();
//			course.setId(onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getId());
//			Unit unit = new Unit();
//			unit.setCourse(course);
//			LearningCycle learningCycle = new LearningCycle();
//			learningCycle.setUnit(unit);
//			lesson.setLearningCycle(learningCycle);
			simplifiedOnlineClass.setLesson(lesson);
			simplifiedOnlineClass.setScheduledDateTime(onlineClass.getScheduledDateTime());
			simplifiedOnlineClass.setStatus(onlineClass.getStatus());
			if (onlineClass.getStudents() != null && onlineClass.getStudents().size() > 0) {
				List<Student> firstStudentInAList = new ArrayList<Student>();
				firstStudentInAList.add(onlineClass.getStudents().get(0));
				simplifiedOnlineClass.setStudents(firstStudentInAList);
			} 
			simplifiedOnlineClass.setTeacherComments(onlineClass.getTeacherComments());
			simplifiedOnlineClass.setStudentEnglishNames(onlineClass.getStudentEnglishNames());
			simplifiedOnlineClass.setPaidTrail(onlineClass.getPaidTrail());
			
			//2015-07-15 添加mode
			simplifiedOnlineClass.setCourseMode(onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getMode());
			
			simplifiedOnlineClasses.add(simplifiedOnlineClass);
		}
		
		return simplifiedOnlineClasses;
	}
	
	public Count findNextLessonPositionByTeacherIdAndStartDateAndEndDateAndStatus(long teacherId, DateParam startDateParam, DateParam endDateParam, String statuses) {
		logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

		List<Status> statueList = new LinkedList<Status>();
		for (String status : statuses.split("\\|")) {
			statueList.add(convertStringToStatus(status));
		}

		//long allTheClassThisMonthCount = countByTeacherIdAndStartDateAndEndDateAndStatus(teacherId, startDateParam, endDateParam, statuses).getTotal();
		long allTheFinishedClassThisMonthCount = countByTeacherIdAndStartDateAndEndDateAndStatus(teacherId, startDateParam, endDateParam, "FINISHED").getTotal();
		
		return new Count(allTheFinishedClassThisMonthCount);
	}
	
	public Count countByTeacherIdAndStartDateAndEndDateAndStatus(long teacherId, DateParam startDateParam, DateParam endDateParam, String statuses) {
		logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

		List<Status> statueList = new LinkedList<Status>();
		for (String status : statuses.split("\\|")) {
			statueList.add(convertStringToStatus(status));
		}

		long onlineClassCount = onlineClassRepository.countByTeacherIdAndStartDateAndEndDateAndStatus(teacherId, startDateParam.getValue(), endDateParam.getValue(), statueList);     
		
		return new Count(onlineClassCount);
	}
	
	public Count countByTeacherIdAndEndDateAndStatus(long teacherId, DateParam endDateParam, String statuses) {
		logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

		List<Status> statueList = new LinkedList<Status>();
		for (String status : statuses.split("\\|")) {
			statueList.add(convertStringToStatus(status));
		}

		long onlineClassesCount = onlineClassRepository.countByTeacherIdAndEndDateAndStatus(teacherId, endDateParam.getValue(), statueList);
		Count resultCount = new Count();
		resultCount.setTotal(onlineClassesCount);
		
		return resultCount;
	}
	
	public Count countByTeacherIdAndStartDateAndStatus(long teacherId, DateParam startDateParam, String statuses) {
		logger.info("find lessonHistories for teacherId = {}, startDate = {}, endDate = {}, status = {}", teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), statuses);

		List<Status> statueList = new LinkedList<Status>();
		for (String status : statuses.split("\\|")) {
			statueList.add(convertStringToStatus(status));
		}

		long onlineClassesCount = onlineClassRepository.countByTeacherIdAndStartDateAndStatus(teacherId, startDateParam.getValue(), statueList);
		Count resultCount = new Count();
		resultCount.setTotal(onlineClassesCount);
		
		return resultCount;
	}
	
	public List<OnlineClass> findByStudentIdAndStartDateAndEndDate(long studentId, DateParam startDateParam, DateParam endDateParam) {
		logger.info("find onlineClasses for studentId = {}, startDate = {}, endDate = {}", studentId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassRepository.findByStudentIdAndStartDateAndEndDate(studentId, startDateParam.getValue(), endDateParam.getValue());
	}
	
	public Count countClassByStudentIdAndStartDateOrEndDate(long studentId, DateParam startDateParam, DateParam endDateParam) {
		if(startDateParam != null && endDateParam == null) {
			return onlineClassRepository.countClassByStudentIdAndStartDateOrEndDate(studentId, startDateParam.getValue(), null);
		}
		if(startDateParam == null && endDateParam != null) {
			return onlineClassRepository.countClassByStudentIdAndStartDateOrEndDate(studentId, null, endDateParam.getValue());
		}
		return null;
	}
	
	
	public List<OnlineClass> findAvailableByTeacherId(long teacherId) {
		logger.info("find classes for teacherId = {}", teacherId);
		return onlineClassRepository.findAvailableByTeacherId(teacherId);
	}
	
	public List<OnlineClass> findAvailableByTeacherIdAndLimitParallelByCourseType(long teacherId, Type type) {
		logger.info("find classes for teacherId = {} and limit trail parallel", teacherId);
		int parallelCount = Redis.Trial.DEFAULT_PARALLEL;
		if(type == Type.TRIAL) {
			parallelCount = getMaxParallelCountFromRedis();
		}
		List<OnlineClass> onlineClasses = onlineClassRepository.findAvailableByTeacherIdAndLimitParallelByCourseType(teacherId, type, parallelCount);
		return simplifyOnlineClasses(onlineClasses);
	}
	
	private int getMaxParallelCountFromRedis() {
		int returnParallelCount = 0;
		try {
			// 记录每次从redis 请求数据耗时，稳定后去除
			long requestStartTime = System.currentTimeMillis();
			
			returnParallelCount = Integer.valueOf(RedisClient.getInstance().get(Redis.Trial.KEY));
			
			long requestEndTime = System.currentTimeMillis();
			logger.error("a requesting get parallel count from redis takes {} milliseconds.", requestEndTime - requestStartTime);
		} catch (Exception e) {
			logger.error("Fail to get redis key = {} \n", Redis.Trial.KEY, e);
			returnParallelCount = Redis.Trial.DEFAULT_PARALLEL;
		}
		return returnParallelCount;
	}
	
	public List<OnlineClass> findAvailableByScheduledDateTime(DateParam scheduledDateTime) {
		logger.info("find classes for scheduled date time = {}", DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassRepository.findAvailableByScheduledDateTime(scheduledDateTime.getValue());
	}
	
	public List<OnlineClass> findAvailableByByTeacherCertificatedCourseIdScheduledDateTime(long courseId, DateParam scheduledDateTime) {
		logger.info("find classes for courseId = {}, scheduled date time = {}", courseId, DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass> onlineClasses = onlineClassRepository.findAvailableByByTeacherCertificatedCourseIdScheduledDateTime(courseId, scheduledDateTime.getValue());
		return simplifyOnlineClasses(onlineClasses);
	}
	
	public BooleanWrapper findIfExceedParallelTrialByScheduledDateTime(DateParam scheduledDateTime) {
		logger.info("count parallel trial course, scheduled date time = {}", DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));
		int parallelCount = getMaxParallelCountFromRedis();
		int result = (int)onlineClassRepository.countParallelTrialByScheduledDateTime(scheduledDateTime.getValue());
		if(result >= parallelCount) {
			return new BooleanWrapper(true);
		}else {
			return new BooleanWrapper(false);
		}
	}
	
	public List<OnlineClass> findAvailableByStartDateAndEndDate(DateParam startDateParam, DateParam endDateParam) {
		logger.info("find available classes for startDate = {}, endDate = {}", DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassRepository.findAvailableByStartDateAndEndDate(startDateParam.getValue(), endDateParam.getValue());
	}
	
	public List<OnlineClass> findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate(long courseId, DateParam startDateParam, DateParam endDateParam) {
		// 记录sql执行时间
		long sqlStartTime = System.currentTimeMillis();
		logger.info("find available classes for courseId = {}, startDate = {}, endDate = {}", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		
		List<OnlineClass> onlineClasses = onlineClassRepository.findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate(courseId, startDateParam.getValue(), endDateParam.getValue());
		
		long sqlEndTime = System.currentTimeMillis();
		logger.error("running the sql took " + (sqlStartTime - sqlEndTime) + " millisecond");
		return onlineClasses;
	}
	
	public List<OnlineClass> findAvailablePracticumByStartDateAndEndDate(DateParam startDateParam, DateParam endDateParam) {
		long courseId = courseRepository.findByCourseType(Course.Type.PRACTICUM).getId();
		logger.info("find available classes for courseId = {}, startDate = {}, endDate = {}", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassRepository.findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate(courseId, startDateParam.getValue(), endDateParam.getValue());
	}
	
	public List<OnlineClass> findAvailableByTeacherCertificatedCourseIdStartDateAndEndDateAndLimitParallelByCourseType(long courseId, DateParam startDateParam, DateParam endDateParam, Type type) {
		logger.info("find available classes for courseId = {}, startDate = {}, endDate = {} and limit trail parallel", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		int parallelCount = Redis.Trial.DEFAULT_PARALLEL;
		if(type == Type.TRIAL) {
			parallelCount = getMaxParallelCountFromRedis();
		}
		List<OnlineClass> onlineClasses = onlineClassRepository.findAvailableByTeacherCertificatedCourseIdStartDateAndEndDateAndLimitParallelByCourseType(courseId, startDateParam.getValue(), endDateParam.getValue(), type, parallelCount);
		
		return simplifyOnlineClasses(onlineClasses);
	}
	
	private List<OnlineClass> simplifyOnlineClasses(List<OnlineClass> onlineClasses) {
		List<OnlineClass> returnOnlineClasses = new ArrayList<OnlineClass>();
		if(!onlineClasses.isEmpty()) {
			for(OnlineClass onlineClass : onlineClasses) {
				OnlineClass returnOnlineClass = new OnlineClass();
				returnOnlineClass.setId(onlineClass.getId());
				Teacher teacher = new Teacher();
				teacher.setId(onlineClass.getTeacher().getId());
				teacher.setRealName(onlineClass.getTeacher().getRealName());
				teacher.setName(onlineClass.getTeacher().getName());
				returnOnlineClass.setTeacher(teacher);
				returnOnlineClass.setAbleToEnterClassroomDateTime(onlineClass.getAbleToEnterClassroomDateTime());
				returnOnlineClass.setScheduledDateTime(onlineClass.getScheduledDateTime());
				returnOnlineClass.setSerialNumber(onlineClass.getSerialNumber());
                returnOnlineClass.setStatus(onlineClass.getStatus());
                
                List<Student> simplifiedStudents = new ArrayList<Student>();
                for (Student student : onlineClass.getStudents()){
                	Student simplifiedStudent = new Student();
        			simplifiedStudent.setId(student.getId());
        			simplifiedStudent.setName(student.getName());
        			simplifiedStudent.setEnglishName(student.getEnglishName());
        			simplifiedStudents.add(simplifiedStudent);
                }
                returnOnlineClass.addStudents(simplifiedStudents);
                
				returnOnlineClasses.add(returnOnlineClass);
			}
		}
		return returnOnlineClasses;
	}
	
	public List<OnlineClass> findOpenTeacherRecruitmentByStartDateAndEndDate(DateParam startDateParam, DateParam endDateParam , String type) {
		logger.info("find open teacher recruitment classes for startDate = {}, endDate = {}", DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassRepository.findOpenTeacherRecruitmentByStartDateAndEndDate(startDateParam.getValue(), endDateParam.getValue(),type);
	}
	
	/**
	 * 获取teacher的available time-slot (online-class). -- web front design： 获取所有的online-class,在front-end使用js进行处理。--名称不太符合
	 * @param teacherId
	 * @param startDateParam
	 * @param endDateParam
	 * @return
	 * 
	 */
	public List<OnlineClass> findAvailableByTeacherIdAndStartDateAndEndDate(Long teacherId, DateParam startDateParam, DateParam endDateParam) {
		logger.info("find available classes for teacher={}, startDate = {}, endDate = {}",teacherId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass> onlineClasses= onlineClassRepository.findAvailableByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam.getValue(), endDateParam.getValue());
		if(CollectionUtils.isEmpty(onlineClasses)){
			return onlineClasses;
		}
		List<OnlineClass>list = new ArrayList<OnlineClass>(); 
		getNewOnlineClassList(onlineClasses, list);
		return list;
	}
	
	public List<OnlineClass> findOpenByCourseIdAndStartDateAndEndDate(long courseId, DateParam startDateParam, DateParam endDateParam) {
		logger.info("find open classes for courseId = {}, startDate = {}, endDate = {}", courseId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT),DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		return onlineClassRepository.findOpenByCourseIdAndStartDateAndEndDate(courseId, startDateParam.getValue(), endDateParam.getValue());
	}
	
	public List<OnlineClass> findOpenByCourseIdAndScheduledDateTime(long courseId, DateParam scheduledDateTime) {
		logger.info("find open classes for courseId = {}, scheduledDateTime = {}", courseId, DateTimeUtils.format(scheduledDateTime.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass> onlineClasses = onlineClassRepository.findOpenByCourseIdAndScheduledDateTime(courseId, scheduledDateTime.getValue());
		return simplifyOnlineClasses(onlineClasses);
	}
	
	public OnlineClass findByStudentIdAndLessonId(long studentId, long lessonId) {
		logger.info("find onlineclass for studentId = {}, lessonId = {}", studentId, lessonId);
		return onlineClassRepository.findByStudentIdAndLessonId(studentId, lessonId);
	}
	
	public List<OnlineClass> findByStudentIdAndCourseType(long studentId) {
		Type classType = Type.DEMO;
		logger.info("find onlineclass for studentId = {}, type = {}", studentId, classType);
		return onlineClassRepository.findByStudentIdAndCourseType(studentId, classType, 0, 1);
	}
	
	public Count findBookedLessonNumberThisWeek(long studentId) {
		logger.info("find onlineclass for studentId = {}", studentId);
		return onlineClassRepository.findBookedLessonNumberThisWeek(studentId);
	}
	
	public List<OnlineClassVO> findBookedInterviewByStartDateAndEndDateAndCourseId(DateParam startDateParam, DateParam endDateParam){
		logger.info("find onlineclasses for book interview  ,start time = {},end time = {}", startDateParam.getValue(),endDateParam.getValue());
		long courseId = courseRepository.findByCourseType(Course.Type.TEACHER_RECRUITMENT).getId();
		long pcourseId = courseRepository.findByCourseType(Course.Type.PRACTICUM).getId();

		return OnlineClassHandler.convert2VOList(onlineClassRepository.findBookedByStartDateAndEndDateAndCourseIdNotFullTime(startDateParam.getValue(), endDateParam.getValue(),courseId,pcourseId));
	}
//    public List<OnlineClassVO> findBookedPracticumByStartDateAndEndDateAndCourseId(DateParam startDateParam, DateParam endDateParam){
//    	logger.info("find onlineclasses for book interview  ,start time = {},end time = {}", startDateParam.getValue(),endDateParam.getValue());
//    	long courseId = courseRepository.findByCourseType(Course.Type.PRACTICUM).getId();    	
//		return OnlineClassHandler.convert2VOList(onlineClassRepository.findBookedByStartDateAndEndDateAndCourseIdNotFullTime(startDateParam.getValue(), endDateParam.getValue(),courseId));	
//	}
	
	public Count findBookedLessonNumberNextWeek(long studentId) {
		logger.info("find onlineclass for studentId = {}", studentId);
		return onlineClassRepository.findBookedLessonNumberNextWeek(studentId);
	}
	
	public OnlineClass doBook(final OnlineClass onlineClass) {
		if (onlineClass == null){
			throw new IllegalStateException("OnlineClass can not be null when calling OnlineClass.doBook");
		}
		Course course = onlineClass.getCourse();
		boolean needReleaseLocks = true;
		//首先判断onlineclass是不是REMOVED状态
		OnlineClass temOC = onlineClassRepository.find(onlineClass.getId());
		if((temOC == null || temOC.getStatus() == Status.REMOVED) && onlineClass.getId() !=0){
			throw new OnlineClassNotExistServiceException("OnlineClass not exist or removed when calling OnlineClass.doBook");
		}
		try{
			checkNoMoreClassHours(onlineClass);//Exit booking logic immediately when found out the student no more class hours
			checkTeacherDateTimeAvailable(onlineClass);	//如果此课程为新增，检查是否已存在Available状态的课程
			checkBookDateTime(onlineClass);
            checkTrialParallel(onlineClass);
			if (canGetBookingLocks(onlineClass)){
				checkDateTimeAlreadyBooked(onlineClass);
				checkClassAlreadyBooked(onlineClass);
				setOnlineClass(onlineClass);
				if (course.isSequential()){
					doBookInorder(onlineClass);
				} else{
					doBookDisorder(onlineClass);
				}
				needReleaseLocks = false;
			}else{
				throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
			}
		}finally{
			if (needReleaseLocks){
				releaseLocks(onlineClass);
			}
		}
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_BOOK);
		
		String operation = "Booked the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_BOOK, operation);
		
		// 创建各种评语
		createComments(onlineClass);
		
		//更新leads状态
		updateLeadsWhenTrialOrOther(onlineClass);
		
		sendEmailAndSMSWhenOnlineClassIsBooked(course, onlineClass);
		
		logger.info("Booked the onlineClass.id={} from student={}",onlineClass.getId(), onlineClass.getStudentEnglishNames());
		return onlineClass;	
	}
	
	public OnlineClass doBookForRecruitment(final OnlineClass onlineClass) {
		if (onlineClass == null){
			throw new IllegalStateException("OnlineClass can not be null when calling OnlineClass.doBook");
		}
		Course course = onlineClass.getCourse();
		boolean needReleaseLocks = true;
		try{
			checkNoMoreClassHours(onlineClass);//Exit booking logic immediately when found out the student no more class hours
			checkBookDateTime(onlineClass);
            checkTrialParallel(onlineClass);
			if (canGetBookingLocks(onlineClass)){
				checkDateTimeAlreadyBooked(onlineClass);
				//checkClassAlreadyBooked(onlineClass);
				setOnlineClass(onlineClass);
				if (course.isSequential()){
					doBookInorder(onlineClass);
				} else{
					doBookDisorder(onlineClass);
				}
				needReleaseLocks = false;
			}else{
				throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
			}
		}finally{
			if (needReleaseLocks){
				releaseLocks(onlineClass);
			}
		}
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_BOOK);
		
		String operation = "Booked the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_BOOK, operation);
		
		// 创建各种评语
		createComments(onlineClass);
		
		//更新leads状态
		updateLeadsWhenTrialOrOther(onlineClass);
		
		sendEmailAndSMSWhenOnlineClassIsBooked(course, onlineClass);
		
		logger.info("Booked the onlineClass.id={} from student={}",onlineClass.getId(), onlineClass.getStudentEnglishNames());
		return onlineClass;	
	}
	
	/**
	 * 如果课程为新增，检查指定老师的指定时间段是否已存在AVAILABLE状态的课程
	 * @param onlineClass
	 */
	private void checkTeacherDateTimeAvailable(OnlineClass onlineClass){
		if(StringUtils.isEmpty(onlineClass.getSerialNumber()) && onlineClassRepository.findIsAvailableByTeacherIdAndStartDateAndEndDate(onlineClass.getTeacher().getId(), onlineClass.getScheduledDateTime(), onlineClass.getScheduledDateTime())){
			throw new OnlineClassAlreadyRequestedServiceException("AVAILABLE OnlineClass Already Exist");
		}
	}
	
	/**
	 * 换课进行操作
	 * @param onlineClass
	 * @return
	 */
	public OnlineClass doBookSwitch(final OnlineClass onlineClass,long oldOnlineClassId) {
		//OnlineClass onlineClass = onlineClassRepository.find(onlineClassId);
		OnlineClass oldOnLineClass = onlineClassRepository.find(oldOnlineClassId);
		if (onlineClass == null){
			throw new IllegalStateException("OnlineClass can not be null when calling OnlineClass.doBook");
		}
		//首先判断onlineclass是不是REMOVED状态
		OnlineClass temOC = onlineClassRepository.find(onlineClass.getId());
		if(temOC == null || temOC.getStatus() == Status.REMOVED){
			throw new OnlineClassNotExistServiceException("OnlineClass not exist or removed when calling OnlineClass.doBook");
		}
		Course course = onlineClass.getCourse();
		//boolean needReleaseLocks = true;
		try{
			//checkNoMoreClassHours(onlineClass);//Exit booking logic immediately when found out the student no more class hours
			checkBookDateTime(onlineClass);
            //checkTrialParallel(onlineClass);
			if (canGetBookingSwitchLocks(onlineClass)){
				onlineClass.setStudents(oldOnLineClass.getStudents());
				checkDateTimeAlreadyBookedSwitch(onlineClass);
				checkClassAlreadyBooked(onlineClass);
				setOnlineClass(onlineClass);
				if (course.isSequential()){
					
					onlineClass.setBookDateTime(new Date());
					onlineClass.setBooker(oldOnLineClass.getBooker());
					onlineClass.setLesson(oldOnLineClass.getLesson());
					//onlineClass.setSerialNumber(oldOnLineClass.getSerialNumber());
					onlineClass.setClassroom(oldOnLineClass.getClassroom());
					onlineClass.setMaxStudentNumber(oldOnLineClass.getMaxStudentNumber());
					onlineClass.setMinStudentNumber(oldOnLineClass.getMinStudentNumber());
					//onlineClass.setStudentEnterClassroomDateTime(onlineClass.getStudentEnterClassroomDateTime());
					onlineClass.setAttatchDocumentSucess(oldOnLineClass.isAttatchDocumentSucess());
					onlineClass.setStatus(Status.BOOKED);
					onlineClassRepository.update(onlineClass);
					
					
					oldOnLineClass.setStatus(Status.AVAILABLE);
					oldOnLineClass.setBookDateTime(null);
					oldOnLineClass.setBooker(null);
					oldOnLineClass.setStudents(null);
					oldOnLineClass.setLesson(null);
					//oldOnLineClass.setSerialNumber(oldOnLineClass.getSerialNumber());
					oldOnLineClass.setClassroom(null);
					//oldOnLineClass.setMaxStudentNumber(oldOnLineClass.getMaxStudentNumber());
					//oldOnLineClass.setMinStudentNumber(oldOnLineClass.getMinStudentNumber());
					oldOnLineClass.setStudentEnterClassroomDateTime(null);
					oldOnLineClass.setAttatchDocumentSucess(false);
					onlineClassRepository.update(oldOnLineClass);
					
				} else{
					doBookDisorder(onlineClass);
				}
				//needReleaseLocks = false;
			}else{
				throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
			}
		}finally{
			releaseLocks(onlineClass);
		}
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_BOOK);
		
		String operation = "Booked the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_BOOK, operation);
		
		// 创建各种评语
		createComments(onlineClass);
		
		//更新leads状态
		updateLeadsWhenTrialOrOther(onlineClass);
		
		sendEmailAndSMSWhenOnlineClassIsBooked(course, onlineClass);
		
		logger.info("Booked the onlineClass.id={} from student={}",onlineClass.getId(), onlineClass.getStudentEnglishNames());
		return onlineClass;	
	}
	
	
	private void updateLeadsWhenTrialOrOther(OnlineClass onlineClass) {
		try {
			if (onlineClass != null && onlineClass.getBooker() != null && onlineClass.getStudents() != null && onlineClass.getStudents().size() > 0
					&& onlineClass.getLesson() != null && onlineClass.getLesson().getLearningCycle() != null
					&& onlineClass.getLesson().getLearningCycle().getUnit() != null
					&& onlineClass.getLesson().getLearningCycle().getUnit().getCourse() != null
					&& (onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Type.TRIAL
							|| onlineClass.getLesson().getSerialNumber().equals("A2-U1-LC1-L1") 
							|| onlineClass.getLesson().getSerialNumber().equals("LT1-U1-LC1-L1"))) {
				leadsManageService.updateLeadsStatus(onlineClass.getStudents().get(0).getId(), onlineClass.getBooker().getId(), Leads.Status.BOOKEDTRIAL.getCode());
				
				Student student = onlineClass.getStudents().get(0);
				long trialOnlineClassCount = onlineClassRepository.countByStudentIdAndCourseType(student.getId(), Course.Type.TRIAL);
				if (trialOnlineClassCount <= 1) { // first booked trial class
					long payConfirmedOrdersCount = orderRepository.countPayConfirmedByStudentId(student.getId());
					if (payConfirmedOrdersCount > 0) {
						studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.LEARNING);
					} else {
						studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.TRIAL_SCHEDULED);
					}
				}
			}
		} catch(Exception e) {
			logger.error("error when update leads status",e);
		}
	}
	
	public OnlineClass doCancel(OnlineClass onlineClassFromJson) {
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassFromJson.getId());
		if (onlineClass == null){
			throw new IllegalStateException("OnlineClass can not be null when calling OnlineClass.doCancel");
		}
		onlineClass.setCancelledStudents(onlineClassFromJson.getCancelledStudents());
		
		checkStudents(onlineClass);
		checkCancelDateTime(onlineClass);
		releaseLocks(onlineClass);
		checkOnlineClassIfHasCancelled(onlineClass);
		
		Course course = getCourse(onlineClass);
		
		if (isWithin24Hours(onlineClass)){
			handleCancellationWithin24Hours(onlineClass);
			// 发送课程取消邮件
			sendEmailandSMSWhenOnlineClassIsCancelled(course, onlineClass);
			return onlineClass;
		}
		
		
		List<Student> cancelledStudents = onlineClass.getCancelledStudents();
		
		OnlineClass onlineClassInfoForEmailAndSMS = new OnlineClass();
		List<Student> students = new ArrayList<Student>();
		for(Student student : onlineClass.getStudents()) {
			Student studentForOnlineClass = studentRepository.find(student.getId());
			if(studentForOnlineClass != null) {
				students.add(studentForOnlineClass);
			}			
		}
		
		onlineClassInfoForEmailAndSMS.setSerialNumber(onlineClass.getSerialNumber());
		onlineClassInfoForEmailAndSMS.setStudents(students);
		onlineClassInfoForEmailAndSMS.setTeacher(onlineClass.getTeacher());
		onlineClassInfoForEmailAndSMS.setScheduledDateTime(onlineClass.getScheduledDateTime());
		onlineClassInfoForEmailAndSMS.setLesson(onlineClass.getLesson());
		
		switch(course.getMode()){
		case ONE_ON_ONE:
			if(course.isSequential()){
				Student student = cancelledStudents.get(0);
				LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), course.getId());
				reSchedule(learningProgress, student, course, onlineClass);
			}
			onlineClass.setStudents(null);
			onlineClass.setLesson(null);
			onlineClass.setBackupTeachers(null);
			onlineClass.setStatus(Status.AVAILABLE);
			break;
		case ONE_TO_MANY:
			if(course.isSequential()){
				for(Student student : cancelledStudents){
					LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), course.getId());
					reSchedule(learningProgress, student, course, onlineClass);
				}
			}
			
			onlineClass = onlineClassRepository.find(onlineClass.getId());
			onlineClass.setCancelledStudents(cancelledStudents);
			boolean cancelledToAvailable = false;
			if (onlineClass.getStudents().size() == 0 && onlineClass.getStatus() == Status.OPEN){
				cancelledToAvailable = true;
			}
			
			onlineClass.getStudents().removeAll(cancelledStudents);
			onlineClass.setBackupTeachers(null);
			if (onlineClass.getStudents().size() < onlineClass.getMaxStudentNumber()){
				onlineClass.setStatus(Status.OPEN);
			} 
			if (cancelledToAvailable){
				onlineClass.setStatus(Status.AVAILABLE);
				onlineClass.setStudents(null);
				onlineClass.setLesson(null);
			}
			break;
		}
		
		deleteComments(onlineClass);
		
		onlineClass.setClassroom(null);
		onlineClass.setComments(null);
		User lastEditor = securityService.getCurrentUser();
		onlineClass.setLastEditor(lastEditor);
		onlineClassRepository.update(onlineClass);
		
		sendEmailandSMSWhenOnlineClassIsCancelled(course, onlineClassInfoForEmailAndSMS);
		
        saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_CANCEL);
		//audit log
		String operation = "Cancelled the " + onlineClassInfoForEmailAndSMS.getOnlineClassName();
		if(course.getMode() == Mode.ONE_TO_MANY && onlineClass.getCancelledStudents().size() > 0){
			operation = "Cancelled the students=" + onlineClass.getCancelledStudentEnglishNames() + " from OnlineClass[" + onlineClass.getSerialNumber() +"]";
		}
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CANCEL, operation);
		return onlineClass;
	}
	
	public OnlineClass create(OnlineClass onlineClass) {
		OnlineClass existedOnlineClass = onlineClassRepository.findByTeacherIdAndScheduledDateTime(onlineClass.getTeacher().getId(), onlineClass.getScheduledDateTime());
		if (existedOnlineClass != null){
			throw new OnlineClassAlreadyExistServiceException("Online class already exist.");
		}
		//It does not allow the online class whose scheduled data time is expired be created
		if (onlineClass.getScheduledDateTime().getTime() + 30*60*1000/*30 mins*/< System.currentTimeMillis()){
			onlineClass.setStatus(Status.EXPIRED);
		}
		
		if (onlineClass.getCourse() != null && onlineClass.getCourse().getMode() == Mode.ONE_TO_MANY){
			onlineClass.setStatus(Status.OPEN);
		}
		
		onlineClassRepository.create(onlineClass);
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_CREATE);
		
		//audit log
		String operation = "Created the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CREATE, operation);
		return onlineClass;
	}
	
	public OnlineClass update(OnlineClass onlineClassFromJson) {
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassFromJson.getId());
		if(onlineClass.getTeacherEnterClassroomDateTime() == null){
			onlineClass.setTeacherEnterClassroomDateTime(onlineClassFromJson.getTeacherEnterClassroomDateTime());
		}
		if(onlineClass.getStudentEnterClassroomDateTime() == null){
			onlineClass.setStudentEnterClassroomDateTime(onlineClassFromJson.getStudentEnterClassroomDateTime());
		}
		onlineClass.setClassroom(onlineClassFromJson.getClassroom());
		User lastEditor = securityService.getCurrentUser();
		onlineClass.setLastEditor(lastEditor);
		onlineClassRepository.update(onlineClass);
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_UPDATE);
		
		//audit log
		String operation = "Update the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_UPDATE, operation);
		
		return onlineClass;
	}
	
	public Response changeTeacher(OnlineClass onlineClassFromJson){
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassFromJson.getId());
		onlineClass.setTeacher(onlineClassFromJson.getTeacher());
		onlineClass.setFinishType(onlineClassFromJson.getFinishType());
		onlineClass.setSubstituteTeacher(onlineClassFromJson.getSubstituteTeacher());
		if (onlineClass.getStatus() != Status.OPEN && onlineClass.getStatus() != Status.BOOKED){
			throw new IllegalStateException("onlineClass's status must be open, booked");
		}	
		
		Date scheduledDateTime = onlineClass.getScheduledDateTime();
		Teacher substituteTeacher = teacherRepository.find(onlineClass.getSubstituteTeacher().getId());
		FinishType onlineClassType = onlineClass.getFinishType();
		
		List<OnlineClass> onlineClasses = onlineClassRepository.findByTeacherIdAndScheduledDateTimeANDStatues(substituteTeacher.getId(), scheduledDateTime, Status.AVAILABLE);
		OnlineClass substituteOnlineClass = null;
		
	    if (!onlineClasses.isEmpty()){
	    	substituteOnlineClass = onlineClasses.get(0);
	    } else {
	    	if (onlineClassRepository.hasBookedAlreadyByTeacherIdAndScheduledDateTime(substituteTeacher.getId(), scheduledDateTime)){
	    		throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the teacher.");
	    	};
	    	substituteOnlineClass = new OnlineClass();
	    	substituteOnlineClass.setScheduledDateTime(scheduledDateTime);
	    	substituteOnlineClass.setTeacher(substituteTeacher);
	    }
	    
	    substituteOnlineClass.setBookDateTime(onlineClass.getBookDateTime());
	    substituteOnlineClass.setBooker(onlineClass.getBooker());
	    substituteOnlineClass.setStudents(new ArrayList<Student>(onlineClass.getStudents()));
	    substituteOnlineClass.setLesson(onlineClass.getLesson());
	    substituteOnlineClass.setSerialNumber(onlineClass.getSerialNumber());
	    substituteOnlineClass.setClassroom(onlineClass.getClassroom());
	    substituteOnlineClass.setMaxStudentNumber(onlineClass.getMaxStudentNumber());
	    substituteOnlineClass.setMinStudentNumber(onlineClass.getMinStudentNumber());
	    substituteOnlineClass.setStudentEnterClassroomDateTime(onlineClass.getStudentEnterClassroomDateTime());
	    substituteOnlineClass.setAttatchDocumentSucess(onlineClass.isAttatchDocumentSucess());
	    String substituteOnlineClassComments = "normal changed class";
	    if (isWithin24Hours(substituteOnlineClass)){
	    	substituteOnlineClassComments = "short notice class";
	    	substituteOnlineClass.setShortNotice(true);
	    }
	    String orignalOnlineClassComments = null;
	    switch (onlineClassType){
	    case TEACHER_NO_SHOW:
	    	 orignalOnlineClassComments =  "change teacher";
	    	 break;
	    case TEACHER_IT_PROBLEM:
	    	 orignalOnlineClassComments =  "change teacher";
	    	 break;
	    case TEACHER_CANCELLATION:
	    	 orignalOnlineClassComments =  "Teacher cancelled";
	    	 break;
	    case SYSTEM_PROBLEM:
	    	 orignalOnlineClassComments =  "change teacher";
	    	 break;
		default:
			 break;
	    }
	    substituteOnlineClass.setComments(substituteOnlineClassComments);
	    substituteOnlineClass.setStatus(onlineClass.getStatus());
	    onlineClassRepository.create(substituteOnlineClass);
	    
	    //创建各种上课相关的comments
	    createComments(substituteOnlineClass);
	    
	    onlineClass.setComments(orignalOnlineClassComments);
//	    if (onlineClassType == FinishType.TEACHER_CANCELLATION){
//	    	onlineClass.setStatus(Status.CANCELED);
//	    }else{
//	    	onlineClass.setStatus(Status.FINISHED);
//	    }
	    onlineClass.setStatus(Status.INVALID);
	    onlineClass.setCanUndoFinish(false);
	    
	    User lastEditor = securityService.getCurrentUser();
		onlineClass.setLastEditor(lastEditor);
		onlineClassRepository.update(onlineClass);
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_CHANGE_TEACHER);
		
		//audit
		String operation = "Change the Teacher from " + onlineClass.getTeacher().getName() + " to "  + substituteOnlineClass.getTeacher().getName() + " for OnlineClass[ " + onlineClass.getSerialNumber() + "]";
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CHANGE_TEACHER, operation);
		
		return new Response(HttpStatus.OK.value());
	}
	
	public Response delete(long id) {
		OnlineClass onlineClass = onlineClassRepository.find(id);
		if (onlineClass != null && !onlineClass.getStudents().isEmpty()){
			throw new OnlineClassAlreadyBookedByOthersServiceException("The class is already booked by others.");
		}
		//如果是别人的backup，移除关系
		//updateBackupDuty(onlineClass);
		
		//只标记，不删除
		if(onlineClass != null) {
			onlineClass.setStatus(Status.REMOVED);
			User lastEditor = securityService.getCurrentUser();
			onlineClass.setLastEditor(lastEditor);
			onlineClassRepository.update(onlineClass);
		}
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_REMOVE);
		
		//audit
		String operation = "Remove the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_REMOVE, operation);
		
		return new Response(HttpStatus.OK.value());
	}
	
	public Response doUndoFinish(OnlineClass onlineClassFromJson) {
		checkOnlineClassParameter(onlineClassFromJson);
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassFromJson.getId());
		checkDateTimeAlreadyBooked(onlineClass);
		switch (onlineClass.getFinishType()){
		case AS_SCHEDULED:
			undoAsScheduled(onlineClass);
			break;
		case STUDENT_NO_SHOW:
			undoStudentNoShow(onlineClass);
			break;
		case STUDENT_IT_PROBLEM:
			undoStudentITProblem(onlineClass);
			break;
		case TEACHER_NO_SHOW:
		case TEACHER_IT_PROBLEM:
			undoTeacherProblem(onlineClass);
			break;
		case SYSTEM_PROBLEM:
			undoSystemProblem(onlineClass);
			break;
		default:
			break;
		}
		onlineClass.setStatus(Status.BOOKED);
		onlineClass.setFinishType(null);
		onlineClass.setCanUndoFinish(false);
		onlineClass.setAsScheduledStudents(null);
		onlineClass.setItProblemStudents(null);
		onlineClass.setNoShowStudents(null);
		User lastEditor = securityService.getCurrentUser();
		onlineClass.setLastEditor(lastEditor);
		onlineClassRepository.update(onlineClass);
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_UNDO_FINISH);
		
		//aduit
		String operation = "Undo the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_UNDO_FINISH, operation);
		
		return new Response(HttpStatus.OK.value());
	}
	/**
	 * 2015-07-17 公开课的结束
	 * @param onlineClass4OpenClass
	 * @return
	 */
	public OnlineClass doFinishOpenClass(OnlineClass onlineClass4OpenClass) {
		OnlineClass onlineClass = onlineClassRepository.find(onlineClass4OpenClass.getId());
		onlineClass.setStatus(Status.FINISHED);
		return onlineClassRepository.update(onlineClass);
	}
	
	public OnlineClass doFinish(OnlineClass onlineClassFromJson) {	
		checkOnlineClassParameter(onlineClassFromJson);
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassFromJson.getId());
		if (onlineClass == null) {
			throw new BadRequestServiceException("can not find onlineClass");
		}
		onlineClass.setCourse(this.getCourse(onlineClass));
        if (onlineClass.getStatus() == Status.FINISHED){
            throw new IllegalStateException("The onlineClass=" + onlineClass.getOnlineClassName() + " already finished");
        }
		onlineClass.setFinishType(onlineClassFromJson.getFinishType());
		onlineClass.setStatus(Status.FINISHED);

		switch (onlineClass.getFinishType()){
			case AS_SCHEDULED:
				handleAsScheduled(onlineClass);
				sendSMSandEmailWhenOnlineClassIsFinshedAsScheduled(onlineClass);
				break;
			case STUDENT_NO_SHOW:
				handleStudentProblem(onlineClass);
				break;
			case STUDENT_IT_PROBLEM:
				handleStudentProblem(onlineClass);
				break;
			case TEACHER_NO_SHOW:
				handleTeacherProblem(onlineClass);
				sendToEducationTeacherNoShowEmail(onlineClass);
				break;
			case TEACHER_IT_PROBLEM:
				handleTeacherProblem(onlineClass);
				//TODO mail
				break;
			case SYSTEM_PROBLEM:
				handleSystemProblem(onlineClass);
		    default:
			     break;    
		}
		//更新undo finish 条件
		updateUndoCondition(onlineClass);
		//更新课程记录
		User lastEditor = securityService.getCurrentUser();
		onlineClass.setLastEditor(lastEditor);
		onlineClassRepository.update(onlineClass);
		//处理老师工资
		createNewPayItem(onlineClass);
		
		saveOnlineClassOperation(onlineClass, Category.ONLINE_CLASS_FINISH); 
		
		//写审计日志
		String operation = "Finished the " + onlineClass.getOnlineClassName();
		securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_FINISH, operation);
		
		//释放Moxtra用户资源
		moxtraUserService.unassign(onlineClass.getTeacher().getId());
		for(Student student : onlineClass.getStudents()) {
			moxtraUserService.unassign(student.getId());
		}
		
		return onlineClass;
	}
	
	public List<OnlineClass> list(List<Long> courseIds, String searchTeacherText, String searchStudentText, String searchSalesText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Status status, FinishType finishType, Boolean shortNotice, Boolean hasClassroom, Integer start, Integer length) {
		logger.info("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.", searchTeacherText, searchStudentText, searchSalesText, scheduledDateTimeFrom, scheduledDateTimeTo, status, finishType, hasClassroom, start, length);
		List<OnlineClass> onlineClasses = onlineClassRepository.list(courseIds, searchTeacherText, searchStudentText, searchSalesText, scheduledDateTimeFrom, scheduledDateTimeTo, status, finishType, shortNotice, hasClassroom, start, length);
		if(CollectionUtils.isEmpty(onlineClasses)){
			return onlineClasses;
		}
		List<OnlineClass>list = new ArrayList<OnlineClass>(); 
		getNewOnlineClassList(onlineClasses, list);
		return list;
	}

	private void getNewOnlineClassList(List<OnlineClass> onlineClasses,
			List<OnlineClass> list) {
		for (OnlineClass os: onlineClasses) {
			OnlineClass onlineClass = new OnlineClass();
			if(null!=os.getLesson()){
				if(os.getLesson().getLearningCycle().getUnit().getCourse().getMode()==Mode.ONE_TO_MANY){
					if(os.getLesson().getLearningCycle().getUnit().getCourse().getType()==Type.OPEN1){
						copyTo(os, onlineClass);
					}else{
						onlineClass=os;
					}
				}else{
					onlineClass=os;
				}
			}else{
				onlineClass=os;
			}
			list.add(onlineClass);
		}
	}

	private void copyTo(OnlineClass os, OnlineClass onlineClass) {
		onlineClass.setId(os.getId());
		onlineClass.setSerialNumber(os.getSerialNumber());
		onlineClass.setLesson(os.getLesson());
		onlineClass.setBooker(os.getBooker());
		onlineClass.setBookDateTime(os.getBookDateTime());
		onlineClass.setLastEditor(os.getLastEditor());
		onlineClass.setLastEditDateTime(os.getLastEditDateTime());
		onlineClass.setTeacher(os.getTeacher());
		onlineClass.setBackupTeachers(os.getBackupTeachers());
		onlineClass.setAbleToEnterClassroomDateTime(os.getAbleToEnterClassroomDateTime());
		onlineClass.setTeacherEnterClassroomDateTime(os.getTeacherEnterClassroomDateTime());
		onlineClass.setScheduledDateTime(os.getScheduledDateTime());
		onlineClass.setStatus(os.getStatus());
		onlineClass.setFinishType(os.getFinishType());
		onlineClass.setClassroom(os.getClassroom());
		onlineClass.setDbyDocument(os.getDbyDocument());
		onlineClass.setWxtCourseId(os.getWxtCourseId());
		onlineClass.setConsumeClassHour(os.isConsumeClassHour());
		onlineClass.setMinStudentNumber(os.getMinStudentNumber());
		onlineClass.setMaxStudentNumber(os.getMaxStudentNumber());
		onlineClass.setArchived(os.isArchived());
		onlineClass.setCanUndoFinish(os.isCanUndoFinish());
		onlineClass.setShortNotice(os.isShortNotice());
		onlineClass.setBackup(os.isBackup());
		onlineClass.setAttatchDocumentSucess(os.isAttatchDocumentSucess());
		onlineClass.setPaidTrail(os.getPaidTrail());
		onlineClass.setPayrollItem(os.getPayrollItem());
		onlineClass.setFiremanToTeacherComment(os.getFiremanToTeacherComment());
		onlineClass.setDemoReport(os.getDemoReport());
		onlineClass.setComments(os.getComments());
		onlineClass.setUnitPrice(os.getUnitPrice());
		onlineClass.setCourseMode(os.getCourseMode());
		long count = openClassRepository.countOpenClassStudentById(os.getId());
		onlineClass.setStudentCount(count);
		/*onlineClass.setStudents(os.getStudents());
		onlineClass.setAsScheduledStudents(os.getAsScheduledStudents());
		onlineClass.setItProblemStudents(os.getItProblemStudents());
		onlineClass.setNoShowStudents(os.getNoShowStudents());
		onlineClass.setTeacherComments(os.getTeacherComments());
		onlineClass.setFiremanToStudentComments(os.getFiremanToStudentComments());*/
	}
	

	public Count count (List<Long> courseIds, String searchTeacherText, String searchStudentText, String searchSalesText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Status status, FinishType finishType, Boolean shortNotice, Boolean hasClassroom) {
		logger.info("list course with params: searchTeacherText = {}, searchStudentText = {}, searchSalesText={}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}.", searchTeacherText, searchStudentText, searchSalesText, scheduledDateTimeFrom, scheduledDateTimeTo, status, finishType, hasClassroom);
		Count count = new Count(onlineClassRepository.count(courseIds, searchTeacherText, searchStudentText, searchSalesText, scheduledDateTimeFrom, scheduledDateTimeTo, status, finishType, shortNotice, hasClassroom));
		return count;
	}
	
	public List<OnlineClass> listForComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String finishType, Integer start, Integer length, Boolean isTeacherCommentsEmpty, Boolean isFiremanCommentsEmpty, String searchOnlineClassText) {
		logger.info("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, finishType = {}, start = {}, length = {}, searchOnlineClassText = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, searchOnlineClassText, start, length);
		return onlineClassRepository.listForComments(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, start, length,isTeacherCommentsEmpty,isFiremanCommentsEmpty,searchOnlineClassText);
	}
	
	public List<OnlineClass> listForFireman(List<Long> courseIds, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, List<Status> statusList, int start, int length) {
		logger.info("list course with params: scheduleFrom = {}, scheduleTo = {}, start = {}, length = {}.", scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
		
		List<OnlineClass> listForFireman = new ArrayList<OnlineClass>();
		listForFireman = onlineClassRepository.listForFireman(courseIds, scheduledDateTimeFrom, scheduledDateTimeTo, statusList, start, length);
		
		List<OnlineClass> simplifiedListForFireman = new ArrayList<OnlineClass>();
		if(listForFireman.isEmpty() == false){
			for(OnlineClass onlineClass : listForFireman){
				OnlineClass simplifiedOnlineClass = new OnlineClass();
				
				simplifiedOnlineClass.setStatus(onlineClass.getStatus());
				simplifiedOnlineClass.setScheduledDateTime(onlineClass.getScheduledDateTime());
				simplifiedOnlineClass.setSerialNumber(onlineClass.getSerialNumber());
				simplifiedOnlineClass.setStudents(onlineClass.getStudents());
				simplifiedOnlineClass.setTeacher(onlineClass.getTeacher());
				simplifiedOnlineClass.setBackupTeacherNames(onlineClass.getBackupTeacherNames());
				simplifiedOnlineClass.setClassroom(onlineClass.getClassroom());
				simplifiedOnlineClass.setId(onlineClass.getId());
				simplifiedOnlineClass.setAttatchDocumentSucess(onlineClass.isAttatchDocumentSucess());
				simplifiedOnlineClass.setTeacherEnterClassroomDateTime(onlineClass.getTeacherEnterClassroomDateTime());
				simplifiedOnlineClass.setStudentEnterClassroomDateTime(onlineClass.getStudentEnterClassroomDateTime());
				simplifiedOnlineClass.setCanUndoFinish(onlineClass.isCanUndoFinish());
				simplifiedOnlineClass.setAttatchDocumentSucess(onlineClass.isAttatchDocumentSucess());
				
				Course course = onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
				Course simplifiedCourse = new Course();
				simplifiedCourse.setId(course.getId());
				simplifiedCourse.setName(course.getName());
				simplifiedOnlineClass.setCourse(simplifiedCourse);
				
				simplifiedListForFireman.add(simplifiedOnlineClass);
			}
			
		}	
		return simplifiedListForFireman;
	}
	
	public List<OnlineClass> listOnlineClassesAndPayrollItems(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, int start, int length) {
		logger.info("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
		return onlineClassRepository.listOnlineClassesAndPayrollItems(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
	}

	public Count countOnlineClassesAndPayrollItems(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, int start, int length) {
		logger.info("court course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
	    Count count = new Count(onlineClassRepository.countOnlineClassesAndPayrollItems(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length));
	    return count;
	}

	public FloatWrapper getSalary(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom,
			 DateTimeParam scheduledDateTimeTo, int start, int length) {
		logger.info(
				"list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, status = {}, finishType = {}, hasClassroom = {}, start = {}, length = {}.",
				searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
        FloatWrapper salary = new FloatWrapper(onlineClassRepository.getSalary(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom,
                scheduledDateTimeTo, start, length));
		return salary;
	}

	public Count countForComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String finishType, Boolean isTeacherCommentsEmpty, Boolean isFiremanCommentsEmpty, String searchOnlineClassText) {
		logger.info("list course with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, finishType = {}, searchOnlineClassText = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType, searchOnlineClassText);
		Count count = new Count(onlineClassRepository.countForComments(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, finishType,isTeacherCommentsEmpty,isFiremanCommentsEmpty,searchOnlineClassText));
		return count;
	}
	
	public List<OnlineClass> listForDemoReport(String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String lifeCycle, Long salesId,String searchStatus,String finishType, Integer start, Integer length) {
		logger.info("list online class with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, lifeCycle = {}, salesId = {},searchStatus = {},finishType = {},start = {}, length = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, lifeCycle, salesId,searchStatus, finishType,start, length);
		return onlineClassRepository.listForDemoReport(searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, lifeCycle, salesId,searchStatus, finishType,start, length);
	}

	public Count countForDemoReport(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String lifeCycle, Long salesId,String searchStatus,String finishType) {
		logger.info("list online class with params: searchTeacherText = {}, searchStudentText = {}, scheduleFrom = {}, scheduleTo = {}, lifeCycle = {}, salesId = {},searchStatus = {},finishType = {}.", searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, lifeCycle, salesId,searchStatus,finishType);
		Count count = new Count(onlineClassRepository.countForDemoReport(searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, lifeCycle, salesId,searchStatus,finishType));
		return count;
	}
	
	public OnlineClass createClassroom(OnlineClass onlineClass) {
		User user = ((CustomizedPrincipal) AppContext.getPrincipal()).getUser();
		return createDBYClassroom(onlineClassRepository.find(onlineClass.getId()), user);
	}
	
	
	public OnlineClass findNextShouldTakeClass(long studentId) {
		logger.info("find lessons for studentId = {}", studentId);
		return onlineClassRepository.findNextShouldTakeClass(studentId);
	}
	
	public OnlineClass findLastedFinishedClassByStudentId(long studentId) {
		logger.info("find lasted finished class for studentId = {}", studentId);
		return onlineClassRepository.findLastedFinishedClassByStudentId(studentId);
	}
	
	public OnlineClass findFirstFinishedClassByStudentId(long studentId) {
		logger.info("find first finished class for studentId = {}", studentId);
		return onlineClassRepository.findFirstFinishedClassByStudentId(studentId);
	}
	
	public List<OnlineClass> findByUnitId(long studentId, long unitId) {
		logger.info("find classes for unitId = {}", unitId);
		return onlineClassRepository.findByUnitId(studentId, unitId);
	}
	
	public List<OnlineClass> findByStudentId(long studentId) {
		logger.info("find classes for studentId = {}", studentId);
		return onlineClassRepository.findByStudentId(studentId);
	}
	
	public OnlineClassFinishCountView countByStudentOrTeacherIdAndStartDateEndDateForAttendance(String teacherId, String studentId, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo,  FinishType finishType){
		OnlineClassFinishCountView view = new OnlineClassFinishCountView();
//		if (finishType != null) {
//			long asSchedule = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, finishType, scheduledDateTimeFrom, scheduledDateTimeTo);
//			
//		} else {	
//			long asSchedule = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.AS_SCHEDULED, scheduledDateTimeFrom, scheduledDateTimeTo);
//			long teacherNoShowCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.TEACHER_NO_SHOW, scheduledDateTimeFrom, scheduledDateTimeTo);
//			long teacherItProblemCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.TEACHER_IT_PROBLEM, scheduledDateTimeFrom, scheduledDateTimeTo);
//			long studentItProblemCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.STUDENT_IT_PROBLEM, scheduledDateTimeFrom, scheduledDateTimeTo);
//			long studentNoShowCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.STUDENT_NO_SHOW, scheduledDateTimeFrom, scheduledDateTimeTo);
//
//			view.setAsScheduledCount(asSchedule);
//			view.setStudentItProblemCount(studentItProblemCount);
//			view.setStudentNoShowCount(studentNoShowCount);
//			view.setTeacherItProblemCount(teacherItProblemCount);
//			view.setTeacherNoShowCount(teacherNoShowCount);
//		}
		long asSchedule = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.AS_SCHEDULED, scheduledDateTimeFrom, scheduledDateTimeTo);
		long teacherNoShowCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.TEACHER_NO_SHOW, scheduledDateTimeFrom, scheduledDateTimeTo);
		long teacherItProblemCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.TEACHER_IT_PROBLEM, scheduledDateTimeFrom, scheduledDateTimeTo);
		long studentItProblemCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.STUDENT_IT_PROBLEM, scheduledDateTimeFrom, scheduledDateTimeTo);
		long studentNoShowCount = onlineClassRepository.countByStudentIdAndFinishTypeForAttendence(teacherId,studentId, FinishType.STUDENT_NO_SHOW, scheduledDateTimeFrom, scheduledDateTimeTo);

		view.setAsScheduledCount(asSchedule);
		view.setStudentItProblemCount(studentItProblemCount);
		view.setStudentNoShowCount(studentNoShowCount);
		view.setTeacherItProblemCount(teacherItProblemCount);
		view.setTeacherNoShowCount(teacherNoShowCount);
		return view;	 
	}
	
	public List<OnlineClass> listByStudentOrTeacherIdAndStartDateEndDateForAttendance(String teacherId, String studentId,
			DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, FinishType finishType,int start, int length) {
		return onlineClassRepository.listByStudentIdAndFinishTypeForAttendence(teacherId, studentId, finishType, scheduledDateTimeFrom,scheduledDateTimeTo, start, length);

	}
	
	public OnlineClass sendStarById(long onlineClassId) {
		logger.info("send star to on line class = {}", onlineClassId);
		OnlineClass onlineClass = onlineClassRepository.find(onlineClassId);
		//TODO Wang Qing 发星星
//		if(onlineClass.getStars() < 5){
//			onlineClass.setStars(onlineClass.getStars() + 1);
//			onlineClassAccessor.update(onlineClass);
//		}
		return onlineClass;
	}
	
	private boolean isWithin24Hours(final OnlineClass onlineClass){
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, 24);
		Date scheduledDateTimeToBeCanceled = onlineClass.getScheduledDateTime();
		if (scheduledDateTimeToBeCanceled.before(now.getTime())) {
			return true;
		}
		return false;
	}
	
	private void handleCancellationWithin24Hours(final OnlineClass onlineClass){
		onlineClass.setFinishType(FinishType.STUDENT_NO_SHOW);
		onlineClass.setStudents(onlineClass.getCancelledStudents());
		
		Course course = getCourse(onlineClass);
		
		OnlineClass newOnlineClass = new OnlineClass();
		newOnlineClass.setStatus(Status.AVAILABLE);
		//TODO opt later - Leon
		if (course.getType() == Type.PRACTICUM || course.getType() == Type.TEACHER_RECRUITMENT){
			newOnlineClass.setStatus(Status.OPEN);
			newOnlineClass.setMaxStudentNumber(1);
			newOnlineClass.setMinStudentNumber(0);
			newOnlineClass.setLesson(onlineClass.getLesson());
		}
		newOnlineClass.setScheduledDateTime(onlineClass.getScheduledDateTime());
		newOnlineClass.setTeacher(onlineClass.getTeacher());
		newOnlineClass.setComments("Released by canceled within 24hours");
		onlineClassRepository.create(newOnlineClass);
		
		doFinish(onlineClass);
	}
	
	//delete related comments;
	private void deleteComments(final OnlineClass onlineClass){
		List<TeacherComment> teacherComments = teacherCommentRepository.findByOnlineClassId(onlineClass.getId());
		for (TeacherComment teacherComment : teacherComments){
			teacherCommentRepository.delete(teacherComment);
		}
		
		List<EducationalComment> educationalComments = educationalCommentRepository.findByOnlineClassId(onlineClass.getId());
		for (EducationalComment educationalComment : educationalComments){
			educationalCommentRepository.delete(educationalComment);
		}
		
		List<FiremanToStudentComment> firemanToStudentComments = firemanToStudentCommentRepository.findByOnlineClassId(onlineClass.getId());
		for (FiremanToStudentComment firemanToStudentComment : firemanToStudentComments){
			firemanToStudentCommentRepository.delete(firemanToStudentComment);
		}
		
		FiremanToTeacherComment firemanToTeacherComment = firemanToTeacherCommentRepository.findByOnlineClassIdAndTeacherId(onlineClass.getId(), onlineClass.getTeacher().getId());
		if(firemanToTeacherComment != null) {
			firemanToTeacherCommentRepository.delete(firemanToTeacherComment);
		}
		
		DemoReport demoReportComment = demoReportCommentRepository.findByOnlineClassId(onlineClass.getId());
		if(demoReportComment != null) {
			demoReportCommentRepository.delete(demoReportComment);
		}
		onlineClass.setFiremanToStudentComments(null);
		onlineClass.setFiremanToTeacherComment(null);
		onlineClass.setTeacherComments(null);
		onlineClass.setDemoReport(null);
	}
	
	private void createComments(OnlineClass bookedOnlineClass){
		for(Student theStudent: bookedOnlineClass.getStudents()) {
			TeacherComment teacherComment = teacherCommentRepository.findByOnlineClassIdAndStudentId(bookedOnlineClass.getId(), theStudent.getId());
			if(teacherComment == null) {
				teacherComment = new TeacherComment();
				teacherComment.setOnlineClass(bookedOnlineClass);
				teacherComment.setStudent(theStudent);
				teacherComment.setTeacher(bookedOnlineClass.getTeacher());
				teacherComment.setEmpty(true);
				teacherCommentRepository.create(teacherComment);
			}
			
			EducationalComment educationalComment = educationalCommentRepository.findByOnlineClassIdAndStudentId(bookedOnlineClass.getId(), theStudent.getId());
			if(educationalComment == null) {
				educationalComment = new EducationalComment();
				educationalComment.setOnlineClass(bookedOnlineClass);
				educationalComment.setStudent(theStudent);
				educationalComment.setEmpty(true);
				educationalCommentRepository.create(educationalComment);
			}
			
			FiremanToStudentComment firemanToStudentComment = firemanToStudentCommentRepository.findByOnlineClassIdAndStudentId(bookedOnlineClass.getId(), theStudent.getId());
			if(firemanToStudentComment == null) {
				firemanToStudentComment = new FiremanToStudentComment();
				firemanToStudentComment.setOnlineClass(bookedOnlineClass);
				firemanToStudentComment.setStudent(theStudent);
				firemanToStudentComment.setEmpty(true);
				firemanToStudentCommentRepository.create(firemanToStudentComment);
			}
		}
		
		FiremanToTeacherComment firemanToTeacherComment = firemanToTeacherCommentRepository.findByOnlineClassIdAndTeacherId(bookedOnlineClass.getId(), bookedOnlineClass.getTeacher().getId());
		if(firemanToTeacherComment == null) {
			firemanToTeacherComment = new FiremanToTeacherComment();
			firemanToTeacherComment.setOnlineClass(bookedOnlineClass);
			firemanToTeacherComment.setTeacher(bookedOnlineClass.getTeacher());
			firemanToTeacherComment.setEmpty(true);
			firemanToTeacherCommentRepository.create(firemanToTeacherComment);
		}
		
		Course course = getCourse(bookedOnlineClass);
		if (course != null && (Type.DEMO == course.getType() || Type.ASSESSMENT2 == course.getType())) {
            logger.info("Create DemoReport,onlineClassID={}",bookedOnlineClass.getId());
			DemoReport demoReportComment = demoReportCommentRepository.findByOnlineClassId(bookedOnlineClass.getId());
			if (bookedOnlineClass.getLesson() != null && bookedOnlineClass.getLesson().getSerialNumber() != null) {
				String bookedOnlineClassLessSerialNumber = bookedOnlineClass.getLesson().getSerialNumber();
				if (bookedOnlineClassLessSerialNumber.equals("DEMO1-U1-LC1-L1") || bookedOnlineClassLessSerialNumber.equals("A1-U1-LC1-L1") || bookedOnlineClassLessSerialNumber.equals("A2-U1-LC1-L1")) {
					if (demoReportComment == null) {
						demoReportComment = new DemoReport();
						demoReportComment.setOnlineClass(bookedOnlineClass);
						demoReportComment.setLifeCycle(LifeCycle.UNFINISHED);
						bookedOnlineClass.setDemoReport(demoReportComment);
						demoReportComment.setLevel(DemoReport.Level.L1U0);
						demoReportComment.setL1(Answer.NO);
						demoReportComment.setL2(Answer.NO);
						demoReportComment.setL3(Answer.NO);
						demoReportComment.setL4(Answer.NO);
						demoReportComment.setL5(Answer.NO);
						demoReportComment.setL6(Answer.NO);
						demoReportComment.setL7(Answer.NO);
						demoReportComment.setS1(Answer.NO);
						demoReportComment.setS2(Answer.NO);
						demoReportComment.setS3(Answer.NO);
						demoReportComment.setS4(Answer.NO);
						demoReportComment.setS5(Answer.NO);
						demoReportComment.setS6(Answer.NO);
						demoReportComment.setS7(Answer.NO);
						demoReportComment.setS8(Answer.NO);
						demoReportComment.setS9(Answer.NO);
						demoReportComment.setS10(Answer.NO);
						demoReportComment.setS11(Answer.NO);
						demoReportComment.setS12(Answer.NO);
						demoReportComment.setS13(Answer.NO);
						demoReportComment.setS14(Answer.NO);
						demoReportComment.setS15(Answer.NO);
						demoReportComment.setR1(Answer.NO);
						demoReportComment.setR2(Answer.NO);
						demoReportComment.setR3(Answer.NO);
						demoReportComment.setR4(Answer.NO);
						demoReportComment.setR5(Answer.NO);
						demoReportComment.setR6(Answer.NO);
						demoReportComment.setR7(Answer.NO);
						demoReportComment.setR8(Answer.NO);
						demoReportComment.setR9(Answer.NO);
						demoReportComment.setR10(Answer.NO);
						demoReportComment.setR11(Answer.NO);
						demoReportComment.setR12(Answer.NO);
						demoReportComment.setR13(Answer.NO);
						demoReportComment.setR14(Answer.NO);
						demoReportComment.setR15(Answer.NO);
						demoReportComment.setR16(Answer.NO);
						demoReportComment.setR17(Answer.NO);
						demoReportComment.setM1(Answer.NO);
						demoReportComment.setM2(Answer.NO);
						demoReportComment.setM3(Answer.NO);
						demoReportComment.setM4(Answer.NO);
						demoReportComment.setM5(Answer.NO);
						demoReportComment.setM6(Answer.NO);
						demoReportComment.setM7(Answer.NO);
						demoReportComment.setM8(Answer.NO);
						demoReportComment.setM9(Answer.NO);
						demoReportComment.setM10(Answer.NO);
						demoReportComment.setTeacher(bookedOnlineClass.getTeacher());
						if (bookedOnlineClass.getStudents().size() > 0) {
							demoReportComment.setStudent(bookedOnlineClass.getStudents().get(0));
						}
						demoReportCommentRepository.create(demoReportComment);
					}
				}
			}

		}
		
	}
	
	
	private void updateUndoCondition(final OnlineClass onlineClass){
		onlineClass.setCanUndoFinish(true);
		Course course = getCourse(onlineClass);
		List<Student> students = onlineClass.getStudents();
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			if (course.isSequential()){
				List<OnlineClass> onlineClasses = onlineClassRepository.findByCourseIdAndStudentIdAndLessonSequence(course.getId(), student.getId(), onlineClass.getLesson().getSequence());
				for(OnlineClass prevOnlineClass : onlineClasses){
					if (prevOnlineClass.getId() != onlineClass.getId()){
						prevOnlineClass.setCanUndoFinish(false);
						User lastEditor = securityService.getCurrentUser();
						onlineClass.setLastEditor(lastEditor);
						onlineClassRepository.update(prevOnlineClass);
					}
					
				}
			}	
			break;
		case ONE_TO_MANY:
			for(Student tmpStudent : students){
				if(course.isSequential()){
					List<OnlineClass> prevOnlineClasses = onlineClassRepository.findByCourseIdAndStudentIdAndLessonSequence(course.getId(), tmpStudent.getId(), onlineClass.getLesson().getSequence());
					for(OnlineClass prevOnlineClass : prevOnlineClasses){
						if (prevOnlineClass.getId() != onlineClass.getId()){
							prevOnlineClass.setCanUndoFinish(false);
							User lastEditor = securityService.getCurrentUser();
							onlineClass.setLastEditor(lastEditor);
							onlineClassRepository.update(prevOnlineClass);
						}
					}
				}
			}	
			break;
		}
	}
	
	private boolean attacheTrailDocForClassroom(OnlineClass onlineClass, Lesson lesson) {
		boolean bAttachSuccess = false;
		//
		String docSetting = lesson.getDbyDocument();
		String [] docs = docSetting.split(",");
		// 反序attach --
		int nLen = docs.length;
		if (nLen<1) {
			return false;
		}
//		for (String doc : docs) {
		for (int nIndex = nLen -1; nIndex>=0; nIndex--) {
			String doc = docs[nIndex];
			AttachDocumentResult attachDocumentResult = DBYAPI.attachDocument(onlineClass.getClassroom(), doc);
			if(attachDocumentResult.isSuccess()) {
				bAttachSuccess = true;
				onlineClass.setDbyDocument(onlineClass.getLesson().getDbyDocument());
				securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_CREATE, "attach trial doc for classroom: " + onlineClass.getClassroom());
			    onlineClass.setAttatchDocumentSucess(true);
			}else {
				securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, "Fail to attach DuoBeiYun classroom document for trial classroom id: " + onlineClass.getId() + ", the error code is: " + attachDocumentResult.getError());
				if (!"repeat_arrange_to_course_error".equals(attachDocumentResult.getError())) {
					// 
				}								
			}
		}
		// 
		if (bAttachSuccess) {
			onlineClassRepository.update(onlineClass);
		}
		
		return bAttachSuccess;
	}
	
	private OnlineClass createDBYClassroom(OnlineClass onlineClass, User user) {
		try {
			// 设置课堂时间为1个小时，可提前1小时进入教室
			Date startDateTime = onlineClass.getAbleToEnterClassroomDateTime();
			if(startDateTime==null){
				startDateTime = new Date(onlineClass.getScheduledDateTime().getTime() - 60 * 60 * 1000);
			}
			
			//2015-07-15 如果开始时间已经过了，会导致创建失败。添加对时间早晚进行判断和处理
			int kCreateTimeBeforeNow = 5*1000; 
			Date now = new Date();
			long gap = startDateTime.getTime() - now.getTime() + 60 * 60 * 1000  ; // 补上
			
			// 开始时间如果比现在小于5秒，
			if (kCreateTimeBeforeNow > gap) {
				// can't create 
				securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, "Fail to create DuoBeiYun classroom for online class id: " + onlineClass.getId() + ", the error is: startTime is too late", user);
				return onlineClass;
			}
			
			
//			// TODO... 测试需求，提供创建操作,创建时间设置为2秒后。测试完成，打开上面的操作，关闭此操作
//			if (kCreateTimeBeforeNow > gap)  {
//				startDateTime = new Date(now.getTime()+5000);
//			}
			now = new Date();
			if (startDateTime.before(now)) {
				startDateTime = new Date(now.getTime()+10*1000);
			}
			
			//2015-07-15 根据类型
			Course.Mode mode = onlineClass.getCourseMode();
			Course course = getCourse(onlineClass);
			String roomType = DuobeiYunClient.COURSE_TYPE_1v1;
			if (Mode.ONE_TO_MANY ==  mode && course.getType() != Type.TEACHER_RECRUITMENT) {
				roomType = DuobeiYunClient.COURSE_TYPE_1vN;
			}
			CreateRoomResult createRoomResult = DBYAPI.createRoom(onlineClass.getLesson().getName(), startDateTime, 2, true, roomType);
			if(createRoomResult.isSuccess()) {
				onlineClass.setClassroom(createRoomResult.getRoom().getRoomId());
				if(user != null) {
					onlineClass.setLastEditor(user);
				}		
				
				// 2015-09-02 trial 多文档 的处理
				
				if (Course.Type.TRIAL == course.getType()) {
					boolean bAttachSuccess = attacheTrailDocForClassroom(onlineClass,onlineClass.getLesson());
					return onlineClass;
				}
				
				AttachDocumentResult attachDocumentResult = DBYAPI.attachDocument(onlineClass.getClassroom(), onlineClass.getLesson().getDbyDocument());
				if(attachDocumentResult.isSuccess()) {
					onlineClass.setDbyDocument(onlineClass.getLesson().getDbyDocument());
					securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_CREATE, "Create classroom for onlineClass: " + onlineClass.getSerialNumber(), user);
				    onlineClass.setAttatchDocumentSucess(true);
				}else {
					securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, "Fail to attach DuoBeiYun classroom document for online class id: " + onlineClass.getId() + ", the error code is: " + attachDocumentResult.getError(), user);
					if (!"repeat_arrange_to_course_error".equals(attachDocumentResult.getError())) {
						onlineClass.setAttatchDocumentSucess(false);
//					EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.FIRE_MAN, onlineClass, attatchDocumentResult,user,"create");
//					EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.TESTERHOU, onlineClass, attatchDocumentResult,user,"create");
//					EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.DEVDENG, onlineClass, attatchDocumentResult,user,"create");
					}								
				}
				onlineClassRepository.update(onlineClass);
			}else {
				logger.error("Fail to create DuoBeiYun classroom for online class id={}, the error code is {} ",  onlineClass.getId(), createRoomResult.getError());
				securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, "Fail to create DuoBeiYun classroom for online class id: " + onlineClass.getId() + ", the error code is: " + createRoomResult.getError(), user);
			}
			
			return onlineClass;
		} catch (Exception e) {
			logger.error("Fail to create DuoBeiYun classroom for online class id={}, the error message is {} ",  onlineClass.getId(), e.getMessage());
			securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, "Fail to create DuoBeiYun classroom for online class id: " + onlineClass.getId() + ", the error code is: " + e.getMessage(), user);

		}
		return onlineClass;
	}
	
	private OnlineClass updateDBYClassroom(OnlineClass onlineClass, User user) {
		try {
			boolean gotDocument = false;
			if (onlineClass.getClassroom() == null) {
				return onlineClass;
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
								logger.info("update dby document........remove document! ,teacher name" + onlineClass.getTeacher().getName() + "class room:" + onlineClass.getClassroom());
							}
						}
					}
				} else {
					logger.info("update dby document ........list  document! ,teacher name" + onlineClass.getTeacher().getName() + "calss room:" + onlineClass.getClassroom());
					securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "get document error");
				}

				if (!gotDocument) {
					AttachDocumentResult attachDocumentResult = DBYAPI.attachDocument(onlineClass.getClassroom(), onlineClass.getLesson().getDbyDocument());
					if (attachDocumentResult.isSuccess()) {
						onlineClass.setDbyDocument(attachDocumentResult.getDocumentId());
						securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "AttachDocumentScheduler........attach document! ,teacher name"
								+ onlineClass.getTeacher().getName() + "class room  :" + onlineClass.getClassroom());
						logger.info("update dby document,attach doc....... ,teacher name:" + onlineClass.getTeacher().getName() + "class room  :" + onlineClass.getClassroom());
						onlineClass.setAttatchDocumentSucess(true);
					} else {
						// String error = attatchDocumentResult.getError();
						// if
						// (!"repeat_arrange_to_course_error".equals(error))
						{
							onlineClass.setAttatchDocumentSucess(false);
							securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "Fail to update DuoBeiYun classroom document for online class id: " + onlineClass.getId()
									+ ", the error code is: " + attachDocumentResult.getError());
							logger.error("update dby document........attach document! ,teacher name" + onlineClass.getTeacher().getName() + "class room  :" + onlineClass.getClassroom());
							//EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.FIRE_MAN, onlineClass, attatchDocumentResult, null, "schedule");
							//EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.TESTERHOU, onlineClass, attatchDocumentResult, null, "schedule");
							EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.DEVDENG, onlineClass, attachDocumentResult, null, "schedule");
						}
					}
				}
				onlineClassRepository.update(onlineClass);
			}
		} catch (Exception e) {
			securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "Fail to update DuoBeiYun classroom title for online class id: " + onlineClass.getId()
					+ ", the error code is: " + e.getMessage());
			logger.error("update DocumentScheduler........attach document! ,teacher name" + onlineClass.getTeacher().getName() + "class room  :" + onlineClass.getClassroom());

		}
		return onlineClass;
	}
	
	private void createNewPayItem (OnlineClass oc) {		
		OnlineClass onlineClass = onlineClassRepository.find(oc.getId());
		//检查是否存在Payroll, 没有就新建一个
		Payroll currentMonthPayroll = payrollRepository.findByScheduledDateAndTeacher(onlineClass.getScheduledDateTime(), onlineClass.getTeacher());
		if (currentMonthPayroll == null){
			Calendar now = Calendar.getInstance();
			now.setTime(onlineClass.getScheduledDateTime());
			now.add(Calendar.MONTH, 1);
			now.set(Calendar.DAY_OF_MONTH, Configurations.Payroll.PAY_DATE_TIME);
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			
			currentMonthPayroll = new Payroll();
			currentMonthPayroll.setPaidDateTime(now.getTime());
			currentMonthPayroll.setStatus(Payroll.Status.UNCONFIRMED);
			currentMonthPayroll.setTeacher(onlineClass.getTeacher());
			currentMonthPayroll.setTransferingFee(Configurations.Payroll.TRANSFOR_FEE);
			payrollRepository.create(currentMonthPayroll);
		}
		
		List<PayrollItem> overlappedPayrollItems = payrollItemRepository.findByOnlineClassScheduledDateTimeAndTeacherId(onlineClass.getScheduledDateTime(), onlineClass.getTeacher().getId());
		for (PayrollItem item: overlappedPayrollItems) {
			payrollItemRepository.delete(item);
		}
		PayrollItem payrollItem = new PayrollItem();
		
		payrollItem.setOnlineClass(onlineClass);
		payrollItem.setPayroll(currentMonthPayroll);
		onlineClass.setPayrollItem(payrollItem);
		
		float extraClassSalary = onlineClass.getTeacher().getExtraClassSalary();
		float baseClassSalary = getCourse(onlineClass).getBaseClassSalary();
		float classSalary = extraClassSalary + baseClassSalary;
		payrollItem.setBaseSalary(classSalary);
		
		switch (onlineClass.getFinishType()) {
		case AS_SCHEDULED:
			payrollItem.setSalary(classSalary);
			payrollItem.setSalaryPercentage(1);
			break;
		case STUDENT_IT_PROBLEM:
			payrollItem.setSalary(classSalary * (float)0.5);
			payrollItem.setSalaryPercentage((float)0.5);
			break;
		case STUDENT_NO_SHOW:
			payrollItem.setSalary(classSalary * (float)0.3);
			payrollItem.setSalaryPercentage((float)0.3);
			break;
		case TEACHER_IT_PROBLEM:
			payrollItem.setSalary(0);
			payrollItem.setSalaryPercentage(0);
			break;
		case TEACHER_NO_SHOW:
			payrollItem.setSalary(0);
			payrollItem.setSalaryPercentage(0);
			break;
		case TEACHER_CANCELLATION:
			payrollItem.setSalary(0);
			payrollItem.setSalaryPercentage(0);
			break;
		case SYSTEM_PROBLEM:
			payrollItem.setSalary(classSalary);
			payrollItem.setSalaryPercentage(1);
			break;
		default:
			break;
		}
			
		payrollItemRepository.create(payrollItem);

	}
	
	private void handleStudentProblem(final OnlineClass onlineClass){
		Course course = getCourse(onlineClass);
		List<Student> students = onlineClass.getStudents();
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
			if(course.isSequential()){
				reSchedule(learningProgress, student, course, onlineClass);
			}
			if (onlineClass.getFinishType() == FinishType.STUDENT_NO_SHOW){
				consumeClassHour(learningProgress, onlineClass);
			}
			break;
		case ONE_TO_MANY:
			for(Student tmpStudent : students){
				LearningProgress tmpLearningProgress = learningProgressRepository.findByStudentIdAndCourseId(tmpStudent.getId(), course.getId());
				if(course.isSequential()){
					reSchedule(tmpLearningProgress, tmpStudent, course, onlineClass);
				}
				if (onlineClass.getFinishType() == FinishType.STUDENT_NO_SHOW){
					consumeClassHour(tmpLearningProgress, onlineClass);
				}
			}	
			break;
		}
	}
	
	private void handleTeacherProblem(final OnlineClass onlineClass){
        try {
            Course course = getCourse(onlineClass);
            List<Student> students = onlineClass.getStudents();

            switch(course.getMode()){
            case ONE_ON_ONE:
                if(course.isSequential()){
                    Student student = students.get(0);
                    LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
                    reSchedule(learningProgress, student, course, onlineClass);
                }
                break;
            case ONE_TO_MANY:
                if(course.isSequential()){
                    for(Student student : students){
                        LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
                        reSchedule(learningProgress, student, course, onlineClass);
                    }
                }
                break;
            }
        } catch (Exception e) {
            logger.error("OnlineClass handleTeacherProblem error,online classID={}",onlineClass.getId(),e);
            EMail.sendSystemErrotToRD("OnlineClassService handleTeacherProblem");
        }
    }
	
	private void handleSystemProblem(final OnlineClass onlineClass){
        try {
            Course course = getCourse(onlineClass);
            List<Student> students = onlineClass.getStudents();

            switch(course.getMode()){
            case ONE_ON_ONE:
                if(course.isSequential()){
                    Student student = students.get(0);
                    LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
                    reSchedule(learningProgress, student, course, onlineClass);
                }
                break;
            case ONE_TO_MANY:
                if(course.isSequential()){
                    for(Student student : students){
                        LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
                        reSchedule(learningProgress, student, course, onlineClass);
                    }
                }
                break;
            }
        } catch (Exception e) {
            logger.error("OnlineClassService:handleSystemProblem error,online classID={}",onlineClass.getId(),e);
            EMail.sendSystemErrotToRD("OnlineClassService handleSystemProblem");
        }
    }
	
	private void handleAsScheduled(final OnlineClass onlineClass){
		Course course = getCourse(onlineClass);
		Lesson lesson = onlineClass.getLesson();
		List<Student> students = onlineClass.getStudents();
		//check current unit is finished
		Unit unit = lesson.getLearningCycle().getUnit();
		List<Lesson> lessons = lessonRepository.findByUnitId(unit.getId());
		long amount = lessons.size();
		
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			if(course.isSequential()){
				forwardLearningProgress(onlineClass, student, course, lesson);
			} else{
				LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), course.getId());
				if (learningProgress == null){
					throw new IllegalStateException("LearningProgress can not be null");
				}
				learningProgress.addCompletedOnlineClass(onlineClass);
				consumeClassHour(learningProgress, onlineClass);
//				long completedOnlineClassNumber = amount;
//				if (learningProgress.getCompletedOnlineClasses() != null){
//					if (isOnlyOneLessonNeedTaken(course)){
//						completedOnlineClassNumber = 1;
//					} 
//					if(learningProgress.getCompletedOnlineClasses().size() >= completedOnlineClassNumber){
//						learningProgress.setStatus(LearningProgress.Status.FINISHED);
//					}
//					
//				}
				//先改成如果没有剩余课时，把状态改为finished
				if (learningProgress.getLeftClassHour() <= 0 && learningProgress.getCompletedOnlineClasses() != null){
					logger.info("Finish the learningProgress={}, completedOnlineClassNumber={} / totalClasses={}", learningProgress.getId(), learningProgress.getCompletedOnlineClasses().size(), amount);
					learningProgress.setStatus(LearningProgress.Status.FINISHED);
				}
				
				learningProgressRepository.update(learningProgress);
			}
			// insert medal
			//先根据unit.id 和 student.id查询一下，勋章是否存在，如果已经存在就不再插入
			List<Medal> medals  = medalRepository.findByStudentIdAndUnitId(student.getId(), unit.getId());
			if(medals != null && medals.size()>0){
			}else{
				List<OnlineClass> finishedLessons = onlineClassRepository.findFinishedByUnitId(student.getId(), unit.getId());
				if(amount == finishedLessons.size()){
					logger.info("ONE_ON_ONE : current lesson id = " + lesson.getId() + " lessonCount = " + amount + " finished lesson count = " + finishedLessons.size());
					Medal medal = new Medal();
					medal.setName(unit.getSerialNumber());
					medal.setDescription(unit.getName());
					medal.setStudent(student);
					medal.setUnit(unit);
					medal.setPristine(true);
					medal.setGainTime(new Date());
					medalRepository.create(medal);
				}
			}
			
			// 判断是否为trial课, 以便更改学生lifecycle
			if (course.getType() == Course.Type.TRIAL || onlineClass.getLesson().getSerialNumber().equals("A2-U1-LC1-L1") || onlineClass.getLesson().getSerialNumber().equals("LT1-U1-LC1-L1")) {
				Long payConfirmedOrdersCount = orderRepository.countPayConfirmedByStudentId(student.getId());
				if (payConfirmedOrdersCount > 0) {
					studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.LEARNING);
				} else {
					studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.TRIAL_FINISHED);
				}
			}

			break;
		case ONE_TO_MANY:
			List<Student> asScheduledStudents = onlineClass.getAsScheduledStudents();
			List<Student> itProblemStudents = onlineClass.getItProblemStudents();
			List<Student> noShowStudents = onlineClass.getNoShowStudents();
			
			for(Student tmpStudent : asScheduledStudents){
				if(course.isSequential()){
					forwardLearningProgress(onlineClass, tmpStudent, course, lesson);
				} else{
					LearningProgress tmpLearningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(tmpStudent.getId(), course.getId());
					if (tmpLearningProgress == null){
						throw new IllegalStateException("LearningProgress can not be null");
					}
					tmpLearningProgress.addCompletedOnlineClass(onlineClass);
					consumeClassHour(tmpLearningProgress, onlineClass);
					if (course.getType() != Type.TEACHER_RECRUITMENT && course.getType() != Type.PRACTICUM && tmpLearningProgress.getCompletedOnlineClasses() != null){
						if(tmpLearningProgress.getCompletedOnlineClasses().size() >= amount){
							tmpLearningProgress.setStatus(LearningProgress.Status.FINISHED);
						}
					}
					learningProgressRepository.update(tmpLearningProgress);
				}
				//insert medal
				List<Medal> medals2  = medalRepository.findByStudentIdAndUnitId(tmpStudent.getId(), unit.getId());
				if(medals2 != null && medals2.size()>0){
				}else{
					List<OnlineClass> finishedLessones2 = onlineClassRepository.findFinishedByUnitId(tmpStudent.getId(), unit.getId());
					if(amount == finishedLessones2.size()){
						logger.info("ONE_TO_MANY : current lesson id = " + lesson.getId() + " lessonCount = " + amount + " finished lesson count = " + finishedLessones2.size());
						Medal medal = new Medal();
						medal.setName(unit.getSerialNumber());
						medal.setDescription(unit.getName());
						medal.setStudent(tmpStudent);
						medal.setUnit(unit);
						medal.setPristine(true);
						medal.setGainTime(new Date());
						medalRepository.create(medal);
					}
				}
			}
			
			for(Student tmpStudent : itProblemStudents){
				if(course.isSequential()){
					LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(tmpStudent.getId(), course.getId());
					
					reSchedule(learningProgress, tmpStudent, course, onlineClass);
				}
			}
			
			for(Student tmpStudent : noShowStudents){
				if(course.isSequential()){
					LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(tmpStudent.getId(), course.getId());
					reSchedule(learningProgress, tmpStudent, course, onlineClass);
					consumeClassHour(learningProgress, onlineClass);
				}
			}
			break;
		}
	}
	
	private void undoStudentNoShow(final OnlineClass onlineClass){
		List<OnlineClass> availableOnlineClasses = onlineClassRepository.findAvailableByTeacherIdAndScheduleDateTime(onlineClass.getTeacher().getId(), onlineClass.getScheduledDateTime());
		for (OnlineClass availableOnlineClass : availableOnlineClasses){
			availableOnlineClass.setStatus(OnlineClass.Status.REMOVED);
			String comments = availableOnlineClass.getComments();
			if (comments == null){
				comments = "";
			}
			availableOnlineClass.setComments(comments + ";" + "removed due to undo operation");
			onlineClassRepository.update(availableOnlineClass);
			saveOnlineClassOperation(availableOnlineClass, Category.ONLINE_CLASS_REMOVE);
			logger.info("Remove onlineClass={} due to undo operation", availableOnlineClass.getSerialNumber());
		}
		Course course = getCourse(onlineClass);
		List<Student> students = onlineClass.getStudents();
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
			if(course.isSequential()){
				rollbackSchedule(learningProgress, student, course, onlineClass);
			}
			if (onlineClass.getFinishType() == FinishType.STUDENT_NO_SHOW){
				compensateClassHour(learningProgress, onlineClass);
			}
			break;
		case ONE_TO_MANY:
			for(Student tmpStudent : students){
				LearningProgress tmpLearningProgress = learningProgressRepository.findByStudentIdAndCourseId(tmpStudent.getId(), course.getId());
				if(course.isSequential()){
					rollbackSchedule(tmpLearningProgress, tmpStudent, course, onlineClass);
				}
				if (onlineClass.getFinishType() == FinishType.STUDENT_NO_SHOW){
					compensateClassHour(tmpLearningProgress, onlineClass);
				}
			}	
			break;
		}
	}
	
	private void undoStudentITProblem(final OnlineClass onlineClass){
		
		Course course = getCourse(onlineClass);
		List<Student> students = onlineClass.getStudents();
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
			if(course.isSequential()){
				rollbackSchedule(learningProgress, student, course, onlineClass);
			}
			if (onlineClass.getFinishType() == FinishType.STUDENT_NO_SHOW){
				compensateClassHour(learningProgress, onlineClass);
			}
			break;
		case ONE_TO_MANY:
			for(Student tmpStudent : students){
				LearningProgress tmpLearningProgress = learningProgressRepository.findByStudentIdAndCourseId(tmpStudent.getId(), course.getId());
				if(course.isSequential()){
					rollbackSchedule(tmpLearningProgress, tmpStudent, course, onlineClass);
				}
				if (onlineClass.getFinishType() == FinishType.STUDENT_NO_SHOW){
					compensateClassHour(tmpLearningProgress, onlineClass);
				}
			}	
			break;
		}
	}
	
	private void undoTeacherProblem(final OnlineClass onlineClass){
		Course course = getCourse(onlineClass);
		List<Student> students = onlineClass.getStudents();
		
		switch(course.getMode()){
		case ONE_ON_ONE:
			if(course.isSequential()){
				Student student = students.get(0);
				LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
				rollbackSchedule(learningProgress, student, course, onlineClass);
			}
			break;
		case ONE_TO_MANY:
			if(course.isSequential()){
				for(Student student : students){
					LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
					rollbackSchedule(learningProgress, student, course, onlineClass);
				}
			}
			break;
		}
	}
	
	private void undoSystemProblem(final OnlineClass onlineClass){
		Course course = getCourse(onlineClass);
		List<Student> students = onlineClass.getStudents();
		
		switch(course.getMode()){
		case ONE_ON_ONE:
			if(course.isSequential()){
				Student student = students.get(0);
				LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
				rollbackSchedule(learningProgress, student, course, onlineClass);
			}
			break;
		case ONE_TO_MANY:
			if(course.isSequential()){
				for(Student student : students){
					LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
					rollbackSchedule(learningProgress, student, course, onlineClass);
				}
			}
			break;
		}
	}
	
	private void undoAsScheduled(final OnlineClass onlineClass){
		Course course = getCourse(onlineClass);
		Lesson lesson = onlineClass.getLesson();
		List<Student> students = onlineClass.getStudents();
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			if(course.isSequential()){
				rollbackLearningProgress(onlineClass, student, course, lesson);
			} else{
				LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseIdAndOnlineClassId(student.getId(), course.getId(), onlineClass.getId());
				if (learningProgress == null){
					throw new IllegalStateException("LearningProgress can not be null");
				}
				learningProgress.removeCompletedOnlineClass(onlineClass);
				compensateClassHour(learningProgress, onlineClass);
				if (learningProgress.getStatus() == LearningProgress.Status.FINISHED){
					learningProgress.setStatus(LearningProgress.Status.STARTED);
				}
				learningProgressRepository.update(learningProgress);
			}
			break;
		case ONE_TO_MANY:
			List<Student> asScheduledStudents = onlineClass.getAsScheduledStudents();
			List<Student> itProblemStudents = onlineClass.getItProblemStudents();
			List<Student> noShowStudents = onlineClass.getNoShowStudents();
			
			for(Student tmpStudent : asScheduledStudents){
				if(course.isSequential()){
					rollbackLearningProgress(onlineClass, tmpStudent, course, lesson);
				} else{
					LearningProgress tmpLearningProgress = learningProgressRepository.findByStudentIdAndCourseIdAndOnlineClassId(tmpStudent.getId(), course.getId(), onlineClass.getId());
					if (tmpLearningProgress == null){
						throw new IllegalStateException("LearningProgress can not be null");
					}
					tmpLearningProgress.removeCompletedOnlineClass(onlineClass);
					compensateClassHour(tmpLearningProgress, onlineClass);
					if (tmpLearningProgress.getStatus() == LearningProgress.Status.FINISHED){
						tmpLearningProgress.setStatus(LearningProgress.Status.STARTED);
					}
					learningProgressRepository.update(tmpLearningProgress);
				}
			}
			
			for(Student tmpStudent : itProblemStudents){
				if(course.isSequential()){
					LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseIdAndOnlineClassId(tmpStudent.getId(), course.getId(), onlineClass.getId());
					rollbackSchedule(learningProgress, tmpStudent, course, onlineClass);
				}
			}
			
			for(Student tmpStudent : noShowStudents){
				if(course.isSequential()){
					LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseIdAndOnlineClassId(tmpStudent.getId(), course.getId(), onlineClass.getId());
					rollbackSchedule(learningProgress, tmpStudent, course, onlineClass);
					compensateClassHour(learningProgress, onlineClass);
				}
			}
			break;
		}
	}
	
	private void forwardLearningProgress(final OnlineClass onlineClass, final Student student, final Course course, final Lesson lesson){
		LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), course.getId());
		if (learningProgress == null){
			throw new IllegalStateException("LearningProgress can not be null");
		}
		//如果是最后一节课，则结课
		Lesson endLesson = lessonRepository.findEndByCourseId(course.getId());
		if(lesson.getId() == endLesson.getId()) {
			learningProgress.setStatus(LearningProgress.Status.FINISHED);
			learningProgressRepository.update(learningProgress);
		}
		//如果不是最后一节课，则将学习进度推进到下一节课
		else {
			Lesson nextShouldTakeLesson = lessonRepository.findNextByCourseIdAndSequence(course.getId(), lesson.getSequence());
			learningProgress.setNextShouldTakeLesson(nextShouldTakeLesson);
			learningProgress.addCompletedOnlineClass(onlineClass);
			learningProgressRepository.update(learningProgress);
		}
		//消耗一个课时
		consumeClassHour(learningProgress, onlineClass);
	}
	
	private void rollbackLearningProgress(final OnlineClass onlineClass, final Student student, final Course course, final Lesson lesson){
		LearningProgress learningProgress = learningProgressRepository.findByStudentIdAndCourseIdAndOnlineClassId(student.getId(), course.getId(), onlineClass.getId());
		if (learningProgress == null){
			throw new IllegalStateException("LearningProgress can not be null");
		}
		//如果是最后一节课，则结课
		Lesson endLesson = lessonRepository.findEndByCourseId(course.getId());
		if(lesson.getId() == endLesson.getId()) {
			learningProgress.setStatus(LearningProgress.Status.STARTED);
			learningProgressRepository.update(learningProgress);
		}else {//如果不是最后一节课，则将学习进度回滚到上一节课
			Lesson prevTakenLesson = lessonRepository.findPrevByCourseIdAndSequence(course.getId(), lesson.getSequence());
			learningProgress.setNextShouldTakeLesson(prevTakenLesson);
			learningProgress.removeCompletedOnlineClass(onlineClass);
			learningProgressRepository.update(learningProgress);
		}
		//补偿一个课时
		compensateClassHour(learningProgress, onlineClass);
	}
	
	public Course getCourse(final OnlineClass onlineClass){
		return onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
	}
	
	private void consumeClassHour(final LearningProgress learningProgress, final OnlineClass onlineClass) {
		if (learningProgress == null){
			throw new IllegalStateException("LearningProgress can not be null");
		}
		
		learningProgress.setLeftClassHour(learningProgress.getLeftClassHour() - 1);
		learningProgressRepository.update(learningProgress);
		
		float unitPrice = calculatedPrice(learningProgress, onlineClass);
		onlineClass.setUnitPrice(unitPrice);
		
		onlineClass.setConsumeClassHour(true);
		onlineClassRepository.update(onlineClass);
		
		// 如果付费课时没有了，而且还没有到达最高级，lifecycle=to be renewed
		Product product = productRepository.find(learningProgress.getProductId());
		if (!learningProgress.getCourse().getType().equals(Course.Type.MAJOR) || product == null || product.getType() == null || product.getType().equals(Product.Type.FREE)) {
			return;
		}
		Student student = onlineClass.getStudents().get(0);
		boolean stillHavePaidClassHour = false;
		List<LearningProgress> paidLearningProgresses = learningProgressRepository.findPaidByStudentId(student.getId());
		for (LearningProgress lp : paidLearningProgresses) {
			if (lp.getLeftClassHour() > 0) {
				stillHavePaidClassHour = true;
				break;
			} 
		}

		if (!stillHavePaidClassHour) {
			boolean isToBeRenewed = false;
			for (LearningProgress lp : paidLearningProgresses) {
				if (lp.getCourse().isSequential()) {// 有序课
					if (lp.getNextShouldTakeLesson().getId() != lessonRepository.findEndByCourseId(getCourse(onlineClass).getId()).getId()) {
						isToBeRenewed = true;
					}
				} else { // 无序课
					if (lp.getCompletedOnlineClasses().size() <= lessonRepository.countByCourseId(lp.getCourse().getId())) {
						isToBeRenewed = true;
					}
				}
			}
			if (isToBeRenewed) {
				studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.TO_BE_RENEWED);
			} else {// 如果付费课时没有了，而且到达了最后一节课，lifeCycle=graduated.
				studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.GRADUATED);
			}
		} 
	}
	
	private float calculatedPrice(final LearningProgress learningProgress, final OnlineClass onlineClass){
		long studentId=learningProgress.getStudent().getId();
		long courseId=learningProgress.getCourse().getId();
		List<OrderItem> list = orderItemRepository.findByStudentAndCourse(studentId, courseId);
		int currentHour = learningProgress.getTotalClassHour()-learningProgress.getLeftClassHour();
		int hours=0;
		float price= 0;
		if(list!=null&&list.size()!=0){
			for (int i = 0; i < list.size(); i++) {
				hours +=list.get(i).getClassHour();
				if(currentHour<=hours){
					price = list.get(i).getDealPrice()/list.get(i).getClassHour();
					break;
				}
			}
		}
		return price;
	}
	
	private void compensateClassHour(final LearningProgress learningProgress, final OnlineClass onlineClass) {
		if (learningProgress == null){
			throw new IllegalStateException("LearningProgress can not be null");
		}
		learningProgress.setLeftClassHour(learningProgress.getLeftClassHour() + 1);
		learningProgressRepository.update(learningProgress);
		onlineClass.setUnitPrice(0);//回滚时清空单节课利润
		onlineClass.setConsumeClassHour(false);
		onlineClassRepository.update(onlineClass);
		
		// 如果LifeCycle是To be renewed或graduate, 回到learning
		Student student = onlineClass.getStudents().get(0);
		if (student.getLifeCycle() == Student.LifeCycle.TO_BE_RENEWED || student.getLifeCycle() == Student.LifeCycle.GRADUATED) {
			studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.LEARNING);
		}
	}
	
	private void checkClassAlreadyBooked(final OnlineClass onlineClass){
		boolean classAlreadyBooked = onlineClassRepository.hasBookedAlreadyByTeacherIdAndScheduledDateTime(onlineClass.getTeacher().getId(), onlineClass.getScheduledDateTime());
		if (classAlreadyBooked){
			throw new OnlineClassAlreadyBookedByOthersServiceException("The class is already booked by others.");
		}
	}
	
	private void checkDateTimeAlreadyBooked(OnlineClass onlineClass){
		Course course = onlineClass.getCourse();
		if (course == null){
			course = getCourse(onlineClass);
		}
		List<Student> students = onlineClass.getStudents();
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			boolean dateTimeBooked = onlineClassRepository.hasScheduled(onlineClass.getId(), student.getId(), onlineClass.getScheduledDateTime());
			if(dateTimeBooked) {
				throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
			}
			break;
		case ONE_TO_MANY:
			for (Student tempStudent : students){
				boolean tempDateTimeBooked = onlineClassRepository.hasScheduled(onlineClass.getId(), tempStudent.getId(), onlineClass.getScheduledDateTime());
				if(tempDateTimeBooked) {
					throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
				}
			}
			break;
		}	
	}
	
	private void checkDateTimeAlreadyBookedSwitch(OnlineClass onlineClass){
		Course course = onlineClass.getCourse();
		if (course == null){
			course = getCourse(onlineClass);
		}
		List<Student> students = onlineClass.getStudents();
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			boolean dateTimeBooked = onlineClassRepository.hasSwitchScheduled(onlineClass.getId(), student.getId(), onlineClass.getScheduledDateTime());
			if(dateTimeBooked) {
				throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
			}
			break;
		case ONE_TO_MANY:
			for (Student tempStudent : students){
				boolean tempDateTimeBooked = onlineClassRepository.hasSwitchScheduled(onlineClass.getId(), tempStudent.getId(), onlineClass.getScheduledDateTime());
				if(tempDateTimeBooked) {
					throw new DateTimeAlreadyScheduledServiceException("The time is already scheduled for the student.");
				}
			}
			break;
		}	
	}
	
	private void checkNoMoreClassHours(OnlineClass onlineClass){
		Course course = onlineClass.getCourse();
		List<Student> students = onlineClass.getStudents();
		
		switch(course.getMode()){
		case ONE_ON_ONE:
			Student student = students.get(0);
			LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), course.getId());
			if(learningProgress ==null){
				logger.error("There is no more learningProgress, the student is " + student.getName());
				throw new NoMoreClassHourForBookingServiceException("There is no learningProgress for" +course.getName());
			}
			if(learningProgress.getLeftClassHour() <= onlineClassRepository.findAvaiableBookingTimeSlot(student.getId(), course.getId())) {
				logger.error("There is no more class hour for booking, the student is " + student.getName() + " and the leftClassHour is " + learningProgress.getLeftClassHour());
				throw new NoMoreClassHourForBookingServiceException("There is no more class hour for booking.");
			}
			break;
		case ONE_TO_MANY:
			for (Student tempStudent : students){
				LearningProgress tmplearningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(tempStudent.getId(), course.getId());
				if(tmplearningProgress.getLeftClassHour() <= onlineClassRepository.findAvaiableBookingTimeSlot(tempStudent.getId(), course.getId())) {
					throw new NoMoreClassHourForBookingServiceException("There is no more class hour for booking.");
				}
			}
			break;
		}	
	}
	
	private void setOnlineClass(final OnlineClass onlineClass){
		if(onlineClass.getSerialNumber() == null){
			onlineClassRepository.create(onlineClass);
		}
		
		User booker = securityService.getCurrentUser();
		if(booker == null) {
			booker = staffRepository.findByUsername(Configurations.System.SYSTEM_USERNAME);
		}
		onlineClass.setBooker(booker);
		onlineClass.setBookDateTime(new Date());
		onlineClass.setLastEditor(booker);
		onlineClass.setBookDateTime(new Date());
		
		if (isWithin24Hours(onlineClass)){
			onlineClass.setShortNotice(true);
		}
	}
	
	private void checkTrialParallel(OnlineClass onlineClass){
		Course course = onlineClass.getCourse();
		if(course.getType() == Type.TRIAL||course.getType() == Type.ASSESSMENT2||course.getType() == Type.ELECTIVE_LT) {
			int parallelCount = (int)onlineClassRepository.countParallelByScheduledDateTimeAndCourseType(onlineClass.getScheduledDateTime(), course.getType());
			int maxParallelCount = Redis.Trial.DEFAULT_PARALLEL;
			//maxParallelCount = getMaxParallelCountFromRedis();
			maxParallelCount = (int)getMaxParallelCountByDate(onlineClass.getScheduledDateTime());
			if(parallelCount >= maxParallelCount) {
				throw new ExceedMaxParallelCountServiceException("Can not exceed max parallel count.");
			}
		}
	}
	
	private long getMaxParallelCountByDate(Date scheduledDateTime) {
		TrialThreshold threshold = trialThresholdRepository.findByTimePoint(scheduledDateTime);
		if (threshold == null) {
			return getMaxParallelCountFromRedis();
		} else {
			return threshold.getTrialAmount();
		}
	}

	private void  saveOnlineClassOperation(OnlineClass onlineClass, Category category){
		OnlineClassOperation onlineClassOperation = new OnlineClassOperation();
		onlineClassOperation.setOnlineClass(onlineClass);
		User operator = securityService.getCurrentUser();
		if(operator == null) {
			operator = staffRepository.findByUsername(Configurations.System.SYSTEM_USERNAME);
		}
		onlineClassOperation.setOperator(operator.getSafeName() == null?"T-interview operator":operator.getSafeName());
		onlineClassOperation.setOperatorId(operator.getId());
		if (onlineClass.getStudents() != null) {
			onlineClassOperation.setStudents(new ArrayList<Student>(onlineClass.getStudents()));
		} else {
			onlineClassOperation.setStudents(null);
		}
		if (onlineClassOperation.getExecuteDateTime() == null) {
			onlineClassOperation.setExecuteDateTime(new Date());
		}
		onlineClassOperation.setCategory(category);
		onlineClassOperationRepository.create(onlineClassOperation);
	}
	
	private OnlineClass doBookDisorder(OnlineClass onlineClass){
		List<Student> students = onlineClass.getStudents();
		int can_book_student_threshold = 1;
		
		Course course = onlineClass.getCourse();
		switch(course.getMode()){
		case ONE_ON_ONE:
			can_book_student_threshold = 1;
			break;
		case ONE_TO_MANY:
			can_book_student_threshold = onlineClass.getMaxStudentNumber();
			break;
		}
		
		if (students.size() >= can_book_student_threshold){
			onlineClass.setStatus(Status.BOOKED);
		} else{
			onlineClass.setStatus(Status.OPEN);
		}
		User lastEditor = securityService.getCurrentUser();
		if(lastEditor == null) {
			lastEditor = staffRepository.findByUsername(Configurations.System.SYSTEM_USERNAME);
		}
		onlineClass.setLastEditor(lastEditor);
		onlineClassRepository.update(onlineClass);
		logger.info("Booked disorder class=[ {}, {}, {}" + "]", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2), onlineClass.getTeacher().getName(), onlineClass.getLesson().getSerialNumber());
	    return onlineClass;
	}
	
	private OnlineClass doBookInorder(OnlineClass onlineClass){
		Course course = onlineClass.getCourse();
		switch(course.getMode()){
		case ONE_ON_ONE:
			logger.info("doBookInorder for ONE_ON_ONE ");
			doBookOneOnOneInorder(onlineClass, course);
			break;
		case ONE_TO_MANY:
			logger.info("doBookInorder for ONE_TO_MANY");
			doBookOneToManyInorder(onlineClass);
			break;
		}
		logger.info("Booked inorder class=[ {}, {}, {}" + "]", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2), onlineClass.getTeacher().getName(), onlineClass.getLesson().getSerialNumber());
		return onlineClass;
	}
	
	private void doBookOneToManyInorder(OnlineClass onlineClass){
		List<Student> students = onlineClass.getStudents();
		if (students.size() >= onlineClass.getMaxStudentNumber()){
			onlineClass.setStatus(Status.BOOKED);
		} else{
			onlineClass.setStatus(Status.OPEN);
		}
		User lastEditor = securityService.getCurrentUser();
		onlineClass.setLastEditor(lastEditor);
		onlineClassRepository.update(onlineClass);
	}
	
	private void doBookOneOnOneInorder(OnlineClass onlineClass, Course course){
		List<Student> students = onlineClass.getStudents();
		Student student = students.get(0);
		
		LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), course.getId());
		if(learningProgress.getLastScheduledLesson() == null) {
			//将当前学习进度的起始课程安排给当前课程记录
            Lesson firstLesson;
            if (learningProgress.getStartUnit() == null){
            	logger.info("doBookOneOnOneInorder -getLastScheduledLesson- getStartUnit is null. now find the first by course id.");
                firstLesson = lessonRepository.findFirstByCourseId(learningProgress.getCourse().getId());
            } else {
            	logger.info("doBookOneOnOneInorder -getLastScheduledLesson- getStartUnit is not null. now findFirstByUnitId.");
                firstLesson = lessonRepository.findFirstByUnitId(learningProgress.getStartUnit().getId());
            }
			onlineClass.setLesson(firstLesson);
			onlineClass.setStatus(Status.BOOKED);
			User lastEditor = securityService.getCurrentUser();
			onlineClass.setLastEditor(lastEditor);
			onlineClassRepository.update(onlineClass);
			
			//更新学习进度
			learningProgress.setLastScheduledLesson(onlineClass.getLesson());
			learningProgressRepository.update(learningProgress);
		} else{
			logger.info("doBookOneOnOneInorder --getLastScheduledLesson is not null");
			//如果已经预约到最后一节课了，则无法再继续排课
			Lesson endLesson = lessonRepository.findEndByCourseId(course.getId());
			if(learningProgress.getLastScheduledLesson().getSequence() >= endLesson.getSequence()) {
				throw new NoMoreLessonForBookingServiceException("There is no more lesson for booking.");
			}
				
			List<OnlineClass> onlineClasses = onlineClassRepository.findLaterBookedByStudentIdAndCourseId(student.getId(), course.getId(), onlineClass.getScheduledDateTime());
			if(onlineClasses.isEmpty()) {
				//将当前学习进度中已预约课程的下一节课安排给当前课程记录
				Lesson nextShouldScheduleLesson = lessonRepository.findNextByCourseIdAndSequence(course.getId(), learningProgress.getLastScheduledLesson().getSequence());
				onlineClass.setLesson(nextShouldScheduleLesson);
				onlineClass.setStatus(Status.BOOKED);
				User lastEditor = securityService.getCurrentUser();
				onlineClass.setLastEditor(lastEditor);
				onlineClassRepository.update(onlineClass);
					
				//更新学习进度
				learningProgress.setLastScheduledLesson(onlineClass.getLesson());
				learningProgressRepository.update(learningProgress);
			}else {
				//将当前课程记录之后的第一个课程记录的课程安排给当前课程记录
				onlineClass.setLesson(onlineClasses.get(0).getLesson());
				onlineClass.setStatus(Status.BOOKED);
				User lastEditor = securityService.getCurrentUser();
				onlineClass.setLastEditor(lastEditor);
				onlineClassRepository.update(onlineClass);
					
				//当前课程记录之后的课程记录,将课程依次后移一位
				final int length = onlineClasses.size();
				for(int i = 0; i <length; i++) {
					OnlineClass tmpOnlineClass = onlineClasses.get(i);
						
					if(i + 1 < length) {
						tmpOnlineClass.setLesson(onlineClasses.get(i + 1).getLesson());
					}else {
						Lesson lesson = lessonRepository.findNextByCourseIdAndSequence(course.getId(), tmpOnlineClass.getLesson().getSequence());
						tmpOnlineClass.setLesson(lesson);
					}
					onlineClass.setLastEditor(lastEditor);
					onlineClassRepository.update(tmpOnlineClass);
				}
					
				OnlineClass theLastLessonHistory = onlineClasses.get(onlineClasses.size() - 1);
				learningProgress.setLastScheduledLesson(theLastLessonHistory.getLesson());
				learningProgressRepository.update(learningProgress);
			}
		}
	}
	
	private void rollbackSchedule(LearningProgress learningProgress, Student student, Course course, OnlineClass onlineClass) {
		if (learningProgress == null){
			throw new IllegalStateException("LearningProgress can not be null");
		}
		
		List<OnlineClass> onlineClasses = onlineClassRepository.findLaterBookedByStudentIdAndCourseId(student.getId(), course.getId(), onlineClass.getScheduledDateTime());
		
		if(onlineClasses.isEmpty()) {
			//更新学习进度
			Lesson nextLesson = lessonRepository.findNextByCourseIdAndSequence(course.getId(), onlineClass.getLesson().getSequence());
			learningProgress.setLastScheduledLesson(nextLesson);
			learningProgressRepository.update(learningProgress);
		}else {
			//重排当前课程记录之后的课程记录,将课程依次前移一位
			final int length = onlineClasses.size();
			for(int i = 0; i < length; i++) {
				final OnlineClass reScheduledOnlineClass = onlineClasses.get(i);
				
				if(i == length - 1) {
					Lesson nextLesson = lessonRepository.findNextByCourseIdAndSequence(course.getId(), reScheduledOnlineClass.getLesson().getSequence());
					reScheduledOnlineClass.setLesson(nextLesson);
				}else { 
					reScheduledOnlineClass.setLesson(onlineClasses.get(i + 1).getLesson());
				}
				final User lastEditor = securityService.getCurrentUser();
				onlineClass.setLastEditor(lastEditor);
				onlineClassRepository.update(reScheduledOnlineClass);
				
				if(!TextUtils.isEmpty(reScheduledOnlineClass.getClassroom())) {
					ExecutorService executorService = Executors.newSingleThreadExecutor();
					FutureTask<OnlineClass> futureTask = new FutureTask<OnlineClass>(new Callable<OnlineClass>(){
						@Override
						public OnlineClass call() throws Exception {
							return updateDBYClassroom(reScheduledOnlineClass, lastEditor);
						}
					});
					executorService.submit(futureTask);
				}
			}
			
			//更新学习进度
			OnlineClass theLastOnlineClass = onlineClasses.get(length - 1);
			learningProgress.setLastScheduledLesson(theLastOnlineClass.getLesson());
			if (learningProgress.getStatus() == LearningProgress.Status.FINISHED){
				learningProgress.setStatus(LearningProgress.Status.STARTED);
			}
			learningProgressRepository.update(learningProgress);
		}
	}
	
	private void reSchedule(LearningProgress learningProgress, Student student, Course course, OnlineClass onlineClass) {
		List<OnlineClass> onlineClasses = onlineClassRepository.findLaterBookedByStudentIdAndCourseId(student.getId(), course.getId(), onlineClass.getScheduledDateTime());
		
		if(onlineClasses.isEmpty()) {
			//更新学习进度
			Lesson prevLesson = lessonRepository.findPrevByCourseIdAndSequence(course.getId(), onlineClass.getLesson().getSequence());
			learningProgress.setLastScheduledLesson(prevLesson);
			learningProgressRepository.update(learningProgress);
		}else {
			//重排当前课程记录之后的课程记录,将课程依次前移一位
			final int length = onlineClasses.size();
			for(int i = length - 1; i >= 0; i--) {
				final OnlineClass reScheduledOnlineClass = onlineClasses.get(i);
				
				if(i == 0) {
					reScheduledOnlineClass.setLesson(onlineClass.getLesson());
				}else { 
					reScheduledOnlineClass.setLesson(onlineClasses.get(i - 1).getLesson());
				}
				final User lastEditor = securityService.getCurrentUser();
				onlineClass.setLastEditor(lastEditor);
				onlineClassRepository.update(reScheduledOnlineClass);
			}
			
			//更新学习进度
			OnlineClass theLastOnlineClass = onlineClasses.get(length - 1);
			learningProgress.setLastScheduledLesson(theLastOnlineClass.getLesson());
			learningProgressRepository.update(learningProgress);
		}
	}

	public void doReScheduleDby(Student student, Course course, OnlineClass inputOnlineClass, boolean isCreate) {
		List<OnlineClass> onlineClasses = onlineClassRepository.findLaterBookedByStudentIdAndCourseId(student.getId(), course.getId(), inputOnlineClass.getScheduledDateTime());

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		FutureTask<OnlineClass> futureTask = new FutureTask<OnlineClass>(new Callable<OnlineClass>() {
			@Override
			public OnlineClass call() throws Exception {
				if (isCreate) {
					User user = securityService.getCurrentUser();
					if (user == null) {
						user = (User) staffRepository.findByUsername(Configurations.System.SYSTEM_USERNAME);
					}
					logger.info("doReScheduleDby -- createDBYClassroom "+inputOnlineClass.getLesson().getName() +" and dby document:"+inputOnlineClass.getLesson().getDbyDocument());
					createDBYClassroom(inputOnlineClass, user);
				}
				if (!onlineClasses.isEmpty()) {
					final int length = onlineClasses.size();
					for (int i = length - 1; i >= 0; i--) {
						final OnlineClass reScheduledOnlineClass = onlineClasses.get(i);

						if (!TextUtils.isEmpty(reScheduledOnlineClass.getClassroom())) {

							final User lastEditor = securityService.getCurrentUser();
							inputOnlineClass.setLastEditor(lastEditor);
							try {
								logger.info("doReScheduleDby -- updateDBYClassroom:"+reScheduledOnlineClass.getLesson().getDbyDocument());
							} catch (Exception e) {
								//
								logger.error("doReScheduleDby -- updateDBYClassroom log for onlineclassid: "+reScheduledOnlineClass.getId());
							}
							return updateDBYClassroom(reScheduledOnlineClass, lastEditor);
						}
					}
				}
				return inputOnlineClass;
			}
		});
		executorService.submit(futureTask);
	}
	
	private void releaseLocks(final OnlineClass onlineClass){
		long now = System.currentTimeMillis();
		
		DistributedLock.unlock(getKeyWithTeacherAtScheduledDateTime(onlineClass.getTeacher().getId(), onlineClass.getScheduledDateTime()));
		List<Student> students = onlineClass.getStudents();
		for (Student student : students){
			DistributedLock.unlock(getKeyWithStudentAtScheduledDateTime(student.getId(), onlineClass.getScheduledDateTime()));
		}
		
		logger.info("Release locks consume {} ms", System.currentTimeMillis() - now);
	}
	
	private boolean canGetBookingLocks(final OnlineClass onlineClass){
		boolean lockResult = canGetLockWithTeacherAtScheduledDateTime(onlineClass) && canGetLockWithStudentsAtScheduledDateTime(onlineClass);
		return lockResult;
	}
	
	private boolean canGetBookingSwitchLocks(final OnlineClass onlineClass){
		boolean lockResult = canGetLockWithTeacherAtScheduledDateTime(onlineClass);
		return lockResult;
	}
	
	private boolean canGetLockWithTeacherAtScheduledDateTime(final OnlineClass onlineClass){
		return DistributedLock.lock(getKeyWithTeacherAtScheduledDateTime(onlineClass.getTeacher().getId(), onlineClass.getScheduledDateTime()));
	}
	
	private boolean canGetLockWithStudentsAtScheduledDateTime(final OnlineClass onlineClass){
		List<Student> students = onlineClass.getStudents();
		for (Student student : students){
			if (!DistributedLock.lock(getKeyWithStudentAtScheduledDateTime(student.getId(), onlineClass.getScheduledDateTime()))){
				return false;
			}
		}
		return true;
	}
	
	private String getKeyWithTeacherAtScheduledDateTime(long teacherId, Date scheduledDateTime){
		return Configurations.Redis.PREFIX_FOR_BOOK_LOCK + TextUtils.DOT + teacherId + TextUtils.DOT + scheduledDateTime.getTime();
	}
	
	private String getKeyWithStudentAtScheduledDateTime(long studentId, Date scheduledDateTime){
		return Configurations.Redis.PREFIX_FOR_BOOK_LOCK + TextUtils.DOT + studentId + TextUtils.DOT + scheduledDateTime.getTime();
	}
	

	private boolean isOnlyOneLessonNeedTaken(final Course course){
		if (course.getType() == Type.DEMO || course.getType() == Type.IT_TEST || course.getType() == Type.TRIAL){
			return true;
		}
		return false;
	}
	
	private Status convertStringToStatus(String statusString) {
	    switch (statusString) {
	    case "AVAILABLE" :
	      return Status.AVAILABLE;
	    case "BOOKED":
	      return Status.BOOKED;
	    case "CANCELED":
	      return Status.CANCELED;
	    case "EXPIRED":
	      return Status.EXPIRED;
	    case "FINISHED":
	      return Status.FINISHED;
	    case "OPEN":
	    	return Status.OPEN;
	    default:
	      return null;
	    }
	  }
	
	private void checkCancelDateTime(OnlineClass onlineClass) {
		Calendar now = Calendar.getInstance();
		if (onlineClass.getScheduledDateTime().before(now.getTime())) {
			throw new OnlineClassAlreadyRequestedServiceException("Can not cancel a class which already started!");
		}
	}
	
	private void checkBookDateTime(OnlineClass onlineClass) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, -30);
		if (onlineClass.getScheduledDateTime().before(now.getTime())) {
			throw new OnlineClassAlreadyRequestedServiceException("Can not book an expired(before 30minutes) date class!");
		}
	}
	
	
	private void checkOnlineClassIfHasCancelled(OnlineClass onlineClass) {
		if(onlineClass.getStatus() != Status.BOOKED && onlineClass.getStatus() != Status.OPEN) {
			throw new OnlineClassAlreadyRequestedServiceException("Has cancelled.");
		}
	}
	
	private void sendEmailandSMSWhenOnlineClassIsCancelled(Course course, OnlineClass onlineClassInfoForEmailAndSMS) {
		switch (course.getType()) {
			case IT_TEST:							
				try{
					for(Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
						SMS.sendItOnlineClassIsCancelledToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
						securityService.logAudit(Level.INFO, Category.SMS_PARENT_IT_CANCEL, "Send SMS to parent when it test online class is cancelled: "
								+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
					}	
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.SMS_PARENT_IT_CANCEL, "Send SMS to parent when it test online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send SMS to parent when it test online class is cancelled:" + e.getMessage(), e);
				}		
				
				try{
					Date datePoint = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48));
					if(!onlineClassInfoForEmailAndSMS.getScheduledDateTime().after(datePoint)) {
						Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
						EMail.sendToItTheItOnlineClassIsCancelledEmail(onlineClassInfoForEmailAndSMS, student);
						securityService.logAudit(Level.INFO, Category.EMAIL_IT_IT_CANCEL, "Send Email to it when it test online class is cancelled: "
								+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));

					}				
				}catch (Exception e){
					securityService.logAudit(Level.ERROR, Category.EMAIL_IT_IT_CANCEL, "Send Email to it when it test online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send Email to it when it test online class is cancelled:" + e.getMessage(), e);
				}
				
				break;
				
			case DEMO:
			case ASSESSMENT2:
				try {
					for(Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
						SMS.sendDemoOnlineClassIsCancelledToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
						securityService.logAudit(Level.INFO, Category.SMS_PARENT_DEMO_CANCEL, "Send SMS to parent when demo online class is cancelled: "
								+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
					}
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.SMS_PARENT_DEMO_CANCEL, "Send SMS to parent when demo online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send SMS to parent when demo online class is cancelled:" + e.getMessage(), e);
				}		
				
				try {
					Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
					EMail.sendToTeacherWhenDemoOnlineClassIsCanceledEmail(onlineClassInfoForEmailAndSMS, student, course);
					securityService.logAudit(Level.INFO, Category.EMAIL_TEACHER_DEMO_CANCEL, "Send Email to teacher when demo online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHER_DEMO_CANCEL, "Send Email to teacher when demo online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send Email to teacher when demo online class is cancelled:" + e.getMessage(), e);
				}
				break;

			case TRIAL: // trial为从demo中拆出
				try {
					for(Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
						SMS.sendTrialOnlineClassIsCancelledToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
						securityService.logAudit(Level.INFO, Category.SMS_PARENT_TRIAL_CANCEL, "Send SMS to parent when trial online class is cancelled: "
								+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
					}
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.SMS_PARENT_TRIAL_CANCEL, "Send SMS to parent when trial online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send SMS to parent when trial online class is cancelled:" + e.getMessage(), e);
				}		
				
				try {
					Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
					EMail.sendToTeacherWhenDemoOnlineClassIsCanceledEmail(onlineClassInfoForEmailAndSMS, student, course);
					securityService.logAudit(Level.INFO, Category.EMAIL_TEACHER_TRIAL_CANCEL, "Send Email to teacher when trial online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHER_TRIAL_CANCEL, "Send Email to teacher when trial online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send Email to teacher when demo online class is cancelled:" + e.getMessage(), e);
				}
				break;
				
			case GUIDE:					
				try {
					for(Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
						SMS.sendGuideOnlineClassIsCancelledToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
						securityService.logAudit(Level.INFO, Category.SMS_PARENT_GUIDE_CANCEL, "Send SMS to parent when guide online class is cancelled: "
								+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
					}	
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.SMS_PARENT_GUIDE_CANCEL, "Send SMS to parent when guide online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send SMS to parent when guide online class is cancelled:" + e.getMessage(), e);
				}
				
				try {
					Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
					EMail.sendToEducationTheGuideOnlineClassIsCancelledEmail(onlineClassInfoForEmailAndSMS, student);
					securityService.logAudit(Level.INFO, Category.EMAIL_EDUCATION_GUIDE_CANCEL, "Send Email to education when guide online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.EMAIL_EDUCATION_GUIDE_CANCEL, "Send Email to education when guide online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send Email to education when guide online class is cancelled:" + e.getMessage(), e);
				}
				break;
				
			case MAJOR:
			case ELECTIVE_LT:
				try {
					Date datePoint = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48));
					if(!onlineClassInfoForEmailAndSMS.getScheduledDateTime().after(datePoint)) {
						for(Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
							SMS.sendMajorOnlineClassIsCancelledToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
							securityService.logAudit(Level.INFO, Category.SMS_PARENT_MAJOR_CANCEL, "Send SMS to parent when major online class is cancelled: "
									+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
						}
					}			
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.SMS_PARENT_MAJOR_CANCEL, "Send SMS to parent when major online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send SMS to parent when major online class is cancelled:" + e.getMessage(), e);
				}

				try {
					Date datePoint = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48));
					if(!onlineClassInfoForEmailAndSMS.getScheduledDateTime().after(datePoint)) {
						Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
						EMail.sendToTeacherWhenOneOnOneOnlineClassIsCancelledIn48HoursEmail(onlineClassInfoForEmailAndSMS, student);
						securityService.logAudit(Level.INFO, Category.EMAIL_TEACHER_MAJOR_CANCEL, "Send Email to teacher when major online class is cancelled: "
								+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					}			
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHER_MAJOR_CANCEL, "Send Email to teacher when major online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send Email to teacher when major online class is cancelled:" + e.getMessage(), e);
				}
				break;
			case NORMAL: // 目前一堆多课程的type会是normal，normal课程也只放一对多课，所以发信这样设置，待一堆多课程具体细节敲定后可能会有更改
				try {
					for(Student student : onlineClassInfoForEmailAndSMS.getStudents()) {
						for(Parent parent : student.getFamily().getParents()) {
							SMS.sendOneToManyOnlineClassIsCancelledToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
							securityService.logAudit(Level.INFO, Category.SMS_PARENT_ONE_TO_MANY_CANCEL, "Send SMS to parent when one to many online class is cancelled: "
									+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
						}
					}
				}catch(Exception e) {
					securityService.logAudit(Level.ERROR, Category.SMS_PARENT_ONE_TO_MANY_CANCEL, "Send SMS to parent when one to many online class is cancelled: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
					logger.error("Exception Send SMS to parent when one to many online class is cancelled:" + e.getMessage(), e);
				}
				break;


			default:
				break;
		}

	}
	
	private void sendEmailAndSMSWhenOnlineClassIsBooked(Course course, OnlineClass onlineClassInfoForEmailAndSMS) {
		OnlineClass findOnlineClass = onlineClassRepository.find(onlineClassInfoForEmailAndSMS.getId());
		if(null != findOnlineClass && findOnlineClass.getStatus() != Status.BOOKED) {
			return;
		}
		Date now = new Date(System.currentTimeMillis());
		if(now.after(onlineClassInfoForEmailAndSMS.getScheduledDateTime())){
			return;
		}
		switch (course.getType()) {
		case IT_TEST:
			try {
				for (Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
					SMS.sendItOnlineClassScheduledTimeToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
					securityService.logAudit(Level.INFO, Category.SMS_PARENT_IT_BOOK, "Send SMS to parent when it test online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName()
							+ ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
				}
			} catch (Exception e) {
				securityService.logAudit(Level.ERROR, Category.SMS_PARENT_IT_BOOK, "Send SMS to parent when it test online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName()
						+ ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send SMS to parent when it test online class is booked:" + e.getMessage(), e);
			}

			try {
				Date datePoint = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48));
				if (!onlineClassInfoForEmailAndSMS.getScheduledDateTime().after(datePoint)) {
					Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
					EMail.sendToItTheItOnlineClassIsBookedEmail(onlineClassInfoForEmailAndSMS, student);
					securityService.logAudit(Level.INFO, Category.EMAIL_IT_IT_BOOKED, "Send Email to it when it test online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName()
							+ ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				}
			} catch (Exception e) {
				securityService.logAudit(Level.ERROR, Category.EMAIL_IT_IT_BOOKED, "Send Email to it when it test online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName()
						+ ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send Email to it when it test online class is booked:" + e.getMessage(), e);
			}

			break;

		case DEMO:
		case ASSESSMENT2:
			try {
				for (Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
					SMS.sendDemoOnlineClassScheduledTimeToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
					securityService.logAudit(
							Level.INFO,
							Category.SMS_PARENT_DEMO_BOOK,
							"Send SMS to parent when demo online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':'
									+ onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':'
									+ DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
				}
			} catch (Exception e) {
				securityService.logAudit(
						Level.ERROR,
						Category.SMS_PARENT_DEMO_BOOK,
						"Send SMS to parent when demo online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':'
								+ onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':'
								+ DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send SMS to parent when demo online class is booked:" + e.getMessage(), e);
			}

			try {
				Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
				EMail.sendToTeacherWhenDemoOnlineClassIsBookedEmail(onlineClassInfoForEmailAndSMS, student, course);
				securityService.logAudit(
						Level.INFO,
						Category.EMAIL_TEACHER_DEMO_BOOK,
						"Send Email to teacher when demo online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':'
								+ onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':'
								+ DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
			} catch (Exception e) {
				securityService.logAudit(
						Level.ERROR,
						Category.EMAIL_TEACHER_DEMO_BOOK,
						"Send Email to teacher when demo online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':'
								+ onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':'
								+ DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send Email to teacher when demo online class is booked:" + e.getMessage(), e);
			}

			break;

		case TRIAL: // trial为从demo中拆出
			try {
				for(Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
					SMS.sendTrialOnlineClassScheduledTimeToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
					securityService.logAudit(Level.INFO, Category.SMS_PARENT_TRIAL_BOOK, "Send SMS to parent when trial online class is booked: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
				}
			}catch(Exception e) {
				securityService.logAudit(Level.ERROR, Category.SMS_PARENT_TRIAL_BOOK, "Send SMS to parent when trial online class is booked: "
						+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send SMS to parent when trial online class is booked:" + e.getMessage(), e);
			}
			
			try {
				Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
				EMail.sendToTeacherWhenDemoOnlineClassIsBookedEmail(onlineClassInfoForEmailAndSMS, student, course);
				securityService.logAudit(Level.INFO, Category.EMAIL_TEACHER_TRIAL_BOOK, "Send Email to teacher when trial online class is booked: "
						+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
			}catch(Exception e) {
				securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHER_TRIAL_BOOK, "Send Email to teacher when trial online class is booked: "
						+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + onlineClassInfoForEmailAndSMS.getStudents().get(0).getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send Email to teacher when trial online class is booked:" + e.getMessage(), e);
			}
			
			break;

		case GUIDE:
			try {
				for (Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
					SMS.sendGuideOnlineClassIsBookedToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
					securityService.logAudit(Level.INFO, Category.SMS_PARENT_GUIDE_BOOK, "Send SMS to parent when guide online class is booked: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2)
							+ ':' + parent.getMobile());
				}
			} catch (Exception e) {
				securityService.logAudit(Level.ERROR, Category.SMS_PARENT_GUIDE_BOOK, "Send SMS to parent when guide online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName()
						+ ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send SMS to parent when guide online class is booked:" + e.getMessage(), e);
			}

			try {
				Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
				EMail.sendToEducationTheGuideOnlineClassIsBookedEmail(onlineClassInfoForEmailAndSMS, student);
				securityService.logAudit(Level.INFO, Category.EMAIL_EDUCATION_GUIDE_BOOK, "Send Email to education when guide online class is booked: "
						+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
			} catch (Exception e) {
				securityService.logAudit(Level.ERROR, Category.EMAIL_EDUCATION_GUIDE_BOOK, "Send Email to education when guide online class is booked: "
						+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send Email to education when guide online class is booked:" + e.getMessage(), e);
			}

			break;

		case MAJOR:
		case ELECTIVE_LT:
			try {
				Date datePoint = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48));
				if (!onlineClassInfoForEmailAndSMS.getScheduledDateTime().after(datePoint)) {
					for (Parent parent : onlineClassInfoForEmailAndSMS.getStudents().get(0).getFamily().getParents()) {
						SMS.sendMajorOnlineClassIsBookedToParentsSMS(parent.getMobile(), onlineClassInfoForEmailAndSMS);
						securityService.logAudit(
								Level.INFO,
								Category.SMS_PARENT_MAJOR_BOOK,
								"Send SMS to parent when major online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':'
										+ DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2) + ':' + parent.getMobile());
					}
				}
			} catch (Exception e) {
				securityService.logAudit(Level.ERROR, Category.SMS_PARENT_MAJOR_BOOK, "Send SMS to parent when major online class is booked: " + onlineClassInfoForEmailAndSMS.getTeacher().getName()
						+ ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send SMS to parent when major online class is booked:" + e.getMessage(), e);
			}

			try {
				Date datePoint = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48));
				if (!onlineClassInfoForEmailAndSMS.getScheduledDateTime().after(datePoint)) {
					Student student = studentRepository.find(onlineClassInfoForEmailAndSMS.getStudents().get(0).getId());
					EMail.sendToTeacherWhenOneOnOneOnlineClassIsBookedIn48HoursEmail(onlineClassInfoForEmailAndSMS, student);
					securityService.logAudit(Level.INFO, Category.EMAIL_TEACHER_MAJOR_BOOK, "Send Email to teacher when major online class is booked: "
							+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				}
			} catch (Exception e) {
				securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHER_MAJOR_BOOK, "Send Email to teacher when major online class is booked: "
						+ onlineClassInfoForEmailAndSMS.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClassInfoForEmailAndSMS.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
				logger.error("Exception Send Email to teacher when major online class is booked:" + e.getMessage(), e);
			}

			break;


		case NORMAL: // 目前一堆多课程的type会是normal，normal课程也只放一对多课，所以发信这样设置，待一堆多课程具体细节敲定后可能会有更改
			// TODO 报名成功后 and 教研确认开课后
			break;
		default:
			break;
		}

	}
	
	// teacher noshow email
	private void sendToEducationTeacherNoShowEmail(OnlineClass onlineClass) {
		try {
			Student student = studentRepository.find(onlineClass.getStudents().get(0).getId());
			EMail.sendToEducationTeacherNoShowEmail(onlineClass, student);
		}catch(Exception e) {
			securityService.logAudit(Level.ERROR, Category.EMAIL_EDUCATION_TEACHER_NOSHOW, "Send Email to education when teacher no show the class: "
					+ onlineClass.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
			logger.error("Exception Send Email to education when teacher no show the class:" + e.getMessage(), e);
		}
	}
	
	// student noshow email
	private void sendToEducationStudentNoShowEmail(OnlineClass onlineClass, Student student) {
		try {
			student = studentRepository.find(student.getId());
			EMail.sendToEducationStudentNoShowEmail(onlineClass, student);
		}catch(Exception e) {
			securityService.logAudit(Level.ERROR, Category.EMAIL_EDUCATION_STUDENT_NOSHOW, "Send Email to education when student noshow the class: "
					+ onlineClass.getTeacher().getName() + ':' + DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
			logger.error("Exception Send Email to education when teacher no show the class:" + e.getMessage(), e);
		}
	}
	
	// 试听课完成	 	对应销售 	 
	private void sendToSalesTheDemoOnlineClassIsFinishedEmail(OnlineClass onlineClass) {
		try {
			Student student = studentRepository.find(onlineClass.getStudents().get(0).getId());
			EMail.sendToSaleTheDemoOnlineClassIsFinishedEmail(onlineClass, student);
		}catch(Exception e) {
			securityService.logAudit(Level.ERROR, Category.EMAIL_EDUCATION_TRIAL_FINISH, "Send Email to sale when the student trial online class is finished: "
					+ onlineClass.getStudents().get(0).getName() + ':' + DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
			logger.error("Exception Send Email to sale when student trial online class is finished:" + e.getMessage(), e);
		}
	}
	
	// IT测试完成	 	对应销售
	private void sendToSalesTheItTestOnlineClassIsFinishedEmail(OnlineClass onlineClass) {
		try {
			Student student = studentRepository.find(onlineClass.getStudents().get(0).getId());
			EMail.sendToSaleTheItOnlineClassIsFinishedEmail(onlineClass, student);
		}catch(Exception e) {
			securityService.logAudit(Level.ERROR, Category.EMAIL_EDUCATION_IT_TEST_FINISH, "Send Email to sale when the student it test online class is finished: "
					+ onlineClass.getStudents().get(0).getName() + ':' + DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
			logger.error("Exception Send Email to sale when student it test online class is finished:" + e.getMessage(), e);
		}
	}
	
	// 课时即将耗尽	 	对应销售(改为CLT)
	private void sendToCLTTheLearningProgressIsComingToFinishEmail(LearningProgress learningProgress) {
		try {
			EMail.sendToCLTTheLearningProgressIsComingToFinishEmail(learningProgress);
		}catch(Exception e) {
			securityService.logAudit(Level.ERROR, Category.EMAIL_CLT_THREE_CLASS_HOUR_LEFT, "Send Email to CLT when the student learning progress is coming to finish: "
					+ learningProgress.getStudent().getName());
			logger.error("Exception Send Email to CLT when the student learning progress is coming to finish:" + e.getMessage(), e);
		}
	}
	
	// 课程完成提醒	本次购买课程已上完	对应销售(+ CLT)
	private void sendToSalesAndCLTTheLearningProgressFinishedEmail(LearningProgress learningProgress) {
		try {
			//EMail.sendToSaleTheLearningProgressIsComingToFinishEmail(learningProgress);
			EMail.sendToSaleAndCLTTheLearningProgressIsFinishedEmail(learningProgress);
		}catch(Exception e) {
			securityService.logAudit(Level.ERROR, Category.EMAIL_SALE_LEARNING_PROGRESS_FINISH, "Send Email to sale when the student learning progress is finished: "
					+ learningProgress.getStudent().getName());
			logger.error("Exception Send Email to sale when the student learning progress is finished:" + e.getMessage(), e);
		}
	}
	
	// 课程完成提醒	本次购买课程已上完	家长
	private void sendLearningProgressIsFinishedToParentSMS(LearningProgress learningProgress) {
		try {
			if(learningProgress.getLeftClassHour() == 0 && learningProgress.getStatus() == LearningProgress.Status.FINISHED){
				for(Parent parent : learningProgress.getStudent().getFamily().getParents()) {
					SMS.sendLearningProgressIsFinishedToParentSMS(parent.getMobile(), learningProgress);
				}					
			}			
		}catch(Exception e) {
			securityService.logAudit(Level.ERROR, Category.SMS_PARENT_LEARNING_PROGRESS_FINISH, "Send SMS to parent when the student learning progress is finished: "
					+ learningProgress.getStudent().getName());
			logger.error("Exception Send Email to sale when the student learning progress is finished:" + e.getMessage(), e);
		}
	}
	
	// finish as schedule 发信
	private void sendSMSandEmailWhenOnlineClassIsFinshedAsScheduled(OnlineClass onlineClass) {
		Course course = onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
		if(course.getType() == Type.TRIAL) { // trial课从Type demo 中取出,且废除demo课,所以直接将DEMO 改为TRIAL
			sendToSalesTheDemoOnlineClassIsFinishedEmail(onlineClass);
		}else if(course.getType() == Type.IT_TEST) {
			sendToSalesTheItTestOnlineClassIsFinishedEmail(onlineClass);
		}
		for(Student student : onlineClass.getStudents()) {
			for(LearningProgress learningProgress : student.getLearningProgresses()) {
				if((learningProgress.getCourse().getType() == Type.MAJOR ||learningProgress.getCourse().getType() == Type.ELECTIVE_LT)
						&& learningProgress.getCourse().getId() == course.getId()) {
					if(learningProgress.getLeftClassHour() == 3) {
						sendToCLTTheLearningProgressIsComingToFinishEmail(learningProgress);
					}
					
					if(learningProgress.getLeftClassHour() == 0) {
						sendToSalesAndCLTTheLearningProgressFinishedEmail(learningProgress);
						sendLearningProgressIsFinishedToParentSMS(learningProgress);
					}							
				}
			}				
		}
		
		if(course.getMode() == Mode.ONE_TO_MANY && !onlineClass.getNoShowStudents().isEmpty()) {
			for(Student student : onlineClass.getNoShowStudents()) {
				sendToEducationStudentNoShowEmail(onlineClass, student);
			}
		}
	}
	
	public OnlineClass findLatestBookedClassByStudentId(long studentId) {
		logger.info("findLatestBookedClassByStudentId studentId = {}", studentId);
		return onlineClassRepository.findLatestBookedClassByStudentId(studentId);
	}
	
	public Count countBackupDutyByTeacherIdAndStartDateAndEndDate(long teacherId, DateParam startDateParam, DateParam endDateParam) {
		logger.info("countBackupDutyByTeacherIdAndStartDateAndEndDate, teacherId: {}, startDate: {}, endDate: {}", teacherId, startDateParam.getValue(), endDateParam.getValue());
		
		long backupDutyCount = onlineClassRepository.countBackupDutyByTeacherIdAndStartDateAndEndDate(teacherId, startDateParam.getValue(), endDateParam.getValue());
		
		return new Count(backupDutyCount);
	}
	
	
	/*
	 * Attention  !!!!!!!!!!, 
	 * this method can only be invoked in Controller layer
	 */
	public void createDBYClassroomTask(OnlineClass onlineClass) {
		User user = securityService.getCurrentUser();
		if(user == null) {
			user = (User) staffRepository.findByUsername(Configurations.System.SYSTEM_USERNAME);
		}
		final User operator = user;
		
		
		createDBYClassroom(onlineClass, operator);
		/*ExecutorService executorService = Executors.newSingleThreadExecutor();
		FutureTask<OnlineClass> futureTask = new FutureTask<OnlineClass>(new Callable<OnlineClass>(){
			@Override
			public OnlineClass call() throws Exception {
			}
		});
		executorService.submit(futureTask);*/
	
	}

//	public OnlineClassPeakViewPreWeek getPeakTimeDataForSchedule() {
//		return null;
//	}
	
	public List<LessonsView> listForLessons(long studentId,int rowNum,int currNum){
		logger.info("list for lessons by studentId = {}", studentId);
		List<LessonsView> lessonsViews = onlineClassRepository.listForLessons(studentId,rowNum,currNum);
		for(LessonsView lessonsView:lessonsViews){
			Medal medal = onlineClassRepository.findMedalId(lessonsView.getOnlineClassId(),studentId);
			lessonsView.setMedalId(medal.getId());
			lessonsView.setMedalName(medal.getName());
		}
		return lessonsViews;
	}
	public long countForLessons(long studentId,int rowNum,int currNum){
		logger.info("count for lessons by studentId = {}", studentId);
		return onlineClassRepository.countForLessons(studentId,rowNum,currNum);
	}
	
	public LessonsView findStudiedLessonsDetail(long onlineClassId,long teacherCommentId,long studentId){
		logger.info("findStudiedLessonsDetail by onlineClassId = {},teacherCommentId = {}", onlineClassId,teacherCommentId);
		LessonsView lessonsView = onlineClassRepository.findStudiedLessonsDetail(onlineClassId,teacherCommentId);
		Medal medal = onlineClassRepository.findMedalId(onlineClassId,studentId);
		lessonsView.setMedalId(medal.getId());
		lessonsView.setMedalName(medal.getName());
		return lessonsView;
	}
	
	public long countByStudentIdAndFinishedAsScheduledAndThisWeek(long studentId) {
		logger.info("countByStudentIdAndFinishedAsScheduledAndThisWeek studentId = {}", studentId);
		return onlineClassRepository.countByStudentIdAndFinishedAsScheduledAndThisWeek(studentId);
	}
	
	private void checkOnlineClassParameter(final OnlineClass onlineClass){
		if (onlineClass == null || onlineClass.getId() == 0){
			throw new IllegalStateException("The onlineClass can not be null or the id of class should > 0");
		}
	}
	
	public OnlineClass checkTrailClassByStudentId(Long studentId,Date startDate,Date endDate){
		List<OnlineClass> onlineClassList = onlineClassRepository.findBookedTrialOnlineClassByStartDateAndEndDate(startDate, endDate);
		Student student = studentRepository.find(studentId);
		if (onlineClassList.size() != 0){
			for(OnlineClass onlineClass:onlineClassList){
				if(onlineClass.getStudents().contains(student)){
					return onlineClass;
				}
			}
		}
		return new OnlineClass();
	}
	
//	@GET
//	@Path("/listForFireman")
//	public List<OnlineClass> listForFireman(@QueryParam("courseIds") List<Long> courseIds, @QueryParam("scheduledDateTimeFrom") DateTimeParam scheduledDateTimeFrom, @QueryParam("scheduledDateTimeTo") DateTimeParam scheduledDateTimeTo, @QueryParam("statusList") List<Status> statusList, @QueryParam("start") int start, @QueryParam("length") int length) {
//		logger.info("list course with params: scheduleFrom = {}, scheduleTo = {}, start = {}, length = {}.", scheduledDateTimeFrom, scheduledDateTimeTo, start, length);
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
//	public Count countForFireman (@QueryParam("courseIds") List<Long> courseIds, @QueryParam("scheduledDateTimeFrom") DateTimeParam scheduledDateTimeFrom, @QueryParam("scheduledDateTimeTo") DateTimeParam scheduledDateTimeTo, @QueryParam("statusList") List<Status> statusList) {
//		logger.info("list course with params: scheduleFrom = {}, scheduleTo = {}, status = {}, hasClassroom = {}.", scheduledDateTimeFrom, scheduledDateTimeTo);
//		Count count = new Count(onlineClassAccessor.countForFireman(courseIds, scheduledDateTimeFrom, scheduledDateTimeTo, statusList));
//		return count;
//	}
	private void checkStudents(final OnlineClass onlineClass){
		List<Student> students = onlineClass.getStudents();
		List<Student> cancelledStudents = onlineClass.getCancelledStudents();
		
		if (cancelledStudents != null && students != null){
			for (Student student : cancelledStudents){
				if (!students.contains(student)){
					throw new IllegalStateException("You try to cancel the student not belong to you");
				}
			}
		}
	}
	public String doBookOnToManyForOpen(long teacherId,long studentId,long onlineClassId,String time){
		logger.info("doBookOnToManyForOpen1: studentId = {}, onlineClassId = {}.", studentId, onlineClassId);
		String operation="doBookOnToManyForOpen1 studentId="+studentId+",onlineClassId="+onlineClassId;
		securityService.logAudit(Level.INFO, Category.IMPORT_EXCEL_DATA, operation.toString());
		long cou1 = openClassRepository.countOpenClassStudentById(onlineClassId, studentId);
		if(cou1>0){
			return "";
		}
		long cou = onlineClassRepository.findOnlineClassByScheduledDateTime(DateTimeUtils.parse(time, DateTimeUtils.DATETIME_FORMAT2),studentId);
		if(cou>0){
			return "您已经在"+time+"约了其他课程";
		}
		//onlineClassRepository.doBookOnToManyForOpen(studentId, onlineClassId);
		String msg = substractOpenClassHour(studentId, onlineClassId);
		if(!"".equals(msg)){
			return msg;
		}
		createTeacherComment(teacherId, studentId, onlineClassId);
		sendSmsForSignUp(studentId, time,teacherId);
		return "";
	}

	private void createTeacherComment(long teacherId, long studentId,
			long onlineClassId) {
		TeacherComment t = new TeacherComment();
		OnlineClass o = new OnlineClass();
		o.setId(onlineClassId);
		Student s = new Student();
		s.setId(studentId);
		Teacher te = new Teacher();
		te.setId(teacherId);
		t.setOnlineClass(o);
		t.setStudent(s);
		t.setTeacher(te);
		teacherCommentRepository.create(t);
	}

	private String substractOpenClassHour(long studentId, long onlineClassId) {
		OpenClassDesc opdesc = openClassRepository.findOpenClassDescByOnlineClassId(onlineClassId);
		if(opdesc!=null && opdesc.getOpType()==OpenClassType.DEDICATED){
			boolean flag = false;
			Student st = studentRepository.find(studentId);
			String[] cid = opdesc.getChannelId().split(",");
			for(int i=0;i<cid.length;i++){
				String id= cid[i]==null?"-1":cid[i];
				if(Long.valueOf(id)==st.getChannel().getId()){
					flag = true;
					break;
				}
			}
			if(!flag){
				LearningProgress lp = learningProgressRepository.findByStudentIdAndOnlineClassId(studentId, onlineClassId);
				if(lp!=null && lp.getLeftClassHour()>0){
					onlineClassRepository.doBookOnToManyForOpen(studentId, onlineClassId);
					learningProgressRepository.subtractOpenClassHour(lp.getId());
				}else{
					return "亲，您的精品公开课已剩余0课时，请联系VIPKID服务人员，400-005-6666。";
				}
			}else{
				onlineClassRepository.doBookOnToManyForOpen(studentId, onlineClassId);
			}
		}else{
			onlineClassRepository.doBookOnToManyForOpen(studentId, onlineClassId);
		}
		return "";
	}
	
	/**
	 * 公开课看回放是扣课时操作
	 * @param studentId
	 * @param onlineClassId
	 * @param oc
	 */
	public String doSubstractForOpenClassReplay(long studentId, long onlineClassId, OnlineClass oc) {
		long count = openClassRepository.countOpenClassStudentById(onlineClassId, studentId);
		if(count==0){
			//onlineClassRepository.doBookOnToManyForOpen(studentId, onlineClassId);
			String msg = substractOpenClassHour(studentId, onlineClassId);
			if(!"".equals(msg)) return msg;
			if(oc.getTeacher()!=null){
				createTeacherComment(oc.getTeacher().getId(), studentId, onlineClassId);
			}
		}
		return "";
	}
	
	private void sendSmsForSignUp(long studentId, String time,long teacherId) {
		Date stime = DateTimeUtils.parse(time, DateTimeUtils.DATETIME_FORMAT2);
		long l = stime.getTime()-new Date().getTime();
		OnlineClass olClass = new OnlineClass();
		olClass.setScheduledDateTime(stime);
		if(l>2*60*60*1000){
			Teacher teacher = teacherRepository.find(teacherId);
			olClass.setTeacher(teacher);
			Student student = studentRepository.find(studentId);
			for(Parent parent : student.getFamily().getParents()) {
				SMS.sendOpenClassReminderToParentSMSBeForeTwoHours(parent.getMobile(), student, olClass);
				logger.info("Success: sendSmsForSignUp from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
			}
		}
	}
	
	public long countTrailByStudentId(long studentId) {
		logger.info("countByStudentIdAndCourseType: studentId = {}, type = {}.", studentId, Type.TRIAL);
		return onlineClassRepository.countByStudentIdAndCourseType(studentId, Type.TRIAL);
	}
	
	public List<OnlineClass> listForStudentComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Integer scores, Boolean existStudentComment, String finishType, String cltId, Integer start, Integer length) {
		return onlineClassRepository.listForStudentComments(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, scores, existStudentComment, finishType, cltId, start, length);
	}
	
	public Count countForStudentComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Integer scores, Boolean existStudentComment, String finishType, String cltId) {
		return new Count(onlineClassRepository.countForStudentComments(courseIds, searchTeacherText, searchStudentText, scheduledDateTimeFrom, scheduledDateTimeTo, scores, existStudentComment, finishType, cltId, null));
	}
	
	public TeacherEvaluationVO listForTeacherEvaluation(List<Long> courseIds, String searchTeacherText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String finishType){
		TeacherEvaluationVO evaluation = new TeacherEvaluationVO();
		evaluation.setTotalNum(onlineClassRepository.countForStudentComments(courseIds, searchTeacherText, null, scheduledDateTimeFrom, scheduledDateTimeTo, null, null, finishType, null, new Boolean(true)));
		evaluation.setStar1(onlineClassRepository.countForStudentComments(courseIds, searchTeacherText, null, scheduledDateTimeFrom, scheduledDateTimeTo, new Integer(1), null, finishType, null, new Boolean(true)));
		evaluation.setStar2(onlineClassRepository.countForStudentComments(courseIds, searchTeacherText, null, scheduledDateTimeFrom, scheduledDateTimeTo, new Integer(2), null, finishType, null, new Boolean(true)));
		evaluation.setStar3(onlineClassRepository.countForStudentComments(courseIds, searchTeacherText, null, scheduledDateTimeFrom, scheduledDateTimeTo, new Integer(3), null, finishType, null, new Boolean(true)));
		evaluation.setStar4(onlineClassRepository.countForStudentComments(courseIds, searchTeacherText, null, scheduledDateTimeFrom, scheduledDateTimeTo, new Integer(4), null, finishType, null, new Boolean(true)));
		evaluation.setStar5(onlineClassRepository.countForStudentComments(courseIds, searchTeacherText, null, scheduledDateTimeFrom, scheduledDateTimeTo, new Integer(5), null, finishType, null, new Boolean(true)));
		return evaluation;
	}
	
	public Date findScheduledDateTimeById(long id){
		return onlineClassRepository.findScheduledDateTimeById(id);
	}

	
	public List<OnlineClassVo> listOnlineClassForCLT(Date scheduledTimeFrom, Date scheduledTimeTo, List<Long> courseIds, OnlineClass.Status status,
			String finishType,Long cltId, String teacherName, String searchText, Integer start, Integer length) {

		return onlineClassRepository.listOnlineClassForCLT(scheduledTimeFrom, scheduledTimeTo, courseIds, status, finishType, cltId, teacherName, searchText, start, length);
	}
	
	public long countOnlineClassForCLT(Date scheduledTimeFrom, Date scheduledTimeTo, List<Long> courseIds, OnlineClass.Status status,
			String finishType,Long cltId, String teacherName, String searchText) {

		return onlineClassRepository.countOnlineClassForCLT(scheduledTimeFrom, scheduledTimeTo, courseIds, status, finishType, cltId, teacherName, searchText);
	}
	
	public List<Date> findAvailableTimeSlotsWithParallelLimit(long courseId, DateParam startDateParam, DateParam endDateParam, Type type) {
		logger.info("find available classes for courseId = {}, startDate = {}, endDate = {} and limit trail parallel",
				courseId, DateTimeUtils.format(startDateParam.getValue(),DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		int parallelCount = Redis.Trial.DEFAULT_PARALLEL;
		List<Date> timeSlotList = null;
		
		switch (type) {
		case TRIAL:
		case ELECTIVE_LT:
		case ASSESSMENT2:
			parallelCount = getMaxParallelCountFromRedis();
			timeSlotList = onlineClassRepository.findOnlineClassTrialTimeSlotsWithParallelLimit(courseId, startDateParam.getValue(), endDateParam.getValue(), parallelCount);
			break;
		default:
			timeSlotList = onlineClassRepository.findOnlineClassTimeSlots(courseId, startDateParam.getValue(), endDateParam.getValue());
			break;
		}
		return timeSlotList;
	}
	
	public List<OnlineClassVO> findByStudentIdAndDateTime(long studentId, DateParam startDateParam, DateParam endDateParam) {
		logger.info("find onlineClasses for studentId = {}, startDate = {}, endDate = {}", studentId, DateTimeUtils.format(startDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT), DateTimeUtils.format(endDateParam.getValue(), DateTimeUtils.DATETIME_FORMAT));
		List<OnlineClass>  onlineClassList = onlineClassRepository.findByStudentIdAndStartDateAndEndDate(studentId, startDateParam.getValue(), endDateParam.getValue());
		return OnlineClassHandler.convert2VOForCommentsList(onlineClassList);
	}

}
