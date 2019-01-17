package com.guess.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author sailor
 * @comment https无证书调用api工具类
 */
public class HttpUtil {

    // static Log logger = LogFactory.getLog(HttpsUtil.class);
    /**
     * 忽视证书HostName
     */
    private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
        public boolean verify(String s, SSLSession sslsession) {
            return true;
        }
    };
    /**
     * Ignore Certification
     */
    private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

        private X509Certificate[] certificates;

        public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = certificates;
            }
        }

        public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
            }

        }

        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    };

    /**
     * 调用https api 获取报文
     */
    public static String doGet(String url) {
        StringBuffer response = new StringBuffer();
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(url);
            method.addRequestHeader("Content-Type", "application/json;charset=" + "UTF-8");

            int status = client.executeMethod(method);
            System.out.println("code:" + status);
            if (status == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        return response.toString();
    }

    public String getData(String url) {
        StringBuffer response = new StringBuffer();
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(url);
            method.addRequestHeader("Content-Type", "application/json;charset=" + "UTF-8");

            int status = client.executeMethod(method);
            System.out.println("code:" + status);
            if (status == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        return response.toString();
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的数据
     *
     * @param url     请求的URL地址
     * @param params  请求的查询参数,可以为null
     * @param charset 字符集
     * @param pretty  是否美化
     * @return 返回请求响应的HTML
     */
    public static String doPost(String url, Map<String, String> params, String charset, boolean pretty) {
        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();
        HttpMethod method = new PostMethod(url);
        // 设置Http Post数据
        // 本地测试中发现此方式可能会出现传参无法成功，暂时不改，若出现Bug,替代方案详见ESunAction中的doPost方法
        if (params != null) {
            HttpMethodParams p = new HttpMethodParams();
            for (Entry<String, String> entry : params.entrySet()) {
                System.err.println("key: " + entry.getKey() + " val: " + entry.getValue());
                p.setParameter(entry.getKey(), entry.getValue());
            }
            method.setParams(p);

        }
        try {
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (pretty)
                        response.append(line).append(System.getProperty("line.separator"));
                    else
                        response.append(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            // logger.error("执行HTTP Post请求" + url + "时，发生异常！", e);
        } finally {
            method.releaseConnection();
        }
        return response.toString();
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String doPost(String url, NameValuePair[] params, String charSet) {
        // Post请求的url
        try {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(url);
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charSet);// 在头文件中设置转码
            post.setRequestBody(params);
            client.executeMethod(post);
            Header[] headers = post.getResponseHeaders();
            int statusCode = post.getStatusCode();
            System.out.println("statusCode:" + statusCode);

            String result = new String(post.getResponseBodyAsString().getBytes(charSet));
            System.out.println(result); // 打印返回消息状态
            post.releaseConnection();
            return result;
        } catch (Exception e) {
            // logger.error(null, e);
        }
        return null;
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String params) {
        StringBuffer response = new StringBuffer();
        try {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(url);
            post.setRequestEntity(new StringRequestEntity(params, "application/json", "UTF-8"));

            int status = client.executeMethod(post);
            System.out.println("code:" + status);
            if (status == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (Exception ex) {
            // logger.error(null, ex);
        } finally {
        }
        return response.toString();
    }

    public String postData(String url, String params) {
        StringBuffer response = new StringBuffer();
        try {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(url);
            post.setRequestEntity(new StringRequestEntity(params, "application/json", "UTF-8"));

            int status = client.executeMethod(post);
            System.out.println("code:" + status);
            if (status == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // logger.error(null, ex);
        } finally {
        }
        return response.toString();
    }

    /* 发送Post请求 */
    public static String doPost(String url, Object body) {
        // post请求
        String par = JSON.toJSONString(body);
        return doPost(url, par);
    }

    /* 发送Post请求 */
    public static String doPost(String url, Object body, Map<String, Object> params) {
        // post请求
        String par = JSON.toJSONString(params);
        if (params == null) {
            return doPost(url, par);
        }
        url = getCombiUrl(url, params);
        return doPost(url, par);
    }

    /* 发送Get请求 */
    public static String doGet(String url, Map<String, Object> params) {
        if (params == null) {
            return doGet(url);
        }

        return doGet(getCombiUrl(url, params));
    }

    // 组合地址
    private static String getCombiUrl(String url, Map<String, Object> params) {
        // post请求
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (null != params) {
            for (Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                try {
                    String value = entry.getValue().toString();
                    if (index == 0) {
                        sb.append(key + "=" + value);
                    } else {
                        sb.append("&" + key + "=" + value);
                    }
                } catch (Exception e) {
                    // logger.error(null, e);
                }
                index++;
            }
        }
        return url + "?" + sb.toString();
    }


    public static String getIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            System.out.println("X-Forwarded-For:" + ip);
            ip = ip.split("\\,")[0];
            return ip;
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        System.out.println("getIpAddress:" + ip);
        return ip;
    }






}
