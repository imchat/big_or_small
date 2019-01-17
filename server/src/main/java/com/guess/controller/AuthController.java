package com.guess.controller;

import com.alibaba.fastjson.JSONObject;
import com.guess.service.AuthService;
import com.guess.util.Config;
import java_sdk.WXPayConstants;
import java_sdk.WXPayUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@Scope("prototype")
@RequestMapping("auth")
public class AuthController extends BaseController {
    @Autowired
    AuthService authService;

    //通过code获取用户信息 ##WEB_AUTH##
    @ResponseBody
    @RequestMapping("userInfo")
    public JSONObject userInfo() {
        JSONObject resultmap = new JSONObject();
        int statusCode = 1;//0成功，1失败
        String mess = "获取用户信息失败";
        String code = this.params.getString("code");

        if (StringUtils.isNotEmpty(code)) {
            try {
                System.out.println("userInfo code:" + code);
                JSONObject data = authService.getOpenid(code);
                if (data != null) {
                    String openid = data.getString("openid");
                    String access_token = data.getString("access_token");
                    JSONObject userInfo = authService.getUserInfo(openid, access_token);
                    if (userInfo != null) {
                        statusCode = 0;
                        mess = "获取用户信息成功";
                        resultmap.putAll(userInfo);

                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        resultmap.put("mess", mess);
        //System.out.println("resultmap:" + resultmap.toJSONString());
        return this.returnMsg(statusCode, mess, resultmap);
    }

    // 获取js签名信息
    @ResponseBody
    @RequestMapping(value = "/jsSign")
    public JSONObject jsSign() {
        JSONObject resultmap = new JSONObject();
        int statusCode = 1;
        String mess = "js签名失败";
        // 当前请求地址，用于WXJS-SDK加密
        String url = this.params.getString("url");
        if (StringUtils.isNotEmpty(url)) {
            // 获取jsapiticke
            String jsTicket = authService.getJsTicket();
            if (StringUtils.isNotEmpty(jsTicket)) {
                statusCode = 0;
                mess = "js签名成功";
                // 初始化wxjs-SDK
                JSONObject sign = new JSONObject();
                Map<String, Object> tickeMap = authService.sign(jsTicket, url);
                // resultmap.put("url", tickeMap.get("url"));
                // resultmap.put("jsapi_ticket",
                // tickeMap.get("jsapi_ticket"));
                sign.put("nonceStr", tickeMap.get("nonceStr"));
                sign.put("timestamp", tickeMap.get("timestamp"));
                sign.put("signature", tickeMap.get("signature"));
                sign.put("appid", Config.getAppId());

                resultmap.put("ticket", sign);
            }

        }
        //System.out.println("resultmap:" + resultmap.toJSONString());
        return this.returnMsg(statusCode, mess, resultmap);
    }


    //支付签名
    @ResponseBody
    @RequestMapping(value = "paySign")
    public JSONObject paySign() {
        int statusCode = 1;
        String mess = "支付签名失败";
        JSONObject resultmap = new JSONObject();
        String nonce_str = "abcdefghijk";
        String timestamp = create_timestamp();
        String body = this.params.getString("package");
        WXPayConstants.SignType sign_type = WXPayConstants.SignType.MD5;
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put("appId", Config.getAppId());
        reqData.put("package", body);
        reqData.put("nonceStr", nonce_str);
        reqData.put("timeStamp", timestamp);
        reqData.put("signType", sign_type.name());
        System.out.println("reqData:" + reqData.toString());
        try {
            resultmap.put("appId", Config.getAppId());
            resultmap.put("nonceStr", nonce_str);
            resultmap.put("timeStamp", timestamp);
            resultmap.put("signType", sign_type.name());
            resultmap.put("paySign", WXPayUtil.generateSignature(reqData, Config.getMchSecret(), sign_type));
            statusCode = 0;
            mess = "支付签名成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("resultmap:" + resultmap.toJSONString());
        return this.returnMsg(statusCode, mess, resultmap);
    }


    private String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    //清空授权
    @ResponseBody
    @RequestMapping(value = "clearAuth")
    public JSONObject clearAuth() {
        JSONObject resultmap = new JSONObject();
        int statusCode = 1;
        String mess = "清空授权成功";
        authService.clearAuth();
        // System.out.println("resultmap:" + resultmap.toJSONString());
        return this.returnMsg(statusCode, mess, resultmap);
    }

}
