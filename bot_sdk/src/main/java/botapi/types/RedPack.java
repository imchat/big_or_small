package botapi.types;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2019-01-03
 * Time: 13:15
 * <p>
 * **************************************************************
 */
public class RedPack {
    private String id;
    private User sender;
    @JSONField(name = "coin_type")
    private String coinType;
    @JSONField(name = "coin_image")
    private String coinImage;
    @JSONField(name = "coin_share_image")
    private String coinShareImage;
    private String name;
    private Integer type;
    @JSONField(name = "sub_type")
    private Integer subType;
    private Float rate;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getCoinImage() {
        return coinImage;
    }

    public void setCoinImage(String coinImage) {
        this.coinImage = coinImage;
    }

    public String getCoinShareImage() {
        return coinShareImage;
    }

    public void setCoinShareImage(String coinShareImage) {
        this.coinShareImage = coinShareImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
