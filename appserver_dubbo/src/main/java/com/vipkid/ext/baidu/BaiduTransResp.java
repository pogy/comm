package com.vipkid.ext.baidu;

import java.util.List;

public class BaiduTransResp {
	
	private String error_code;
	private String error_msg;
	private List<ResultItem> trans_result;
	private String from;
	private String to;
	private String query;
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public List<ResultItem> getTrans_result() {
		return trans_result;
	}
	public void setTrans_result(List<ResultItem> trans_result) {
		this.trans_result = trans_result;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
}

class ResultItem {
	private String src;
	private String dst;
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	
	
}
