package com.vipkid.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.ext.dby.ListDocumentsResult;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.FiremanToStudentCommentAdapter;
import com.vipkid.model.json.moxy.FiremanToTeacherCommentAdapter;
import com.vipkid.model.json.moxy.DemoReportAdapter;
import com.vipkid.model.json.moxy.LessonAdapter;
import com.vipkid.model.json.moxy.PayrollItemAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.json.moxy.TeacherCommentAdapter;
import com.vipkid.model.json.moxy.UserAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;
import com.vipkid.util.TextUtils;
import com.vipkid.model.json.moxy.StudentCommentAdapter;
/**
 * 在线课程
 */
@Entity
@Table(name = "online_class", schema = DBInfo.SCHEMA)
public class OnlineClass extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Status {
		AVAILABLE, // 可排课
		OPEN, //一对多课程，可接受预约
		BOOKED, //已预约
		FINISHED, // 已结束
		CANCELED, // 已取消
		EXPIRED,  // 已过期
		REMOVED,  // 已删除， 用于统计删除timeslot
		INVALID, //已无效，课程换老师后原课程变为INVALID
	}
	
	public enum FinishType {		
		AS_SCHEDULED,		// 正常结束
		STUDENT_NO_SHOW,	// 学生缺课
		TEACHER_NO_SHOW,	// 教师缺课
		TEACHER_NO_SHOW_WITH_BACKUP,
		TEACHER_NO_SHOW_WITH_SHORTNOTICE,
		STUDENT_IT_PROBLEM, // 学生IT问题
		TEACHER_IT_PROBLEM, // 老师IT问题
		TEACHER_IT_PROBLEM_WITH_BACKUP,
		TEACHER_IT_PROBLEM_WITH_SHORTNOTICE,
		TEACHER_CANCELLATION, // 老师申请取消
		SYSTEM_PROBLEM, //系统问题
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 编号，为创建时的时间戳
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false)
	private String serialNumber;

	// 课程
	@XmlJavaTypeAdapter(LessonAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "lesson_id", referencedColumnName = "id")
	private Lesson lesson;
	
	// 预约人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "creater_id", referencedColumnName = "id")
	private User booker;
	
	// 预约时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "book_date_time")
	private Date bookDateTime;
	
	// 最后编辑人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "last_editor_id", referencedColumnName = "id")
	private User lastEditor;
	
	// 最后编辑时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_edit_date_time")
	private Date lastEditDateTime;

	// 学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "online_class_student", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "online_class_id"))
	private List<Student> students = new LinkedList<Student>();
	
	@XmlJavaTypeAdapter(StudentAdapter.class)
    @ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "online_class_as_scheduled_student", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "online_class_id"))
	private List<Student> asScheduledStudents;
	
	@XmlJavaTypeAdapter(StudentAdapter.class)
    @ManyToMany(cascade = CascadeType.REFRESH)
   	@JoinTable(name = "online_class_it_problem_student", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "online_class_id"))
	private List<Student> itProblemStudents;
	
	@XmlJavaTypeAdapter(StudentAdapter.class)
    @ManyToMany(cascade = CascadeType.REFRESH)
   	@JoinTable(name = "online_class_no_show_student", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "online_class_id"))
	private List<Student> noShowStudents;

	// 老师
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private Teacher teacher;
	
	// 代课老师
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "online_class_backup_teacher", inverseJoinColumns = @JoinColumn(name = "backup_teacher_id"), joinColumns = @JoinColumn(name = "online_class_id"))	
	private List<Teacher> backupTeachers;

	// 预约时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "scheduled_date_time")
	private Date scheduledDateTime;
	
	// 可以进入教室时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "able_to_enter_classroom_date_time")
	private Date ableToEnterClassroomDateTime;
	
	// 老师进入教室时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "teacher_enter_classroom_date_time")
	private Date teacherEnterClassroomDateTime;

	// 学生进入教室时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "student_enter_classroom_date_time")
	private Date studentEnterClassroomDateTime;

	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	
	// 结束类型
	@Enumerated(EnumType.STRING)
	@Column(name = "finish_type")
	private FinishType finishType;
	
	// 在线教室地址
	@Column(name = "classroom")
	private String classroom;
	
	// doc id
	@Column(name = "dby_document")
	private String dbyDocument;

	//网校通课程Id
	@Column(name = "wxt_course_id")
	private String wxtCourseId;
	
	// 是否消耗课时
	@Column(name = "consume_class_hour")
	private boolean consumeClassHour;
	
	// 最小参课学生数
	@Column(name = "min_student_number")
	private int minStudentNumber = Configurations.OnlineClass.MIN_STUDENT_NUMBER;
	
	// 最大参课学生数
	@Column(name = "max_student_number")
	private int maxStudentNumber = Configurations.OnlineClass.MAX_STUDENT_NUMBER;
	
	// 已删除标志
	@Column(name = "archived")
	private boolean archived;
	
	@Column(name = "can_undo_finish")
	private boolean canUndoFinish;
	
	//是否是紧急备用课程
	@Column(name = "short_notice")
	private boolean shortNotice;
	
	//是否是备用课程，也可以理解为onlineClass对应的老师（teacher 字段）是否为代课老师
	@Column(name = "backup")
	private boolean backup;

	//是否上传 ppt 成功
	@Column(name ="attatchDocumentSucess", columnDefinition="bit default 1")
    private boolean attatchDocumentSucess;
	
	// 是否上过trail课，并且已付费
	@Column(name = "is_paid_trail")
	private boolean isPaidTrail;


	// 工资项
    @XmlJavaTypeAdapter(PayrollItemAdapter.class)
	@OneToOne(mappedBy="onlineClass")
	private PayrollItem payrollItem;
    
	// 教师评语
    @XmlJavaTypeAdapter(TeacherCommentAdapter.class)
	@OneToMany(mappedBy="onlineClass")
	private List<TeacherComment> teacherComments;
    
    //家长评语
    @XmlJavaTypeAdapter(StudentCommentAdapter.class)
    @OneToMany(mappedBy="onlineClass")
    private List<StudentComment> studentComments;
    
	// Fireman对教师评语
    @XmlJavaTypeAdapter(FiremanToTeacherCommentAdapter.class)
    @OneToOne(mappedBy="onlineClass")
	private FiremanToTeacherComment firemanToTeacherComment;
    
	// Fireman对学生评语
    @XmlJavaTypeAdapter(FiremanToStudentCommentAdapter.class)
    @OneToMany(mappedBy="onlineClass")
	private List<FiremanToStudentComment> firemanToStudentComments;
    
    // 面试课程的
    @XmlJavaTypeAdapter(DemoReportAdapter.class)
    @OneToOne(mappedBy="onlineClass")
	private DemoReport demoReport;
    
    
//	// 学术督导评语
//  @XmlJavaTypeAdapter(EducationalCommentAdapter.class)
//  @OneToMany(mappedBy="onlineClass")
//	private List<EducationalComment> educationalComments;    

	// 备注
    @Lob
    @Column(name="comments")
    private String comments;
    
    @Column(name = "unit_price")
	private float unitPrice;
   
	@Transient
	private Course course;
	
	/**
	 * 2015-07-15 添加属性--类型（1v1 1vN）
	 */
	@Transient
//	@Enumerated(EnumType.STRING)
	private	Course.Mode courseMode = Mode.ONE_ON_ONE; 
	
	/**
	 * 2015-08-29 添加属性--course类型-- trial 课判断时使用
	 */
	@Transient
	private	Course.Type courseType; 
	
	
	public Course.Type getCourseType() {
		return courseType;
	}

	public void setCourseType(Course.Type courseType) {
		this.courseType = courseType;
	}

	@Transient
	private String studentEnglishNames;
	
	@Transient
	private String backupTeacherNames;
	
	@Transient
	private String cancelledStudentEnglishNames;
	
	@Transient
	private List<Student> cancelledStudents;
	
	@Transient
	private Teacher substituteTeacher;
	
	@Transient
	private List<ListDocumentsResult> docmentResultList;
	
	@Transient
	private Long studentCount;
	
	public Long getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(Long studentCount) {
		this.studentCount = studentCount;
	}

	@PrePersist
	public void prePersist() {
		if (this.serialNumber == null){
			this.serialNumber = Long.toString((new Date()).getTime());
		}
		// 可以提前1小时进入教室
		this.ableToEnterClassroomDateTime = new Date(scheduledDateTime.getTime() - 60 * 60 * 1000);
		
		this.lastEditDateTime = new Date();
	}
	
	/**
	 * 2015-05-16  为online class设置类型（1v1 or 1vN）
	 */
	@PostLoad
	public void updateCourseMode() {
		try {
			Course.Mode mode = this.getLesson().getLearningCycle().getUnit().getCourse().getMode();
			if (null != mode) {
				this.setCourseMode(mode);
			}
		} catch(Exception e) {
			
		}
		
		// 2015-08-29
		try {
			Course.Type type = this.getLesson().getLearningCycle().getUnit().getCourse().getType();
			if (null != type) {
				this.setCourseType(type);
			}
		} catch(Exception e) {
			
		}
 	}
	
	@PreUpdate
	public void preUpdate() {
		this.lastEditDateTime = new Date();
	}
	
	public float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Lesson getLesson() {
		return lesson;
	}

	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public List<Teacher> getBackupTeachers() {
		return backupTeachers;
	}

	public void setBackupTeachers(List<Teacher> backupTeachers) {
		this.backupTeachers = backupTeachers;
	}

	public Date getScheduledDateTime() {
		return scheduledDateTime;
	}

	public void setScheduledDateTime(Date scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public Date getTeacherEnterClassroomDateTime() {
		return teacherEnterClassroomDateTime;
	}

	public void setTeacherEnterClassroomDateTime(Date teacherEnterClassroomDateTime) {
		this.teacherEnterClassroomDateTime = teacherEnterClassroomDateTime;
	}

	public Date getStudentEnterClassroomDateTime() {
		return studentEnterClassroomDateTime;
	}

	public void setStudentEnterClassroomDateTime(Date studentEnterClassroomDateTime) {
		this.studentEnterClassroomDateTime = studentEnterClassroomDateTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public FinishType getFinishType() {
		return finishType;
	}

	public void setFinishType(FinishType finishType) {
		this.finishType = finishType;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isCanUndoFinish() {
		return canUndoFinish;
	}

	public void setCanUndoFinish(boolean canUndoFinish) {
		this.canUndoFinish = canUndoFinish;
	}
	
	public boolean isShortNotice() {
		return shortNotice;
	}

	public void setShortNotice(boolean shortNotice) {
		this.shortNotice = shortNotice;
	}

	public boolean isBackup() {
		return backup;
	}

	public void setBackup(boolean backup) {
		this.backup = backup;
	}

	public boolean isAttatchDocumentSucess() {
		return attatchDocumentSucess;
	}

	public void setAttatchDocumentSucess(boolean attatchDocumentSucess) {
		this.attatchDocumentSucess = attatchDocumentSucess;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public void addStudent(Student student){
		this.students.add(student);
		concatStudentEnglishNames();
	}
	
	public void addStudents(List<Student> students){
		this.students.addAll(students);
		concatStudentEnglishNames();
	}
	
	public void removeStudent(Student student){
		this.students.remove(student);
		concatStudentEnglishNames();
	}
	
	public void removeStudents(List<Student> students){
		this.students.remove(students);
		concatStudentEnglishNames();
	}	
	
	public String getStudentEnglishNames() {
		concatStudentEnglishNames();
		return studentEnglishNames;
	}

	public void setStudentEnglishNames(String studentEnglishNames) {
		this.studentEnglishNames = studentEnglishNames;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getClassroom() {
		return classroom;
	}	

	public String getDbyDocument() {
		return dbyDocument;
	}

	public void setDbyDocument(String dbyDocument) {
		this.dbyDocument = dbyDocument;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public int getMinStudentNumber() {
		return minStudentNumber;
	}

	public void setMinStudentNumber(int minStudentNumber) {
		this.minStudentNumber = minStudentNumber;
	}

	public int getMaxStudentNumber() {
		return maxStudentNumber;
	}

	public void setMaxStudentNumber(int maxStudentNumber) {
		this.maxStudentNumber = maxStudentNumber;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Teacher getSubstituteTeacher() {
		return substituteTeacher;
	}

	public void setSubstituteTeacher(Teacher substituteTeacher) {
		this.substituteTeacher = substituteTeacher;
	}

	public String getBackupTeacherNames() {
		concatBackupTeacherNames();
		return backupTeacherNames;
	}

	public void setBackupTeacherNames(String backupTeacherNames) {
		this.backupTeacherNames = backupTeacherNames;
	}
	
	public List<Student> getAsScheduledStudents() {
		return asScheduledStudents;
	}

	public void setAsScheduledStudents(List<Student> asScheduledStudents) {
		this.asScheduledStudents = asScheduledStudents;
	}

	public List<Student> getItProblemStudents() {
		return itProblemStudents;
	}

	public void setItProblemStudents(List<Student> itProblemStudents) {
		this.itProblemStudents = itProblemStudents;
	}

	public List<Student> getNoShowStudents() {
		return noShowStudents;
	}

	public void setNoShowStudents(List<Student> noShowStudents) {
		this.noShowStudents = noShowStudents;
	}

	public List<Student> getCancelledStudents() {
		return cancelledStudents;
	}

	public void setCancelledStudents(List<Student> cancelledStudents) {
		this.cancelledStudents = cancelledStudents;
	}

	public String getCancelledStudentEnglishNames() {
		concatCancelledStudentEnglishNames();
		return cancelledStudentEnglishNames;
	}

	public void setCancelledStudentEnglishNames(String cancelledStudentEnglishNames) {
		this.cancelledStudentEnglishNames = cancelledStudentEnglishNames;
	}

	private void concatStudentEnglishNames(){
		if (this.studentEnglishNames != null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		if (students != null){
			int count = 0;
			for(Student tmpStudent : students) {
				count ++;
				if (count > 6) {
					sb.append(TextUtils.SPACE + "...");
					break;
				}
				sb.append(tmpStudent.getEnglishName()).append(TextUtils.SPACE);
			}
		}
		
		this.studentEnglishNames = sb.toString().trim();
	}
	
	private void concatBackupTeacherNames(){
		StringBuilder sb = new StringBuilder();
		if (backupTeachers != null){
			for(Teacher tmpTeacher : backupTeachers) {
				sb.append(tmpTeacher.getName()).append(TextUtils.SPACE);
			}
		}
		
		this.backupTeacherNames = sb.toString().trim();
	}
	
	private void concatCancelledStudentEnglishNames(){
		StringBuilder sb = new StringBuilder();
		if (cancelledStudents != null){
			for(Student tmpStudent : cancelledStudents) {
				sb.append(tmpStudent.getEnglishName()).append(TextUtils.SPACE);
			}
			
			this.cancelledStudentEnglishNames = sb.toString().trim();
		}
	}
	
	public PayrollItem getPayrollItem() {
		return payrollItem;
	}

	public void setPayrollItem(PayrollItem payrollItem) {
		this.payrollItem = payrollItem;
		if(this.payrollItem != null && this.payrollItem.getOnlineClass() != null){
			if (this.payrollItem.getOnlineClass().getId() != this.getId()) {
				this.payrollItem.setOnlineClass(this);
			}
		}
	}

	public List<TeacherComment> getTeacherComments() {
		return teacherComments;
	}

	public void setTeacherComments(List<TeacherComment> teacherComments) {
		this.teacherComments = teacherComments;
	}

	public FiremanToTeacherComment getFiremanToTeacherComment() {
		return firemanToTeacherComment;
	}

	public void setFiremanToTeacherComment(FiremanToTeacherComment firemanToTeacherComment) {
		this.firemanToTeacherComment = firemanToTeacherComment;
	}

	public List<FiremanToStudentComment> getFiremanToStudentComments() {
		return firemanToStudentComments;
	}

	public void setFiremanToStudentComments(List<FiremanToStudentComment> firemanToStudentComments) {
		this.firemanToStudentComments = firemanToStudentComments;
	}
	
	public boolean isConsumeClassHour() {
		return consumeClassHour;
	}

	public void setConsumeClassHour(boolean consumeClassHour) {
		this.consumeClassHour = consumeClassHour;
	}

	public Date getAbleToEnterClassroomDateTime() {
		return ableToEnterClassroomDateTime;
	}

	public void setAbleToEnterClassroomDateTime(Date ableToEnterClassroomDateTime) {
		this.ableToEnterClassroomDateTime = ableToEnterClassroomDateTime;
	}

	public User getBooker() {
		return booker;
	}

	public void setBooker(User booker) {
		this.booker = booker;
	}

	public Date getBookDateTime() {
		return bookDateTime;
	}

	public void setBookDateTime(Date bookDateTime) {
		this.bookDateTime = bookDateTime;
	}

	public User getLastEditor() {
		return lastEditor;
	}

	public void setLastEditor(User lastEditor) {
		this.lastEditor = lastEditor;
	}

	public Date getLastEditDateTime() {
		return lastEditDateTime;
	}

	public void setLastEditDateTime(Date lastEditDateTime) {
		this.lastEditDateTime = lastEditDateTime;
	}

	public String getWxtCourseId() {
		return wxtCourseId;
	}

	public void setWxtCourseId(String wxtCourseId) {
		this.wxtCourseId = wxtCourseId;
	}

	public DemoReport getDemoReport() {
		return demoReport;
	}

	public void setDemoReport(DemoReport demoReport) {
		this.demoReport = demoReport;
	}

	//I removed toString() because it invokes many times when jpa cascades. 
	public String getOnlineClassName(){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("OnlineClass [serialNumber=")
				.append(serialNumber == null ? "" : serialNumber)
				.append(",")
				.append("teacher=")
				.append(teacher == null ? "" : teacher.getName())
				.append(",")
				.append("scheduledDateTime=")
				.append(scheduledDateTime == null ? "" : DateTimeUtils.format(scheduledDateTime,DateTimeUtils.DATETIME_FORMAT2))
				.append(" with students=")
				.append(getStudentEnglishNames())
				.append("]");
		return buffer.toString();
	}
	
	
	public List<ListDocumentsResult> getDocmentResultList() {
		return docmentResultList;
	}

	public void setDocmentResultList(List<ListDocumentsResult> docmentResultList) {
		this.docmentResultList = docmentResultList;
	}
	
	public Course.Mode getCourseMode() {
		return courseMode;
	}

	public void setCourseMode(Course.Mode courseMode) {
		this.courseMode = courseMode;
	}
	
	public boolean getPaidTrail() {
		return isPaidTrail;
	}

	public void setPaidTrail(boolean isPaidTrail) {
		this.isPaidTrail = isPaidTrail;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OnlineClass other = (OnlineClass) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public List<StudentComment> getStudentComments() {
		return studentComments;
	}

	public void setStudentComments(List<StudentComment> studentComments) {
		this.studentComments = studentComments;
	}
	
}
