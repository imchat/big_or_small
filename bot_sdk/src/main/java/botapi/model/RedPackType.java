package botapi.model;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-27
 * Time: 20:21
 * <p>
 * **************************************************************
 */
public enum RedPackType {
    Personal(101, "点对点红包"),//点对点红包
    Common(102, "普通红包"),//普通红包
    Share(103, "分享红包"),//分享红包
    PublicPersonal(104, "公号点对点红包"), //公号点对点红包
    PublicCommon(105, "公号普通红包"),//公号普通红包
    PublicShare(106, "公号拉人"),//公号拉人
    RegisterShare(107, "新注册用户拉人");//新注册用户拉人

    RedPackType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RedPackType get(int code) {
        for (RedPackType e : RedPackType.values()) {
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
