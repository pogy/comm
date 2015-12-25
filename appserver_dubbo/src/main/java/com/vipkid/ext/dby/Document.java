package com.vipkid.ext.dby;

import java.io.Serializable;

public class Document implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String originalDocName;

	private String createTime;

	private String status;
	
	private String documentId;

    public String getOriginalDocName() {
        return originalDocName;
    }

    public void setOriginalDocName(String originalDocName) {
        this.originalDocName = originalDocName;
    }

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

    @Override
    public String toString() {
        return "Document{" +
                "originalDocName='" + originalDocName + '\'' +
                ", createTime=" + createTime +
                ", status='" + status + '\'' +
                ", documentId='" + documentId + '\'' +
                '}';
    }
}
