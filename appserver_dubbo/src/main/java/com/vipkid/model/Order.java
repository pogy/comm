package com.vipkid.model;

import com.vipkid.model.json.moxy.*;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 订单
 */
@Entity
@Table(name = "order", schema = DBInfo.SCHEMA)
public class Order extends Base {
	private static final long serialVersionUID = 1L;

	public enum Status {
		TO_PAY, // 未支付
		CANCELED, // 已取消
		PAID, // 已支付
		PAY_CONFIRMED // 确认付款
	}

	public enum PayBy {		
		ALIPAY, // 支付宝
		TRANSFER, // 银行转账
		WECHATPAY, //微信支付
		UNIONPAY, // 银联支付
		YOUZAN //有赞支付
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 编号 = 创建时间 + id
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false, unique = true)
	private String serialNumber;

	// 家庭
	@XmlJavaTypeAdapter(FamilyAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "family_id", referencedColumnName = "id", nullable = false)
	private Family family;

	// 学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student student;

	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;
	
	@Column(name = "online_pay_failed")
	private boolean onlinePayFailed;

	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;

	// 支付时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "paid_date_time")
	private Date paidDateTime;
	
	// 合同开始时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "contract_start_time")
	private Date contractStartTime;
	
	// 合同结束时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "contract_end_time")
	private Date contractEndTime;

	// 取消时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "canceled_date_time")
	private Date canceledDateTime;
	
	// 支付方式
	@Enumerated(EnumType.STRING)
	@Column(name = "pay_by")
	private PayBy payBy;
	
	// 创建人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "creater_id", referencedColumnName = "id", nullable = false)
	private User creater;
	
	// 确认人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "confirmer_id", referencedColumnName = "id")
	private User confirmer;
	
	// 成交价合计
	@Column(name = "total_deal_price", nullable = false)
	private float totalDealPrice;
	
	@Column(name = "out_trade_number")
	private String outTradeNumber;
	
	@Column(name = "out_trade_status")
	private String outTradeStatus;
	
	// 备注
	@Lob
	@Column(name = "comment")
	private String comment;
	
	//订单项集合
	@XmlJavaTypeAdapter(OrderItemAdapter.class)
	@OneToMany(mappedBy="order", cascade = CascadeType.REFRESH)
	private List<OrderItem> orderItems;
	
	@Column(name = "payer")
	private String payer;
	
	@PrePersist
	public void prePersist() {
		this.status = Status.TO_PAY;
		this.createDateTime = new Date();
		//this.serialNumber = UUID.randomUUID().toString();
		//DateTimeUtils.format(new Date(), DateTimeUtils.DATE_FORMAT2) + this.id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
	
	

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public PayBy getPayBy() {
		return payBy;
	}

	public void setPayBy(PayBy payBy) {
		this.payBy = payBy;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getPaidDateTime() {
		return paidDateTime;
	}

	public void setPaidDateTime(Date paidDateTime) {
		this.paidDateTime = paidDateTime;
	}
	

	public Date getContractStartTime() {
		return contractStartTime;
	}

	public void setContractStartTime(Date contractStartTime) {
		this.contractStartTime = contractStartTime;
	}

	public Date getContractEndTime() {
		return contractEndTime;
	}

	public void setContractEndTime(Date contractEndTime) {
		this.contractEndTime = contractEndTime;
	}

	public Date getCanceledDateTime() {
		return canceledDateTime;
	}

	public void setCanceledDateTime(Date canceledDateTime) {
		this.canceledDateTime = canceledDateTime;
	}

	public User getCreater() {
		return creater;
	}

	public void setCreater(User creater) {
		this.creater = creater;
	}

	public User getConfirmer() {
		return confirmer;
	}

	public void setConfirmer(User confirmer) {
		this.confirmer = confirmer;
	}

	public float getTotalDealPrice() {
		return totalDealPrice;
	}

	public void setTotalDealPrice(float totalDealPrice) {
		this.totalDealPrice = totalDealPrice;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isOnlinePayFailed() {
		return onlinePayFailed;
	}

	public void setOnlinePayFailed(boolean onlinePayFailed) {
		this.onlinePayFailed = onlinePayFailed;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public String getOutTradeNumber() {
		return outTradeNumber;
	}

	public void setOutTradeNumber(String outTradeNumber) {
		this.outTradeNumber = outTradeNumber;
	}

	public String getOutTradeStatus() {
		return outTradeStatus;
	}

	public void setOutTradeStatus(String outTradeStatus) {
		this.outTradeStatus = outTradeStatus;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}
	
}
