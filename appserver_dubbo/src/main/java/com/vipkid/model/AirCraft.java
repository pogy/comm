package com.vipkid.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.service.pojo.AirCraftThemeView;

/**
 * 飞船
 */
@Entity
@Table(name = "air_craft", schema = DBInfo.SCHEMA)
public class AirCraft extends Base {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 飞机编号
	@Column(name = "sequence")
	private int sequence;
	
	// 学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id")
	private Student student;
	
	//当前飞机的所有皮肤
	@Transient
	private List<AirCraftThemeView> airCraftThemes;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
	
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<AirCraftThemeView> getAirCraftThemes() {
		return airCraftThemes;
	}

	public void setAirCraftThemes(List<AirCraftThemeView> airCraftThemes) {
		this.airCraftThemes = airCraftThemes;
	}
	
	
	
}
