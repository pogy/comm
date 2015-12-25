package com.vipkid.ext.moxtra;

import java.util.Date;

public class MoxtraAPITester {

	public static void main(String[] args) {
		GetAccessTokenResponse getAccessTokenResponse = MoxtraAPI.getAccessToken();
		if(getAccessTokenResponse.isSuccess()) {
			System.out.println(getAccessTokenResponse.getAccessToken());
			System.out.println(getAccessTokenResponse.getTokenType());
			System.out.println(getAccessTokenResponse.getExpiresIn());
			System.out.println(getAccessTokenResponse.getScope());
			
			Date startDateTime = new Date();
			Date endDateTime = new Date(startDateTime.getTime() + 30 * 60 * 1000);
			ScheduleMeetingResponse scheduleMeetingResponse = MoxtraAPI.scheduleMeeting(getAccessTokenResponse.getAccessToken(), "Test Class", startDateTime, endDateTime);
			System.out.println(scheduleMeetingResponse.getSessionKey());
			System.out.println(scheduleMeetingResponse.getStartMeetURL());
		}
	}

}
