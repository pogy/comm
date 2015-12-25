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

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.UserAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
/**
 * 
* @ClassName: Channel
* @Description: 渠道 new
* 
*
 */
@Entity
@Table(name = "channel", schema = DBInfo.SCHEMA)
public class Channel extends Base {
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = -7536792334578261205L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "source_name", nullable = false, unique = true)
	private String sourceName;
	
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "channel_name1", nullable = false)
	private String channelName1;
	
	@Column(name = "channel_name2")
	private String channelName2;

	@Column(name = "channel_name3")
	private String channelName3;
	
	@Column(name = "channel_name4")
	private String channelName4;
	
	@Column(name = "channel_name5")
	private String channelName5;
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;
	
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	@Column(name = "level")
	private String level;
	
	@Column(name = "source_old",insertable = false)
	private String sourceOld;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public String getChannelName1() {
		return channelName1;
	}

	public void setChannelName1(String channelName1) {
		this.channelName1 = channelName1;
	}

	public String getChannelName2() {
		return channelName2;
	}

	public void setChannelName2(String channelName2) {
		this.channelName2 = channelName2;
	}


	public String getChannelName3() {
		return channelName3;
	}

	public void setChannelName3(String channelName3) {
		this.channelName3 = channelName3;
	}

	public String getChannelName4() {
		return channelName4;
	}

	public void setChannelName4(String channelName4) {
		this.channelName4 = channelName4;
	}

	public String getChannelName5() {
		return channelName5;
	}

	public void setChannelName5(String channelName5) {
		this.channelName5 = channelName5;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSourceOld() {
		return sourceOld;
	}

	public void setSourceOld(String sourceOld) {
		this.sourceOld = sourceOld;
	}
	
}
