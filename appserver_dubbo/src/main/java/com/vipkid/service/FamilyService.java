package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.model.AirCraft;
import com.vipkid.model.AirCraftTheme;
import com.vipkid.model.AssessmentReport;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.EducationalComment;
import com.vipkid.model.Family;
import com.vipkid.model.FiremanToStudentComment;
import com.vipkid.model.FiremanToTeacherComment;
import com.vipkid.model.FollowUp;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.Medal;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClassOperation;
import com.vipkid.model.Order;
import com.vipkid.model.OrderItem;
import com.vipkid.model.Parent;
import com.vipkid.model.PayrollItem;
import com.vipkid.model.Pet;
import com.vipkid.model.Student;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.User;
import com.vipkid.repository.AirCraftRepository;
import com.vipkid.repository.AirCraftThemeRepository;
import com.vipkid.repository.AssessmentReportRepository;
import com.vipkid.repository.EducationalCommentRepository;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.FiremanToStudentCommentRepository;
import com.vipkid.repository.FiremanToTeacherCommentRepository;
import com.vipkid.repository.FollowUpRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.MedalRepository;
import com.vipkid.repository.OnlineClassOperationRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OrderItemRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.PayrollItemRepository;
import com.vipkid.repository.PetRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherCommentRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.FamilyDoNotHaveEnoughInvitationNumberException;
import com.vipkid.service.exception.FamilyHaveNoStudentLearningException;
import com.vipkid.service.pojo.Count;

@Service("familyService")
public class FamilyService {
	private Logger logger = LoggerFactory.getLogger(FamilyService.class.getSimpleName());
	
	@Resource
	private FamilyRepository familyRepository;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private LearningProgressRepository learningProgressRepository;
	
	@Resource
	private ParentRepository parentRepository;
	
	@Resource
	private StudentRepository studentRepository;
	
	@Resource
	private AirCraftRepository airCraftRepository;
	
	@Resource
	private AirCraftThemeRepository airCraftThemeRepository;
	
	@Resource
	private AssessmentReportRepository assessmentReportRepository;
	
	@Resource
	private EducationalCommentRepository educationalCommentRepository;
	
	@Resource
	private FiremanToStudentCommentRepository firemanToStudentCommentRepository;
	
	@Resource
	private FiremanToTeacherCommentRepository firemanToTeacherCommentRepository;
	
	@Resource
	private FollowUpRepository followUpRepository;
	
	@Resource
	private MedalRepository medalRepository;
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private OrderItemRepository orderItemRepository;
	
	@Resource 
	private OnlineClassOperationRepository onlineClassOperationRepository;
	
	@Resource
	private OrderRepository orderRepository;
	
	@Resource
	private PetRepository petRepository;
	
	@Resource
	private PayrollItemRepository payrollItemRepository;
	
	@Resource
	private TeacherCommentRepository teacherCommentRepository;
	
	
	
	public Family find(long id) {
		logger.debug("find family for id = {}", id);
		return familyRepository.find(id);
	}
	
	public List<Family> list(String search,String province,String city,int start, int length) {
		logger.debug("list family with params: search = {}, province = {}, city = {}, start = {}, length = {}.", search, province, city, start, length);
		return familyRepository.list(search, province, city, start, length);
	}

	public Count count(String search,String province, String city) {
		logger.debug("count family with params: search = {}, province = {}, city = {}, start = {}, length = {}.", search, province, city);
		return new Count(familyRepository.count(search, province, city));
	}
	
	public Family findByStudentId(long studentId) {
		logger.debug("find family for student id = {}", studentId);
		return familyRepository.findByStudentId(studentId);
	}
	
	public Family getInvitation(long familyId,int cost) {
		Family family = familyRepository.find(familyId);
		long numberInvited = family.getStudentNumberIInvented();
		if (numberInvited >= cost) {
			family.setStudentNumberIInvented(numberInvited - cost);
			familyRepository.update(family);
			
			switch (cost) {
			case 3:
				//1课时
				addNewClassHour(familyId, 1);
				break;
			case 5:
				//2课时
				addNewClassHour(familyId, 2);
				break;
			case 10:
				//5课时
				addNewClassHour(familyId, 5);
				break;
			case 15:
				//10课时
				addNewClassHour(familyId, 10);
				break;
			default:
				break;
			}
		} else {
			throw new FamilyDoNotHaveEnoughInvitationNumberException("您还需要{}个邀请。", cost - numberInvited);
		}
		return family;
	}
	
	private void addNewClassHour(Long familyId, int hour) {
		LearningProgress learningProgress = learningProgressRepository.findByFamilyId(familyId);
		
		if (learningProgress != null) {
			int currentTotalClassHour = learningProgress.getTotalClassHour();
			int currentLeftClassHour = learningProgress.getLeftClassHour();
			
			learningProgress.setTotalClassHour(currentTotalClassHour + hour);
			learningProgress.setLeftClassHour(currentLeftClassHour + hour);
			
			learningProgressRepository.update(learningProgress);
		} else {
			throw new FamilyHaveNoStudentLearningException();
		}
	}
	
	public Family setUrlParamForInvitationPage(long familyId) {
		logger.debug("setUrlParamForInvitationPage");
		Family family = familyRepository.find(familyId);
		
		if (family.getInvitationId() == null) {
			family.setInvitationId(PasswordEncryptor.encrypt(family.getId() + family.getName()));
			familyRepository.update(family);
		}
		
		return family;
	}
	
	public Family create(Family family) {
		logger.debug("create family: {}", family);
		
		User creater = securityService.getCurrentUser();
		family.setCreater(creater);
		User lastEditor = securityService.getCurrentUser();
		family.setLastEditor(lastEditor);
		familyRepository.create(family);
		
		securityService.logAudit(Level.INFO, Category.FAMILY_CREATE, "Create family: " + family.getName());
		
		return family;
	}
	
	public Family update(Family family) {
		logger.debug("update family: {}", family);
		
		User lastEditor = securityService.getCurrentUser();
		family.setLastEditor(lastEditor);
		familyRepository.update(family);
		
		securityService.logAudit(Level.INFO, Category.FAMILY_UPDATE, "Update family: " + family.getName());
		
		return family;
	}
	
	public Response delete(long familyId) {
		List<Student> students = studentRepository.findByFamilyId(familyId);
		for (Student student : students){
			long studentId = student.getId();
			
			List<AirCraft> airCrafts = airCraftRepository.findByStudentId(studentId);
			for (AirCraft airCraft : airCrafts){
				List<AirCraftTheme> airCraftThemes = airCraftThemeRepository.findByAirCraftId(airCraft.getId());
				for (AirCraftTheme airCraftTheme : airCraftThemes){
					airCraftThemeRepository.delete(airCraftTheme);
				}
				airCraftRepository.delete(airCraft);
			}
			
			List<AssessmentReport> assessmentReports = assessmentReportRepository.findByStudentId(studentId);
			for (AssessmentReport assessmentReport : assessmentReports){
				assessmentReportRepository.delete(assessmentReport);
			}
			
			List<EducationalComment> educationalComments = educationalCommentRepository.findByStudentId(studentId);
			for (EducationalComment educationalComment : educationalComments){
				educationalCommentRepository.delete(educationalComment);
			}
			
			List<FollowUp> followups = followUpRepository.findByStudentId(studentId);
			for(FollowUp followup : followups){
				followUpRepository.delete(followup);
			}
			
			List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentId(studentId);
			for(LearningProgress learningProgress : learningProgresses){
				learningProgress.setCompletedOnlineClasses(null);
				learningProgressRepository.update(learningProgress);
				learningProgressRepository.delete(learningProgress);
			}
			
			List<Medal> medals = medalRepository.findByStudentId(studentId);
			for(Medal medal : medals){
				medalRepository.delete(medal);
			}
			
			List<Order> orders = orderRepository.findByStudentId(studentId);
			for(Order order : orders){
				List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
				for(OrderItem orderItem : orderItems){
					orderItemRepository.delete(orderItem);
				}
				orderRepository.delete(order);
			}
			
			List<Pet> pets = petRepository.findByStudentId(studentId);
			for(Pet pet : pets){
				petRepository.delete(pet);
			}
			
			List<TeacherComment> teacherComments = teacherCommentRepository.findByStudentId(studentId);
			for(TeacherComment teacherComment : teacherComments){
				teacherComment.setStudent(null);;
				teacherCommentRepository.update(teacherComment);
				teacherCommentRepository.delete(teacherComment);
			}
			
			List<FiremanToStudentComment> firemanToStudentComments = firemanToStudentCommentRepository.findByStudentId(studentId);
			for(FiremanToStudentComment firemanToStudentComment : firemanToStudentComments){
				firemanToStudentCommentRepository.delete(firemanToStudentComment);
			}
			
			List<OnlineClassOperation> onlineClassOperations = onlineClassOperationRepository.findByStudentId(studentId);		
			for(OnlineClassOperation onlineClassOperation : onlineClassOperations){
				onlineClassOperation.setStudents(null);
				onlineClassOperationRepository.update(onlineClassOperation);
				onlineClassOperationRepository.delete(onlineClassOperation);
			}
			
			List<OnlineClass> onlineClasses = onlineClassRepository.findAllByStudentId(studentId);
			for(OnlineClass onlineClass : onlineClasses){
				if (getCourse(onlineClass).getMode() == Course.Mode.ONE_TO_MANY){
					onlineClass.removeStudent(student);
					onlineClassRepository.update(onlineClass);
				} else {
					onlineClass.setStudents(null);
					onlineClass.setBackupTeachers(null);
					onlineClass.setAsScheduledStudents(null);
					onlineClass.setItProblemStudents(null);
					onlineClass.setNoShowStudents(null);
					onlineClassRepository.update(onlineClass);
					
					List<LearningProgress> tmpLearningProgresses = learningProgressRepository.findByOnlineClassId(onlineClass.getId());
					for (LearningProgress learingProgress : tmpLearningProgresses){
						learingProgress.setCompletedOnlineClasses(null);
						learningProgressRepository.update(learingProgress);
					}
					
					FiremanToTeacherComment firemanToTeacherComment = firemanToTeacherCommentRepository.findByOnlineClassId(onlineClass.getId());
					if (firemanToTeacherComment != null){
						firemanToTeacherComment.setTeacherBehaviorProblem(null);
						firemanToTeacherComment.setTeacherITProblem(null);
						firemanToTeacherCommentRepository.update(firemanToTeacherComment);
						firemanToTeacherCommentRepository.delete(firemanToTeacherComment);
					}
					
					
					PayrollItem payrollItem = payrollItemRepository.findByOnlineClassId(onlineClass.getId());
					if (payrollItem != null){
						payrollItemRepository.delete(payrollItem);
					}

					List<TeacherComment> comments = teacherCommentRepository.findByOnlineClassId(onlineClass.getId());
					for (TeacherComment comment : comments){
						teacherCommentRepository.delete(comment);
					}
					onlineClassRepository.delete(onlineClass);
				}
				
				
			}
			
			student.removeFavorTeachers();
			studentRepository.update(student);
			studentRepository.delete(student);	
		}
		
		List<Parent> parents = parentRepository.findByFamilyId(familyId);
		for (Parent parent : parents){
			List<Student> allStudents = studentRepository.findByUserId(parent.getId());
			for(Student student : allStudents){
				if (student.getCreater() != null){
					student.setCreater(null);
					studentRepository.update(student);
				}
				if (student.getLastEditor() != null){
					student.setLastEditor(null);
					studentRepository.update(student);
				}
				
			}
			parentRepository.delete(parent);
		}
		
		
		familyRepository.deleteById(familyId);
		return new Response(HttpStatus.OK.value());
	}
	
	private Course getCourse(final OnlineClass onlineClass){
		return onlineClass.getLesson().getLearningCycle().getUnit().getCourse();
	}
	
}
