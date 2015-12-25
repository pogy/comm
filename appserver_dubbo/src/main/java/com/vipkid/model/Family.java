package com.vipkid.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.ItTestAdapter;
import com.vipkid.model.json.moxy.ParentAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.json.moxy.UserAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.util.TextUtils;

/**
 * 家庭
 */
@Entity
@Table(name = "family", schema = DBInfo.SCHEMA)
public class Family extends Base {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 家长
	@XmlJavaTypeAdapter(ParentAdapter.class)
	@OneToMany(mappedBy="family")
	private List<Parent> parents;
	
	// 学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@OneToMany(mappedBy="family")
	private List<Student> students;
	
	// 创建人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "creater_id", referencedColumnName = "id")
	private User creater;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
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
	
	// 座机
	@Column(name = "phone")
	private String phone;
	
	// 省
	@Column(name = "province")
	private String province;

	// 市
	@Column(name = "city")
	private String city;

	// 区
	@Column(name = "district")
	private String district;

	// 地址
	@Column(name = "address")
	private String address;
	
	// 邮编
	@Column(name = "zipcode")
	private String zipcode;

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
	
	// 支付宝账号
	@Column(name = "alipay_account")
	private String alipayAccount;
	
	// PayPal账号
	@Column(name = "paypal_account")
	private String payPalAccount;
	
	// 我邀请的学生数量
	@Column(name = "student_num_i_invented")
	private long studentNumberIInvented;

	@Column(name = "invitation_id")
	private String invitationId;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "invited_by_family_id", referencedColumnName = "id", unique = true)
	private Family invitedBy;
	
 	// 标记是否进行IT测试
 	@Column(name = "has_tested")
 	private boolean hasTested = false;
 	
 	// ItTest列表
  	@XmlJavaTypeAdapter(ItTestAdapter.class)
 	@OneToMany(mappedBy="family")
  	private List<ItTest> itTests = new LinkedList<ItTest>();
	
	@PrePersist
	public void prePersist() {
		this.createDateTime = new Date();
		this.lastEditDateTime = new Date();
		this.lastEditor = this.creater;
	}
	
	@PreUpdate
	public void preUpdate() {
		this.lastEditDateTime = new Date();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		if(students == null) {
			return TextUtils.NONE;
		}else {
			StringBuilder sbName = new StringBuilder();
			for(Student student : students) {
				if(student.getName() != null){
					sbName.append(student.getName()).append(TextUtils.SEPERATOR);
				}				
				sbName.append(student.getEnglishName()).append(TextUtils.COMMA);
			}
			sbName.append("family");
			
			return sbName.toString();
		}
	}
	
	public long getStudentNumberIInvented() {
		return studentNumberIInvented;
	}

	public void setStudentNumberIInvented(long studentNumberIInvented) {
		this.studentNumberIInvented = studentNumberIInvented;
	}

	public String getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(String invitationId) {
		this.invitationId = invitationId;
	}

	public Family getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(Family invitedBy) {
		this.invitedBy = invitedBy;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}

	public String getPayPalAccount() {
		return payPalAccount;
	}

	public void setPayPalAccount(String payPalAccount) {
		this.payPalAccount = payPalAccount;
	}

	public List<Parent> getParents() {
		return parents;
	}

	public void setParents(List<Parent> parents) {
		this.parents = parents;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public User getCreater() {
		return creater;
	}

	public void setCreater(User creater) {
		this.creater = creater;
	}

	public User getLastEditor() {
		return lastEditor;
	}

	public void setLastEditor(User lastEditor) {
		this.lastEditor = lastEditor;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getLastEditDateTime() {
		return lastEditDateTime;
	}

	public void setLastEditDateTime(Date lastEditDateTime) {
		this.lastEditDateTime = lastEditDateTime;
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
	
}
