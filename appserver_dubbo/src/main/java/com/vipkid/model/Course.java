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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.UnitAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 教学方案
 */
@Entity
@Table(name = "course", schema = DBInfo.SCHEMA)
public class Course extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Mode {
		ONE_ON_ONE, // 1:1课
		ONE_TO_MANY // 1:N课
	}
	
	public enum Type {
		IT_TEST, // IT测试课
		DEMO, // 试听课
		GUIDE, // 新生指导课
		TEACHER_RECRUITMENT, // 教师招聘课
		NORMAL, // 普通课
		MAJOR,	//主修课
		TEST, //test
		PRACTICUM,
		TRIAL,
		ELECTIVE_LT,
		ASSESSMENT2	,
		OPEN1,
		OPEN2,//公开课增加open2 zfp
        REVIEW
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	// 编号，如C1
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false, unique = true)
	private String serialNumber;
	
	// 单元
	@XmlJavaTypeAdapter(UnitAdapter.class)
	@OneToMany(mappedBy="course")
	private List<Unit> units;

	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "show_name", nullable = false)
	private String showName;

	// 描述
	@Column(name = "description")
	private String description;

	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	// 模式
	@Enumerated(EnumType.STRING)
	@Column(name = "mode", nullable = false)
	private Mode mode;
	
	// 类型
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private Type type;
	
	// 课程是否有序
	@Column(name = "sequential", nullable = false)
	private boolean sequential;
	
	// 课程是否需要预备老师
    @Column(name = "need_backup_teacher", nullable = false)
    private boolean needBackupTeacher;
    
    // 每节基本工资
 	@Column(name = "base_class_salary")
 	private float baseClassSalary;

	// 课程是否免费
    @Column(name = "free", nullable = false)
    private boolean free;
    
    // 辅修课对应的主修课学力单元，当学生达到此单元时，开放相应的辅修课给学生
    @XmlJavaTypeAdapter(UnitAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "entry_unit_id", referencedColumnName = "id")
    private Unit entryUnit;
    
    // 子节点类型，如level,unit
    @Column(name = "childType")
    private String childType;

	@PrePersist
	public void prePersist() {
		this.createDateTime = new Date();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public boolean isSequential() {
		return sequential;
	}

	public void setSequential(boolean sequential) {
		this.sequential = sequential;
	}

	public boolean isNeedBackupTeacher() {
		return needBackupTeacher;
	}

	public void setNeedBackupTeacher(boolean needBackupTeacher) {
		this.needBackupTeacher = needBackupTeacher;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Unit getEntryUnit() {
		return entryUnit;
	}

	public void setEntryUnit(Unit entryUnit) {
		this.entryUnit = entryUnit;
	}	
 	
    public float getBaseClassSalary() {
		return baseClassSalary;
	}

	public void setBaseClassSalary(float baseClassSalary) {
		this.baseClassSalary = baseClassSalary;
	}
	
	public String getAssignationName() {
		if(this.name.equals("Demo")) {
			return "Interview";
		}else if(this.name.equals("Assessment")) {
			return "Interview";
		}else {
			return this.name;
		}
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getChildType() {
		return childType;
	}

	public void setChildType(String childType) {
		this.childType = childType;
	}
}
