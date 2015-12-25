package com.vipkid.ext.dby;


public class AttachDocumentResult extends Result {
	private static final long serialVersionUID = 1L;
	
	private String documentId;
	private String roomId;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}


	
}
