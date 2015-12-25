package com.vipkid.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 审计
 */
@Entity
@Table(name = "audit", schema = DBInfo.SCHEMA)
public class Audit extends Base {
	private static final long serialVersionUID = 1L;

	public enum Level {
		INFO, // 信息
		WARNING, // 警告
		ERROR // 错误
	}
	
	public enum Category {
		/** 学生管理 */
		STUDENT_CREATE, // 新建学生
		STUDENT_UPDATE, // 更新学生
		STUDENT_RESET_PASSWORD, // 重置学生密码
		STUDENT_LEARNING_PROGRESS_UPDATE, //更新学生学习进度
		
		/** 家长管理 */
		PARENT_CREATE, // 新建家长
		PARENT_UPDATE, // 更新家长
		PARENT_RESET_PASSWORD, //重置家长密码
		
		/** 教师管理 */
		TEACHER_CREATE, // 新建教师
		TEACHER_UPDATE, // 更新教师
		TEACHER_BANK_INFO,//更新银行信息
		TEACHER_RESET_PASSWORD, // 重置教师密码
		TEACHER_CHANGE_PASSWORD, // 修改教师密码
		TEACHER_LOCK, // 锁定教师
		TEACHER_UNLOCK, // 解锁教师
		TEACHER_ACCOUNT_READ, //读老师银行账号
		TEACHER_ACCOUNT_UPDATE, //改老师银行账号
		
		/**课程基本信息修改*/
		
		LESSON_BASIC_INFO_UPDATE,  //修改lesson的基本信息
		COURSE_BASIC_INFO_UPDATE,  //修改course的基本信息
		UNIT_BASIC_INFO_UPDATE,  //修改unit的基本信息
		LEARNINGCYCLE_BASIC_INFO_UPDATE,  //修改unit的基本信息
		
		/**导入Course*/
		IMPORT_COURSE_DATA,  //修改unit的基本信息
		
		
		/** 教师工资 */
		TEACHER_SALARY_EMAIL_SENT,
		
		/** 家庭管理 */
		FAMILY_CREATE, // 新建家庭
		FAMILY_UPDATE, // 更新家庭
		
		/** 员工管理 */
		STAFF_CREATE, // 新建员工
		STAFF_UPDATE, // 更新员工
		STAFF_RESET_PASSWORD, // 重置员工密码
		STAFF_CHANGE_PASSWORD, // 修改员工密码
		STAFF_LOCK, // 锁定员工
		STAFF_UNLOCK, // 解锁员工
		SALES_TEAM_CREATE, // 新建销售团队
		
		/** 教师评价管理 */
		EDUCATIONAL_COMMENT_CREATE, // 新建教学评价
		EDUCATIONAL_COMMENT_UPDATE, // 更新教学评价
		
		/** 教师评价管理 */
		TEACHER_COMMENT_CREATE, // 新建教师评价
		TEACHER_COMMENT_UPDATE, // 更新教师评价
		
		/** 面试报告管理 */
		DEMO_REPORT_CREATE, // 新建面试报告
		DEMO_REPORT_UPDATE, // 更新面试报告
		DEMO_REPORT_CONFIRMED, // 教学主管确认面试报告
		DEMO_REPORT_SUBMITED, //面试报告已提交 
		
		/** 商品管理 */
		PRODUCT_CREATE, // 新建商品
		PRODUCT_UPDATE, // 更新商品
		PRODUCT_OFF_SALE, // 商品下架
		PRODUCT_ON_SALE, // 商品上海
		
		/** 订单管理 */
		ORDER_CREATE, // 新建订单
		ORDER_UPDATE, // 更新订单
		ORDER_CONFIRM, //确认订单
		ORDER_CANCEL,	//取消订单
		
		/** 在线课程 */
		ONLINE_CLASS_CREATE, // 创建在线课程
		ONLINE_CLASS_BOOK, // 预约在线课程
		ONLINE_CLASS_CANCEL, // 取消在线课程
		ONLINE_CLASS_FINISH, // 完成在线课程
		ONLINE_CLASS_UPDATE, // 更新在线课程
		ONLINE_CLASS_UNDO_FINISH, // undo完成在线课程
		ONLINE_CLASS_CLASSROOM_CREATE, // 创建在线课程教室
		ONLINE_CLASS_CLASSROOM_UPDATE, // 更新在线课程教室
		ONLINE_CLASS_CHANGE_TEACHER, // 换老师
		ONLINE_CLASS_REMOVE, // 移除available slot
		
		/** 市场活动 **/
		MARKETING_ACTIVITY_CREATE,//创建市场活动
		MARKETING_ACTIVITY_UPDATE,//更新市场活动
		/** 跟进管理 */
		FOLLOW_UP_CREATE, // 创建跟进信息
		
		/** 代课老师 */
		BACKUP_TEACHER_ARRANGE,
		BACKUP_TEACHER_REMOVE,
		
		/** 周报月报管理 */
		ASSESSMENT_REPORT_UPLOAD, // 上传周报月报
		
		/** 文件 */
		FILE_UPLOAD,
		
		/** 系统错误 */
		SYSTEM_ERROR,
		
		/** PPT管理*/
		SLIDE_UPDATE,
		PPT_UPDATE,
		
		/** 学习进度管理 */
		LEARNING_PROGRESS_UPDATE,  // 手动修改学习进度
		
		/** 多贝云 */
		DBY_UPLOAD_PPT_SUCCESS,  // 成功上传PPT到多贝云
		DBY_UPLOAD_PPT_FAIL,  // 无法上传PPT到多贝云
		
		/** 审核教师申请 */
		AUDIT_APPLICATION,  // 审核教师招聘申请
		
		/** 短信管理 */
		SMS_PARENT_IT_CANCEL, // ITtest课程取消，给家长发短信
		SMS_PARENT_DEMO_CANCEL, // Demo课程取消，给家长发短信
		SMS_PARENT_TRIAL_CANCEL, // trial课程取消，给家长发短信
		SMS_PARENT_GUIDE_CANCEL, // Guide课程取消，给家长发短信
		SMS_PARENT_MAJOR_CANCEL, // Major课程取消，给家长发短信
		SMS_PARENT_ONE_TO_MANY_CANCEL, // one to many课程取消，给家长发短信
		SMS_PARENT_IT_BOOK, // ITtest课程预定成功，给家长发短信
		SMS_PARENT_DEMO_BOOK, // Demo课程预定成功，给家长发短信
		SMS_PARENT_TRIAL_BOOK, // Trail课程预定成功，给家长发短信
		SMS_PARENT_GUIDE_BOOK, // Guide课程预定成功，给家长发短信
		SMS_PARENT_MAJOR_BOOK, // Major课程预定成功，给家长发短信
		
		SMS_MONDAY_PARENT,  // 每周一10:00	提醒所有家长
		SMS_SUNDAY_PARENT, // 每周日19:00	家长   下周课程
		SMS_PRE_TWO_HOUR_PARENT, // 上课当日提醒	课前2小时	家长
		SMS_PRE_THREE_HOUR_PARENT, // 试听上课当日提醒	课前2小时	家长
		SMS_LATE_PARENT, // 学生迟到
		SMS_PARENT_LEARNING_PROGRESS_FINISH, // 课时耗尽
		
		/** 邮件管理 */
		EMAIL_IT_IT_CANCEL, // ITtest课程取消，给IT发邮件
		EMAIL_TEACHER_DEMO_CANCEL, // Demo课程取消，给IT发邮件
		EMAIL_TEACHER_TRIAL_CANCEL, // Trial课程取消，给IT发邮件
		EMAIL_EDUCATION_GUIDE_CANCEL, // Guide课程取消，给IT发邮件
		EMAIL_TEACHER_MAJOR_CANCEL, // Normal课程取消，给老师发邮件
		EMAIL_IT_IT_BOOKED, // ITtest课程预定成功，给IT发邮件
		EMAIL_TEACHER_DEMO_BOOK, // Demo课程预定成功，给老师发邮件
		EMAIL_TEACHER_TRIAL_BOOK, // Trial课程预定成功，给老师发邮件
		EMAIL_EDUCATION_GUIDE_BOOK,  // Guide课程预定成功，给老师发邮件
		EMAIL_TEACHER_MAJOR_BOOK, // Major课程预定成功，给老师发邮件
		EMAIL_EDUCATION_TEACHER_NOSHOW, // teacher no show
		EMAIL_EDUCATION_STUDENT_NOSHOW, // student no show
		EMAIL_EDUCATION_DEMO_FINISH, // demo完成
		EMAIL_EDUCATION_TRIAL_FINISH, // trial完成
		EMAIL_EDUCATION_IT_TEST_FINISH, // it test完成
		EMAIL_SALE_THREE_CLASS_HOUR_LEFT, // 课时即将耗尽发送邮件给Sales
		EMAIL_CLT_THREE_CLASS_HOUR_LEFT, // 课时即将耗尽发送邮件给CLT
		EMAIL_SALE_LEARNING_PROGRESS_FINISH, // 课时耗尽
		
		EMAIL_SUNDAY_TEACHER, // 
		EMAIL_SATURDAY_TEACHER, // 有课+没课的老师下周提醒
		EMAIL_SUNDAY_IT, // IT
		EMAIL_EVERYDAY_TEACHER, // 每天上课前3小时发送今日与明日课程
		EMAIL_EVERYDAY_IT, // 每天00:00提醒今日与明日IT
		EMAIL_PRE_ONE_HOUR_TEACHER, // 上课当日提醒	课前1小时	老师
		EMAIL_PRE_MONTH_TEACHER, // 如果老师没设置足够的空闲时间
		EMAIL_SATURDAY_TEACHER_INTERVIEW_STUDENTS, // 每周六interview 结果统计
		
		EMAIL_EDUCATION_TEACHER_CONTRACT_WILL_EXPIRE, // 教师合同即将到期
		EMAIL_TEACHERCRUIT_DEMO_PRE_1DAYS, // 教师招聘24小时提前demo-class reminder 
		EMAIL_TEACHERCRUIT_DEMO_PRE_2DAYS, // 教师招聘48小时提前demo-class reminder 
		EMAIL_TEACHERCRUIT_DEMO_THX_AFTER_30MIN, // 教师招聘demo-class 30 minutes
		EMAIL_TEACHERCRUIT_CONTRACT_REMINDER, // 教师招聘 ENTER PHASE 3 - 116 HOURS AFTER.
		EMAIL_TEACHERCRUIT_CONTRACT_SIGN_END, // 教师招聘 ENTER PHASE 3 截止未处理.
		
		/** 邮件发送警告 */
		EMAIL_SEND_FROM_OHER_SERVER, // 从默认server以外发送了邮件

		/** setting 管理 */
		SETTINGS_UPDATE, // 更新设置参数
		SETTINGS_CREATE, // 新建设置参数
		
		/** channel level 管理 */
		CHANNEL_LEVEL_CREATE, // 新建渠道评级
		CHANNEL_INFO_UPDATE, // 新建渠道评级
		
		IMPORT_EXCEL_DATA,//市场导入excel数据
		BOOK_ONLINECLASS_AFTER_CANCEL_ONLINECLASS,//先取消某一时间某个老师的课，再约该时间另一个老师的课
		UPDATE_STARS,//批量更新学生星星数量
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	// 操作员
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "operator", nullable = false)
	private String operator;

	// 级别
	@Enumerated(EnumType.STRING)
	@Column(name = "level")
	private Level level;
	
	// 类别
	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private Category category;

	// 执行时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execute_date_time", nullable = false)
	private Date executeDateTime;

	// 操作
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "operation", nullable = false)
	private String operation;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Date getExecuteDateTime() {
		return executeDateTime;
	}

	public void setExecuteDateTime(Date executeDateTime) {
		this.executeDateTime = executeDateTime;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
