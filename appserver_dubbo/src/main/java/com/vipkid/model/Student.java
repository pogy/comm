package com.vipkid.model;

import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.ChannelAdapter;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.FamilyAdapter;
import com.vipkid.model.json.moxy.FollowUpAdapter;
import com.vipkid.model.json.moxy.InventionCodeAdapter;
import com.vipkid.model.json.moxy.LearningProgressAdapter;
import com.vipkid.model.json.moxy.MarketingActivityAdapter;
import com.vipkid.model.json.moxy.OnlineClassesAdapter;
import com.vipkid.model.json.moxy.OrderAdapter;
import com.vipkid.model.json.moxy.StaffAdapter;
import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.util.TextUtils;

/**
 * 学生
 * 用户名为学生学号，规则为：8位，学生总数+6978
 */
@Entity
@Table(name = "student", schema = DBInfo.SCHEMA)
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
public class Student extends User {
	private static final long serialVersionUID = 1L;

	public enum LifeCycle {
		DEFAULT, // 占位
		SIGNUP,  // 已注册：当学生注册成功后
		ASSIGNED,  // 已分配：当销售管理将学生分配给相应销售后
			CONTACT,    // 正在联系学生阶段, 以废弃
		CONTACTED,    // 该生的第一个contact生成。且，没有付费订单生成时
			IT_TEST, // 正在进行IT测试阶段，废弃
			DEMO, // 正在试听阶段， 废弃
		TRIAL_SCHEDULED, // 该生的第一个trial被book。且，没有付费订单生成时
		TRIAL_FINISHED, // 该生的某个trial被finish as scheduled。且，没有付费订单生成时
			DEMO_REPORT_SEND, // 已发送试听报告，废弃
			NOT_INTRESTING, // 不感兴趣：学生没有报名意向，转移到跟进状态
			NOT_SUITABLE, // 不适合：年龄太小或基础太弱，转移到跟进状态
			FRESH_FOLLOW_UP, // 新生跟单中，废弃
		LEARNING,  // 在读
			RENEW_FOLLOW_UP, // 续费跟单中，废弃
		TO_BE_RENEWED, // Learning 状态的学生消耗完所有付费课时，且未达到最高级最后一课。则定义为To be renewed
		GRADUATED,  // 已毕业：上完所有课程
			WRONG_INFO, // 信息错误，转移到跟进状态
		WONT_RENEW, // To be renewed状态持续90天后，自动改为Won't Renew
		REFUND, // 退款，需要手动操作
	}
	
	public enum StudentType {
		VIP,
		NORMAL
	}
	
	public enum Grade {
		K_XIAOBAN,  // 幼儿园小班
		K_ZHONGBAN, // 幼儿园中班
		K_DABAN, // 幼儿园大班
		PRIMARY_GRADE_1,	// 小学一年级
		PRIMARY_GRADE_2,	// 小学二年级
		PRIMARY_GRADE_3, 	// 小学三年级
		PRIMARY_GRADE_4, 	// 小学四年级
		PRIMARY_GRADE_5, 	// 小学五年级
		PRIMARY_GRADE_6 	// 小学六年级
	}
	
	public enum Source {
		WEBSITE, // 网站
		PARENT_PORTAL_WEIXIN,
		PARENT_PORTAL_MOBILE_BROWSER,//在家长端使用微信以外的浏览器注册
		OTHERS, // 其他
		WEIXIN,
		WEB_AIBAIMAMA,
		WEB_JIAZHANGBANG,
		WEB_XIAOXINMAMA,
		M_IBM,
		M_BAIDU,
		M_DELL,
		M_CHUANGXIN,
		M_YOUNG_MBA,
		M_RUIMA,
		M_VIPKID,
		M_VIPKID2,
		M_VIPKID3,
		M_VIPKID4,
		M_VIPKID5,
		M_HALLOWEEN,
		M_CHANGJIANG,
		M_JIAZHANGBANG,
		RECOMMENDER,
		MOBILE_BROWSER,
		WEIBO,
		M_ARTICLE,
		M_HEZI,
		M_JIAZHANGTUIJIAN,
		M_XIAOXINMAMA,
		M_BEVA,
		M_BBYYS,
		M_TNZZ_ENGLAND,
		M_TNZZ_SOUTHAFRICA,
		M_TNZZ_VIDEO,
		M_XIMENGZI,
		M_CHENSICHENG,
		M_ANQIER,
		M_MAHAOXUAN,
		M_LIHAOYU,
		M_WEIBO2015,
		M_XIAOXINMAMA2,
		M_WANGZIXUAN,
		ditui1,
		ditui2,
		hezi2,
		hezi3,
		hezi4,
		hezi5,
		miniang,
		haosaishi1,
		haosaishi2,
		peisheng2,
		bbyys3,
		bbyys2,
		xingkong1,
		xingkong2,
		readers,
		HSSMYLZ,
		HSSYPDD,
		hezihb1,
		hezihb2,
		HOTLINE,
		research,
		HSS400,
		LUNTAN,
		kf400,
		unknown,
		dabaigongkaike,
		gongkaike1,
		honglingjinwechat,	//红领巾亲子俱乐部微信
		saishiwangwechat,	//青少年赛事网微信
		saishiwangweb,	//中国青少年赛事网网站
		dingyichen,
		vipparents,
		haosaishi1a,
		m_ruima,
		sm_weibo_wenyi,
		sm_wechat_service_wenyi,
		sm_wechat_subscriptions_sighup,
		channel_franchiser_tnzz_water
	}
	
	// 记录学生参加过的活动。 不同的活动可能对应不同的课程奖励
	public enum MarketActivities {
		M_XIAOXINMAMA,
		M_BEVA,
		M_BBYYS,
		M_TNZZ_ENGLAND,
		M_TNZZ_SOUTHAFRICA,
		M_TNZZ_VIDEO,
		M_XIMENGZI,
		M_CHENSICHENG,
		M_ANQIER,
		M_MAHAOXUAN,
		M_LIHAOYU,
		M_WEIBO2015,
		M_XIAOXINMAMA2,
		M_WANGZIXUAN,
		ditui1,
		ditui2,
		hezi2,
		hezi3,
		hezi4,
		hezi5,
		miniang,
		haosaishi1,
		haosaishi2,
		peisheng2,
		bbyys3,
		bbyys2,
		xingkong1,
		xingkong2,
		readers,
		HSSMYLZ,
		HSSYPDD,
		hezihb1,
		hezihb2,
		research,
		HSS400,
		LUNTAN,
		kf400,
		unknown,
		dabaigongkaike,
		gongkaike1,
		honglingjinwechat,	//红领巾亲子俱乐部微信
		saishiwangwechat,	//青少年赛事网微信
		saishiwangweb,	//中国青少年赛事网网站
		dingyichen,
		vipparents,
		haosaishi1a,
		m_ruima,
		sm_weibo_wenyi,
		sm_wechat_service_wenyi,
		sm_wechat_subscriptions_signup,
		channel_franchiser_tnzz_water
	}
	
	@Transient
	private transient Set<Role> roleSet = new HashSet<Role>();

	// 家庭
	@XmlJavaTypeAdapter(FamilyAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "family_id", referencedColumnName = "id")
	private Family family;
	
	// 学习进度
	@XmlJavaTypeAdapter(LearningProgressAdapter.class)
	@OneToMany(mappedBy="student")
	private List<LearningProgress> learningProgresses = new LinkedList<LearningProgress>();
	
	// 订单
	@XmlJavaTypeAdapter(OrderAdapter.class)
	@OneToMany(mappedBy="student")
	private List<Order> orders;
	
	// qq
	@Column(name = "qq")
	private String qq;
	
	// 头像图片链接
	@Column(name = "avatar")
	private String avatar;
	
	// 标记是否第一次登陆
	@Column(name = "welcome",  nullable = false)
	private boolean welcome;

	// 英文姓名
	@Column(name = "english_name", nullable = false)
	private String englishName;
	
	// 生日
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.DATE)
	@Column(name = "birthday")
	private Date birthday;
	
	// 外教班主任
	@XmlJavaTypeAdapter(StaffAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "foreign_lead_teacher_id", referencedColumnName = "id")
	private Staff foreignLeadTeacher;
	
	// 中教班主任   
	@XmlJavaTypeAdapter(StaffAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "chinese_lead_teacher_id", referencedColumnName = "id")
	private Staff chineseLeadTeacher;
	
	// 销售
	@XmlJavaTypeAdapter(StaffAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "sales_id", referencedColumnName = "id")
	private Staff sales;
	
	// 学生来源
	@Enumerated(EnumType.STRING)
	@Column(name = "source")
	private Source source;
	
	// 学生类型
	@Enumerated(EnumType.STRING)
	@Column(name = "student_type")
	private StudentType studentType = StudentType.NORMAL;

	// 市场活动
	@XmlJavaTypeAdapter(MarketingActivityAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "marketing_activity_id", referencedColumnName = "id")
	private MarketingActivity marketingActivity;
	
	// 阶段
	@Enumerated(EnumType.STRING)
	@Column(name = "life_cycle")
	private LifeCycle lifeCycle;
	
    // 学校
    @Column(name = "school")
    private String school;
    
	// 年级
	@Enumerated(EnumType.STRING)
	@Column(name = "grade")
	private Grade grade;

    // 了解学生
	@Lob
    @Column(name = "know_the_student")
    private String knowTheStudent; 
    
    // 个性
    @Lob
    @Column(name = "personality")
    private String personality;
    
    // 是否有英语基础
    @Column(name = "learned_english")
    private boolean learnedEnglish;
    
    // 上过的其他英语培训学习
    @Column(name = "training_schools")
    private String trainingSchools;
    
    // 备注
    @Lob
    @Column(name = "notes")
    private String notes;
    
    // 星星币个数
    @Column(name="stars")
    private int stars;
    
    //每周目标上多少节课
    @Column(name="target_classes_per_week")
    private int targetClassesPerWeek = 5; // 5 by default
    
    //允许的水平测试次数 2015-08-29
    @Column(name="max_time_level_exam")
    private int maxTimesLevelExam = 1; // 2 by default
    
    // 销售意向
    @Column(name="customer_stage")
    private int customerStage;
    
    // 该学生喜欢的老师列表
    @XmlJavaTypeAdapter(TeacherAdapter.class)
 	@ManyToMany(mappedBy="favoredByStudents")
 	private List<Teacher> favorTeachers = new ArrayList<Teacher>();
    
    // 参加过的活动
 	@Column(name = "attended_activities")
 	protected String attendedActivities;
 	
 	// 在线课程列表
 	@XmlJavaTypeAdapter(OnlineClassesAdapter.class)
 	@ManyToMany(mappedBy = "students")
 	private List<OnlineClass> onlineClasses;
 	
	@XmlJavaTypeAdapter(InventionCodeAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "invention_code_id", referencedColumnName = "id")
	private InventionCode inventionCode;
	
 	// follow up 列表
  	@XmlJavaTypeAdapter(FollowUpAdapter.class)
  	@OneToMany(mappedBy = "stakeholder")
  	private List<FollowUp> followUps;
  	
  	// 分配给销售的时间
 	@XmlJavaTypeAdapter(DateTimeAdapter.class)
 	@Temporal(TemporalType.TIMESTAMP)
 	@Column(name = "assigned_to_sales_date_time")
 	private Date assignedToSalesDateTime;
 	
    //上次合同结束时间
 	@XmlJavaTypeAdapter(DateTimeAdapter.class)
 	@Temporal(TemporalType.TIMESTAMP)
 	@Column(name = "pre_contract_end_time")
 	private Date preContractEndTime;
 	
 	/**
 	 * 2015-07-01 为学生添加current performance难易评价
 	 */
 	@Enumerated(EnumType.STRING)
 	@Column(name="current_performance", insertable=false)
 	private StudentPerformance currentPerformance;
 	
	// channel
	@XmlJavaTypeAdapter(ChannelAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "channel_id", referencedColumnName = "id")
	private Channel channel;
	
	@Column(name="channel_keyword")
	private String channelKeyword="";
 	
	/**
	 * 是否引导水平测试 2015-09-14
	 */
	@Column(name="levelexam_guide", insertable=false)
	private int guideToLevelExam = 0;

	public int getGuideToLevelExam() {
		return guideToLevelExam;
	}

	public void setGuideToLevelExam(int guideToLevelExam) {
		this.guideToLevelExam = guideToLevelExam;
	}

	// 参加过的活动
 	@Transient
 	private Set<MarketActivities> attendedActivitySet = Collections.synchronizedSet(new HashSet<MarketActivities>());
 	
 	//指定给CLT的时间
 	@Column(name="assign_clt_date_time")
 	private Date assignCltTime;
 	
 	// 根据 parent relation 排序 - mother father grandfather grandmother other
 	public void arrangeParents() {
 		if(this.family != null) {
 			List<Parent> parents = new ArrayList<Parent>();
 			List<Parent> mothers = new ArrayList<Parent>();
 			List<Parent> fathers = new ArrayList<Parent>();
 			List<Parent> grandfathers = new ArrayList<Parent>();
 			List<Parent> grandmothers = new ArrayList<Parent>();
 			List<Parent> others = new ArrayList<Parent>();
 			for(Parent parent : this.family.getParents()) {
 				if(parent.getRelation() != null) {
 					switch(parent.getRelation()) {
 	 				case MOTHER :
 	 					mothers.add(parent);
 	 					break;
 	 				case FATHER :
 	 					fathers.add(parent);
 	 					break;
 	 				case GRANDFATHER :
 	 					grandfathers.add(parent);
 	 					break;
 	 				case GRANDMOTHER :
 	 					grandmothers.add(parent);
 	 					break;
 	 				case OTHER_FAMILIES :
 	 					others.add(parent);
 	 					break;
 	 				}
 				}				
 			}
 			parents.addAll(mothers);
 			parents.addAll(fathers);
 			parents.addAll(grandfathers);
 			parents.addAll(grandmothers);
 			parents.addAll(others);
 			this.getFamily().setParents(parents);
 		}
 	}
 	
	public StudentType getStudentType() {
		return studentType;
	}

	public void setStudentType(StudentType studentType) {
		this.studentType = studentType;
	}

 	public void setAttendedActivities(String attendedActivities) {
		this.attendedActivities = attendedActivities;
	}
 	
	public String getAttendedActivities() {
		if(attendedActivities == null){
			return "";
		}
		return attendedActivities;
	}

	public Set<MarketActivities> getAttendedActivitySet() {
		if(attendedActivities != null && attendedActivities.length() > 0){
			String[] strings = attendedActivities.split(TextUtils.SPACE);
			for(String string : strings) {
				for(MarketActivities marketActivities: MarketActivities.values()){
					if(string.equals(marketActivities.toString())){
						addAttendedActivity(marketActivities);
					}
				}
			}
		}
		return attendedActivitySet;
	}
	
	public void addAttendedActivity(MarketActivities marketActivities) {
		if(attendedActivitySet == null){
			attendedActivitySet = new HashSet<MarketActivities>();
		}
		attendedActivitySet.add(marketActivities);
		
		StringBuilder result = new StringBuilder();
		for(MarketActivities activitie : attendedActivitySet) {
			result.append(activitie.name()).append(TextUtils.SPACE);
		}
		
		this.attendedActivities = result.toString().trim();
	}
	
	public void removeAttendedActivity(MarketActivities marketActivity) {
		attendedActivitySet.remove(marketActivity);
		
		StringBuilder sbRoles = new StringBuilder();
		for(MarketActivities activity : attendedActivitySet) {
			sbRoles.append(activity.name()).append(TextUtils.SPACE);
		}
		
		this.attendedActivities = sbRoles.toString().trim();
	}

	@PrePersist
	public void prePersist() {
		super.prePersist();
		this.roles = Role.STUDENT.name();
		if(this.lifeCycle == null) {
			this.lifeCycle = LifeCycle.SIGNUP;
		}		
		this.welcome = true;
	}
	
	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
	}
	
	public Student() {
		roleSet.add(Role.STUDENT);
	}
    
	@Override
	public Set<Role> getRoleSet() {
		return roleSet;
	}
	
	public List<Teacher> getFavorTeachers() {
		return favorTeachers;
	}

	public void setFavorTeachers(List<Teacher> favorTeachers) {
		for (Teacher teacher : favorTeachers) {
			this.addFavoredTeacher(teacher);
		}
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
		
		// 可能会引起JPA对象池死锁
//		List<Student> students = this.family.getStudents();
//		if (students == null) {
//			students = new ArrayList<Student>();
//		}
//		
//		if(!students.contains(this)) {
//			students.add(this);
//			this.family.setStudents(students);
//		}
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public Staff getForeignLeadTeacher() {
		return foreignLeadTeacher;
	}

	public void setForeignLeadTeacher(Staff foreignLeadTeacher) {
		this.foreignLeadTeacher = foreignLeadTeacher;
	}

	public Staff getChineseLeadTeacher() {
		return chineseLeadTeacher;
	}

	public void setChineseLeadTeacher(Staff chineseLeadTeacher) {
		this.chineseLeadTeacher = chineseLeadTeacher;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public String getKnowTheStudent() {
		return knowTheStudent;
	}

	public void setKnowTheStudent(String knowTheStudent) {
		this.knowTheStudent = knowTheStudent;
	}

	public String getPersonality() {
		return personality;
	}

	public void setPersonality(String personality) {
		this.personality = personality;
	}

	public boolean isLearnedEnglish() {
		return learnedEnglish;
	}

	public void setLearnedEnglish(boolean learnedEnglish) {
		this.learnedEnglish = learnedEnglish;
	}

	public String getTrainingSchools() {
		return trainingSchools;
	}

	public void setTrainingSchools(String trainingSchools) {
		this.trainingSchools = trainingSchools;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setRoleSet(Set<Role> roleSet) {
		this.roleSet = roleSet;
	}

	public List<LearningProgress> getLearningProgresses() {
		return learningProgresses;
	}

	public void setLearningProgresses(List<LearningProgress> learningProgresses) {
		this.learningProgresses = learningProgresses;
	}

	public boolean isWelcome() {
		return welcome;
	}

	public void setWelcome(boolean welcome) {
		this.welcome = welcome;
	}
	
	
	public void addFavoredTeacher(Teacher teacher) {
		boolean find = false;
		for (Teacher t : this.getFavorTeachers()) {
			if (t.getId() == teacher.getId()) {
				find = true;
			}
		}
		
		if (!find) {
			this.getFavorTeachers().add(teacher);
		}
		 
		if (!teacher.getFavoredByStudents().contains(this)) {
			teacher.addFavoredByStudent(this);
		}
	}

	public void removeFavorTeacher(Teacher teacher) {
		if (this.getFavorTeachers().contains(teacher)) {
			this.getFavorTeachers().remove(teacher);
		}
		
		if (teacher.getFavoredByStudents().contains(this)) {
			teacher.getFavoredByStudents().remove(this);
		}
	}
	
	public void removeFavorTeachers() {
		this.favorTeachers = null;
	}

	public int getCustomerStage() {
		return customerStage;
	}

	public void setCustomerStage(int customerStage) {
		this.customerStage = customerStage;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Staff getSales() {
		return sales;
	}

	public void setSales(Staff sales) {
		this.sales = sales;
	}

	public List<OnlineClass> getOnlineClasses() {
		return onlineClasses;
	}

	public void setOnlineClasses(List<OnlineClass> onlineClasses) {
		this.onlineClasses = onlineClasses;
	}
	
	public String getSafeName() {
		if(TextUtils.isEmpty(name)) {
			return englishName;
		}else {
			return name;
		}
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public List<FollowUp> getFollowUps() {
		return followUps;
	}

	public void setFollowUps(List<FollowUp> followUps) {
		this.followUps = followUps;
	}

	public Date getAssignedToSalesDateTime() {
		return assignedToSalesDateTime;
	}

	public void setAssignedToSalesDateTime(Date assignedToSalesDateTime) {
		this.assignedToSalesDateTime = assignedToSalesDateTime;
	}

	public Date getPreContractEndTime() {
		return preContractEndTime;
	}

	public void setPreContractEndTime(Date preContractEndTime) {
		this.preContractEndTime = preContractEndTime;
	}
	
		public MarketingActivity getMarketingActivity() {
		return marketingActivity;
	}

	public void setMarketingActivity(MarketingActivity marketingActivity) {
		this.marketingActivity = marketingActivity;
	}

	public InventionCode getInventionCode() {
		return inventionCode;
	}

	public void setInventionCode(InventionCode inventionCode) {
		this.inventionCode = inventionCode;
	}

	public int getTargetClassesPerWeek() {
		return targetClassesPerWeek;
	}

	public void setTargetClassesPerWeek(int targetClassesPerWeek) {
		this.targetClassesPerWeek = targetClassesPerWeek;
	}
	
	public StudentPerformance getCurrentPerformance() {
		return currentPerformance;
	}

	public void setCurrentPerformance(StudentPerformance currentPerformance) {
		this.currentPerformance = currentPerformance;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public String getChannelKeyword() {
		return channelKeyword;
	}

	public void setChannelKeyword(String channelKeyword) {
		this.channelKeyword = channelKeyword;
	}
	
	public int getMaxTimesLevelExam() {
		return maxTimesLevelExam;
	}

	public void setMaxTimesLevelExam(int maxTimesLevelExam) {
		this.maxTimesLevelExam = maxTimesLevelExam;
	}
	
	public Date getAssignCltTime() {
		return assignCltTime;
	}

	public void setAssignCltTime(Date assignCltTime) {
		this.assignCltTime = assignCltTime;
	}

}
