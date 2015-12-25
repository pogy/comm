package com.vipkid.model;

import java.util.Date;
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
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.CourseAdapter;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.UnitAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 商品
 */
@Entity
@Table(name = "product", schema = DBInfo.SCHEMA)
public class Product extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Status {
		ON_SALE, //上架
		OFF_SALE //下架
	}
	
	public enum Type {
		FREE,
		PAID
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 商品类型
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;

	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;

	// 描述
	@Lob
	@Column(name = "description")
	private String description;
	
	// 每课时价格
	@Column(name = "class_hour_price", nullable = false)
	private float classHourPrice;
	
	// 课程方案
	@XmlJavaTypeAdapter(CourseAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
	private Course course;
	
	// 单元
	@XmlJavaTypeAdapter(UnitAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "product_unit", inverseJoinColumns = @JoinColumn(name = "unit_id"), joinColumns = @JoinColumn(name = "product_id"))
	@OrderBy("sequence ASC")
	private List<Unit> units;
	
	// 每课时基本工资
	@Column(name = "base_salary")
	private float baseSalary;
	
	@PrePersist
	public void prePersist() {
		this.status = Status.ON_SALE;
		this.createDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getClassHourPrice() {
		return classHourPrice;
	}

	public void setClassHourPrice(float classHourPrice) {
		this.classHourPrice = classHourPrice;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public float getBaseSalary() {
		return baseSalary;
	}

	public void setBaseSalary(float baseSalary) {
		this.baseSalary = baseSalary;
	}
	
}
