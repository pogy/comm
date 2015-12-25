package com.vipkid.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.AgentAdapter;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 市场活动
 */
@Entity
@Table(name = "marketing_activity", schema = DBInfo.SCHEMA)
public class MarketingActivity extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		NORMAL,  // 普通
		INVENTION,  // 邀请码
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 代理商
	@XmlJavaTypeAdapter(AgentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "agent_id", referencedColumnName = "id", nullable = false)
	private Agent agent;
	
	// 活动主题图
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "banner", nullable = false)
	private String banner;
	
	// 声明
	@Column(name = "statement")
	private String statement;
	
	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
//	// 渠道号
//	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
//	@Column(name = "channel", nullable = false, unique = true)
//	private String channel;
	
	// 类型
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;
	
	// 是否有人数限制
	@Column(name = "has_limited")
	private boolean hasLimited;
	
	// 人数限制
	@Column(name = "limited_number")
	private long limitedNumber;
	
	// 邀请码数量
	@Column(name = "invention_code_number")
	private long inventionCodeNumber;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;

	// 是否已发布
	@Column(name = "has_released")
	private boolean hasReleased = false;
	
	// channel
	//@XmlJavaTypeAdapter(ChannelAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "channel_id", referencedColumnName = "id")
	private Channel channel;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isHasLimited() {
		return hasLimited;
	}

	public void setHasLimited(boolean hasLimited) {
		this.hasLimited = hasLimited;
	}

	public long getLimitedNumber() {
		return limitedNumber;
	}

	public void setLimitedNumber(long limitedNumber) {
		this.limitedNumber = limitedNumber;
	}

	public long getInventionCodeNumber() {
		return inventionCodeNumber;
	}

	public void setInventionCodeNumber(long inventionCodeNumber) {
		this.inventionCodeNumber = inventionCodeNumber;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public boolean isHasReleased() {
		return hasReleased;
	}

	public void setHasReleased(boolean hasReleased) {
		this.hasReleased = hasReleased;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	
}
