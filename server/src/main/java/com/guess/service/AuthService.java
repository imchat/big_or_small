package com.guess.service;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface AuthService {
    JSONObject getOpenid(String code);

    String getJsTicket();

    JSONObject getUserInfo(String openid, String access_token);

    Map<String, Object> sign(String jsapi_ticket, String url);

    //获取本地用户信息
    JSONObject getUserInfo(String openid);
     void clearAuth();
}
