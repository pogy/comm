package com.vipkid.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.AirCraftAdapter;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 飞船皮肤
 */
@Entity
@Table(name = "air_craft_theme", schema = DBInfo.SCHEMA)
public class AirCraftTheme extends Base {
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 飞船
	@XmlJavaTypeAdapter(AirCraftAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "air_craft_id", referencedColumnName = "id", nullable = false)
	private AirCraft airCraft;
	
	// 皮肤图片地址
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "url", nullable = false)
	private String url;
	
	// 是否是当前飞船皮肤
	@Column(name = "current")
	private boolean current;
	
	//皮肤标题
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	//皮肤描述
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "introduction", nullable = false)
	private String introduction;

	// 价格
	@Column(name = "price", nullable = false)
	private int price;
	
	// 皮肤级别
	@Column(name = "level", nullable = false)
	private int level;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AirCraft getAirCraft() {
		return airCraft;
	}

	public void setAirCraft(AirCraft airCraft) {
		this.airCraft = airCraft;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
