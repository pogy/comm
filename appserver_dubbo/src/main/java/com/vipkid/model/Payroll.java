package com.vipkid.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.PayrollItemAdapter;
import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 工资单
 */
@Entity
@Table(name = "payroll", schema = DBInfo.SCHEMA)
public class Payroll extends Base {
	private static final long serialVersionUID = 1L;

	public enum Status {
		CONFIRMED, // 确认
		UNCONFIRMED // 未确认
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	// 工资单项
	@XmlJavaTypeAdapter(PayrollItemAdapter.class)
	@OneToMany(mappedBy = "payroll")
	private List<PayrollItem> payrollItems;

	// 老师
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "teacher_id", referencedColumnName = "id", nullable = false)
	private Teacher teacher;

	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.UNCONFIRMED;

	// 支付时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "paid_date_time")
	private Date paidDateTime;

	// 评估
	@Column(name = "evaluation")
	private int evaluation;

	// 培训补助
	@Column(name = "training_fee")
	private float trainingFee;

	// 是否有培训补助
	@Column(name = "has_training_fee")
	private boolean hasTrainingFee;

	// 推荐补助
	@Column(name = "referral_fee")
	private float referralFee;

	// 推荐人数
	@Column(name = "referral_number")
	private float referralNumber;

	// TESOL认证补助
	@Column(name = "tesol_reimbursement")
	private float tesolReimbursement;

	// 是否有TESOL认证补助
	@Column(name = "has_tesol_reimbursement")
	private boolean hasTESOLReimbursement;

	// 银行转账补助
	@Column(name = "transfering_fee")
	private float transferingFee;

	// 其它工资
	@Column(name = "other_salary")
	private float otherSalary;

	// 表现罚款
	@Column(name = "performance_penalty")
	private float performancePenalty;

	// 其它罚款
	@Column(name = "other_penalty")
	private float otherPenalty;

	// 工资总额
	@Column(name = "salary")
	private float salary;
	
	// 备注
    @Lob
    @Column(name="comment")
    private String comment;

	@Transient
	private long payrollItemCount;

	public List<PayrollItem> getPayrollItems() {
		return payrollItems;
	}

	public void setPayrollItems(List<PayrollItem> payrollItems) {
		this.payrollItems = payrollItems;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public int getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPayrollItemCount() {
		return payrollItemCount;
	}

	public void setPayrollItemCount(long payrollItemCount) {
		this.payrollItemCount = payrollItemCount;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getPaidDateTime() {
		if (null != paidDateTime) {
			return paidDateTime;
		}
		return new Date();
	}

	public void setPaidDateTime(Date paidDateTime) {
		this.paidDateTime = paidDateTime;
	}

	public float getTrainingFee() {
		return trainingFee;
	}

	public void setTrainingFee(float trainingFee) {
		this.trainingFee = trainingFee;
	}

	public boolean isHasTrainingFee() {
		return hasTrainingFee;
	}

	public void setHasTrainingFee(boolean hasTrainingFee) {
		this.hasTrainingFee = hasTrainingFee;
	}

	public float getReferralFee() {
		return referralFee;
	}

	public void setReferralFee(float referralFee) {
		this.referralFee = referralFee;
	}

	public float getReferralNumber() {
		return referralNumber;
	}

	public void setReferralNumber(float referralNumber) {
		this.referralNumber = referralNumber;
	}

	public float getTesolReimbursement() {
		return tesolReimbursement;
	}

	public void setTesolReimbursement(float tesolReimbursement) {
		this.tesolReimbursement = tesolReimbursement;
	}

	public boolean isHasTESOLReimbursement() {
		return hasTESOLReimbursement;
	}

	public void setHasTESOLReimbursement(boolean hasTESOLReimbursement) {
		this.hasTESOLReimbursement = hasTESOLReimbursement;
	}

	public float getTransferingFee() {
		return transferingFee;
	}

	public void setTransferingFee(float transferingFee) {
		this.transferingFee = transferingFee;
	}

	public float getOtherSalary() {
		return otherSalary;
	}

	public void setOtherSalary(float otherSalary) {
		this.otherSalary = otherSalary;
	}

	public float getPerformancePenalty() {
		return performancePenalty;
	}

	public void setPerformancePenalty(float performancePenalty) {
		this.performancePenalty = performancePenalty;
	}

	public float getOtherPenalty() {
		return otherPenalty;
	}

	public void setOtherPenalty(float otherPenalty) {
		this.otherPenalty = otherPenalty;
	}

}
