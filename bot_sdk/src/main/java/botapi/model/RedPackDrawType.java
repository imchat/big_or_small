package botapi.model;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2019-01-04
 * Time: 13:27
 * <p>
 * **************************************************************
 */
public enum RedPackDrawType {
    LUCK(0, "拼手气"),//0拼手气
    AVG(1, "平均");//1平均

    RedPackDrawType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RedPackDrawType get(int code) {
        for (RedPackDrawType e : RedPackDrawType.values()) {
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
