package botapi.model;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-28
 * Time: 19:06
 * <p>
 * **************************************************************
 */
public enum MessageType {
    Text(1,"文本"),
    NewChatMembers(9,"入群");

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MessageType get(int code) {
        for (MessageType e : MessageType.values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
