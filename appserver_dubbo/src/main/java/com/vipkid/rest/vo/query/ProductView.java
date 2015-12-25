package com.vipkid.rest.vo.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vipkid.model.Product.Status;
import com.vipkid.model.Product.Type;

public class ProductView {
	
	private Long id;
	private String name;
	private Status status;
	private Type type;
	private Date createDateTime;
	private String description;
	private float classHourPrice;
	private float baseSalary;
	private CourseView course;
	private List<UnitView> units = new ArrayList<UnitView>();
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
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
	public float getBaseSalary() {
		return baseSalary;
	}
	public void setBaseSalary(float baseSalary) {
		this.baseSalary = baseSalary;
	}
	public CourseView getCourse() {
		return course;
	}
	public void setCourse(CourseView course) {
		this.course = course;
	}
	public List<UnitView> getUnits() {
		return units;
	}
	public void setUnits(List<UnitView> units) {
		this.units = units;
	}
	
	

}
