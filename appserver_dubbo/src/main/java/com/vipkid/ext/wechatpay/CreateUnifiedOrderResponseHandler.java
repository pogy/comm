package com.vipkid.ext.wechatpay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.ext.wechatpay.util.WechatPayXMLUtil;
import com.vipkid.ext.wechatpay.util.WeChatPaySignUtil;
import com.vipkid.util.CharSet;
import com.vipkid.util.Configurations;

public class CreateUnifiedOrderResponseHandler implements ResponseHandler<CreateUnifiedOrderResponse> {
	private static Logger logger = LoggerFactory.getLogger(CreateUnifiedOrderResponseHandler.class.getSimpleName());
	
	@Override
	public CreateUnifiedOrderResponse handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
		CreateUnifiedOrderResponse createUnifiedOrderResponse;
		
		if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			String responseXML = EntityUtils.toString(httpEntity, CharSet.UTF_8);
			logger.info("CreateUnifiedOrder responseXML : " + responseXML);
			
			try {
				createUnifiedOrderResponse = WechatPayXMLUtil.unmarshal(responseXML, CreateUnifiedOrderResponse.class);
				createUnifiedOrderResponse = checkUnifiedOrderResponse(createUnifiedOrderResponse);
			} catch (JAXBException e) {
				logger.error("exception when unmarshal wechat pay response: " + e);
				createUnifiedOrderResponse = new CreateUnifiedOrderResponse();
				createUnifiedOrderResponse.setSuccess(false);
				createUnifiedOrderResponse.setErrorMessage("exception when unmarshal response");
			}
	        
		} else {
			createUnifiedOrderResponse = new CreateUnifiedOrderResponse();
			createUnifiedOrderResponse.setSuccess(false);
			createUnifiedOrderResponse.setErrorMessage("network error,StatusCode:" + httpResponse.getStatusLine().getStatusCode());

		}
		
		return createUnifiedOrderResponse;
	}
	
	
	private CreateUnifiedOrderResponse checkUnifiedOrderResponse(CreateUnifiedOrderResponse createUnifiedOrderResponse) throws UnsupportedEncodingException {
		boolean isSuccess = false;
		String errorMessage = "";
		if (WechatPayAPI.SUCCESS.equals(createUnifiedOrderResponse.getReturnCode())) {//通信标识
			Map<String,String> paramMap = generateResponseParamMap(createUnifiedOrderResponse);
			boolean isVerifyPassed = WeChatPaySignUtil.verifySign(paramMap, createUnifiedOrderResponse.getSign(),
					Configurations.WechatPay.PAY_SIGN_KEY, Configurations.WechatPay.INPUT_CHARSET);
			if (isVerifyPassed) {
				if (WechatPayAPI.SUCCESS.equals(createUnifiedOrderResponse.getResultCode())) {//业务结果
					isSuccess = true;
				} else {
					errorMessage = "result_code:" + createUnifiedOrderResponse.getResultCode()
							+ ",err_code:" + createUnifiedOrderResponse.getErrorCode()
							+ ",err_code_des:" + createUnifiedOrderResponse.getErrorCodeDescribe();
				}
			} else {
				errorMessage = "sign verify failed";
			}
		} else {
			errorMessage = "return_code:" + createUnifiedOrderResponse.getReturnCode() 
					+ ",return_msg:" + createUnifiedOrderResponse.getReturnMessage();
		}
		
		createUnifiedOrderResponse.setSuccess(isSuccess);
		createUnifiedOrderResponse.setErrorMessage(errorMessage);
		
		return createUnifiedOrderResponse;
	}
	
	private Map<String,String> generateResponseParamMap(CreateUnifiedOrderResponse response) {
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("return_code", response.getReturnCode());
		paramMap.put("return_msg", response.getReturnMessage());
		paramMap.put("appid", response.getAppId());
		paramMap.put("mch_id", response.getMerchantId());
		paramMap.put("device_info", response.getDeviceInfo());
		paramMap.put("nonce_str", response.getNonceString());
		paramMap.put("result_code", response.getResultCode());
		paramMap.put("err_code", response.getErrorCode());
		paramMap.put("err_code_des", response.getErrorCodeDescribe());
		paramMap.put("trade_type", response.getTradeType());
		paramMap.put("prepay_id", response.getPrepayId());
		paramMap.put("code_url", response.getCodeUrl());
		
		return paramMap; 
		
	}

}
