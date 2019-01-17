package botapi;

import botapi.model.RedPackDrawType;
import botapi.model.RedPackSubType;
import botapi.model.RedPackType;
import botapi.resp.ApiResult;
import botapi.types.Chat;
import botapi.types.Message;
import botapi.types.User;
import botapi.utils.HttpClient;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-29
 * Time: 18:05
 * <p>
 * **************************************************************
 */
public class IMChatBot {
    private String token;
    private String site = "http://open.imchat.com";

    public IMChatBot(String appId, String appSecret) {
        this.token = appId + ":" + appSecret;
    }

    public IMChatBot(String site, String appId, String appSecret) {
        this.site = site;
        this.token = appId + ":" + appSecret;
    }

    /**
     * 机器人所在的群ID
     *
     * @return
     * @throws Exception
     */
    public ApiResult<List<Chat>> getChats() throws Exception {
        JSONObject req = new JSONObject();
        String s = HttpClient.post(getApi("getChats"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.CHATS);
    }

    /**
     * 机器人信息
     *
     * @return
     * @throws Exception
     */
    public ApiResult<User> getMe() throws Exception {
        JSONObject req = new JSONObject();
        String s = HttpClient.post(getApi("getMe"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.ME);
    }

    /**
     * 接收消息
     *
     * @return
     * @throws Exception
     */
    public ApiResult<List<Message>> getUpdates(int limit, long timeout) throws Exception {
        JSONObject req = new JSONObject();
        req.put("limit", limit);
        req.put("timeout", timeout);
        String s = HttpClient.post(getApi("getUpdates"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.LIST_OF_UPDATES);
    }

    /**
     * 发送文本消息
     *
     * @param text
     * @param at
     * @return
     * @throws Exception
     */
    public ApiResult<Message> sendMessage(String chatId, String text, Set<String> at) throws Exception {
        JSONObject req = new JSONObject();
        req.put("chat_id", chatId);
        req.put("text", text);
        req.put("at", at);
        String s = HttpClient.post(getApi("sendMessage"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.MESSAGE);
    }

    /**
     * 发送图片
     *
     * @param file 图片文件
     * @return
     * @throws Exception
     */
    public ApiResult<Message> sendPhoto(String chatId, File file) throws Exception {
        String photo = upload(file);
        if (StringUtils.isEmpty(photo)) {
            return null;
        }
        JSONObject req = new JSONObject();
        req.put("chat_id", chatId);
        req.put("photo", photo);
        String s = HttpClient.post(getApi("sendPhoto"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.MESSAGE);
    }

    /**
     * 发送图片
     *
     * @param photo 图片url
     * @return
     * @throws Exception
     */
    public ApiResult<Message> sendPhoto(String chatId, String photo) throws Exception {
        JSONObject req = new JSONObject();
        req.put("chat_id", chatId);
        req.put("photo", photo);
        String s = HttpClient.post(getApi("sendPhoto"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.MESSAGE);
    }

    /**
     * 发送link
     *
     * @param chatId
     * @param title
     * @param desc
     * @param document
     * @param thumb
     * @return
     * @throws Exception
     */
    public ApiResult<Message> sendDocument(String chatId, String title, String desc, String document, String thumb) throws Exception {
        JSONObject req = new JSONObject();
        req.put("chat_id", chatId);
        req.put("title", title);
        req.put("desc", desc);
        req.put("thumb", thumb);
        req.put("document", document);
        String s = HttpClient.post(getApi("sendDocument"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.MESSAGE);
    }

    /**
     * 发红包
     *
     * @param name     红包名称
     * @param coinType 币种
     * @param amount   金额
     * @param count    数量
     * @param rate     邀请奖励比例
     * @param type     红包类型,101拼手气，102普通红包，103个人红包,104拉人拼手气，105拉人普通红包,106新手红包，107公号红包
     * @param subType  红包子类型,1自已分享群友不可转，2群友帮分享，3红包广场无需分享
     * @param drawType 红包领取类型,//0拼手气，1平均
     * @param receive  红包接收人
     * @return
     */
    public ApiResult<Message> sendRedPack(String chatId, String name, String coinType, BigDecimal amount, int count, float rate, RedPackType type, RedPackSubType subType, RedPackDrawType drawType, String receive) throws Exception {
        JSONObject req = new JSONObject();
        req.put("chat_id", chatId);
        req.put("name", name);
        req.put("coinType", coinType);
        req.put("amount", amount);
        req.put("count", count);
        req.put("rate", rate);
        req.put("type", type.getCode());
        req.put("subType", subType.getCode());
        req.put("drawType", drawType.getCode());
        req.put("receive", receive);
        String s = HttpClient.post(getApi("sendRedPack"), req.toJSONString());
        return getResult(s, ApiResult.ResultTypes.MESSAGE);
    }

    /**
     * 文件上传
     *
     * @param filePath 本地路径
     * @return
     * @throws Exception
     */
    public String upload(String filePath) throws Exception {
        String s = HttpClient.postMultipart(getApi("upload"), null, filePath);
        ApiResult result = getResult(s, ApiResult.ResultTypes.UPLOAD);
        if (result.isOk() && result.getErrorCode() == 0) {
            return result.getResult().toString();
        }
        return "";
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String upload(File file) throws Exception {
        String s = HttpClient.postMultipart(getApi("upload"), null, file);
        ApiResult result = getResult(s, ApiResult.ResultTypes.UPLOAD);
        if (result.isOk() && result.getErrorCode() == 0) {
            return result.getResult().toString();
        }
        return "";
    }

    private ApiResult getResult(String res, ApiResult.ResultTypes resultType) {
        try {
            if (StringUtils.isNotEmpty(res)) {
                ApiResult result = JSONObject.parseObject(res, resultType.getType());
                return result;
            }
        } catch (Exception ex) {

        }
        return ApiResult.build(false, "Error", 0, null);
    }

    /**
     * 获取Token
     *
     * @param method 接口名称
     * @return
     */
    private String getApi(String method) {
        return String.format(site + "/message/" + token + "/" + method);
    }
}
