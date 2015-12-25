package com.vipkid.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.CourseAdapter;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.ItTestAdapter;
import com.vipkid.model.json.moxy.OnlineClassesAdapter;
import com.vipkid.model.json.moxy.PartnerAdapter;
import com.vipkid.model.json.moxy.StaffAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.json.moxy.TeacherApplicationAdapter;
import com.vipkid.model.json.moxy.TeacherLifeCycleLogAdapter;

import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.service.pojo.TeacherInfoVO;

/**
 * 老师
 * 用户名为老师邮箱
 */
@Entity
@Table(name = "teacher", schema = DBInfo.SCHEMA)
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
public class Teacher extends User {
	private static final long serialVersionUID = 1L;
	
	public enum Currency {
		US_DOLLAR, // 美元
		CANADIAN_DOLAR, // 加元
		CNY // 人民币
	}
	
	public enum Certificate {
		TESOL, // 英语教育认证
		TEFL // 作为外语的英语教学认证
	}
	
	public enum Type {
		FULL_TIME, // 全职
		PART_TIME,  // 兼职
		TEST //test
	}
	
	public enum LifeCycle {
		SIGNUP, // 注册 + 收集基本信息
		BASIC_INFO,	// 2015-08-08 添加basic-info 状态，从signup分离
		INTERVIEW, //面试		
		SIGN_CONTRACT, //签合同
		TRAINING, // 教师培训
		PRACTICUM,//试讲 
		REGULAR, //成为正式老师Ø
		QUIT, // 离职
		FAIL //被剔除的老师，永不录用的那种
		
	}
	
	public enum RecruitmentChannel {
		CHEGG, // Chegg渠道
		STAFF_REFERAL, // 员工推荐 
		TEACHER_REFERAL, // 老师推荐
		SELF_REFERAL, // 自荐
		PARTNER_JON, // Partner：Jon
		PARTNER_RAYMOND, //Partner：Raymond
		PARTNER_JOY_XU, // Partner：Joy Xu
		PARTNER_RYAN_TAN, // Partner：Ryan Tan
		PARTNER_HELEN, // Partner：Helen
		PARTNER_NEISSA, // Partner：Neissa
		OTHER // 其他
	}
	
	public enum Hide {
		ALL,
		SCHEDULE,
		TRIAL,
		NONE
	}
	
	@Transient
	private transient Set<Role> roleSet = new HashSet<Role>();
	
	// 编号, 注册顺序, 如00001
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false, unique = true)
	private String serialNumber;
	
	// 真实姓名
	@Column(name = "real_name")
	private String realName;
		
	// 头像图片链接
	@Column(name = "avatar")
	private String avatar;
	
	@Column(name = "photos")
	private String photos;
	
	// 电子邮箱
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "email", nullable = false)
	private String email;
	
	// Skype
	@Column(name = "skype")
	private String skype;
	
	// linkedIn
	@Column(name = "linkedin")
	private String linkedIn;
	
	// qq
	@Column(name = "qq")
	private String qq;
	
	// 手机
	@Column(name = "mobile")
	private String mobile;
	
	// 简历
	@Column(name = "resume")
	private String resume;
	
	// 学历证书
	@Column(name = "bachelor_diploma")
	private String bachelorDiploma;
		
	// 学历证书
	@Column(name = "additional_diplomas")
	private String additionalDiplomas;
	
	// 教学经验（年）
	@Column(name = "teaching_experience")
	private int teachingExperience;
	
	// 教学证书
	@Column(name = "certificates")
	private String certificates;
	
	// 教学证书
	@Column(name = "certificate_files")
	private String certificateFiles;	
	
	//合同
	@Column(name = "contract")
	private String contract;	
	
//	// 2015-08-15 Quit操作时间
//	@Column(name="quit_time")
//	private Date quitTime;
	
//	// 2015-08-15 Quit操作人
//	@XmlJavaTypeAdapter(StaffAdapter.class)
//	@ManyToOne(cascade = CascadeType.REFRESH)
//	@JoinColumn(name = "quit_operator", referencedColumnName = "id")
//	private Staff quitOperator;
	
	// 2015-08-15 对应的manager
	@XmlJavaTypeAdapter(StaffAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "manager", referencedColumnName = "id")
	private Staff manager;
	

	// 经过培训能够教授的课程
	@XmlJavaTypeAdapter(CourseAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "teacher_certificated_course", inverseJoinColumns = @JoinColumn(name = "course_id"), joinColumns = @JoinColumn(name = "teacher_id"))
	private List<Course> certificatedCourses;
	
	// 喜欢该老师的学生列表
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "student_favorate_teacher", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "teacher_id"))
	private List<Student> favoredByStudents = new ArrayList<Student>();
	
	//申请记录
	@XmlJavaTypeAdapter(TeacherApplicationAdapter.class)
	@OneToMany(mappedBy = "teacher")
	private List<TeacherApplication> teacherApplications;	
	
	// 招募渠道
	@Enumerated(EnumType.STRING)
	@Column(name = "recruitment_channel")
	private RecruitmentChannel recruitmentChannel;
	
	// 阶段
	@Enumerated(EnumType.STRING)
	@Column(name = "life_cycle", nullable = false)
	private LifeCycle lifeCycle;
	
	// 类型
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;
	
	// 地址
	@Column(name = "address")
	private String address;
	
	// 简介
	@Lob
	@Column(name = "introduction")
	private String introduction;
	
	@Column(name = "short_video")
	private String shortVideo;
	
	// 时区
	@Column(name = "timezone")
	private String timezone = Timezone.ASIA_SHANGHAI;
	
	// 生日
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "birthday")
	private Date birthday;
	
	// 国籍
	@Enumerated(EnumType.STRING)
	@Column(name = "country")
	private Country country;
	
	// 工作
	@Column(name = "job")
	private String job;
	
	// 护照
	@Column(name = "passport")
	private String passport;
	
	// 生活照1
	@Column(name = "life_picture1")
	private String lifePicture1;
	
	// 生活照2
	@Column(name = "life_picture2")
	private String lifePicture2;
	
	
	// 每课时额外工资
	@Column(name = "extra_class_salary")
	private float extraClassSalary;
	
	// 每课时加班工资
	@Column(name = "over_time_class_salary")
	private float overTimeClassSalary;
	
	// 累计逃课次数
	@Column(name = "no_show_time")
	private long noShowTime;
	
	// 工资结算货币
	@Enumerated(EnumType.STRING)
	@Column(name="currency")
	private Currency currency = Currency.US_DOLLAR;
	
	// 合同起始日期
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "contract_start_date")
	private Date contractStartDate;
	
	// 合同结束日期
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "contract_end_date")
	private Date contractEndDate;
	
	// 在线课程列表
	@XmlJavaTypeAdapter(OnlineClassesAdapter.class)
    @OneToMany(mappedBy = "teacher")
    private List<OnlineClass> onlineClasses;
	
	// 备注
	@Column(name = "notes")
	private String notes;
	
	// 银行卡号
	@Column(name = "bank_card_number")
	private String bankCardNumber;
	
	// 银行户主姓名
	@Column(name = "bank_account_name")
	private String bankAccountName;
	
	// 银行名称
	@Column(name = "bank_name")
	private String bankName;
	
	// 银行地址
	@Column(name = "bank_address")
	private String bankAddress;
	
	// 银行国籍代码
	@Column(name = "bank_swift_code")
	private String bankSWIFTCode;
	
	// PayPal账号
	@Column(name = "paypal_account")
	private String payPalAccount;
	
	// 本字段决定老师何时被隐藏
	@Enumerated(EnumType.STRING)
	@Column(name="hide")
	private Hide hide = Hide.NONE;
	
	// 简介
	@Column(name = "summary")
	private String summary;
	
	//推荐人 partner
	@XmlJavaTypeAdapter(PartnerAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "partner_id", referencedColumnName = "id")
	private Partner partner;
	
	// 推荐人，这里存老师id和名字用逗号分隔。
	@Column(name = "referee")
	private String referee;
	
	// 用于招聘自动登录的ID
	@Column(name= "recruitment_id")
	private String recruitmentId;
	
	// 标记是否进行IT测试
 	@Column(name = "has_tested")
 	private boolean hasTested = false;
	 	
 	// ItTest列表
  	@XmlJavaTypeAdapter(ItTestAdapter.class)
 	@OneToMany(mappedBy="teacher")
  	private List<ItTest> itTests = new LinkedList<ItTest>();
	
	// 是否有课
	@Transient
	private boolean isNoAvailable;
	
	//中文介绍
	@Lob
 	@Column(name = "introduction_zh")
 	private String introductionZh;
 	
  	//毕业院校
 	@Column(name = "graduated_from")
 	private String graduatedFrom;
 	
 	//多个词语以分号隔开
 	@Column(name = "teacher_tags")
 	private String teacherTags;
 	
 	//VIPKID 对老师的评语
 	@Column(name = "vipkid_remarks")
	private String vipkidRemarks;
 	
 	//申请记录
 	@XmlJavaTypeAdapter(TeacherLifeCycleLogAdapter.class)
	@OneToMany(mappedBy = "teacher",cascade = CascadeType.REFRESH)
	private List<TeacherLifeCycleLog> teacherLifeCycleLogs; //= new LinkedList<TeacherLifeCycleLog>()

 	
	public String getIntroductionZh() {
		return introductionZh;
	}

	public void setIntroductionZh(String introductionZh) {
		this.introductionZh = introductionZh;
	}

	public String getGraduatedFrom() {
		return graduatedFrom;
	}

	public void setGraduatedFrom(String graduatedFrom) {
		this.graduatedFrom = graduatedFrom;
	}

	public String getTeacherTags() {
		return teacherTags;
	}

	public void setTeacherTags(String teacherTags) {
		this.teacherTags = teacherTags;
	}

	public String getVipkidRemarks() {
		return vipkidRemarks;
	}

	public void setVipkidRemarks(String vipkidRemarks) {
		this.vipkidRemarks = vipkidRemarks;
	}

	@Transient
	private TeacherApplication currentTeacherApplication;

	
	public String getReferee() {
		return referee;
	}

	public void setReferee(String referee) {
		this.referee = referee;
	}

	
	public String getRecrutmentId() {
		return recruitmentId;
	}

	public void setRecrutmentId(String recruitmentId) {
		this.recruitmentId = recruitmentId;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}


	@PrePersist
	public void prePersist() {
		super.prePersist();
		roles = Role.TEACHER.name();
	}
	
	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
	}
	
	public Teacher() {
		roleSet.add(Role.TEACHER);
		roles = Role.TEACHER.name();
	}
    
	@Override
	public Set<Role> getRoleSet() {
		return roleSet;
	}
	
	public Hide getHide() {
		return hide;
	}

	public boolean isNoAvailable() {
		return isNoAvailable;
	}

	public void setNoAvailable(boolean isNoAvailable) {
		this.isNoAvailable = isNoAvailable;
	}
	
	public void setHide(Hide hide) {
		this.hide = hide;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	public String getShortVideo() {
		return shortVideo;
	}

	public void setShortVideo(String shortVideo) {
		this.shortVideo = shortVideo;
	}
	
	
	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public float getExtraClassSalary() {
		return extraClassSalary;
	}

	public void setExtraClassSalary(float extraClassSalary) {
		this.extraClassSalary = extraClassSalary;
	}

	public long getNoShowTime() {
		return noShowTime;
	}

	public void setNoShowTime(long noShowTime) {
		this.noShowTime = noShowTime;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getTeachingExperience() {
		return teachingExperience;
	}

	public void setTeachingExperience(int teachingExperience) {
		this.teachingExperience = teachingExperience;
	}

	public String getCertificates() {
		return certificates;
	}

	public void setCertificates(String certificates) {
		this.certificates = certificates;
	}

	public List<Course> getCertificatedCourses() {
		return certificatedCourses;
	}

	public void setCertificatedCourses(List<Course> certificatedCourses) {
		this.certificatedCourses = certificatedCourses;
	}

	public RecruitmentChannel getRecruitmentChannel() {
		return recruitmentChannel;
	}

	public void setRecruitmentChannel(RecruitmentChannel recruitmentChannel) {
		this.recruitmentChannel = recruitmentChannel;
	}

	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getBankCardNumber() {
		return bankCardNumber;
	}

	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getBankSWIFTCode() {
		return bankSWIFTCode;
	}

	public void setBankSWIFTCode(String bankSWIFTCode) {
		this.bankSWIFTCode = bankSWIFTCode;
	}

	public String getPayPalAccount() {
		return payPalAccount;
	}

	public void setPayPalAccount(String payPalAccount) {
		this.payPalAccount = payPalAccount;
	}
	
	public List<Student> getFavoredByStudents() {
		return favoredByStudents;
	}

	public void setFavoredByStudents(List<Student> favoredByStudents) {
		for (Student student: favoredByStudents) {
			this.addFavoredByStudent(student);
		}
	}
	
	public void addFavoredByStudent(Student student) {
		boolean find = false;
		for (Student s : this.getFavoredByStudents()) {
			if (s.getId() == student.getId()) {
				find = true;
				break;
			}
		}
		
		if (!find) {
			this.getFavoredByStudents().add(student);
		}
		
		if (!student.getFavorTeachers().contains(this)) {
			student.addFavoredTeacher(this);
		}
	}

	public List<OnlineClass> getOnlineClasses() {
		return onlineClasses;
	}

	public void setOnlineClasses(List<OnlineClass> onlineClasses) {
		this.onlineClasses = onlineClasses;
	}

	public float getOverTimeClassSalary() {
		return overTimeClassSalary;
	}

	public void setOverTimeClassSalary(float overTimeClassSalary) {
		this.overTimeClassSalary = overTimeClassSalary;
	}
		
	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}


	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public String getBachelorDiploma() {
		return bachelorDiploma;
	}

	public void setBachelorDiploma(String bachelorDiploma) {
		this.bachelorDiploma = bachelorDiploma;
	}

	public String getAdditionalDiplomas() {
		return additionalDiplomas;
	}

	public void setAdditionalDiplomas(String additionalDiplomas) {
		this.additionalDiplomas = additionalDiplomas;
	}

	public void setRoleSet(Set<Role> roleSet) {
		this.roleSet = roleSet;
	}
	
	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getLifePicture1() {
		return lifePicture1;
	}

	public void setLifePicture1(String lifePicture1) {
		this.lifePicture1 = lifePicture1;
	}

	public String getLifePicture2() {
		return lifePicture2;
	}

	public void setLifePicture2(String lifePicture2) {
		this.lifePicture2 = lifePicture2;
	}

	public List<TeacherApplication> getTeacherApplications() {
		return teacherApplications;
	}

	public void setTeacherApplications(List<TeacherApplication> teacherApplications) {
		this.teacherApplications = teacherApplications;
	}

	public String getCertificateFiles() {
		return certificateFiles;
	}

	public void setCertificateFiles(String certificateFiles) {
		this.certificateFiles = certificateFiles;
	};
	
	
	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}
	
	@Override
	public boolean equals(Object obj) {
		Teacher teacher = (Teacher)obj;
		return teacher.getId() == this.getId();
	};
	
	@Override
	public int hashCode() {
		return (int) this.getId();
	}
	
	public String getSafeName() {
		if(this.realName != null) {
			return realName;
		}else {
			return super.name;
		}
	}

	public String getRecruitmentId() {
		return recruitmentId;
	}

	public void setRecruitmentId(String recruitmentId) {
		this.recruitmentId = recruitmentId;
	}

	public boolean isHasTested() {
		return hasTested;
	}

	public void setHasTested(boolean hasTested) {
		this.hasTested = hasTested;
	}

	public List<ItTest> getItTests() {
		return itTests;
	}

	public void setItTests(List<ItTest> itTests) {
		this.itTests = itTests;
	}

	public TeacherApplication getCurrentTeacherApplication() {
		return currentTeacherApplication;
	}

	public void setCurrentTeacherApplication(
			TeacherApplication currentTeacherApplication) {
		this.currentTeacherApplication = currentTeacherApplication;
	}
	
	public boolean checkBankInfoChanges(Teacher teacher){
		if(this.getBankAccountName() != null){
			if(teacher.getBankAccountName() == null){
				return true;
			}else{
				if(!this.getBankAccountName().equals(teacher.getBankAccountName())){
					return true;
				}
			}
		}
		if(this.getBankAccountName() == null){
			if(teacher.getBankAccountName()!= null){
				return true;
			}
		}		
		
		//
		if(this.getBankAddress() != null){
			if(teacher.getBankAddress() == null){
				return true;
			}else{
				if(!this.getBankAddress().equals(teacher.getBankAddress())){
					return true;
				}
			}
		}
		if(this.getBankAddress() == null){
			if(teacher.getBankAddress() != null){
				return true;
			}
		}
		
		
		//
		if(this.getBankCardNumber() != null){
			if(teacher.getBankCardNumber() == null){
				return true;
			}else{
				if(!this.getBankCardNumber().equals(teacher.getBankCardNumber())){
					return true;
				}
			}
		}
		if(this.getBankCardNumber() == null){
			if(teacher.getBankCardNumber() != null){
				return true;
			}
		}
		
		
		//
		if( this.getBankName() != null){
			if(teacher.getBankName() == null){
				return true;
			}else{
				if(!this.getBankName().equals(teacher.getBankName())){
					return true;
				}
			}
		}
		if(this.getBankName() == null){
			if(teacher.getBankName() != null){
				return true;
			}
		}
		
		
		//
		if(this.getBankSWIFTCode() != null){
			if(teacher.getBankSWIFTCode() == null){
				return true;
			}else{
				if(!this.getBankSWIFTCode().equals(teacher.getBankSWIFTCode())){
					return true;
				}
			}
		}
		if(this.getBankSWIFTCode() == null){
			if(teacher.getBankSWIFTCode()!= null){
				return true;
			}
		}
		
		//
		if(this.getPayPalAccount() != null){
			if(teacher.getPayPalAccount() == null){
				return true;
			}else{
				if(!this.getPayPalAccount().equals(teacher.getPayPalAccount())){
					return true;
				}
			}
		}
		if(this.getPayPalAccount() == null){
			if(teacher.getPayPalAccount()!= null){
				return true;
			}
		}
		
		return false;
	}

	public boolean checkBankInfoChanges(TeacherInfoVO teacher){
		if(this.getBankAccountName() != null){
			if(teacher.getBankAccountName() == null){
				return true;
			}else{
				if(!this.getBankAccountName().equals(teacher.getBankAccountName())){
					return true;
				}
			}
		}
		if(this.getBankAccountName() == null){
			if(teacher.getBankAccountName()!= null){
				return true;
			}
		}		
		
		//
		if(this.getBankAddress() != null){
			if(teacher.getBankAddress() == null){
				return true;
			}else{
				if(!this.getBankAddress().equals(teacher.getBankAddress())){
					return true;
				}
			}
		}
		if(this.getBankAddress() == null){
			if(teacher.getBankAddress() != null){
				return true;
			}
		}
		
		
		//
		if(this.getBankCardNumber() != null){
			if(teacher.getBankCardNumber() == null){
				return true;
			}else{
				if(!this.getBankCardNumber().equals(teacher.getBankCardNumber())){
					return true;
				}
			}
		}
		if(this.getBankCardNumber() == null){
			if(teacher.getBankCardNumber() != null){
				return true;
			}
		}
		
		
		//
		if( this.getBankName() != null){
			if(teacher.getBankName() == null){
				return true;
			}else{
				if(!this.getBankName().equals(teacher.getBankName())){
					return true;
				}
			}
		}
		if(this.getBankName() == null){
			if(teacher.getBankName() != null){
				return true;
			}
		}
		
		
		//
		if(this.getBankSWIFTCode() != null){
			if(teacher.getBankSWIFTCode() == null){
				return true;
			}else{
				if(!this.getBankSWIFTCode().equals(teacher.getBankSWIFTCode())){
					return true;
				}
			}
		}
		if(this.getBankSWIFTCode() == null){
			if(teacher.getBankSWIFTCode()!= null){
				return true;
			}
		}				
		
		//
		if(this.getPayPalAccount() != null){
			if(teacher.getPayPalAccount() == null){
				return true;
			}else{
				if(!this.getPayPalAccount().equals(teacher.getPayPalAccount())){
					return true;
				}
			}
		}
		if(this.getPayPalAccount() == null){
			if(teacher.getPayPalAccount()!= null){
				return true;
			}
		}
		
		return false;
		
	}

//	public Date getQuitTime() {
//		return quitTime;
//	}
//
//	public void setQuitTime(Date quitTime) {
//		this.quitTime = quitTime;
//	}

//	public Staff getQuitOperator() {
//		return quitOperator;
//	}
//
//	public void setQuitOperator(Staff quitOperator) {
//		this.quitOperator = quitOperator;
//	}

	public Staff getManager() {
		return manager;
	}

	public void setManager(Staff manager) {
		this.manager = manager;
	}
	
	
	// 
	/**
	 * 2015-08-15 判断是否quit操作 -- lifeCycle变成quit。
	 * @param oldTeacher -- 该teacher id对应的原数据
	 * @return
	 */
	public boolean changeLifeCycle2Quit(Teacher oldTeacher) {
		//
		LifeCycle lifeCycleQUIT = LifeCycle.QUIT;
		try {
			if (this.getLifeCycle().equals(lifeCycleQUIT) && !oldTeacher.getLifeCycle().equals(lifeCycleQUIT) ) {
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	
	public boolean changeLifeCycle(Teacher oldTeacher) {
		//
		try {
			if (null != this.getLifeCycle() && null != oldTeacher.getLifeCycle()) {
				if (this.getLifeCycle() != oldTeacher.getLifeCycle()) {
					return true;
				}
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	
	// 2015-08-15  为其设置quit managerName 对manager获取name
	@Transient
	private String managerName;	

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	
	
	// 2015-08-15  对quit operator 获取name
	@Transient
	private String operatorName;
	
	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	//2015-08-20 
	@Transient
	private String operator;
	
	@Transient
	private Long operationDateTime;
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Long getOperationDateTime() {
		return operationDateTime;
	}

	public void setOperationDateTime(Long operationDateTime) {
		this.operationDateTime = operationDateTime;
	}

	/**
	 * 2015-08-15  为其设置quit managerName
	 * 2015-08-15  为其设置quit managerName
	 */
	@PostLoad
	public void updateTeacherManagerName() {
		try {
			String strName = this.manager.getEnglishName();
			if (null !=strName) {
				this.setManagerName(strName);;
			}
		} catch(Exception e) {
			
		}
 	
		try {
			String strName = this.getOperatorName();
			if (null !=strName) {
				this.setOperatorName(strName);;
			}
		} catch(Exception e) {
			
		}
 	}

	public List<TeacherLifeCycleLog> getTeacherLifeCycleLogs() {
		return teacherLifeCycleLogs;
	}

	public void setTeacherLifeCycleLogs(
			List<TeacherLifeCycleLog> teacherLifeCycleLogs) {
		this.teacherLifeCycleLogs = teacherLifeCycleLogs;
	}
}
