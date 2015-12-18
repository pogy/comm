package com.lucene.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 版权  QQ �?309453289
 * @author ZengWeiLong
 * @date 2012 - 4 - 27
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = -1106443702974940064L;
	
	/**
	 * 当前页
	 */
	private int currentPage;
	/**
	 * 分页大小
	 */
	private int pageSize = 20;
	/**
	 * 总记录条数
	 */
	private int totalCount;
	/**
	 * 当前列表记录数
	 */
	private List<T> pageList;
	/**
	 * 总页数
	 */
	private int totalPages;

	public Page(int currentPage, int pageSize, int totalCount, List<T> pageList) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.pageList = pageList;
		this.totalPages = (totalCount + pageSize - 1) / pageSize;
	}

	/**
	 * 当前页
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * 当前页
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * 分页大小
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 分页大小
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 总记录条数
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * 总记录条数
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 当前列表记录数
	 * @return the pageList
	 */
	public List<T> getPageList() {
		return pageList;
	}

	/**
	 * 当前列表记录数
	 * @param pageList the pageList to set
	 */
	public void setPageList(List<T> pageList) {
		this.pageList = pageList;
	}

	/**
	 * 总页数
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * 总页数
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}


}
