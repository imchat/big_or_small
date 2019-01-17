package com.guess.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    static {
        initConfig();
    }

    public static String appId;
    public static String appSecret;
    public static String mchId;
    public static String mchSecret;
    public static String imchatUrl;
    public static String siteUrl;
    public static String botId;
    public static String botSecret;
    public static String groupId;
    public static String domain;
    //竞猜时间(分钟)
    private static int gameIntervalTime ;
    //出块数量
    private static int gameBlockSize;
    //两轮间隔时间
    private static int roundIntervalTime;

    public static String getAppId() {
        return appId;
    }

    public static void setAppId(String appId) {
        Config.appId = appId;
    }

    public static String getAppSecret() {
        return appSecret;
    }

    public static void setAppSecret(String appSecret) {
        Config.appSecret = appSecret;
    }

    public static String getMchId() {
        return mchId;
    }

    public static void setMchId(String mchId) {
        Config.mchId = mchId;
    }

    public static String getMchSecret() {
        return mchSecret;
    }

    public static void setMchSecret(String mchSecret) {
        Config.mchSecret = mchSecret;
    }

    public static String getImchatUrl() {
        return imchatUrl;
    }

    public static void setImchatUrl(String imchatUrl) {
        Config.imchatUrl = imchatUrl;
    }

    public static String getSiteUrl() {
        return siteUrl;
    }

    public static void setSiteUrl(String siteUrl) {
        Config.siteUrl = siteUrl;
    }

    public static String getBotId() {
        return botId;
    }

    public static void setBotId(String botId) {
        Config.botId = botId;
    }

    public static String getBotSecret() {
        return botSecret;
    }

    public static void setBotSecret(String botSecret) {
        Config.botSecret = botSecret;
    }

    public static String getGroupId() {
        return groupId;
    }

    public static void setGroupId(String groupId) {
        Config.groupId = groupId;
    }

    public static int getGameIntervalTime() {
        return gameIntervalTime;
    }

    public static void setGameIntervalTime(int gameIntervalTime) {
        Config.gameIntervalTime = gameIntervalTime;
    }

    public static int getGameBlockSize() {
        return gameBlockSize;
    }

    public static void setGameBlockSize(int gameBlockSize) {
        Config.gameBlockSize = gameBlockSize;
    }

    public static int getRoundIntervalTime() {
        return roundIntervalTime;
    }

    public static void setRoundIntervalTime(int roundIntervalTime) {
        Config.roundIntervalTime = roundIntervalTime;
    }

    public static String getDomain() {
        return domain;
    }

    public static void setDomain(String domain) {
        Config.domain = domain;
    }

    private static void initConfig() {
        Properties prop = new Properties();
        try {
            InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties");
            if (in == null) {
                String url = "./config.properties";
                in = new BufferedInputStream(new FileInputStream(url));
            }
            prop.load(in);

            if (prop.containsKey("appId")) {
                Config.setAppId(prop.getProperty("appId"));
            }
            if (prop.containsKey("appSecret")) {
                Config.setAppSecret(prop.getProperty("appSecret"));
            }
            if (prop.containsKey("mchId")) {
                Config.setMchId(prop.getProperty("mchId"));
            }
            if (prop.containsKey("mchSecret")) {
                Config.setMchSecret(prop.getProperty("mchSecret"));
            }
            if (prop.containsKey("siteUrl")) {
                Config.setSiteUrl(prop.getProperty("siteUrl"));
            }
            if (prop.containsKey("imchatUrl")) {
                Config.setImchatUrl(prop.getProperty("imchatUrl"));
            }
            if (prop.containsKey("botId")) {
                Config.setBotId(prop.getProperty("botId"));
            }
            if (prop.containsKey("botSecret")) {
                Config.setBotSecret(prop.getProperty("botSecret"));
            }
            if (prop.containsKey("groupId")) {
                Config.setGroupId(prop.getProperty("groupId"));
            }
            if (prop.containsKey("gameIntervalTime")) {
                Config.setGameIntervalTime(Integer.valueOf(prop.getProperty("gameIntervalTime")));
            }
            if (prop.containsKey("gameBlockSize")) {
                Config.setGameBlockSize(Integer.valueOf(prop.getProperty("gameBlockSize")));
            }
            if (prop.containsKey("roundIntervalTime")) {
                Config.setRoundIntervalTime(Integer.valueOf(prop.getProperty("roundIntervalTime")));
            }
            if (prop.containsKey("domain")) {
                Config.setDomain(prop.getProperty("domain"));
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
