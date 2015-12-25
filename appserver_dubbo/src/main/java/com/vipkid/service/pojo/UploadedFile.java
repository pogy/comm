package com.vipkid.service.pojo;

import java.io.Serializable;

public class UploadedFile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//文件名 ，如：111.png
	private String name;
	
	//全路径，如：http://resource.vipkid.com.cn/avatar/111.png
	private String url;
	
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
	
	
}
