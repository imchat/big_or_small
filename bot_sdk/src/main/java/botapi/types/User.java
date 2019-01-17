package botapi.types;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-29
 * Time: 17:55
 * <p>
 * **************************************************************
 */
public class User {
    private String id;
    private String avatar;
    @JSONField(name = "username")
    private String userName;
    @JSONField(name = "nickname")
    private String nickName;
    @JSONField(name = "is_bot")
    private boolean isBot;
    @JSONField(name = "first_name")
    private String firstName;
    @JSONField(name = "lastName")
    private String lastName;
    @JSONField(name = "language_code")
    private String languageCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
