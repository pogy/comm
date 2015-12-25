package com.vipkid.model;

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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.ActivityAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 教学资源
 */
@Entity
@Table(name = "resource", schema = DBInfo.SCHEMA)
public class Resource extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		PPT,	//幻灯片
		VIDEO,	//视频
		AUDIO,	//音频
		IMAGE,	//图片
		GAME	//游戏
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 类型
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;
	
	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	// 地址
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "url", nullable = false)
	private String url;
	
	// 教学活动
	@XmlJavaTypeAdapter(ActivityAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "resource_activity", inverseJoinColumns = @JoinColumn(name = "activity_id"), joinColumns = @JoinColumn(name = "resource_id"))
	private List<Activity> activities;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	
	
	
}
