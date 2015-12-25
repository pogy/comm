package com.vipkid.ext.moxtra;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.vipkid.model.json.gson.GsonManager;

public class GetAccessTokenResponseHandler implements ResponseHandler<GetAccessTokenResponse> {
	
	@Override
	public GetAccessTokenResponse handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
		GetAccessTokenResponse getAccessTokenResponse = null;
		
		if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			String json = EntityUtils.toString(httpEntity);
			
			getAccessTokenResponse = GsonManager.getInstance().getGson().fromJson(json, GetAccessTokenResponse.class);
			getAccessTokenResponse.setSuccess(true);
		}else {
			getAccessTokenResponse = new GetAccessTokenResponse();
			getAccessTokenResponse.setSuccess(false);
			getAccessTokenResponse.setErrorCode(httpResponse.getStatusLine().getStatusCode());
		}
		
		return getAccessTokenResponse;
	}

}
