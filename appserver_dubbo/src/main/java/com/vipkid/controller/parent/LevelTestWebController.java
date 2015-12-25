package com.vipkid.controller.parent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Student;
import com.vipkid.model.StudentExam;
import com.vipkid.service.StudentExamService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.Count;

@Controller
public class LevelTestWebController extends BaseWebController {
	public static final String PATH = "/parent/leveltest";

	// 开始测试连接url
	public static final String PATH_STARTEXAM = "/parent/startexam";

	//
	public static final String PATH_EXAMRESULT = "/studentexamresult/{result}";
	public static final String PATH_EXAMLEVEL0RESULT = "/studentlevel0/{result}";

	public static final String COOKIE_KEY_STUDENT_ID = "studentId";// "examstudentid";
	public static final String COOKIE_KEY_FAMILY_ID = "familyId";
	public static final String COOKIE_KEY_STUDENTEXAM_UUID = "studentexam_uuid";

	// 2015-07-08 开放的测试结果显示 url
//	public static final String PUB_PATH_EXAMRESULT = "/pubic/studentexamresult/{result}";
	public static final String PUB_PATH_EXAMRESULT = "/public/studentexamresult/{result}"; // 2015-08-03 修改pubic --> public
	// 视图模板 string
	public static final String VIEW_EXAMRESULT = "/studentexam/studentexamresult";
	public static final String VIEW_EXAMLEVEL0RESULT = "/studentexam/studentlevel0";
	
	// 2015-08-03 检查是否已经自己测试过2次
	private static int kLimitLevelTestCount = 2;
			
	private Logger logger = LoggerFactory
			.getLogger(LevelTestWebController.class.getSimpleName());

	@Resource
	private StudentService studentService;

	@Resource
	private StudentExamService studentExamService;

	@Override
	protected void registerViewController(
			ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(LevelTestWebController.PATH)
				.setViewName(LevelTestWebController.PATH);
	}

	/**
	 * 初始化
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = LevelTestWebController.PATH, method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request){
		model.addAttribute("name", "水平测试");
		model.addAttribute("path", LevelTestWebController.PATH);
		//公共信息
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
		long studentId = Long.parseLong(sid.toString());
		StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if(student == null){
			return "redirect:/login";
		}
		initCommon(model, request);
		
		// 添加学生名称
		model.addAttribute("studentName",student.getEnglishName());
		//获取测试数据
		List<StudentExam> testDatas = studentExamService.list(studentId, 0, 99999);
		model.addAttribute("testDatas", testDatas);
		
		// 2015-09-14 ... 必须重新获取，否则，测试次数和次数限制没有变为新值！
		int nLevelExamLimit = kLimitLevelTestCount;
		Student studentModel = studentService.find(studentId);
//		StudentVO studentVO = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if (null != studentModel && studentModel.getMaxTimesLevelExam()>0) {
			nLevelExamLimit = studentModel.getMaxTimesLevelExam();
		}
		
		// 
		student.setMaxTimesLevelExam(nLevelExamLimit);
		UserCacheUtil.storeCurrentUser(new Long(studentId).toString(), student);
		
		model.addAttribute("testEnable", false);
		int nTestNum = testDatas.size();
		if (nTestNum < nLevelExamLimit) {
			model.addAttribute("testEnable", true);
		}
		
		return LevelTestWebController.PATH;
	}

	/**
	 * 开始水平测试
	 * 
	 * @param model
	 * @param request
	 * @return 从cookies中获取familyId, studentId,
	 */
	@RequestMapping(value = LevelTestWebController.PATH_STARTEXAM, method = RequestMethod.GET)
	public void startStudentLevelExam(Model model, 
			@CookieValue(value="familyId", required=false, defaultValue="") Long familyId,
			HttpServletRequest request,
			HttpServletResponse responese) {
		model.addAttribute("name", "水平测试");
		model.addAttribute("path", LevelTestWebController.PATH_STARTEXAM);
		try {
			// 1. 检查登陆和用户
			Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
			
			long studentId = Long.parseLong(sid.toString());
//			StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
//			if (studentVO == null) {
//				responese.sendRedirect("/login");
//				return;
//				// return "redirect:/login";
//			}
			
			int nLevelExamLimit = kLimitLevelTestCount;
			StudentVO studentVO = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
			if (null != studentVO && studentVO.getMaxTimesLevelExam()>0) {
				nLevelExamLimit = studentVO.getMaxTimesLevelExam();
			}
			
			// 2015-08-03 检查是否已经2次，超出测试限制了。
			Count examCount = studentExamService.count(studentId);
			
			if (examCount.getTotal() >= nLevelExamLimit ) {
				responese.sendRedirect(PATH);
				return ;
			}
			// 2. service创建一条记录
			Student student = studentService.find(studentId);
			
			StudentExam studentExam = new StudentExam();
			studentExam.setCreateDatetime(new Date());
			studentExam.setStudent(student);
			String uuid = UUID.randomUUID().toString();
			studentExam.setRecordUuid(uuid);
//			studentExam.setFamilyId(familyId);
			studentExam.setFamilyId(student.getFamily().getId());
			studentExam.setExamComment("");
			studentExam.setExamLevel("");
			studentExam.setExamScore(0);
			
			studentExam = studentExamService.create(studentExam);

			// return LevelTestWebController.PATH_STARTEXAM;

			// 跳转到测试课件url
			Cookie cookieUUID = new Cookie(COOKIE_KEY_STUDENTEXAM_UUID, uuid);
			// cookieUUID.setHttpOnly(true);
			// cookieUUID.setDomain("/");
			cookieUUID.setPath("/");
			responese.addCookie(cookieUUID);

			String examURL = "http://resource.vipkid.com.cn/e_learning/mainstory/story.html";	//product
//			String examURL = "http://resource.vipkid.com.cn/e_learning/beta_test/story.html";	//beta-test
//			String examURL = "http://resource.vipkid.com.cn/e_learning/a2_test/story.html"; 	//a2-test
			responese.sendRedirect(examURL);
			return;
		} catch (Exception e) {
			//
			logger.error(e.getMessage());
		}
	}

	/**
	 * 2015-07-08 公开的测试结果显示 -- public 处理
	 * @return
	 */
	@RequestMapping(value = LevelTestWebController.PUB_PATH_EXAMRESULT, method = RequestMethod.GET)
	public String ShowPublicStudentExamResult(Model model,
			@PathVariable("result") String result) {
		//
		model.addAttribute("name", "水平测试结果");
	
		//
		String strExamLevel = result;
		String strLevelImgURL = getLevelImgUrl(strExamLevel.toLowerCase());
		if (null == strLevelImgURL) {
			// 错误了
			model.addAttribute("error","true");//"请完成测试再提交您的结果。"
		} else {
			model.addAttribute("imgurl",strLevelImgURL);
			model.addAttribute("error","false");//"请完成测试再提交您的结果。"
		}

		
		return LevelTestWebController.VIEW_EXAMRESULT; 
	}
	/**
	 * 结果上报url处理
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = LevelTestWebController.PATH_EXAMRESULT, method = RequestMethod.GET)
	public String reportStudentExamResult(Model model,
			@PathVariable("result") String result,
			@CookieValue(value=COOKIE_KEY_STUDENTEXAM_UUID, required=false, defaultValue="") String strExam_uuid,
			HttpServletRequest request, HttpServletResponse responese) {
		//公共信息
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
		long studentId = Long.parseLong(sid.toString());
		StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if(student == null){
			logger.error("can't get studentVO for {}",studentId);
			return "redirect:/login";
		}

		// uuid check
		if (null == strExam_uuid || StringUtils.isEmpty(strExam_uuid)) {
			return "redirect:/parent/leveltest";
		}

		// update exam
		String strExamLevel = result.toUpperCase();
				
		StudentExam studentExam = studentExamService.updateStudentExam(studentId, student.getFamilyId(), strExamLevel, strExam_uuid);
		if (null == studentExam) {
			return "redirect:/parent/leveltest";
		}

		//
		model.addAttribute("name", "水平测试结果");
		model.addAttribute("studentExam", studentExam);
		String strLevelImgURL = getLevelImgUrl(strExamLevel.toLowerCase());
		if (null == strLevelImgURL) {
			// 错误了
			model.addAttribute("error","true");//"请完成测试再提交您的结果。"
		} else {
			model.addAttribute("imgurl",strLevelImgURL);
			model.addAttribute("error","false");//"请完成测试再提交您的结果。"
		}
		Cookie cookieUUID = new Cookie(COOKIE_KEY_STUDENTEXAM_UUID, "");
		// cookieUUID.setHttpOnly(true);
		// cookieUUID.setDomain("vipkid.com.cn");
		cookieUUID.setPath("/");
		cookieUUID.setMaxAge(0); //
		responese.addCookie(cookieUUID);

		return LevelTestWebController.VIEW_EXAMRESULT;
	}

	/**
	 * 2015-08-03 新的测试级别的结果url
	 */
	static String kExamLevelImgURLFoundation = "http://resource.vipkid.com.cn/e_learning/images/resultv2/ESL-Skill-Building.png";
	static String kExamLevelImgURLGradeK = "http://resource.vipkid.com.cn/e_learning%2Fimages%2Fresultv2%2Fgradek.png";
	static String kExamLevelImgURLGrade1 = "http://resource.vipkid.com.cn/e_learning%2Fimages%2Fresultv2%2Fgrade%EF%BC%91.png";
	static String kExamLevelImgURLGrade2 = "http://resource.vipkid.com.cn/e_learning%2Fimages%2Fresultv2%2Fgrade-2.png";
	
	private static HashMap<String, String>  kLevelImageUrl = new HashMap<String,String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{		
			put("foundation",kExamLevelImgURLFoundation);
			
			put("l1u0",kExamLevelImgURLFoundation);
			put("l1u1",kExamLevelImgURLGradeK);
			put("l1u4",kExamLevelImgURLGradeK);
			put("l1u9",kExamLevelImgURLGradeK);
			
			put("l2u1",kExamLevelImgURLGradeK);
			put("l2u4",kExamLevelImgURLGradeK);
			put("l2u7",kExamLevelImgURLGrade1);
			put("l2u10",kExamLevelImgURLGrade1);
			
			put("l3u1",kExamLevelImgURLGrade1);
			put("l3u4",kExamLevelImgURLGrade1);
			put("l3u7",kExamLevelImgURLGrade2);
			put("l3u10",kExamLevelImgURLGrade2);
			
			put("l4u1",kExamLevelImgURLGrade2);
			put("l4u4",kExamLevelImgURLGrade2);
			put("l4u7",kExamLevelImgURLGrade2);
			put("l4u10",kExamLevelImgURLGrade2);
		}
	};
	
	private String getLevelImgUrl(String strLevel) {
		//
		String strURL = null;
		if (!StringUtils.isEmpty(strLevel)) {
			strURL = kLevelImageUrl.get(strLevel.toLowerCase());
		}
		
		return strURL;
	}
	
	/**
	 * 0级结果上报url处理
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = LevelTestWebController.PATH_EXAMLEVEL0RESULT, method = RequestMethod.GET)
	public String reportStudentExamLevel0Result(Model model,
			@PathVariable("result") String result,
			@CookieValue(value=COOKIE_KEY_STUDENTEXAM_UUID, required=false, defaultValue="") String strExam_uuid,
			@CookieValue(value=COOKIE_KEY_STUDENT_ID, required=false, defaultValue="0") long studentId,
			HttpServletRequest request, HttpServletResponse responese) {
		
		//公共信息
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
//		long studentId = Long.parseLong(sid.toString());
		StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if(student == null){
			logger.error("can't get studentVO for {}",studentId);
			return "redirect:/login";
		}
		
		// uuid check
		if (null == strExam_uuid || StringUtils.isEmpty(strExam_uuid)) {
			return "redirect:/parent/leveltest";
		}

		// update exam
		String strExamLevel = result.toUpperCase();
				
		StudentExam studentExam = studentExamService.updateStudentExam(studentId, student.getFamilyId(), strExamLevel, strExam_uuid);
		if (null == studentExam) {
			return "redirect:/parent/leveltest";
		}

		//
		model.addAttribute("name", "新手指导");
		model.addAttribute("studentExam", studentExam);

		Cookie cookieUUID = new Cookie(COOKIE_KEY_STUDENTEXAM_UUID, "");
		// cookieUUID.setHttpOnly(true);
		// cookieUUID.setDomain("vipkid.com.cn");
		cookieUUID.setPath("/");
		cookieUUID.setMaxAge(0); //
		responese.addCookie(cookieUUID);
		
		//
		return LevelTestWebController.VIEW_EXAMLEVEL0RESULT;
	}
}
