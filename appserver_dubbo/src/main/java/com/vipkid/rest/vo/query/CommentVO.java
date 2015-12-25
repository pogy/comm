package com.vipkid.rest.vo.query;

public class CommentVO {
	private Long id;
	private boolean empty;
	private UserVO student;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	public UserVO getStudent() {
		return student;
	}
	public void setStudent(UserVO student) {
		this.student = student;
	}
	
	

}
