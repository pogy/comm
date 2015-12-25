package com.vipkid.rest.pay.alipay;

import com.google.common.collect.Maps;
import com.vipkid.model.Order;
import com.vipkid.service.OrderService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by davieli on 2015/5/30.
 */
@Controller
@RequestMapping("/api/service")
public class AlipayCallbackController {
    private static final Logger logger = LoggerFactory.getLogger(AlipayCallbackController.class);

    @Resource
    private OrderService orderService;

    @RequestMapping("/AlipayNotifyServlet")
    @ResponseBody
    public Map<String, Object> callback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = Maps.newHashMap();
        logger.info("------------------------------>>>>>>>>>>>>>>>>>>>>> do alipay callback begin");
        logger.info("getContextPath ==== " + request.getContextPath());
        Map<String, String> params = doHandleParameters(request);
        if (MapUtils.isNotEmpty(params)) {
            if (changeOrderStatus(params)) {
                logger.info("order status change success ！");
                result.put("ret", true);
                result.put("msg", "");
            } else {
                logger.info("order status change fail ！");
                response.getWriter().println("fail");
                result.put("ret", false);
                result.put("msg", "order status change fail ！");
            }
        } else {
            logger.error("AliPay callBack param is wrong!!!!");
            result.put("ret", false);
            result.put("msg", "AliPay callBack param is wrong!!!!");
        }
        logger.info("------------------------------>>>>>>>>>>>>>>>>>>>>> do alipay callback end");
        return result;
    }

    private Map<String, String> doHandleParameters(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        //商户订单号
        String out_trade_no = request.getParameter("out_trade_no");
        //支付宝交易号
        String trade_no = request.getParameter("trade_no");
        //交易状态
        String trade_status = request.getParameter("trade_status");
        if (StringUtils.isBlank(out_trade_no) || StringUtils.isBlank(trade_no)) {
            return null;
        }
        //支付结果
        String is_success = request.getParameter("is_success");
        if (StringUtils.isBlank(is_success)) {
            if ("TRADE_SUCCESS".equals(trade_status)) {
                is_success = "T";
            } else {
                is_success = "F";
            }
        }
        params.put("out_trade_no", out_trade_no);
        params.put("trade_no", trade_no);
        params.put("trade_status", trade_status);
        params.put("is_success", is_success);
        logger.info("Pay with Alipay Status， out_trade_no = " + out_trade_no + " trade_no = " + trade_no + " trade_status = " + trade_status.toString());

        return params;
    }

    //改变订单状态
    private boolean changeOrderStatus(Map<String, String> params) {
        logger.debug(" begin change order status ！");
        if (params.get("is_success").equals("T")) {
            Order order = orderService.find(Integer.valueOf(params.get("out_trade_no")));
            if (null != order) {
                order.setOutTradeNumber(params.get("trade_no"));
                order.setOutTradeStatus(params.get("trade_status"));
                order.setPayBy(Order.PayBy.ALIPAY);
                order.setPaidDateTime(new Date());
                //order.setStatus(Status.PAID);
                order.setOnlinePayFailed(false);
                //在learningProgress里面增加lefthour
                Order confirmOrder = orderService.doConfirm(order);
                logger.info("order status change success ！");
                if (confirmOrder != null) {
                    return true;
                }
            } else {
                logger.error("未找到支付订单，更新订单失败，订单号={}",params.get("out_trade_no"));
            }
        } else {
            logger.info("Alipay failed");
        }
        return false;
    }
}
