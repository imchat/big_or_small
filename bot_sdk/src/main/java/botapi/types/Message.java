package botapi.types;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-29
 * Time: 17:47
 * <p>
 * **************************************************************
 */
public class Message {
    //消息类型
    private int type;
    //消息ID
    @JSONField(name = "message_id")
    private String messageId;
    //发送人
    private User from;
    //发送时间
    private long date;
    //群信息
    private Chat chat;
    //@
    private List<MessageEntity> entities;
    //文本内容
    private String text;
    //图片内容
    private Photo photo;
    //新增入群
    @JSONField(name = "new_chat_members")
    private List<User> newChatMembers;
    //文件内容
    private File file;
    //红包
    @JSONField(name = "redpack")
    private RedPack redPack;
    //图文
    private Document document;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String message_id) {
        this.messageId = message_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<User> getNewChatMembers() {
        return newChatMembers;
    }

    public void setNewChatMembers(List<User> newChatMembers) {
        this.newChatMembers = newChatMembers;
    }

    public List<MessageEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<MessageEntity> entities) {
        this.entities = entities;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public RedPack getRedPack() {
        return redPack;
    }

    public void setRedPack(RedPack redPack) {
        this.redPack = redPack;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
