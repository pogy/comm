package com.vipkid.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Country;
import com.vipkid.model.Course;
import com.vipkid.model.Gender;
import com.vipkid.model.ItTest.FinalResult;
import com.vipkid.model.Leads;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Partner;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Teacher;
import com.vipkid.model.Teacher.Hide;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.TeacherApplication;
import com.vipkid.model.TeacherLifeCycleLog;
import com.vipkid.model.User;
import com.vipkid.model.User.Status;
import com.vipkid.repository.LeadsRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.repository.UserRepository;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.AvailableCondition;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.ParentPortalTeacherQueryResult;
import com.vipkid.service.pojo.StringWrapper;
import com.vipkid.service.pojo.TeacherCondition;
import com.vipkid.service.pojo.TeacherInfoVO;
import com.vipkid.service.pojo.TeacherNameView;
import com.vipkid.service.pojo.TeacherView;
import com.vipkid.service.pojo.parent.OnlineClassesView;
import com.vipkid.service.pojo.parent.TeView;
import com.vipkid.service.pojo.parent.TeacherDetailView;
import com.vipkid.service.pojo.parent.TeachersView;
import com.vipkid.util.Configurations;

@Service
public class TeacherService {
	private Logger logger = LoggerFactory.getLogger(TeacherService.class.getSimpleName());

	@Resource
	private TeacherRepository teacherRepository;

	@Resource
	private UserRepository userRepository;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private StudentRepository studentRepository;
	
	// 2015-08-31 需要进行leads查询和staff查询
	@Resource
	private LeadsRepository leadsRepository;
	
	@Resource
	private StaffRepository staffRepository;
	
	@Resource
	private TeacherLifeCycleLogService teacherLifeCycleLogService;

	// 2015-08-24 teacher lifeCycle operator;
	@Resource 
	private StaffService staffService;
	
	public Teacher find(long id) {
		logger.info("find teacher for id = {}", id);
		return teacherRepository.find(id);
	}

	public List<Teacher> list(List<String> lifeCycles, String search, Gender gender, Status status, Country country,
			DateTimeParam contractStartDate, DateTimeParam contractEndDate,
			DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo,
			Long certificatedCourseId, Long partnerId, String teacherType,
			FinalResult finalResult, Integer start, Integer length,String teacherTags) {
		logger.info(
				"list teacher with params: search = {}, gender = {}, status = {},, country = {}, contractStartDate = {}, contractEndDate = {}, certificatedCourseId = {}, teacherType = {}, testFinalResult = {}, start = {}, length = {}.",
				search, gender, status, country, contractStartDate, contractEndDate, certificatedCourseId, partnerId, teacherType, finalResult, start, length);
		return teacherRepository.list(lifeCycles, search, gender, status, country, contractStartDate, contractEndDate, scheduledDateTimeFrom, scheduledDateTimeTo, certificatedCourseId, partnerId,
				teacherType, finalResult, start, length,teacherTags);
	}
	
	public List<Teacher> list(List<String> lifeCycles, String search, Gender gender, Status status, Country country,
			DateTimeParam contractStartDate, DateTimeParam contractEndDate,
			DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo,
			Long certificatedCourseId, Long partnerId, String teacherType,
			FinalResult finalResult, Integer start, Integer length,String teacherTags,String strAccountType,
			// 2015-08-13 添加其他条件 TM-improve quit-time 时间段
			DateTimeParam quitStartDate,
			DateTimeParam quitEndDate,
			// 2015-08-13 添加其他条件 TM-improve apply-time 时间段 -- (teacher.registerDateTime)
			DateTimeParam applyFromDate,
			DateTimeParam applyEndDate,
			String applyResult,
			//2015-08-18interview需要这个
			Long interviewerId,
			Long practicumTeacherId,
			String[] managers) {
		logger.info(
				"list teacher with params: search = {}, gender = {}, status = {},, country = {}, contractStartDate = {}, contractEndDate = {}, certificatedCourseId = {}, teacherType = {}, testFinalResult = {}, start = {}, length = {},applyResult = {}.",
				search, gender, status, country, contractStartDate, contractEndDate, certificatedCourseId, partnerId, teacherType, finalResult, start, length);
		return teacherRepository.list(lifeCycles, search, gender, status, country, contractStartDate, contractEndDate, scheduledDateTimeFrom, scheduledDateTimeTo, certificatedCourseId, partnerId,
				teacherType, finalResult, start, length,teacherTags,strAccountType,
				quitStartDate, quitEndDate, applyFromDate, applyEndDate,applyResult,interviewerId,practicumTeacherId,managers);
	}

	public List<TeacherNameView> findBySearchCondition( String search,  long courseId, int length) {
		return teacherRepository.findBySearchCondition(search, courseId, length);
	}

	public Count count(List<String> lifeCycles, String search, Gender gender, Status status, LifeCycle lifeCycle,
			 Country country, DateTimeParam contractStartDate,DateTimeParam contractEndDate,
			 DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, 
			 Long certificatedCourseId, Long partnerId, String teacherType,
			 FinalResult finalResult,String teacherTags,String strAccountType,String applyResult,String interviewerId,String practicumTeacherId,String[] managers) {
		logger.info(
				"count teacher with params: search = {}, gender = {}, status = {}, country = {}, contractStartDate = {}, contractEndDate = {}, certificatedCourseId = {}, teacherType = {}, testFinalResult = {}.",
				search, gender, status, lifeCycle, country, contractStartDate, contractEndDate, certificatedCourseId, teacherType, finalResult);
		return new Count(teacherRepository.count(lifeCycles, search, gender, status, country, contractStartDate, contractEndDate, scheduledDateTimeFrom, scheduledDateTimeTo, certificatedCourseId,
				partnerId, teacherType, finalResult,teacherTags,strAccountType,applyResult,interviewerId,practicumTeacherId,managers));
	}

	public Teacher doResetPassword(long id) {
		Teacher teacher = teacherRepository.find(id);
		if (teacher == null) {
			throw new UserNotExistServiceException("Teacher[id: {}] is not exist.", id);
		} else {
			String newPassword = Configurations.Auth.DEFAULT_TEACHER_PASSWORD;
			teacher.setPassword(PasswordEncryptor.encrypt(newPassword));
			teacherRepository.update(teacher);

			securityService.logAudit(Level.INFO, Category.TEACHER_RESET_PASSWORD, "Reset password for teacher: " + teacher.getName());

			EMail.sendResetTeacherPasswordEmail(teacher.getName(), teacher.getEmail(), newPassword);
		}

		return teacher;
	}

	public Teacher doLock(long id) {
		Teacher teacher = teacherRepository.find(id);
		if (teacher == null) {
			throw new UserNotExistServiceException("Teacher[id: {}] is not exist.", id);
		} else {
			teacher.setStatus(Status.LOCKED);
			/*
			 * teacher.setCertificatedCourses(new ArrayList<Course>()); Calendar
			 * todayEnd = Calendar.getInstance(); todayEnd.setTime(new Date());
			 * todayEnd.set(Calendar.HOUR_OF_DAY, 23);
			 * todayEnd.set(Calendar.MINUTE, 59); todayEnd.set(Calendar.SECOND,
			 * 59); teacher.setContractEndDate(todayEnd.getTime());
			 */
			teacherRepository.update(teacher);

			securityService.logAudit(Level.INFO, Category.TEACHER_LOCK, "Lock teacher: " + teacher.getName());
		}

		return teacher;
	}

	public Teacher doUnlock(long id) {
		Teacher teacher = teacherRepository.find(id);
		if (teacher == null) {
			throw new UserNotExistServiceException("Teacher[id: {}] is not exist.", id);
		} else {
			teacher.setStatus(Status.NORMAL);
			teacherRepository.update(teacher);

			securityService.logAudit(Level.INFO, Category.TEACHER_UNLOCK, "Unlock teacher: " + teacher.getName());
		}

		return teacher;
	}

	public ParentPortalTeacherQueryResult query( long studentUserId, String teacherCondition, String availableCondition,
			 Hide hide, long amount, long start, long courseId,
			 DateParam startDateParam, DateParam endDateParam, DateParam dateFilter,
			 DateParam timeFilter) {
		ParentPortalTeacherQueryResult result = new ParentPortalTeacherQueryResult();

		result.setAmount(amount);

		if (availableCondition.equalsIgnoreCase("ALL")) {
			result.setAvailableCondition(AvailableCondition.ALL);
		} else {
			result.setAvailableCondition(AvailableCondition.AVAILABLE);
		}

		if (teacherCondition.equalsIgnoreCase("ALL")) {
			result.setTeacherCondition(TeacherCondition.ALL);
		} else {
			result.setTeacherCondition(TeacherCondition.FAVERATE);
		}

		List<TeacherView> teachers = teacherRepository.query(studentUserId, teacherCondition, availableCondition, hide, amount, start, courseId, startDateParam.getValue(), endDateParam.getValue(),
				dateFilter != null ? dateFilter.getValue() : null, timeFilter != null ? timeFilter.getValue() : null);

		result.setTeachers(teachers);

		return result;
	}

	public List<Teacher> search( DateParam dateParam, String gender, long studentUserId,
			 String teacherName, Hide hide) {
		// logger.info("in search service: data = {}, gender = {}, teacherName = {}",
		// dataParam.getValue(), gender, teacherName);
		List<Teacher> searchTeacherResult = null;
		Date dateValue = null;
		if (dateParam != null){
			dateValue = dateParam.getValue();
		}
		searchTeacherResult = teacherRepository.search(gender, teacherName, hide, dateValue);
		return searchTeacherResult;
	}

	public Teacher create(Teacher teacher) {
		logger.info("create teacher: {}", teacher);

		Teacher findTeacher = teacherRepository.findByUsername(teacher.getUsername());
		if (findTeacher == null) {
			User creater = securityService.getCurrentUser();
			teacher.setCreater(creater);
			Staff lastEditor = (Staff) securityService.getCurrentUser();
			teacher.setLastEditor(lastEditor);
			teacher.setEmail(teacher.getUsername());
			teacher.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
			teacher.setSerialNumber(String.format("%05d", teacherRepository.totalCount() + 1));
			teacher.setLifeCycle(LifeCycle.REGULAR);
			teacher.setCertificates(teacher.getCertificates().trim());
			teacher.setRegisterDateTime(new Date());
			teacherRepository.create(teacher);

			securityService.logAudit(Level.INFO, Category.TEACHER_CREATE, "Create teacher: " + teacher.getName());

			return teacher;
		} else {
			throw new UserAlreadyExistServiceException("Teacher already exist.");
		}
	}

	/**
	 * teacher recruit apply update. it should send apply email to applicant and
	 * partner.
	 * 
	 * @param teacher
	 * @return
	 */

	public Teacher updateApply(Teacher teacher) {
		this.update(teacher);

		// 2015-07-10 被teacher update 调用，导致每次update teacher时，就发送了apply thx email。
		// 仅signup状态的teacher发送邮件。 2015-08-25 SIGNUP --> BASIC_INFO
		LifeCycle status = teacher.getLifeCycle();
		if (LifeCycle.BASIC_INFO == status) {
			EMail.sendToApplicantForApply(teacher);
		}
		securityService.logAudit(Level.INFO, Category.TEACHER_UPDATE, "updateApply teacher: " + teacher.getName());

		return teacher;
	}

    /**
     * 只更新基本信息，关联信息不更新
     * @param teacherInfoVO
     */
    public void updatePersonalInfo(TeacherInfoVO teacherInfoVO) {
        if (null != teacherInfoVO && null != teacherInfoVO.getId()) {
            Teacher teacherInDB = teacherRepository.find(teacherInfoVO.getId());
            List<OnlineClass> dbOnlineClasses = teacherInDB.getOnlineClasses();
            List<TeacherApplication> teacherApplications = teacherInDB.getTeacherApplications();
            List<Course> certificatedCourses = teacherInDB.getCertificatedCourses();

            
            boolean teacherChangeBankInfo = teacherInDB.checkBankInfoChanges(teacherInfoVO);
            
            	
            BeanUtils.copyProperties(teacherInfoVO, teacherInDB);
            teacherInDB.setOnlineClasses(dbOnlineClasses);
            teacherInDB.setTeacherApplications(teacherApplications);
            teacherInDB.setCertificatedCourses(certificatedCourses);
            teacherRepository.update(teacherInDB);
            
            if(teacherChangeBankInfo){
            	securityService.logAudit(Level.INFO, Category.TEACHER_BANK_INFO, "teacherChangeBankInfo teacher's id is:"+teacherInDB.getId() 
            			+ ", teacher's bank account name is:" + teacherInDB.getBankAccountName() 
            			+ ", teacher's bank address is:" + teacherInDB.getBankAddress()
            			+ ", teacher's bank card number is:"+ teacherInDB.getBankCardNumber()
            			+ ", teacher's bank name is:" + teacherInDB.getBankName()
            			+ ", teacher's bank SWIFT code is:" + teacherInDB.getBankSWIFTCode()
            			+ ", teacher's pay pal account is:" + teacherInDB.getPayPalAccount(), teacherInDB);
            } 
            securityService.logAudit(Level.INFO, Category.TEACHER_UPDATE, "updatePersonalInfo teacher's name: " + teacherInDB.getName());
        }
    }

	public Teacher update(Teacher teacherFromJson) {
		if (null == teacherFromJson) {
			logger.error("error teacher: null");
			return null;
		}
		Teacher teacher = teacherRepository.find(teacherFromJson.getId());
		if (null == teacher) {
			logger.error("error teacher: null");
			return null;	
		}
		       
        boolean teacherChangeBankInfo = teacher.checkBankInfoChanges(teacherFromJson);
        
        //如果teacher 已经被quit 不在改变 合同结束时间 start 
		boolean flag = true;
		if(teacher.getLifeCycle().equals(LifeCycle.QUIT)
				&&teacherFromJson.getLifeCycle().equals(teacher.getLifeCycle())){
			flag=false;
		}
		//如果teacher 已经被quit 不在改变 合同结束时间 end

		// 2015-08-28  VK-2187 -- teacher show name 
		if (LifeCycle.BASIC_INFO == teacherFromJson.getLifeCycle()) {
			String strRealName = teacherFromJson.getRealName();
						
			strRealName = strRealName.trim();
			int endIndex = strRealName.indexOf(" ");
			if (endIndex<0) {
				teacherFromJson.setName(strRealName);
			} else {
				String firstName = strRealName.substring(0, endIndex);
				String secondName = "";
				secondName = strRealName.substring(endIndex).trim();
				secondName = secondName.substring(0,1);
				String strName = firstName +" "+secondName.toUpperCase();
				teacherFromJson.setName(strName);
			}
		}
		
		User lastEditor = securityService.getCurrentUser();
		// 2015-08-24 lifeCycle log
		if (teacherFromJson.changeLifeCycle(teacher)) {
		
			TeacherLifeCycleLog changeLog = new TeacherLifeCycleLog();
			changeLog.setCreateDateTime(new Date());
			changeLog.setFromStatus(teacher.getLifeCycle().toString());
			User staff = userRepository.find(lastEditor.getId()); 
			changeLog.setOperator(staff);
			changeLog.setTeacher(teacher);
			changeLog.setToStatus(teacherFromJson.getLifeCycle().toString());
			
			teacherLifeCycleLogService.update(changeLog);
		}
				
		refreshTeacher(teacher, teacherFromJson);
		logger.info("update teacher: {}", teacher);
		teacher.setLastEditor(lastEditor);
		teacher.setEmail(teacher.getUsername());
		if (teacher.getPartner() != null) {
			Partner partner = teacher.getPartner();
			if (partner.getId() == 1) {
				teacher.setPartner(null);
			}
		}
		if (teacher.getLifeCycle().equals(LifeCycle.QUIT)&&flag) {
			teacher.getCertificatedCourses().clear();
			Calendar todayEnd = Calendar.getInstance();
			todayEnd.setTime(new Date());
			todayEnd.set(Calendar.HOUR_OF_DAY, 23);
			todayEnd.set(Calendar.MINUTE, 59);
			todayEnd.set(Calendar.SECOND, 59);
			teacher.setContractEndDate(todayEnd.getTime());
		}		

		// 2015-08-15 判断要执行quit操作
//		if (teacherFromJson.changeLifeCycle2Quit(teacher)) {
//			teacher.setOperator(teacherFromJson.getOperator());
//			teacher.setQuitTime(new Date());
//		}
		
		teacherRepository.update(teacher);
		
		if(teacherChangeBankInfo){
			securityService.logAudit(Level.INFO, Category.TEACHER_BANK_INFO, "teacherChangeBankInfo teacher's id is:"+teacher.getId() 
        			+ ", teacher's bank account name is:" + teacher.getBankAccountName() 
        			+ ", teacher's bank address is:" + teacher.getBankAddress()
        			+ ", teacher's bank card number is:"+ teacher.getBankCardNumber()
        			+ ", teacher's bank name is:" + teacher.getBankName()
        			+ ", teacher's bank SWIFT code is:" + teacher.getBankSWIFTCode()
        			+ ", teacher's pay pal account is:" + teacher.getPayPalAccount(), teacher);
  
        }   
		securityService.logAudit(Level.INFO, Category.TEACHER_UPDATE, "Update teacher: " + teacher.getName());

		return teacher;
	}

	public Teacher changePassword(long teacherId, String password, String originalPassword) {
		logger.info("change password for teacherId: {}, password: {}, originalPassword: {}", teacherId, password, originalPassword);

		Teacher teacher = teacherRepository.find(teacherId);
		if (teacher.getPassword().equals(PasswordEncryptor.encrypt(originalPassword))) {
			teacher.setPassword(PasswordEncryptor.encrypt(password));
		} else {
			throw new UserNotExistServiceException("Wrong password.");
		}

		securityService.logAudit(Level.INFO, Category.TEACHER_CHANGE_PASSWORD, "Change password for teacher: " + teacher.getName());

		return teacher;
	}

	public Teacher doNext(Teacher teacher) {
		// 2015-08-25 SIGNUP-->BASIC_INFO
		LifeCycle lifeCycle = teacher.getLifeCycle();
		if (lifeCycle != LifeCycle.SIGNUP && lifeCycle != LifeCycle.BASIC_INFO && lifeCycle != LifeCycle.INTERVIEW && 
				lifeCycle != LifeCycle.SIGN_CONTRACT && lifeCycle != LifeCycle.TRAINING && lifeCycle != LifeCycle.PRACTICUM) {
			throw new IllegalStateException("Invalide Status");
		}

		if (lifeCycle != null) {
			switch (lifeCycle) {
			case BASIC_INFO:// 2015-08-25 SIGNUP-->BASIC_INFO signup不通过此处更新状态。
				teacher.setLifeCycle(LifeCycle.INTERVIEW);
				break;
			case INTERVIEW:
				teacher.setLifeCycle(LifeCycle.SIGN_CONTRACT);
				break;
			case SIGN_CONTRACT:
				teacher.setLifeCycle(LifeCycle.TRAINING);
				break;
			case TRAINING:
				teacher.setLifeCycle(LifeCycle.PRACTICUM);
				break;
			case PRACTICUM:
				teacher.setLifeCycle(LifeCycle.REGULAR);
				break;
			default:
				break;

			}

			teacher.setLastEditor(securityService.getCurrentUser());
			teacher.setLastEditDateTime(new Date());
			teacherRepository.update(teacher);

			User lastEditor = securityService.getCurrentUser();
			// 2015-08-24 lifeCycle log
			Teacher oldTeacher = teacherRepository.find(teacher.getId());
			if (teacher.changeLifeCycle(oldTeacher)) {
			
				TeacherLifeCycleLog changeLog = new TeacherLifeCycleLog();
				changeLog.setCreateDateTime(new Date());
				changeLog.setFromStatus(oldTeacher.getLifeCycle().toString());
				User staff = userRepository.find(lastEditor.getId()); 
				changeLog.setOperator(staff);
				changeLog.setTeacher(teacher);
				changeLog.setToStatus(teacher.getLifeCycle().toString());
				
				teacherLifeCycleLogService.update(changeLog);
			}
			
			securityService.logAudit(Level.INFO, Category.TEACHER_UPDATE, "Update teacher: " + teacher.getName());

		}
		return teacher;
	}

	// @PUT
	// @Path("/changePassword")
	// public Teacher changePassword(Teacher teacher) {
	// logger.info("change password for teacher: {}", teacher);
	//
	// teacher.setPassword(PasswordEncryptor.encrypt(teacher.getPassword()));
	// teacherAccessor.update(teacher);
	//
	// securityController.logAudit(Level.INFO, Category.TEACHER_CHANGE_PASSWORD,
	// "Change password for teacher: " + teacher.getName());
	//
	// return teacher;
	// }

	public List<Teacher> findNotExistsOnlineClassByScheduledDateAndCertificatedCourseId(DateTimeParam scheduledDateTime, Long courseId) {
		logger.info("find not exists onlineClass for scheduled date = {}, certificated course id = {}", scheduledDateTime, courseId);
		return teacherRepository.findNotExistsOnlineClassByScheduledDate(scheduledDateTime.getValue(), courseId);
	}

	public StringWrapper findLifeCycleById(Long id) {
		return new StringWrapper(teacherRepository.findLifeCycleById(id).toString());
	}

	public List<StringWrapper> listNameAndId() {
		List<TeacherNameView> teachers = teacherRepository.findRegularTeachers();

		List<StringWrapper> nameIdList = new ArrayList<StringWrapper>();
		for (int i = 0; i < teachers.size(); i++) {
			nameIdList.add(new StringWrapper(teachers.get(i).getId() + "," + teachers.get(i).getRealName()));
		}
		return nameIdList;
	}
	
	public List<Teacher> findNotExistsOnlineClassByScheduledDate(Date scheduledDateTime, Long courseId) {
		return teacherRepository.findNotExistsOnlineClassByScheduledDate(scheduledDateTime, courseId);
	}
	
	public List<TeachersView>listTeachers(int tabSign,String courseType,int timeWeek,String teacherName,int seachSign,long studentId,int rowNum,int currNum){
		logger.info("listTeachers for teacherName = {}, certificated course id = {},studentId ={}", teacherName, courseType,studentId);
		List<TeachersView> teachersViews = teacherRepository.listTeachers(tabSign, courseType, getWeekStart(timeWeek), getWeekEnd(timeWeek), teacherName, seachSign, studentId, rowNum, currNum);
		if(timeWeek==-1){
			for(TeachersView tView:teachersViews){
				boolean hasAvailable = teacherRepository.findHasAvailableByTeacherId(tView.getTeacherId(), getWeekStart(2),getWeekEnd(2));
				if(hasAvailable){
					hasAvailable = teacherRepository.findHasAvailableByTeacherId(tView.getTeacherId(), getWeekStart(1),getWeekEnd(1));
					if(!hasAvailable){
						tView.setHasAvailableWeek(1);
					}
				}else{
					tView.setHasAvailableWeek(2);
				}
				if(hasAvailable){
					tView.setHasAvailableWeek(2);
				}
				tView.setHasAvailable(hasAvailable);
			}
		}else{
			for(TeachersView tView:teachersViews){
				boolean hasAvailable = teacherRepository.findHasAvailableByTeacherId(tView.getTeacherId(), getWeekStart(timeWeek),getWeekEnd(timeWeek));
				tView.setHasAvailableWeek(timeWeek);
				tView.setHasAvailable(hasAvailable);
			}
		}
		return teachersViews;
	}
	public long countTeachers(int tabSign,String courseType,int timeWeek,String teacherName,int seachSign,long studentId){
		logger.info("listTeachers for teacherName = {}, certificated course id = {},studentId ={}", teacherName, courseType,studentId);
		return teacherRepository.countTeachers(tabSign, courseType, getWeekStart(timeWeek), getWeekEnd(timeWeek), teacherName, seachSign, studentId);
	}
	
	public Date getWeekStart(int timeWeek){
		if(timeWeek==-1){
			return null;
		}
		Calendar startWeekCalendar = Calendar.getInstance();
		startWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if(timeWeek==2){
			startWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
        startWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        startWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startWeekCalendar.set(Calendar.MINUTE, 0);
        startWeekCalendar.set(Calendar.SECOND, 0);
        startWeekCalendar.set(Calendar.MILLISECOND, 0);
        Date weekStart = startWeekCalendar.getTime();
        if((new Date().getTime()+24*60*60*1000)>weekStart.getTime()){
        	weekStart = new Date(new Date().getTime()+24*60*60*1000);
        }
		return weekStart;
		
	}
	
	public Date getWeekEnd(int timeWeek){
		if(timeWeek==-1){
			return null;
		}
		Calendar endWeekCalendar = Calendar.getInstance();
        endWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        if(timeWeek==2){
        	endWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
        endWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        endWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endWeekCalendar.set(Calendar.MINUTE, 59);
        endWeekCalendar.set(Calendar.SECOND, 59);
        endWeekCalendar.set(Calendar.MILLISECOND, 0);
        Date weekEnd = endWeekCalendar.getTime();
        return weekEnd;
	}
	public List<TeachersView>listTeachersForPreschedule(Integer seaType,
			Long teacherId,
			Integer week,
			String day,
			String timeStart,
			String timeEnd,
			String courseType,
			long studentId,
			Integer currNum,String teacherName){
		logger.info("listTeachersForPreschedule for studentId = {}, teacherId= {},serivalNumber ={}", studentId,teacherId, courseType);
		return teacherRepository.listTeachersForPreschedule(seaType, teacherId,  timeForPreschedule(week,day, timeStart,1),timeForPreschedule(week,day, timeEnd,2), courseType,studentId, currNum,teacherName);
	}
	
	public long countTeachersForPreschedule(Integer seaType,
			Long teacherId,
			Integer week,
			String day,
			String timeStart,
			String timeEnd,
			String courseType,
			long studentId,String teacherName){
		logger.info("countTeachersForPreschedule for studentId = {}, teacherId= {},serivalNumber ={}", studentId,teacherId, courseType);
		return teacherRepository.countTeachersForPreschedule(seaType, teacherId,  timeForPreschedule(week,day, timeStart,1),timeForPreschedule(week,day, timeEnd,2), courseType,studentId,teacherName);
	}
	
	/**
	 * 
	* @Title: findOnlineClassByTimeOrTeacherId 
	* @Description: 通过时间区间+teacherId查询onlineclass用于约课教师模式
	* @param parameter
	* @author zhangfeipeng 
	* @return OnlineClassesView[][]
	* @throws
	 */
	public  OnlineClassesView[][] findOnliclassByTeacherIdAndTime(long teacherId,Integer week,String day,String timeStart,String timeEnd,long studentId,String courseType){
		DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		List<OnlineClassesView> onlineClassesViews = teacherRepository.findOnliclassForTelMode(teacherId, timeForPreschedule(week,day, timeStart,1),timeForPreschedule(week,day, timeEnd,2),courseType);
		List<OnlineClassesView> onlineClassesViewStudent = teacherRepository.findOnliclassByStrudentAndTime(studentId, timeForPreschedule(week,day, timeStart,1),timeForPreschedule(week,day, timeEnd,2),courseType);
		OnlineClassesView[][] OnlineClassesViewArr = OnlineClassesViewArr(week);
		if(onlineClassesViews!=null&&onlineClassesViews.size()>0){
			for (OnlineClassesView ol :onlineClassesViews) {
				for (int i = 0; i < OnlineClassesViewArr.length; i++) {
					for (int j = 1; j < OnlineClassesViewArr[i].length; j++) {
						if(ol.getScheduledDateTime()!=null){
							String time =  df.format(ol.getScheduledDateTime());
							String [] t=time.split(" ");
							if(OnlineClassesViewArr[i][j].getX().equals(t[0])&&OnlineClassesViewArr[i][j].getY().equals(t[1])){
								ol.setX(OnlineClassesViewArr[i][j].getX());
								ol.setY(OnlineClassesViewArr[i][j].getY());
								OnlineClassesViewArr[i][j] = ol;
								break;
							}
						}
					}
				}
			}
		}
		if(onlineClassesViewStudent!=null&&onlineClassesViewStudent.size()>0){
			for (OnlineClassesView ol :onlineClassesViewStudent) {
				for (int i = 0; i < OnlineClassesViewArr.length; i++) {
					for (int j = 1; j < OnlineClassesViewArr[i].length; j++) {
						if(ol.getScheduledDateTime()!=null){
							String time =  df.format(ol.getScheduledDateTime());
							String [] t=time.split(" ");
							if(OnlineClassesViewArr[i][j].getX().equals(t[0])&&OnlineClassesViewArr[i][j].getY().equals(t[1])){
								if(ol.getId()==OnlineClassesViewArr[i][j].getId()){
									OnlineClassesViewArr[i][j].setType(1);
								}else{
									OnlineClassesViewArr[i][j].setType(2);
								}
								OnlineClassesViewArr[i][j].setOid(ol.getId());
								OnlineClassesViewArr[i][j].setOteacherId(ol.getTeacherId());
								OnlineClassesViewArr[i][j].setOteacherName(ol.getTeacherName());
								break;
							}
						}
					}
				}
			}
		}
		return OnlineClassesViewArr;
	}
	
	/**
	 * 
	* @Title: findOnlineClassByTimeOrTeacherId 
	* @Description: 通过时间区间或者时间区间+teacherId查询onlineclass用于约课日历模式
	* @param parameter
	* @author zhangfeipeng 
	* @return OnlineClassesView[][]
	* @throws
	 */
	public OnlineClassesView[][]findOnlineClassByTimeOrTeacherId(int seaType,long teacherId,Integer week,long studentId,String courseType,String teacherName){
		DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		List<OnlineClassesView> onlineClassesViews = teacherRepository.findOnliclassForCalMode(seaType,teacherId, timeForPreschedule(week,null, null,1),timeForPreschedule(week,null, null,2),courseType,studentId,teacherName);
		List<OnlineClassesView> onlineClassesViewStudent = teacherRepository.findOnliclassByStrudentAndTime(studentId, timeForPreschedule(week,null, null,1),timeForPreschedule(week,null, null,2),courseType);
		OnlineClassesView[][] OnlineClassesViewArr = OnlineClassesViewArr(week);
		if(onlineClassesViews!=null&&onlineClassesViews.size()>0){
			for (OnlineClassesView ol :onlineClassesViews) {
				for (int i = 0; i < OnlineClassesViewArr.length; i++) {
					for (int j = 1; j < OnlineClassesViewArr[i].length; j++) {
						if(ol.getScheduledDateTime()!=null){
							String time =  df.format(ol.getScheduledDateTime());
							String [] t=time.split(" ");
							if(OnlineClassesViewArr[i][j].getX().equals(t[0])&&OnlineClassesViewArr[i][j].getY().equals(t[1])){
								OnlineClassesViewArr[i][j].setId(ol.getId());
								OnlineClassesViewArr[i][j].setScheduledDateTime(ol.getScheduledDateTime());
								OnlineClassesViewArr[i][j].setTeacherId(ol.getTeacherId());
								OnlineClassesViewArr[i][j].setTeacherName(ol.getTeacherName());
								OnlineClassesViewArr[i][j].setStatus(ol.getStatus());
								OnlineClassesViewArr[i][j].setCalendarType(1);
								OnlineClassesViewArr[i][j].setSize(OnlineClassesViewArr[i][j].getSize()+1);
								/*if(ol.getStatus().toString().equals("AVAILABLE")){
								}
								if(ol.getStatus().toString().equals("BOOKED")){
									OnlineClassesViewArr[i][j].setBookSize(OnlineClassesViewArr[i][j].getBookSize()+1);
								}*/
								break;
							}
						}
					}
				}
			}
		}
		if(onlineClassesViewStudent!=null&&onlineClassesViewStudent.size()>0){
			for (OnlineClassesView ol :onlineClassesViewStudent) {
				for (int i = 0; i < OnlineClassesViewArr.length; i++) {
					for (int j = 1; j < OnlineClassesViewArr[i].length; j++) {
						if(ol.getScheduledDateTime()!=null){
							String time =  df.format(ol.getScheduledDateTime());
							String [] t=time.split(" ");
							if(OnlineClassesViewArr[i][j].getX().equals(t[0])&&OnlineClassesViewArr[i][j].getY().equals(t[1])){
								/*if(OnlineClassesViewArr[i][j].getBookSize()==0&&OnlineClassesViewArr[i][j].getSize()==0){
									//表明当前学生当前时间已经约定课程 ，但教师不在目前查询条件下 并且当前没有空闲时间
									OnlineClassesViewArr[i][j].setCalendarType(-1);
								}else if(OnlineClassesViewArr[i][j].getSize()==0&&OnlineClassesViewArr[i][j].getBookSize()==1){
									//表明次时间点 仅有一个老师 并且已被当前学生约
									OnlineClassesViewArr[i][j].setCalendarType(1);
								}else if(OnlineClassesViewArr[i][j].getSize()>0&&OnlineClassesViewArr[i][j].getBookSize()==0){
									//表明当前学生当前时间已经约定课程 ，但教师不在目前查询条件下  并且当前有空闲时间
									OnlineClassesViewArr[i][j].setCalendarType(2);
								}else if(OnlineClassesViewArr[i][j].getSize()>0&&OnlineClassesViewArr[i][j].getBookSize()>0){
									//表明当前学生当前时间已经约定课程 ，教师在目前查询条件下 ，并且当前时间内还有其他老师有空余时间
									OnlineClassesViewArr[i][j].setCalendarType(3);
								}*/
								OnlineClassesViewArr[i][j].setOid(ol.getId());
								OnlineClassesViewArr[i][j].setOteacherId(ol.getTeacherId());
								OnlineClassesViewArr[i][j].setOteacherName(ol.getTeacherName());
								OnlineClassesViewArr[i][j].setCalendarType(2);
								OnlineClassesViewArr[i][j].setScheduledDateTime(ol.getScheduledDateTime());
								break;
							}
						}
					}
				}
			}
		}
		return OnlineClassesViewArr;
	}
	
	public  OnlineClassesView[][] OnlineClassesViewArr(Integer week){
		String [] yArrow = {"09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30",
				"13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00",
				"17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30"};
		String [] xArrow = dayArr(week);
		OnlineClassesView[][] onlineClassesViewArr = new OnlineClassesView[26][8];
		for (int i = 0; i < onlineClassesViewArr.length; i++) {
			for (int j = 0; j < onlineClassesViewArr[i].length; j++) {
				OnlineClassesView ol = new OnlineClassesView();
				ol.setY(yArrow[i]);
				if(j==0){
					ol.setX(xArrow[j]);
				}else{
					ol.setX(xArrow[j-1]);
				}
				onlineClassesViewArr[i][j] = ol;
			}
		}
		return onlineClassesViewArr;
	}

	public  String [] dayArr(Integer week){
		DateFormat df = new SimpleDateFormat("MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		if(week==2){
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
		String [] arr = new String[7];
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Date weekStart = calendar.getTime();
		arr[0] = df.format(weekStart);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		weekStart = calendar.getTime();
		arr[1] = df.format(weekStart);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		weekStart = calendar.getTime();
		arr[2] = df.format(weekStart);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		weekStart = calendar.getTime();
		arr[3] = df.format(weekStart);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		weekStart = calendar.getTime();
		arr[4] = df.format(weekStart);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		weekStart = calendar.getTime();
		arr[5] = df.format(weekStart);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		weekStart = calendar.getTime();
		arr[6] = df.format(weekStart);
		return arr;
	}
	
	/**
	 * 
	* @Title: timeForPreschedule 
	* @Description: TODO 
	* @param type 1 开始时间 2 结束时间
	* @author zhangfeipeng 
	* @return Date
	* @throws
	 */
	public Date timeForPreschedule(Integer week,
			String day,
			String time,
			int type){
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		if(week==2){
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
		if(day!=null&&!day.equals("日期不限")){
			if(day.equals("周一")){
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			}else if(day.equals("周二")){
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
			}else if(day.equals("周三")){
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
			}else if(day.equals("周四")){
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
			}else if(day.equals("周五")){
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			}else if(day.equals("周六")){
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			}else{
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			}
		}else{
			if(type==1){
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			}else{
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			}
		}
		if(day!=null&&!day.equals("日期不限")&&time!=null
				&&!time.equals("起始时间")&&!time.equals("截止时间")){
			String[]t = time.split(":");
			calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(t[0]));
			calendar.set(Calendar.MINUTE, Integer.valueOf(t[1]));
		}else{
			if(type==1){
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
			}else{
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
			}
		}
		if(type==1){
			calendar.set(Calendar.SECOND, 0);
		}else{
			calendar.set(Calendar.SECOND, 59);
		}
		calendar.set(Calendar.MILLISECOND, 0);
        Date weekTime = calendar.getTime();
        if(type==1&&(new Date().getTime()+24*60*60*1000)>weekTime.getTime()){
        	weekTime = new Date(new Date().getTime()+24*60*60*1000);
        }else if(type==2){
        	weekTime = new Date(weekTime.getTime()+1);//截至时间向后延时1毫秒
        }
		return weekTime;
	}
	
	public List<TeachersView> listTeacherForCal(Date scheduledDateTime,int seaType,long teacherId,long studentId,String courseType,int currNum,String teacherName){
		List<TeachersView> teacherViews = teacherRepository.listTeacherForCal(scheduledDateTime, seaType, teacherId, studentId, courseType, currNum,teacherName);
		if(teacherViews!=null&&!teacherViews.isEmpty()){
			for (TeachersView teachersView:teacherViews) {
				long count = studentRepository.countFavorateTeacher(teachersView.getTeacherId(), studentId);
				if(count==1){
					teachersView.setStudentId(studentId);
				}else{
					teachersView.setStudentId(-1);
				}
			}
		}
		return teacherViews;
	}
	
	public long counrTeacherForCal(Date scheduledDateTime,int seaType,long teacherId,long studentId,String courseType,String teacherName){
		return teacherRepository.counrTeacherForCal(scheduledDateTime, seaType, teacherId, studentId, courseType,teacherName);
	}
	private void refreshTeacher(final Teacher teacher, final Teacher teacherFromJson){
		List<OnlineClass> dbOnlineClasses = teacher.getOnlineClasses();
		List<TeacherApplication> teacherApplications = teacher.getTeacherApplications();
		BeanUtils.copyProperties(teacherFromJson, teacher);
		teacher.setOnlineClasses(dbOnlineClasses);
		teacher.setTeacherApplications(teacherApplications);
	}
	
	/**
	 * 
	* @Title: listTeachersView 
	* @Description: 加载搜索老师下拉框数据 
	* @param parameter
	* @author zhangfeipeng 
	* @return List<TeView>
	* @throws
	 */
	public List<TeView> listTeachersView(String courseType,String teacherName){
		return teacherRepository.listTeachersView(courseType,teacherName);
	}
	
	/**
	 * 
	* @Title: findTeacherDetailById 
	* @Description: 家长端教师详情页面查询
	* @param parameter
	* @author zhangfeipeng 
	* @return TeacherDetailView
	* @throws
	 */
	public TeacherDetailView findTeacherDetailById(long teacherId,long studentId){
		TeacherDetailView teacherDetailView = teacherRepository.findTeacherDetailById(teacherId);
		if(teacherDetailView!=null){
			boolean hasAvailable = teacherRepository.findHasAvailableByTeacherId(teacherId, getWeekStart(2),getWeekEnd(2));
			if(hasAvailable){
				hasAvailable = teacherRepository.findHasAvailableByTeacherId(teacherId, getWeekStart(1),getWeekEnd(1));
				if(!hasAvailable){
					teacherDetailView.setHasAvailableWeek(1);
				}
			}else{
				teacherDetailView.setHasAvailableWeek(2);
			}
			if(hasAvailable){
				teacherDetailView.setHasAvailableWeek(2);
			}
			long count = studentRepository.countFavorateTeacher(teacherId, studentId);
			if(count==1){
				teacherDetailView.setStudentId(studentId);
			}else{
				teacherDetailView.setStudentId(-1);
			}
			teacherDetailView.setHasAvailable(hasAvailable);
		}else{
			teacherDetailView = new TeacherDetailView();
		}
		return teacherDetailView;
		
	}
	
	public List<TeachersView> listTeachers(long studentId){
		List<TeachersView> teachersViews = teacherRepository.listTeachers(studentId);
		if(teachersViews!=null&&!teachersViews.isEmpty()){
			for(TeachersView tView:teachersViews){
				boolean hasAvailable = teacherRepository.findHasAvailableByTeacherId(tView.getTeacherId(),getWeekStart(2),getWeekEnd(2));
				if(hasAvailable){
					hasAvailable = teacherRepository.findHasAvailableByTeacherId(tView.getTeacherId(), getWeekStart(1),getWeekEnd(1));
					if(!hasAvailable){
						tView.setHasAvailableWeek(1);
					}
				}else{
					tView.setHasAvailableWeek(2);
				}
				if(hasAvailable){
					tView.setHasAvailableWeek(2);
				}
				tView.setHasAvailable(hasAvailable);
			}
		}
		return teachersViews;
	}

	// 2015-08-15 signup 的teacher filter 和count
	public Count countSignup(String search, String status, String recruitChannel) {
		// 
		logger.info("teacher countSignup with params: search = {}, status = {},recruitChannel = {}.", search, status,recruitChannel);
		return new Count(teacherRepository.countSignup( search, status,recruitChannel));
	}

	public List<Teacher> listSignup(String search, Status status,
			String recruitChannel, Integer start, Integer length) {
		//
		logger.info(
				"list teacher with params: search = {}, status = {},recruitChannel = {}, start = {}, length = {}.",
				search, status, recruitChannel, start, length);
		return teacherRepository.listSignup(search, status, recruitChannel, start, length);
	}
	
	public Count countNormal(LifeCycle lifeCycle, String search, String status,String[] certificatedCourseId, String[] managers, Long operatorId,
			Gender gender, Country country,Teacher.Type teacherType,
			// 2015-08-15 
			DateTimeParam operationStartDate,
			DateTimeParam operationEndDate,
			String[] contractEndDate,String strAccountType
			) {
		// 
		logger.info("teacher countNormal with params: search = {}, status = {},teacherType = {}.", search, status,teacherType);
		return new Count(teacherRepository.countNormal(lifeCycle, search, status, certificatedCourseId, managers, operatorId, 
				gender, country, teacherType, operationStartDate, operationEndDate,contractEndDate,strAccountType) );
	}

	public List<Teacher> listNormal(LifeCycle lifeCycle, String search, String status,String[] certificatedCourseIds, String[] managers,
			Gender gender, Country country, Teacher.Type teacherType,
			Integer start, Integer length, String[] contractEndDate,
			Long operatorId, DateTimeParam operationStartDate, DateTimeParam operationEndDate,String strAccountType
			) {
		//
		logger.info(
				"listNormal with params: search = {}, status = {},teacherType = {}, start = {}, length = {}.",
				search, status, teacherType, start, length);
		return teacherRepository.listNormal(lifeCycle, search, status, certificatedCourseIds, managers, gender, country, teacherType, start, length,contractEndDate,operatorId,operationStartDate,operationEndDate, strAccountType) ;
	}

	public List<Teacher> listAll(String search, Integer start, Integer length) {
		logger.info("listAll with params:search = {}, start = {}, length = {}.",search,start,length);
		return teacherRepository.listAll(search,start,length);
	}

	public Count countAll(String search) {
		logger.info("listAll with params:search = {}.",search);
		return new Count(teacherRepository.countAll(search));
	}

	public List<String> getRegularTeacherContractDate() {
		List<Date> dateList = teacherRepository.getRegularTeacherContractDate();
		List<String> resultList = new ArrayList<String>();
		for(Date date:dateList){
			if(date != null){
				Calendar left = Calendar.getInstance();
				left.setTime(date);
				left.set(Calendar.DAY_OF_MONTH,1);
				left.set(Calendar.HOUR_OF_DAY, 0);
				left.set(Calendar.MINUTE, 0);
				left.set(Calendar.SECOND, 0);
				left.set(Calendar.MILLISECOND, 0);	

				if(resultList == null || resultList.indexOf(String.valueOf(left.getTimeInMillis())) == -1){
					resultList.add(String.valueOf(left.getTimeInMillis()));
				}
			}
		}
		return resultList;
	}

//	public List<String> loadOptions(String name) {
//		if()
//		return null;
//	}

	public boolean sendEmailForTrialTestResult(Long studentId,String  strResult) {
		// 
		Student student = studentRepository.find(studentId);
		if (null == student) {
			securityService.logAudit(Level.ERROR, Category.TEACHER_COMMENT_CREATE, "trail test result email but error: no student" );
			return false;
		}
		
		Leads leads = leadsRepository.findByStudentId(studentId);
		Long saleId = leads.getSalesId();
		Staff staff = null;
		String strSaleEmail = null;
		if (null != saleId) {
			staff = staffRepository.find(saleId);
			if (null != staff && null != staff.getEmail()) {
				strSaleEmail = staff.getEmail();
			}
		}
		// 
		EMail.sendStudentTrialLevelExamEmail(student, strSaleEmail, strResult);
		
		return true;
	}
}
