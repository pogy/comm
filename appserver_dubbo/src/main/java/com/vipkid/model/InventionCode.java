package com.vipkid.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.MarketingActivityAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 邀请码
 */
@Entity
@Table(name = "invention_code", schema = DBInfo.SCHEMA)
public class InventionCode extends Base {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 编码
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "code", nullable = false, unique = true)
	private String code;
	
	// 市场活动
	@XmlJavaTypeAdapter(MarketingActivityAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "marketing_activity_id", referencedColumnName = "id", nullable = false)
	private MarketingActivity marketingActivity;
	
	// 是否已使用
	@Column(name = "has_used", nullable = false)
	private boolean hasUsed = false;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public MarketingActivity getMarketingActivity() {
		return marketingActivity;
	}

	public void setMarketingActivity(MarketingActivity marketingActivity) {
		this.marketingActivity = marketingActivity;
	}

	public boolean isHasUsed() {
		return hasUsed;
	}

	public void setHasUsed(boolean hasUsed) {
		this.hasUsed = hasUsed;
	}
	
	
}
