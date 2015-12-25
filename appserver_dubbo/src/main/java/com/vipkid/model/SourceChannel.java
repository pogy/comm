package com.vipkid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vipkid.model.util.DBInfo;

/**
 * @className: SourceChannel
 * @Description: 老渠道与新渠道的映射
 * @author wangbing
 * @createdate 2015/07/27
 */
@Entity
@Table(name = "source_channel_mapping", schema = DBInfo.SCHEMA)
public class SourceChannel{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	//老渠道名称
	@Column(name = "source_old")
	private String sourceName;

	//新渠道
	@ManyToOne
	@JoinColumn(name = "channel_id", referencedColumnName = "id")
	private Channel channel;

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

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}
