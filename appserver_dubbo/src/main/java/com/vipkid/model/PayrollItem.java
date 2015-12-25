package com.vipkid.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.OnlineClassAdapter;
import com.vipkid.model.json.moxy.PayrollAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 工资单项
 */
@Entity
@Table(name = "payroll_item", schema = DBInfo.SCHEMA)
public class PayrollItem extends Base {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	// 工资单
	@XmlJavaTypeAdapter(PayrollAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "payroll_id", referencedColumnName = "id", nullable = false)
	private Payroll payroll;

	// 课时费基准
	@Column(name = "base_salary")
	private float baseSalary;

	// 课时费
	@Column(name = "salary")
	private float salary;
	
	// 支付比例
	@Column(name = "salary_percentage")
	private float salaryPercentage;
	
	// 替补课时费
	@Column(name = "backup_duty_salary")
	private float backupDutySalary;
	
	// 备注
    @Lob
    @Column(name="comments")
    private String comments;

	// 在线课程
    @XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id", nullable = false)
	private OnlineClass onlineClass;

	public OnlineClass getOnlineClass() {
		return onlineClass;
	}

	public void setOnlineClass(OnlineClass onlineClass) {
		this.onlineClass = onlineClass;
		
		if(this.onlineClass != null && this.onlineClass.getPayrollItem() !=null){
			if (this.onlineClass.getPayrollItem().getId() != this.getId()) {
				this.onlineClass.setPayrollItem(this);
			}
		}
		
	}

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public float getSalaryPercentage() {
		return salaryPercentage;
	}

	public void setSalaryPercentage(float salaryPercentage) {
		this.salaryPercentage = salaryPercentage;
	}

	public Payroll getPayroll() {
		return payroll;
	}

	public void setPayroll(Payroll payroll) {
		this.payroll = payroll;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public float getBackupDutySalary() {
		return backupDutySalary;
	}

	public void setBackupDutySalary(float backupDutySalary) {
		this.backupDutySalary = backupDutySalary;
	}
	
	public float getBaseSalary() {
		return baseSalary;
	}

	public void setBaseSalary(float baseSalary) {
		this.baseSalary = baseSalary;
	}

}
