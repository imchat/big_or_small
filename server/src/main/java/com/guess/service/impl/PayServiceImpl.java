package com.guess.service.impl;

import botapi.IMChatBot;
import botapi.model.RedPackDrawType;
import botapi.model.RedPackSubType;
import botapi.model.RedPackType;
import botapi.resp.ApiResult;
import botapi.types.Message;
import com.alibaba.fastjson.JSONObject;
import com.guess.dao.AuthDao;
import com.guess.dao.GameDao;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;
import com.guess.service.PayService;
import com.guess.util.Config;
import com.guess.util.GuessUtil;
import java_sdk.IMCConfig;
import java_sdk.WXPay;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("PayService")
public class PayServiceImpl implements PayService {

    @Autowired
    GameDao gameDao;
    @Autowired
    AuthDao authDao;
    IMChatBot imChatBot = new IMChatBot(Config.getImchatUrl(), Config.getBotId(), Config.getBotSecret());
    WXPay wxpay = null;


    public PayServiceImpl() {
        try {
            wxpay = new WXPay(new IMCConfig(Config.getImchatUrl(), Config.getAppId(), Config.getMchId(), Config.getMchSecret()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //统一下单，生成预付单 ##PREPAY##
    @Override
    public OrderModel unifiedOrder(String openid, int amount, int guessType, int productId, String ip) {
        try {
            String round_no = gameDao.getGameRundNo(GuessUtil.GAME_ROUND_TYPE.LastRound);//最新轮次号
            System.out.println("round_no:" + round_no);
            //是否是否已下过单

            String out_trade_no = gameDao.getGameUserMapping(round_no, openid);
            System.out.println("out_trade_no:" + out_trade_no);
            if (!StringUtils.isNotEmpty(out_trade_no)) {
                //活动是否停止
                GameModel gameDetail = gameDao.getGameDetail(round_no);
                System.out.println("gameDetail:" + JSONObject.toJSONString(gameDetail));
                if (gameDetail != null && gameDetail.getStatus() == 0) {//0正在进行中，1停止竞猜，2已结束,3流局

                    Map<String, String> reqData = new HashMap<String, String>();
                    out_trade_no = getUUid();
                    reqData.put("body", "猜大小");
                    reqData.put("out_trade_no", out_trade_no);
                    reqData.put("device_info", "web");
                    reqData.put("fee_type", "FORCE");
                    reqData.put("total_fee", String.valueOf(amount));
                    reqData.put("spbill_create_ip", ip);
                    reqData.put("notify_url", Config.getSiteUrl() + "/callBack/notify");
                    reqData.put("openid", openid);
                    reqData.put("trade_type", "JSAPI");  // 此处指定为扫码支付
                    reqData.put("product_id", String.valueOf(productId));
                    reqData.put("transfer_type", "INSTANT");//支付方式：DELAY延时支付，INSTANT即时支付
                    Map<String, String> resp = wxpay.unifiedOrder(reqData);
                    System.out.println(resp);
                    if (resp != null && resp.get("return_code").equalsIgnoreCase("SUCCESS")) {
                        String prepay_id = resp.get("prepay_id");//预付单号
                        //保存用户订单
                        OrderModel order = new OrderModel();
                        order.setPrepay_id(prepay_id);
                        order.setStatus(0);//本地订单状态,0等待用户支付，1支付成功，2支付失败
                        order.setGuessType(guessType);
                        order.setRound_no(round_no);
                        order.setOut_trade_no(out_trade_no);
                        order.setAmount(new BigDecimal(amount));
                        order.setOpenid(openid);
                        order.setProductId(productId);
                        order.setStatus(0);
                        long result = gameDao.saveGamePay(out_trade_no, order);
                        if (result > 0) {
                            return order;
                        }
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    //退款##PAY_REFUND##
    public long refund(String round_no, OrderModel detail, GameModel gameModel , boolean isFlow) {

        try {
            int guessType = detail.getGuessType();
            String openid = detail.getOpenid();
            BigDecimal amount = detail.getAmount();
            String out_trade_no = detail.getOut_trade_no();
            int userStatus = detail.getStatus();//用户状态
            if (userStatus != 1) {
                return -1;
            }
            Map<String, String> reqData = new HashMap<String, String>();
            String out_refund_no = getUUid();
            reqData.put("appid", Config.getAppId());
            reqData.put("mch_id", Config.getMchId());
            reqData.put("out_refund_no", out_refund_no);
            reqData.put("out_trade_no", out_trade_no);
            reqData.put("refund_fee", amount.stripTrailingZeros().toPlainString());
            reqData.put("refund_fee_type", "FORCE");
            Map<String, String> reposeData = wxpay.refund(reqData);
            System.out.println("refund reposeData:" + JSONObject.toJSONString(reposeData));
            if (reposeData != null && reposeData.get("return_code").equalsIgnoreCase("SUCCESS")) {
                detail.setStatus(4);
                gameDao.saveGamePay(out_trade_no, detail);
                //退款消息
                JSONObject user = authDao.getUserInfo(openid);
                if (user == null) {
                    user = new JSONObject();
                }
                String nickName = user.getString("nickname");
                String message = "";
                if (isFlow) {
                    message = "@" + nickName + " " + gameModel.getMonth()+"#"+gameModel.getSequence() + "期流局，退款：" + amount.stripTrailingZeros().toPlainString() + " FORCE";
                } else {
                    message = "@" + nickName + " " +  gameModel.getMonth()+"#"+gameModel.getSequence()+ "期已停止竞猜，退款：" + amount.stripTrailingZeros().toPlainString() + " FORCE";
                }
                Set<String> at = new HashSet<>();
                at.add(openid);
                imChatBot.sendMessage(Config.getGroupId(), message, at);

                return 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;

    }

    //用机器人向群里发送消息 ##TEXT_MESSAGE##
    @Override
    public boolean sendMessage(String message, String openid) {
        try {
            Set<String> at = null;
            if (StringUtils.isNotEmpty(openid)) {
                at = new HashSet<>();
                at.add(openid);
            }
            ApiResult<Message> result = imChatBot.sendMessage(Config.getGroupId(), message, at);
            if (result != null && result.getErrorCode() == 0) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //使用机器人发红包 ##RED_PACKAGE##
    @Override
    public boolean sendRedPack(String rewardName, String coinType, BigDecimal rewardAmount, int count, float rate, RedPackType rewardType, RedPackSubType rewardSubType, RedPackDrawType drawType, String receiveOpenid) {
        try {
            ApiResult result = imChatBot.sendRedPack(Config.getGroupId(), rewardName, coinType, rewardAmount, count, rate, rewardType, rewardSubType, drawType, receiveOpenid);
            System.out.println("sendRedPack:" + JSONObject.toJSONString(result));
            if (result.getErrorCode() == 0) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //商户收款
    @Override
    public boolean receiveOrder(String out_trade_no) {
        try {
            Map<String, String> reqData = new HashMap<String, String>();
            reqData.put("out_trade_no", out_trade_no);
            Map<String, String> resp = wxpay.receiveOrder(reqData);
            System.out.println(resp);
            if (resp != null) {
                JSONObject order = JSONObject.parseObject(JSONObject.toJSONString(resp));
                if (order != null) {
                    if (order.getString("return_code").equalsIgnoreCase("SUCCESS")) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String getUUid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
