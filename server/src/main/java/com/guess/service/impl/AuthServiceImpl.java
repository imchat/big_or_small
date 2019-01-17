package com.guess.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guess.dao.AuthDao;
import com.guess.service.AuthService;
import com.guess.util.Config;
import com.guess.util.RocksDBUtil;
import com.guess.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

@Service("AuthService")
public class AuthServiceImpl implements AuthService {
    static final String access_token_url = Config.getImchatUrl() + "/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    static final String reg_token_url = Config.getImchatUrl() + "/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    static final String js_ticket_url = Config.getImchatUrl() + "/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
    static final String auth_user_url = Config.getImchatUrl() + "/sns/userinfo?access_token=%s&openid=%s";
    @Autowired
    AuthDao authDao;

    /**
     * 获取用户OPENID
     *
     * @param code
     * @return
     */
    public JSONObject getOpenid(String code) {
        String url = String.format(access_token_url, Config.getAppId(), Config.getAppSecret(), code);
        String jsonRst = HttpUtil.doGet(url);
        System.err.println("**********getOpenId: " + jsonRst);
        if (StringUtils.isNotEmpty(jsonRst)) {
            JSONObject resultData = JSONObject.parseObject(jsonRst, JSONObject.class);
            if (null != resultData && resultData.getIntValue("errorcode") == 0) {
                return resultData;
            }
        }
        return null;
    }


    // 获取AccessToken
    public String getAccessToken() {

        String accessToken = authDao.getAccessToken(Config.getAppId());
        if (!StringUtils.isNotEmpty(accessToken)) {
            String url = String.format(reg_token_url, Config.getAppId(), Config.getAppSecret());
            String responseString = HttpUtil.doGet(url);
            if (StringUtils.isNotEmpty(responseString)) {
                JSONObject resultData = JSON.parseObject(responseString, JSONObject.class);
                if (resultData != null && resultData.getIntValue("errorcode") == 0) {

                    accessToken = resultData.getString("access_token");
                    if (StringUtils.isNotEmpty(accessToken)) {
                        authDao.saveAccessToken(Config.getAppId(), accessToken);
                        System.out.println("get access_token:" + accessToken);
                    }
                }
            }
        }
        return accessToken;
    }

    // 获取js-tickent
    public String getJsTicket() {
        // 检测token是否过期
        String ticket = authDao.getJsTicket(Config.getAppId());
        if (!StringUtils.isNotEmpty(ticket)) {
            String url = String.format(js_ticket_url, this.getAccessToken());
            String responseString = HttpUtil.doGet(url);
            if (StringUtils.isNotEmpty(responseString)) {
                System.out.println("Got JSAPI ticket = " + responseString);
                JSONObject jsTicket = JSON.parseObject(responseString, JSONObject.class);
                if (jsTicket != null && jsTicket.getIntValue("errorcode") == 0) {
                    ticket = jsTicket.getString("ticket");
                    authDao.saveJsTicket(Config.getAppId(), ticket);
                }
            }
        }
        return ticket;
    }

    //获取用户信息
    public JSONObject getUserInfo(String openid, String access_token) {
        JSONObject userInfo = this.getUserInfo(openid);
        if (userInfo == null) {
            if (!StringUtils.isNotEmpty(access_token)) {
                return null;
            }
            String url = String.format(auth_user_url, access_token, openid);
            String responseString = HttpUtil.doGet(url);
            if (StringUtils.isNotEmpty(responseString)) {
                userInfo = JSON.parseObject(responseString, JSONObject.class);
                if (userInfo != null && userInfo.getIntValue("errorcode") == 0) {
                    authDao.saveUserInfo(openid, userInfo);
                    return userInfo;
                }
            }
        }
        return userInfo;
    }


    // 微信签名
    public Map<String, Object> sign(String jsapi_ticket, String url) {
        Map<String, Object> ret = new HashMap<String, Object>();

        String string1;
        String signature = "";
        String nonce_str = "abcdefghijk";// create_nonce_str();
        String timestamp = create_timestamp();
        // 注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        System.out.println("string1 sign:" + string1);
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

    @Override
    public JSONObject getUserInfo(String openid) {
        return authDao.getUserInfo(openid);
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    //清空授权
    public void clearAuth() {
        String appId = Config.getAppId();
        RocksDBUtil.deleteDB("JsTicket", appId);
        RocksDBUtil.deleteDB("AccessToken", appId);
        return;
    }

}
