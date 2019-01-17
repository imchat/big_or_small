package com.guess.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.guess.dao.AuthDao;
import com.guess.util.RocksDBUtil;
import com.guess.util.RocksKeys;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class AuthDaoImpl implements AuthDao {
    private static int EXPIRE_TIME = 3600;

    //获取AccessToken
    public String getAccessToken(String appId) {
        JSONObject data = RocksDBUtil.readDB(RocksKeys.AccessTokenKey, appId);
        if (data != null) {
            long expireTime = data.getLongValue("expireTime");
            if (System.currentTimeMillis() >= expireTime) {
                RocksDBUtil.deleteDB(RocksKeys.AccessTokenKey, appId);
                return null;
            }
            return data.getString("accessToken");
        }
        return null;
    }

    //把AccessToken保存到本地
    public long saveAccessToken(String appId, String accessToken) {
        if (!StringUtils.isNotEmpty(accessToken)) {
            return -1;
        }
        JSONObject data = new JSONObject();
        data.put("accessToken", accessToken);
        data.put("expireTime", DateUtils.addSeconds(new Date(), EXPIRE_TIME).getTime());
        return RocksDBUtil.writeDB(RocksKeys.AccessTokenKey, appId, data);
    }

    //从本地获取JsTicket
    public String getJsTicket(String appId) {
        JSONObject data = RocksDBUtil.readDB(RocksKeys.JsTicketKey, appId);
        System.out.println("getJsTicket:" + JSONObject.toJSONString(data));
        if (data != null) {
            long expireTime = data.getLongValue("expireTime");
            if (System.currentTimeMillis() >= expireTime) {
                RocksDBUtil.deleteDB(RocksKeys.JsTicketKey, appId);
                return null;
            }
            return data.getString("ticket");
        }
        return null;
    }

    //把JsTicket保存到本地
    public long saveJsTicket(String appId, String ticket) {
        if (!StringUtils.isNotEmpty(ticket)) {
            return -1;
        }
        JSONObject data = new JSONObject();
        data.put("ticket", ticket);
        data.put("expireTime", DateUtils.addSeconds(new Date(), EXPIRE_TIME).getTime());
        return RocksDBUtil.writeDB(RocksKeys.JsTicketKey, appId, data);
    }

    //获取本地用户信息
    public JSONObject getUserInfo(String openid) {
        if (!StringUtils.isNotEmpty(openid)) {
            return null;
        }
        return RocksDBUtil.readDB(RocksKeys.UserListKey, openid);

    }

    //保存到本地用户信息
    public long saveUserInfo(String openid, JSONObject userInfo) {
        return RocksDBUtil.writeDB(RocksKeys.UserListKey, openid, userInfo);
    }
}
