package com.guess.service;

import com.alibaba.fastjson.JSONObject;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;

import java.util.Map;

public interface GameService {
    //更新轮次信息
    GameModel updateGameRund();

    //获取游戏基础数据
    GameModel getGameDetail(String rund_no);

    //获取用户游戏支付数据
    OrderModel getGamePay(String out_trade_no);

    //保存用户游戏支付数据
    long saveGamePay(String out_trade_no, OrderModel detail);

    //获取每轮游戏用户参与数据
    OrderModel getGameUser(String openid, String round_no);


    GameModel getLastGameDetail();

    String getLastGameRoundNo();

    //获取上一轮数据
    GameModel getUpRoundGameDetail();

}
