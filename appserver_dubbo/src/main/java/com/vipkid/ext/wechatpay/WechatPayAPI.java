package com.vipkid.ext.wechatpay;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.ext.wechatpay.CreateUnifiedOrderRequest.TradeType;
import com.vipkid.ext.wechatpay.util.WeChatPayHttpUtil;
import com.vipkid.ext.wechatpay.util.WeChatPaySignUtil;
import com.vipkid.ext.wechatpay.util.WechatPayXMLUtil;
import com.vipkid.model.Order;
import com.vipkid.util.Configurations;

public class WechatPayAPI {
    private static Logger logger = LoggerFactory.getLogger(WechatPayAPI.class.getSimpleName());

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    public static CreateUnifiedOrderResponse createUnifiedOrder(Order order, String customerIP, TradeType tradeType, String openId) {
        CreateUnifiedOrderResponse response = null;

        try {
            CreateUnifiedOrderRequest createUnifiedOrderRequest = new CreateUnifiedOrderRequest(order, customerIP, tradeType, openId);
            String requestXML = WechatPayXMLUtil.marshal(createUnifiedOrderRequest, CreateUnifiedOrderRequest.class);

            logger.info("CreateUnifiedOrder requestXML :" + requestXML);

            response = WeChatPayHttpUtil.post(requestXML, Configurations.WechatPay.Service.CREATE_UNIFIED_ORDER,
                    new CreateUnifiedOrderResponseHandler());
            if (response.isSuccess()) {
                logger.info("success to create unified order");
            } else {
                logger.info("fail to create unified order, error message is {}", response.getErrorMessage());
            }

        } catch (Exception e) {
            logger.error("exception when create unified order: {}", e);
        }

        return response;
    }

    /**
     * 进行 sign 和相关格式校验
     *
     * @param requestXML
     * @return UnifiedOrderNotifyRequest
     */
    public static UnifiedOrderNotifyRequest verifyUnifiedOrderNotifyRequest(String requestXML) {
        UnifiedOrderNotifyRequest notifyRequest = null;
        boolean isSuccess = false;
        String errorMessage = "";
        try {
            logger.info("unmarshal UnifiedOrderNotifyRequest : " + requestXML);
            notifyRequest = WechatPayXMLUtil.unmarshal(requestXML, UnifiedOrderNotifyRequest.class);
            if (SUCCESS.equals(notifyRequest.getReturnCode())) {//通信标识
                Map<String, String> paramMap = WechatPayXMLUtil.parseToMap(requestXML);
                String sign = paramMap.get("sign");
                if (sign != null) {
                    //sign 不参与签名校验
                    paramMap.remove("sign");
                }
                boolean isVerifyPassed = false;
                isVerifyPassed = WeChatPaySignUtil.verifySign(paramMap, sign, Configurations.WechatPay.PAY_SIGN_KEY,
                        Configurations.WechatPay.INPUT_CHARSET);
                if (isVerifyPassed) {
                    if (SUCCESS.equals(notifyRequest.getResultCode()) &&
                            (StringUtils.isBlank(notifyRequest.getOpenId())
                                    || StringUtils.isBlank(notifyRequest.getTransactionId())
                                    || StringUtils.isBlank(notifyRequest.getOutTradeNo())
                            )) {
                        isSuccess = false;
                        errorMessage = "参数校验错误";
                    } else {
                        isSuccess = true;
                    }
                } else {
                    logger.info("error when verify unifiedOrderNotifyRequest sign");
                    errorMessage = "签名失败";
                }
            } else {
                errorMessage = "参数格式校验错误";
            }
        } catch (Exception e) {
            logger.error("error when parse UnifiedOrderNotifyRequest xml" + e.getMessage());
            errorMessage = "参数格式校验错误";
        }
        if (notifyRequest == null) {
            notifyRequest = new UnifiedOrderNotifyRequest();
        }
        notifyRequest.setSuccess(isSuccess);
        notifyRequest.setErrorMessage(errorMessage);
        return notifyRequest;
    }
}