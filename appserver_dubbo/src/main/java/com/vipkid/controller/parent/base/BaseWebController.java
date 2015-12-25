package com.vipkid.controller.parent.base;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Student;
import com.vipkid.repository.OrderRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.service.AirCraftThemeService;
import com.vipkid.service.MedalService;
import com.vipkid.service.PetService;
import com.vipkid.service.StudentAuthService;
import com.vipkid.service.StudentService;
import com.vipkid.service.TeacherCommentService;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;
import com.vipkid.util.Configurations;


public abstract class BaseWebController extends WebMvcConfigurerAdapter {
    private final Logger logger = LoggerFactory.getLogger(BaseWebController.class);
	protected static final String ROOT_PATH = "/";
	
	@Resource
	private StudentService studentService;

    @Resource
    private StudentAuthService studentAuthService;

    @Resource
    private StudentRepository studentRepository;
	
	@Resource
	private OrderRepository orderRepository;
	
	@Resource 
	private TeacherCommentService teacherCommentService;
	
	@Resource
	private PetService petService;
	
	@Resource
	private AirCraftThemeService airCraftThemeService;
	
	@Resource
	private MedalService medalService;
	
	@Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
		registerViewController(viewControllerRegistry);
    }
	
	protected abstract void registerViewController(ViewControllerRegistry viewControllerRegistry);
	
	protected void initCommon(Model model,HttpServletRequest request){
		//公共信息
		long studentId = Long.valueOf(UserCacheUtil.getValueFromCookie(request, "studentId").toString()) ;
		
		StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
        if (null == student) {
            logger.info("Can not load student from cache,studentID={}",studentId);
            Student studentInDB = studentRepository.find(studentId);
            if (null != studentInDB) {
                logger.info("Load student from DB,studentID={}",studentId);
                student = new StudentVO();
                student.setId(studentInDB.getId());
                student.setEnglishName(studentInDB.getEnglishName());
                student.setStars(studentInDB.getStars());
                student.setName(studentInDB.getSafeName());
                student.setToken(studentInDB.getToken());
                student.setAvatar(studentInDB.getAvatar());
                student.setFamilyId(studentInDB.getFamily().getId());
                // 2015-09-14 次数限制 是否已经guide
                student.setGuideToLevelExam(studentInDB.getGuideToLevelExam());
                student.setMaxTimesLevelExam(studentInDB.getMaxTimesLevelExam());
                
                student.setTargetClassesPerWeek(studentInDB.getTargetClassesPerWeek());
                UserCacheUtil.storeCurrentUser(Long.toString(student.getId()), student);
            } else {
                logger.error("Student is not exist,studentID={}",studentId);
                throw new UserNotExistServiceException("Student is not exist,studentID= " + studentId);
            }
        }
		//-------------解决上完课，不能实时更新勋章和星星数量-----------------------
		long medalCount = medalService.count(studentId);
		Student s = studentService.find(studentId);
		student.setStars(s.getStars());
		student.setTargetClassesPerWeek(s.getTargetClassesPerWeek());
		UserCacheUtil.storeCurrentUser(Long.toString(studentId), student);
		
		List<Student> students = studentService.findByFamilyId(student.getFamilyId());
		model.addAttribute("students", students);
		model.addAttribute("studentEnglishName", student.getEnglishName());
		int starCount = student.getStars();
		if (starCount < 10000) {
			model.addAttribute("starCount", starCount);
		} else {
			model.addAttribute("starCount", "1W+");
		}
		model.addAttribute("medalCount", medalCount);
		model.addAttribute("currentStudentAvatar", UserCacheUtil.AVATAR_PREFIX + student.getAvatar() + UserCacheUtil.AVATAR_SUFFIX);
		String pid = String.valueOf(UserCacheUtil.getValueFromCookie(request, "parentId"));
		
		String pname = UserCacheUtil.getParentName(pid);
		model.addAttribute("parentName", pname);
		// 2015-07-31 开放水平测试链接
//		model.addAttribute("allowLevelTest", StringUtils.isBlank(pname)? false : pname.indexOf("test")>=0);
		model.addAttribute("allowLevelTest", true);
		
		model.addAttribute("studentId", UserCacheUtil.getValueFromCookie(request, "studentId"));
		model.addAttribute("familyId", UserCacheUtil.getValueFromCookie(request, "familyId"));
		
		Count count = orderRepository.countTopayByFamilyId(student.getFamilyId());
		model.addAttribute("toPayNum", count.getTotal());
		
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.SECOND, 0);
		startCalendar.set(Calendar.MILLISECOND, 0);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endCalendar.set(Calendar.MINUTE, 59);
		endCalendar.set(Calendar.SECOND, 59);
		endCalendar.set(Calendar.MILLISECOND, 999);
		long getStarsThisWeek = teacherCommentService.findStarsByStudentIdAndTimeRange(studentId, startCalendar.getTime(), endCalendar.getTime());
		model.addAttribute("getStarsThisWeek", getStarsThisWeek);
		
		model.addAttribute("learningUrl", Configurations.OSS.LEARNING_URL);
		
		long starsForPetsThisWeek = petService.findStarsByStudentIdAndTimeRange(studentId, startCalendar.getTime(), endCalendar.getTime());
		long starsForAirThisWeek = airCraftThemeService.findStarsByStudentIdAndTimeRange(studentId, startCalendar.getTime(), endCalendar.getTime());
		model.addAttribute("useStarsThisWeek", starsForPetsThisWeek + starsForAirThisWeek);
	}
	
	protected void initHome(Model model,HttpServletRequest request){
		model.addAttribute("learningUrl", Configurations.OSS.LEARNING_URL);
	}
}
