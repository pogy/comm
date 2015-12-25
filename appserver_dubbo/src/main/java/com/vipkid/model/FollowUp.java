package com.vipkid.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.StaffAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 任务
 */
@Entity
@Table(name = "follow_up", schema = DBInfo.SCHEMA)
public class FollowUp extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Status {
		CREATED, // 已创建
		FINISHED, // 已完成
		CANCELED  // 已取消
	}
	
	public enum Category {
		SALES, // 销售
		EDUCATION, // 教学
		OPERATION, // 运营
		COMMENT, // 评论
		IT_SUPPORT, // 技术支持
	} 
	
	public enum Type {
		// sale
		ASSIGNED_INTERESTED_CUSTOMERPROBLEM_CONTACTLATER, 
        ASSIGNED_INTERESTED_DEVICERPROBLEM_CONTACTLATER,
        ASSIGNED_INTERESTED_AGREE_TRIAL,
        ASSIGNED_INTERESTED_DECIDED_TOPAY,
        ASSIGNED_INTERESTED_PAY_CONFIRMED,
        ASSIGNED_INTERESTED_OTHER,
        ASSIGNED_NOTINTERESTED_PRICETOOHIGH,
        ASSIGNED_NOTINTERESTED_NOTASEXPECTED,
        ASSIGNED_NOTINTERESTED_NOTINTERESTED,
        ASSIGNED_NOTINTERESTED_WRONGAGE,
        ASSIGNED_NOTINTERESTED_TESTDATA,
        ASSIGNED_FAIL_NOANSWERCALL,
        ASSIGNED_FAIL_HANGUP,
        ASSIGNED_FAIL_PHONESHUTDOWN,
        ASSIGNED_FAIL_EMPTYNUMBER,

        CONTACTED_INTERESTED_CUSTOMERPROBLEM_CONTACTLATER, 
        CONTACTED_INTERESTED_DEVICERPROBLEM_CONTACTLATER,
        CONTACTED_INTERESTED_AGREE_TRIAL,
        CONTACTED_INTERESTED_DECIDED_TOPAY,
        CONTACTED_INTERESTED_PAY_CONFIRMED,
        CONTACTED_INTERESTED_OTHER,
        CONTACTED_NOTINTERESTED_PRICETOOHIGH,
        CONTACTED_NOTINTERESTED_NOTASEXPECTED,
        CONTACTED_NOTINTERESTED_NOTINTERESTED,
        CONTACTED_NOTINTERESTED_WRONGAGE,
        CONTACTED_NOTINTERESTED_TESTDATA,
        CONTACTED_FAIL_NOANSWERCALL,
        CONTACTED_FAIL_HANGUP, 
        CONTACTED_FAIL_PHONESHUTDOWN,
        CONTACTED_FAIL_EMPTYNUMBER,

        TRIAL_SCHEDULED_INTERESTED_CUSTOMERPROBLEM_RESCHEDULE, 
        TRIAL_SCHEDULED_INTERESTED_DEVICEPROBLEM_RESCHEDULE,
        TRIAL_SCHEDULED_INTERESTED_CUSTOMERPROBLEM_POSTPONED,
        TRIAL_SCHEDULED_INTERESTED_DEVICEPROBLEM_POSTPONED,
        TRIAL_SCHEDULED_INTERESTED_AGREE_TO_TRIAL,
        TRIAL_SCHEDULED_INTERESTED_DECIDED_TOPAY,
        TRIAL_SCHEDULED_INTERESTED_PAY_CONFIRMED,
        TRIAL_SCHEDULED_INTERESTED_OTHER,
        TRIAL_SCHEDULED_NOTINTERESTED_PRICETOOHIGH,
        TRIAL_SCHEDULED_NOTINTERESTED_NOTASEXPECTED,
        TRIAL_SCHEDULED_NOTINTERESTED_NOTINTERESTED,
        TRIAL_SCHEDULED_FAIL_NOANSWERCALL,
        TRIAL_SCHEDULED_FAIL_HANGUP,
        TRIAL_SCHEDULED_FAIL_PHONESHUTDOWN,
        TRIAL_SCHEDULED_FAIL_EMPTYNUMBER,

        TRIAL_FINISHED_INTERESTED_NORMAL, 
        TRIAL_FINISHED_INTERESTED_HIGH, 
        TRIAL_FINISHED_INTERESTED_DECIDED_TOPAY,
        TRIAL_FINISHED_INTERESTED_PAY_CONFIRMED,
        TRIAL_FINISHED_INTERESTED_OTHER,
        TRIAL_FINISHED_NOTINTERESTED_PRICETOOHIGH,
        TRIAL_FINISHED_NOTINTERESTED_SLOWINTERNET,
        TRIAL_FINISHED_NOTINTERESTED_NOTASEXPECTED,
        TRIAL_FINISHED_NOTINTERESTED_KIDDISLIKE,
        TRIAL_FINISHED_NOTINTERESTED_GOTOOTHER,
        TRIAL_FINISHED_NOTINTERESTED_NOPLAN,
        TRIAL_FINISHED_FAIL_NOANSWERCALL,
        TRIAL_FINISHED_FAIL_HANGUP,
        TRIAL_FINISHED_FAIL_PHONESHUTDOWN,
        TRIAL_FINISHED_FAIL_EMPTYNUMBER,
		// lagacy
		EFFECTIVE_CONTACT_INTERVIEW_SCHEDULED,
		EFFECTIVE_CONTACT_READY_TO_PAY,
		EFFECTIVE_CONTACT_CONTINUE_TO_FOLLOWUP,
		EFFECTIVE_CONTACT_PAY_CONFIRMED,
		
		ANSWERED_BUT_OK_TO_FOLLOWUP_SIGNED_WITH_OTHER_SCHOOLS,
		
		NOT_ANSWERED_NO_ANSWERING,
		NOT_ANSWERED_REJECT_ANSWER_THE_PHONE,
		NOT_ANSWERED_PHONE_OFF,
		NOT_ANSWERED_OUT_OF_SERVICE,
		NOT_ANSWERED_LINE_BUSY,
		
		UNEFFECTIVE_CONTACT_WRONG_PERSON,
		UNEFFECTIVE_CONTACT_NO_INTEREST,
		UNEFFECTIVE_CONTACT_KID_WRONG_AGE,
		
		// education
		FIRST_MONTH_SERVICE_SOLVED,
		FIRST_MONTH_SERVICE_UNSOLVED,
		
		MONTHLY_SERVICE_SOLVED,
		MONTHLY_SERVICE_UNSOLVED,
		
		RENEWAL_SOLVED,
		RENEWAL_UNSOLVED,
		
		ISSUE_TRACKING_SOLVED,
		ISSUE_TRACKING_UNSOLVED,
		
		// operation
		IT_SERVICE_SOLVED,
		IT_SERVICE_UNSOLVED,
		
		MATERIAL_DELEVARY_SOLVED,
		MATERIAL_DELEVARY_UNSOLVED,
		
		OTHERS_SOLVED,
		OTHERS_UNSOLVED,
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;
	
	// 类型
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;
	
	// 分类
	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private Category category;
	
	// 内容
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "content", nullable = false)
	private String content;
	
	// 创建人
	@XmlJavaTypeAdapter(StaffAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "creater_id", referencedColumnName = "id", nullable = false)
	private Staff creater;
	
	// 相关人
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "stakeholder_id", referencedColumnName = "id", nullable = false)
	private Student stakeholder;
	
	// 指派人
	@XmlJavaTypeAdapter(StaffAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "assignee_id", referencedColumnName = "id")
	private Staff assignee;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;

	// 完成时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "finished_date_time")
	private Date finishedDateTime;

	// 取消时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "canceled_date_time")
	private Date canceledDateTime;
	
	// 预期完成时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "target_date_time")
	private Date targetDateTime;
	
	// 是否为最新记录
	@Column(name = "current")
	private boolean current;
	
	@PrePersist
	public void prePersist() {
		this.status = Status.CREATED;
		this.createDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Staff getCreater() {
		return creater;
	}

	public void setCreater(Staff creater) {
		this.creater = creater;
	}

	public Student getStakeholder() {
		return stakeholder;
	}

	public void setStakeholder(Student stakeholder) {
		this.stakeholder = stakeholder;
	}

	public Staff getAssignee() {
		return assignee;
	}

	public void setAssignee(Staff assignee) {
		this.assignee = assignee;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getFinishedDateTime() {
		return finishedDateTime;
	}

	public void setFinishedDateTime(Date finishedDateTime) {
		this.finishedDateTime = finishedDateTime;
	}

	public Date getCanceledDateTime() {
		return canceledDateTime;
	}

	public void setCanceledDateTime(Date canceledDateTime) {
		this.canceledDateTime = canceledDateTime;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Date getTargetDateTime() {
		return targetDateTime;
	}

	public void setTargetDateTime(Date targetDateTime) {
		this.targetDateTime = targetDateTime;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
	
}
