package com.vipkid.ext.moxtra;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vipkid.model.json.gson.GsonManager;

public class ScheduleMeetingResponseHandler implements ResponseHandler<ScheduleMeetingResponse> {
	
	@Override
	public ScheduleMeetingResponse handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
		ScheduleMeetingResponse scheduleMeetingResponse = null;
		
		if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			String json = EntityUtils.toString(httpEntity);

			try {
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
				scheduleMeetingResponse = GsonManager.getInstance().getGson().fromJson(jsonObject.get("data").toString(), ScheduleMeetingResponse.class);
				scheduleMeetingResponse.setSuccess(true);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}else {
			scheduleMeetingResponse = new ScheduleMeetingResponse();
			scheduleMeetingResponse.setSuccess(false);
			scheduleMeetingResponse.setErrorCode(httpResponse.getStatusLine().getStatusCode());
		}
		
		return scheduleMeetingResponse;
	}

}
