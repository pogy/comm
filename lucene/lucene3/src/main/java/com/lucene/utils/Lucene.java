package com.lucene.utils;

import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TopDocs;
/**
 * 检索内容
 * @author totyuZWL
 *
 */
public class Lucene {
	
	private String[] content;
	
	private String url;
	
	private String[] reValues;
	
	private String[] fields;
	
	private int pageSize;
	
	private int current;
	
	private int rowCount;
	
	private int rowPage;
	
	private TopDocs topDocs;
	
	private List<Map<String, String>> list;
	
	public void setContent(String[] content) {
		this.content = content;
	}

	public String[] getContent() {
		return content;
	}

	public void setReValues(String[] reValues) {
		this.reValues = reValues;
	}

	public String[] getReValues() {
		return reValues;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String[] getFields() {
		return fields;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}

	public List<Map<String, String>> getList() {
		return list;
	}

	public void setRowPage(int rowPage) {
		this.rowPage = rowPage;
	}

	public int getRowPage() {
		return rowPage;
	}

	public void setTopDocs(TopDocs topDocs) {
		this.topDocs = topDocs;
	}

	public TopDocs getTopDocs() {
		return topDocs;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getCurrent() {
		return current;
	}
}
