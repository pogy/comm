package com.vipkid.rest.vo.query;

/**
 * 返回教师评价结果的VO
 * by wangbing 20150911
 */
public class TeacherEvaluationVO {
	
	private long id;	//Teacher Id
	
	private String name;	//Teacher Name
	
	private long totalNum;	//总评价数量
	
	private long star1;	//1星数量
	
	private long star2;	//2星数量
	
	private long star3;	//3星数量
	
	private long star4;	//4星数量
	
	private long star5;	//5星数量

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

	public long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(long totalNum) {
		this.totalNum = totalNum;
	}

	public long getStar1() {
		return star1;
	}

	public void setStar1(long star1) {
		this.star1 = star1;
	}

	public long getStar2() {
		return star2;
	}

	public void setStar2(long star2) {
		this.star2 = star2;
	}

	public long getStar3() {
		return star3;
	}

	public void setStar3(long star3) {
		this.star3 = star3;
	}

	public long getStar4() {
		return star4;
	}

	public void setStar4(long star4) {
		this.star4 = star4;
	}

	public long getStar5() {
		return star5;
	}

	public void setStar5(long star5) {
		this.star5 = star5;
	}
}
