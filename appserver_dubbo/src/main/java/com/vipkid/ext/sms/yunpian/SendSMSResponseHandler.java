package com.vipkid.ext.sms.yunpian;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.vipkid.model.json.gson.GsonManager;

public class SendSMSResponseHandler implements ResponseHandler<SendSMSResponse> {
	
	@Override
	public SendSMSResponse handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
		SendSMSResponse sendSMSResponse;
		
		if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			String json = EntityUtils.toString(httpEntity);
			
			sendSMSResponse = GsonManager.getInstance().getGson().fromJson(json, SendSMSResponse.class);
			if(sendSMSResponse.getCode() == 0) {
				sendSMSResponse.setSuccess(true);
			}else {
				sendSMSResponse.setSuccess(false);
			}
			
		}else {
			sendSMSResponse = new SendSMSResponse();
			sendSMSResponse.setSuccess(false);
			sendSMSResponse.setMessage("Error when sending SMS.");
			sendSMSResponse.setDetail(Integer.toString(httpResponse.getStatusLine().getStatusCode()));
		}
		
		return sendSMSResponse;
	}

}
