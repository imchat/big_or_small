package java_sdk;

import java.io.InputStream;
import java.net.URL;

public class IMCConfig extends WXPayConfig {
    String appid;
    String mchid;
    String appkey;
    String domain;
//
//    public IMCConfig(String appId, String mchId, String key) {
//        this.appid = appId;
//        this.mchid = mchId;
//        this.appkey = key;
//        this.domain = "open.imchat.com";
//    }

    public IMCConfig(String imchatHost, String appId, String mchId, String key) {
        try {
            this.appid = appId;
            this.mchid = mchId;
            this.appkey = key;
            URL url = new URL(imchatHost);
            this.domain = url.getHost();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    String getAppID() {
        return appid;
    }

    @Override
    String getMchID() {
        return mchid;
    }

    @Override
    String getKey() {
        return appkey;
    }

    @Override
    InputStream getCertStream() {
        return null;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return new IMCPayDomain(this.domain);
    }
}
