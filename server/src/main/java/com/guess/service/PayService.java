package com.guess.service;

import botapi.model.RedPackDrawType;
import botapi.model.RedPackSubType;
import botapi.model.RedPackType;
import com.alibaba.fastjson.JSONObject;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;
import com.guess.util.Config;

import java.math.BigDecimal;

public interface PayService {
    OrderModel unifiedOrder(String openid, int amount, int guessType, int productId, String ip);

    //商户自动收款
    boolean receiveOrder(String out_trade_no);

    //退款
    long refund(String round_no, OrderModel detail, GameModel gameModel ,boolean isFlow);

    //发送消息
    boolean sendMessage(String message, String openid);
    //发送红包
    boolean sendRedPack(String  rewardName, String coinType, BigDecimal rewardAmount, int  count, float rate, RedPackType rewardType , RedPackSubType rewardSubType , RedPackDrawType drawType,String receiveOpenid);
}
