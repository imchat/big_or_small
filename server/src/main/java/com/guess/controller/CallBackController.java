package com.guess.controller;

import com.alibaba.fastjson.JSONObject;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;
import com.guess.service.GameService;
import com.guess.service.PayService;
import com.guess.util.XmlUtil;
import java_sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@Scope("prototype")
@RequestMapping("callBack")
public class CallBackController {
    @Autowired
    GameService gameService;
    @Autowired
    PayService payService;

    //下单回调 ##PAY_CHECK##
    @ResponseBody
    @RequestMapping("/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String code = "Failure";
        String msg = "Failure";
        JSONObject detail = XmlUtil.readerXmlToMap(request);
        System.out.println("payNotify:" + JSONObject.toJSONString(detail));
        if (detail != null) {
            String out_trade_no = detail.getString("out_trade_no");
            String return_code = detail.getString("return_code");

            if ("SUCCESS".equals(return_code)) {
                int result_code = detail.getIntValue("result_code");
                int status = 0;
                if (result_code == 0) {

                    status = 1;
                } else {
                    status = 2;
                }
                OrderModel order = gameService.getGamePay(out_trade_no);
                if (order != null) {
                    order.setStatus(status);//本地订单状态,0等待用户支付，1支付成功，2支付失败
                    order.setTransaction_id(detail.getString("transaction_id"));
                    long result = 0;
                    //是否超时回调
                    String round_no = order.getRound_no();
                    GameModel gameDetail = gameService.getGameDetail(round_no);
                    if (gameDetail == null || gameDetail.getStatus() != 0) {
                        result = payService.refund(round_no, order, gameDetail, false);
                    } else {
                        result = gameService.saveGamePay(out_trade_no, order);
                    }
                    if (result > 0) {
                        code = "SUCCESS";
                        msg = "OK";
                    }
                }

            }

        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("return_code", code);
        map.put("return_msg", msg);

        response.setContentType("application/xml;charset=UTF-8");
        response.getWriter().write(WXPayUtil.mapToXml(map));
        response.getWriter().flush();
    }
}
