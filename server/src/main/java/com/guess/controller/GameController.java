package com.guess.controller;

import com.alibaba.fastjson.JSONObject;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;
import com.guess.service.AuthService;
import com.guess.service.GameService;
import com.guess.service.PayService;
import com.guess.util.*;
import java_sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Controller
@Scope("prototype")
@RequestMapping("game")
public class GameController extends BaseController {
    @Autowired
    GameService gameService;
    @Autowired
    AuthService authService;
    @Autowired
    PayService payService;
    //产品选项
    static Map<Integer, JSONObject> productMap = new HashMap<Integer, JSONObject>() {
        {
            put(1, new JSONObject() {{
                put("amount", 5);
                put("guessType", 2);//中奖类型，0是小，1是大
            }});

            put(2, new JSONObject() {{
                put("amount", 5);
                put("guessType", 1);
            }});
            put(3, new JSONObject() {{
                put("amount", 10);
                put("guessType", 2);
            }});
            put(4, new JSONObject() {{
                put("amount", 10);
                put("guessType", 1);
            }});
            put(5, new JSONObject() {{
                put("amount", 15);
                put("guessType", 2);
            }});
            put(6, new JSONObject() {{
                put("amount", 15);
                put("guessType", 1);
            }});
        }
    };

    //获取游戏信息
    @ResponseBody
    @RequestMapping("detail")
    public JSONObject detail() {
        String openid = this.params.getString("openid");
        JSONObject resultmap = new JSONObject();
        int statusCode = 1;//0成功，1失败
        String mess = "获取游戏信息失败";
        try {
            GameModel gameInfo = gameService.getLastGameDetail();
            if (gameInfo != null) {
                statusCode = 0;
                gameInfo.setCurrTime(System.currentTimeMillis());
                resultmap.put("gameInfo", gameInfo);
                //获取我的下单信息
                String round_no = gameInfo.getRound_no();
                JSONObject myInfo = authService.getUserInfo(openid);
                if (myInfo != null) {
                    OrderModel gameUser = gameService.getGameUser(openid, round_no);
                    if (gameUser != null) {
                        myInfo.putAll(JSONObject.parseObject(JSONObject.toJSONString(gameUser)));
                    } else {
                        myInfo.put("status", 0);
                    }
                }
                resultmap.put("myInfo", myInfo);
                //获取上一轮数据
                GameModel upRound = gameService.getUpRoundGameDetail();
                if (upRound != null) {
                    resultmap.put("upRound", upRound);
                }
                mess = "获取游戏信息成功";

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // System.out.println("resultmap:" + resultmap.toJSONString());
        return this.returnMsg(statusCode, mess, resultmap);
    }

    //统一下单
    @ResponseBody
    @RequestMapping("unifiedOrder")
    public JSONObject unifiedOrder() {
        JSONObject resultmap = new JSONObject();
        int statusCode = 1;//0成功，1失败
        String mess = "统一下单失败";
        try {

            String openid = this.params.getString("openid");
            int productId = this.params.getIntValue("productId");
            JSONObject product = productMap.get(productId);
            if (product != null) {
                int amount = product.getIntValue("amount");
                int guessType = product.getIntValue("guessType");//中奖类型，0是小，1是大
                String ip = HttpUtil.getIpAddress(request);
                OrderModel order = payService.unifiedOrder(openid, amount, guessType, productId, ip);
                if (order != null) {
                    statusCode = 0;//0成功，1失败
                    mess = "统一下单成功";
                    resultmap.putAll(JSONObject.parseObject(JSONObject.toJSONString(order)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        System.out.println("resultmap:" + resultmap.toJSONString());
        return this.returnMsg(statusCode, mess, resultmap);
    }


    //确认下单,待确认 ##PAY_CALLBACK##
    @ResponseBody
    @RequestMapping("confirmOrder")
    public JSONObject confirmOrder() {
        JSONObject resultmap = new JSONObject();
        int statusCode = 1;//0成功，1失败
        String mess = "确认下单失败";
        try {
            String out_trade_no = this.params.getString("out_trade_no");
            OrderModel order = gameService.getGamePay(out_trade_no);
            if (order != null) {
                int status = order.getStatus();
                if (status == 0) {
                    order.setStatus(6);//状态：0未参与，1等待公布中奖，2已中奖未发送，3未中奖,4已退款,5已中奖已发送,6已支付等待确认，
                    gameService.saveGamePay(out_trade_no, order);

                }

            }
            statusCode = 0;//0成功，1失败
            mess = "确认下单成功";

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        resultmap.put("mess", mess);
        System.out.println("resultmap:" + resultmap.toJSONString());
        return this.returnMsg(statusCode, mess, resultmap);
    }

}
