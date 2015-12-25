package com.vipkid.ext.dby;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;


public class ListDocumentsResult extends Result {
	private static final long serialVersionUID = 1L;
	
	private List<Document> documents;
	private String roomId;
	
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

    @Override
    public String toString() {
        return "ListDocumentsResult{" +
                "documents=" + (CollectionUtils.isEmpty(documents)?"null":documents.toString()) +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}
