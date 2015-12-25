package com.vipkid.service.pojo.parent;

import java.io.Serializable;

/**
 * 
* @ClassName: CourseView g
* @Description: 展示course下拉框
* @author zhangfeipeng 
* @date 2015年6月9日 上午11:41:01 
*
 */
public class CourseView implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = -531830147983707676L;
	
	private long id;
	private String name;
	
	
	public CourseView(){
		
	}
	public CourseView(long id,
			String name){
		this.id=id;
		this.name=name;
		
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
