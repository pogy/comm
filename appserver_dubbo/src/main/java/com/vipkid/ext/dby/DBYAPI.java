package com.vipkid.ext.dby;

import java.io.File;
import java.util.Date;

import com.duobeiyun.DuobeiYunClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vipkid.model.Role;
import com.vipkid.model.json.gson.SimpleFormatDateTypeAdapter;
import com.vipkid.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBYAPI {
	private static final Logger logger = LoggerFactory.getLogger(DBYAPI.class);
	public static CreateRoomResult createRoom(String title, Date startDateTime, int duration, boolean video, String roomType) {
        logger.info("CreateRoom,title={},startDateTime={},duration={},video={},rootType={}",title,startDateTime,duration,video,roomType);
		DuobeiYunClient duobeiYunClient = new DuobeiYunClient();
		if (title == null){
			throw new IllegalStateException("WXT room's titile can not be null");
		}
		title = title.trim();
		String result = duobeiYunClient.createRoom(title, startDateTime, duration, video, roomType);
        logger.info("DuoBeiYunClient createRoom,result={}",result);
		CreateRoomResult createRoomResult = getGson().fromJson(result, CreateRoomResult.class);
        if (null == createRoomResult) {
            logger.warn("DuoBeiYunClient return null when createRoomResult");
            createRoomResult = new CreateRoomResult();
            createRoomResult.setSuccess(false);
            createRoomResult.setError("DuoBeiYunClient return null when createRoomResult");
        }
        logger.info("DuoBeiYunClient createRoom,createRoomResult={}, roomType={}",createRoomResult.toString(), roomType);
		return createRoomResult;
	}
	
	public static UpdateRoomTitleResult updateRoomTitle(String roomId, String title) {
		DuobeiYunClient duobeiYunClient = new DuobeiYunClient();
		String result = duobeiYunClient.updateRoomTitle(roomId, title);
        logger.info("DuoBeiYunClient updateRoomTitle,result={},rootId={}",result,roomId);
		UpdateRoomTitleResult updateRoomTitleResult = getGson().fromJson(result, UpdateRoomTitleResult.class);
        if (null == updateRoomTitleResult) {
            logger.warn("DuoBeiYunClient return null when updateRoomTitle,rootID={}",roomId);
            updateRoomTitleResult = new UpdateRoomTitleResult();
            Room room = new Room();
            room.setRoomId(roomId);
            room.setTitle(title);
            updateRoomTitleResult.setRoom(room);
            updateRoomTitleResult.setSuccess(false);
            updateRoomTitleResult.setError("DuoBeiYunClient return null when updateRoomTitle");
        }
        logger.info("DuoBeiYunClient updateRoomTitle,updateRoomTitleResult={},roomID={}",updateRoomTitleResult.toString(),roomId);
		return updateRoomTitleResult;
	}
	
	public static UploadDocumentResult uploadDocument(File file) {
		DuobeiYunClient duobeiYunClient = new DuobeiYunClient();
		String result = duobeiYunClient.uploadDocument(file.getName(), file);
        logger.info("DuoBeiYunClient uploadDocument,result={}",result);
		UploadDocumentResult uploadDocumentResult = getGson().fromJson(result, UploadDocumentResult.class);
		return uploadDocumentResult;
	}
	
	public static AttachDocumentResult attachDocument(String roomId, String documentId) {
		DuobeiYunClient duobeiYunClient = new DuobeiYunClient();
		String result = duobeiYunClient.attachDocument(roomId, documentId);
        logger.info("DuoBeiYunClient attachDocument,result={},roomId={},documentId={}",result,roomId,documentId);
		AttachDocumentResult attachDocumentResult = getGson().fromJson(result, AttachDocumentResult.class);
		return attachDocumentResult;
	}
	
	public static ListDocumentsResult listDocuments(String roomId) {
		DuobeiYunClient duobeiYunClient = new DuobeiYunClient();
		String result = duobeiYunClient.listDocuments(roomId);
        logger.info("DuoBeiYunClient list Documents,result={},roomId={}",result,roomId);
		ListDocumentsResult listDocumentsResult = getGson().fromJson(result, ListDocumentsResult.class);
        if (null == listDocumentsResult) {
            logger.warn("Can not list documents from DuoBeiYunClient,listDocumentsResult is null,rootID={}",roomId);
            listDocumentsResult = new ListDocumentsResult();
            listDocumentsResult.setRoomId(roomId);
            listDocumentsResult.setSuccess(false);
            listDocumentsResult.setError("DuoBeiYunClient list Documents,result is null");
        }
        logger.info("List documents from DuoBeiYunClient,listDocumentsResult={},rootId={}",listDocumentsResult.toString(),roomId);
		return listDocumentsResult;
	}
	
	public static RemoveDocumentResult removeDocument(String roomId, String documentId) {
		DuobeiYunClient duobeiYunClient = new DuobeiYunClient();
		String result = duobeiYunClient.removeDocument(roomId, documentId);
        logger.info("DuoBeiYunClient remove Document,result={},roomId={},documentId={}",result,roomId,documentId);
		RemoveDocumentResult removeDocumentResult = getGson().fromJson(result, RemoveDocumentResult.class);
		return removeDocumentResult;
	}
	
	public static String getRoomURL(String userId, String name, String roomId, Role role) {
		DuobeiYunClient duobeiYunClient = new DuobeiYunClient();
		switch(role) {
		case STUDENT:
			return duobeiYunClient.generateRoomEnterUrl(userId, name, roomId, DuobeiYunClient.ROLE_STUDENT);
		case TEACHER:
			return duobeiYunClient.generateRoomEnterUrl(userId, name, roomId, DuobeiYunClient.ROLE_TEACHER);
		case STAFF_OPERATION:
			return duobeiYunClient.generateRoomEnterUrl(userId, name, roomId, DuobeiYunClient.ROLE_MONITOR);
		default:
			return TextUtils.NONE;
		}
	}
	
	private static Gson getGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new SimpleFormatDateTypeAdapter());
		return gsonBuilder.create();
	}
}
