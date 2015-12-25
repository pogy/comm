package com.vipkid.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.ResourceAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * PPT
 */
@Entity
@Table(name = "ppt", schema = DBInfo.SCHEMA)
public class PPT extends Base {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 资源
	@XmlJavaTypeAdapter(ResourceAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "resource_id", referencedColumnName = "id", unique = true)
	private Resource resource;
	
	@Column(name = "slide_initialized")
	private boolean slideInitialized;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public boolean isSlideInitialized() {
		return slideInitialized;
	}

	public void setSlideInitialized(boolean slideInitialized) {
		this.slideInitialized = slideInitialized;
	}
	
}
