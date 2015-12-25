package com.vipkid.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vipkid.model.util.DBInfo;

@Entity
@Table(name = "seq_seed", schema = DBInfo.SCHEMA)
public class SeqSeed extends Base {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "seed_type")
	private String seedType;
	
	@Column(name = "seed_date")
	private String seedDate;
	
	@Column(name = "date_format")
	private String dateFormat;
	
	@Column(name = "seed_value")
	private long seedValue;
	
	@Column(name = "value_format")
	private String valueFormat;
	
	@Column(name = "create_time")
	private Date createTime;
	
	
	public String getSeedType() {
		return seedType;
	}
	public void setSeedType(String seedType) {
		this.seedType = seedType;
	}
	public String getSeedDate() {
		return seedDate;
	}
	public void setSeedDate(String seedDate) {
		this.seedDate = seedDate;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public long getSeedValue() {
		return seedValue;
	}
	public void setSeedValue(long seedValue) {
		this.seedValue = seedValue;
	}
	
	public String getValueFormat() {
		return valueFormat;
	}
	public void setValueFormat(String valueFormat) {
		this.valueFormat = valueFormat;
	}

	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public enum SeedType {
		OrderNo //订单号
	}
}
