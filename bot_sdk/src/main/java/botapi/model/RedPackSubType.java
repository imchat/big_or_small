package botapi.model;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-27
 * Time: 20:24
 * <p>
 * **************************************************************
 */
public enum RedPackSubType {
    SELF_SHARE(0,"自己分享群友不可转"),//自己分享群友不可转
    FRIEND_SHARE(1,"群友帮分享"),//群友帮分享
    SQUARE(2,"红包广场无需分享");//红包广场无需分享

    RedPackSubType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RedPackSubType get(int code) {
        for (RedPackSubType e : RedPackSubType.values()) {
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
