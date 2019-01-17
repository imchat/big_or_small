package com.guess.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.guess.dao.GameDao;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;
import com.guess.util.GuessUtil;
import com.guess.util.RocksDBUtil;
import com.guess.util.RocksKeys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GameDaoImpl implements GameDao {
    @Override
    public long saveGameList(String round_no, GameModel game) {
        return RocksDBUtil.writeDBToString(RocksKeys.GameListKey, round_no, JSONObject.toJSONString(game));
    }

    @Override
    public GameModel getGameDetail(String round_no) {
        if (!StringUtils.isNotEmpty(round_no)) {
            return null;
        }
        String value = RocksDBUtil.readDBByString(RocksKeys.GameListKey, round_no);
        if (StringUtils.isNotEmpty(value)) {
            return JSONObject.parseObject(value, GameModel.class);
        }
        return null;
    }

    @Override
    public long saveGameRundNo(String round_no, GuessUtil.GAME_ROUND_TYPE type) {
        return RocksDBUtil.writeDBToString(RocksKeys.GameRoundNoKey, type.name(), round_no);
    }

    @Override
    public String getGameRundNo(GuessUtil.GAME_ROUND_TYPE type) {
        return RocksDBUtil.readDBByString(RocksKeys.GameRoundNoKey, type.name());
    }

    @Override
    public long saveGameMonthSequence(String month, JSONObject detail) {
        return RocksDBUtil.writeDB(RocksKeys.GameMonthSequenceKey, month, detail);
    }

    @Override
    public JSONObject getGameMonthSequence(String month) {
        return RocksDBUtil.readDB(RocksKeys.GameMonthSequenceKey, month);
    }

    @Override
    public long saveGamePay(String out_trade_no, OrderModel order) {
        return RocksDBUtil.writeDBToString(RocksKeys.GamePayListKey, out_trade_no, JSONObject.toJSONString(order));
    }

    @Override
    public OrderModel getGamePay(String out_trade_no) {
        if (!StringUtils.isNotEmpty(out_trade_no)) {
            return null;
        }
        String value = RocksDBUtil.readDBByString(RocksKeys.GamePayListKey, out_trade_no);
        if (StringUtils.isNotEmpty(value)) {
            return JSONObject.parseObject(value, OrderModel.class);
        }
        return null;
    }

    @Override
    public long saveGameUserMapping(String round_no, String openid, String out_trade_no) {
        return RocksDBUtil.writeDBToString(String.format(RocksKeys.GameUserMappingKey, round_no), openid, out_trade_no);
    }

    @Override
    public String getGameUserMapping(String round_no, String openid) {
        return RocksDBUtil.readDBByString(String.format(RocksKeys.GameUserMappingKey, round_no), openid);
    }

    @Override
    public List<String> getGameUserMappingList(String round_no) {
        String key = String.format(RocksKeys.GameUserMappingKey, round_no);
        return RocksDBUtil.getListByString(key);
    }


}
