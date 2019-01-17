package com.guess.dao;

import com.alibaba.fastjson.JSONObject;

public interface AuthDao {
     String getAccessToken(String appId);
    long saveAccessToken(String appId, String accessToken);
    String getJsTicket(String appId);
    long saveJsTicket(String appId, String ticket);
    JSONObject getUserInfo(String openid);
    long saveUserInfo(String openid, JSONObject userInfo);
}
