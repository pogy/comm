package com.vipkid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.PPTAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 一页幻灯片
 */
@Entity
@Table(name = "slide", schema = DBInfo.SCHEMA)
public class Slide extends Base {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 幻灯片
	@XmlJavaTypeAdapter(PPTAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "ppt_id", referencedColumnName = "id", nullable = false)
	private PPT ppt;
	
	// 开始时间
	@Column(name = "start_time")
	private double startTime;
	
	// 页数
	@Column(name = "page")
	private int page;
	
	// 页数
	@Column(name = "slide_image_url")
	private String slideImageUrl;
	
	// 提示
	@Lob
	@Column(name = "tips")
	private String tips;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public PPT getPPT() {
		return ppt;
	}

	public void setPPT(PPT ppt) {
		this.ppt = ppt;
	}

	public String getSlideImageUrl() {
		return slideImageUrl;
	}

	public void setSlideImageUrl(String slideImageUrl) {
		this.slideImageUrl = slideImageUrl;
	}
	
	
	
}
