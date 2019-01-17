package com.guess.controller;/**
 * Created by wangconghua on 2018/1/15.
 */

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangconghua
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2018/1/15
 */
public class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    public JSONObject params;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) JSONObject data) {

        this.request = request;
        this.response = response;
        if (data == null) {
            data = new JSONObject();
        }

        System.out.println("url:" + request.getRequestURI());
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null && request.getParameterMap().size() > 0) {
            try {
                for (Map.Entry<String, String[]> item : parameterMap.entrySet()) {
                    data.put(item.getKey(), item.getValue()[0]);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.params = JSONObject.parseObject(JSONObject.toJSONString(data));
        System.out.println("params:" + JSONObject.toJSONString(this.params));

    }

    public JSONObject returnMsg(int errorCode, String errorMsg, JSONObject returnObj) {
        if (null == returnObj)
            returnObj = new JSONObject();

        if (errorCode > 0 && errorCode != 200) {
            returnObj.put("mess", errorMsg);
            response.setHeader("ErrorCode", String.valueOf(errorCode));
            response.setHeader("Access-Control-Expose-Headers", "ErrorCode,X-ServerTime");

        } else {
            response.setHeader("Access-Control-Expose-Headers", "X-ServerTime");

        }
        returnObj.put("code", errorCode);
        return returnObj;
    }


}
