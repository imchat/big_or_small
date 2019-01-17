package com.guess.dao;

import com.alibaba.fastjson.JSONObject;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;
import com.guess.util.GuessUtil;

import java.util.List;

public interface GameDao {
    //保存每轮游戏信息
    long saveGameList(String round_no, GameModel game);

    //获取游戏信息
    GameModel getGameDetail(String round_no);

    //保存游戏的轮次号
    long saveGameRundNo(String round_no, GuessUtil.GAME_ROUND_TYPE type);

    //获取游戏轮次号
    String getGameRundNo(GuessUtil.GAME_ROUND_TYPE type);

    //保存当前月和轮次号信息
    long saveGameMonthSequence(String month, JSONObject detail);

    //保存当前月和轮次号信息
    JSONObject getGameMonthSequence(String month);

    //保存用户下单信息
    long saveGamePay(String out_trade_no, OrderModel order);

    //获取用户下单信息
    OrderModel getGamePay(String out_trade_no);

    //保存用户支付成功
    long saveGameUserMapping(String round_no, String openid, String out_trade_no);

    //获取用户支付信息
    String getGameUserMapping(String round_no, String openid);

    //获取用户支付列表
    List<String> getGameUserMappingList(String round_no);
}
