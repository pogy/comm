package com.vipkid.ext.dby;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.duobeiyun.DuobeiYunClient;

public class DBYAPITester {

private static final SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static void testCreateRoom() {
		DuobeiYunClient client = new DuobeiYunClient();
		String result = null;
		Date startTime = new Date(System.currentTimeMillis()+60*1000);
		int duration = 2;
		result = client.createRoom("test-create-" + SF.format(startTime), new Date(), duration, true, DuobeiYunClient.COURSE_TYPE_1vN);
		System.out.println(result);
	}
	
	private static void testGetDocumentStatus() {
		DuobeiYunClient client = new DuobeiYunClient();
		String documentId = "Your documentId here";
		String result = client.getDocumentStatus(documentId);
		System.out.println(result);
	}

	private static void testUpload() throws UnsupportedEncodingException {
		DuobeiYunClient client = new DuobeiYunClient();
		File toupload = new File("/path/to/your/file");
		String result = client.uploadDocument(toupload.getName(), toupload);
		System.out.println(result);
	}

	private static void testTrialClassroom() {
		DuobeiYunClient client = new DuobeiYunClient(); 
		String strClassRoomId = "jzed467685b371404f8ecb0e977c81e221";//"jzf945009ca5634ead935e8afe2d889949";
		String teacherURL = client.generateRoomEnterUrl("trial_teacher", "trial_teacher", strClassRoomId, DuobeiYunClient.ROLE_TEACHER);
		System.out.println("teacherURL:"+teacherURL);
		String studentURL = client.generateRoomEnterUrl("trial_student", "trial_student", strClassRoomId, DuobeiYunClient.ROLE_STUDENT);
		System.out.println("studentURL:"+studentURL);
	}
	
	public static void main(String[] args) throws Exception {
//		testCreateRoom();
//		testGetDocumentStatus();
//		testUpload();
		
		testTrialClassroom();
	}
}
