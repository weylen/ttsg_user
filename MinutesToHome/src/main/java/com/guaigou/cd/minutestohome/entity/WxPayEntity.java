package com.guaigou.cd.minutestohome.entity;

/**
 * Created by weylen on 2016-08-21.
 */
public class WxPayEntity {

    // {"appid":"wx0f894d6426a78405","noncestr":"7986815595262117445","package":"Sign=WXPay","partnerid":"1378024102",
    // "prepayid":"wx20160826175829db244099a70600688370","sign":"8A5C764371DB132F1146B26B1F49460C","timestamp":"1472205501"}

    private String appid;
    private String noncestr;
    private String partnerid;
    private String prepayid;
    private String sign;
    private String timestamp;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
