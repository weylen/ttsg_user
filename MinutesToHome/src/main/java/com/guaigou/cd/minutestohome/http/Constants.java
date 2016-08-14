package com.guaigou.cd.minutestohome.http;

import java.util.HashMap;

/**
 * Created by weylen on 2016-07-20.
 */
public class Constants {

    public static final String BASE_URL = "http://s-280082.gotocdn.com/";
    // 测试
//    public static final String BASE_URL = "http://192.168.1.167:8080/cs_app/";

    public static final String EMPTY_STRING = new String();

    public static HashMap<String, String> ORDER_PARAM = null;
    // 商户PID
    public static final String PARTNER = "2088901028180245";
    // 商户收款账号
    public static final String SELLER = "870508809@qq.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN7mEKswUIJgI9JN\n" +
            "qGjM4ouWzSth/1IQcM198hHJisPynmoCNAHKsc5rYwIv5FEnq5IBaaq2dgzvh8n+\n" +
            "XRoEnIl05Ppfhhcc40Dp3ro28+ZE3T2cYFQnmkiUmY2Paa2cUWO6Xcvoiwx9g3is\n" +
            "nbXsOgAIjGnmHirvPe7QEvklH1oZAgMBAAECgYBOR64C8OzVXL431V4XsBy/uS5E\n" +
            "1h28zFlRUbH53TYGquZHhI6PMohPXhdfZUJh3fS7ShO6CGpdEKI3pkU8JfJzC+YD\n" +
            "KHxssWv59EwOaN6+vwY6Wv10jLQIcKbeMCm2Wpsie6EIJi5qxfDgj47ZOLCewcK3\n" +
            "kjym7bljeEY5G+3QAQJBAPYtpLs1uXwycpaO+oO4Pft/o+dst2+RRuitSYhNYcgi\n" +
            "UNkc0M3CsWgbh3tAEPRUNYpmwWeH9/+NoPhqXJmtpMECQQDnyqduwGzHTRvqEYVF\n" +
            "/sxZU7VHD7xIiOejCE7OKdaAyqVbebWHn4CsolSrXbmHOflFRX+vkY8YQsEk4DWo\n" +
            "dtNZAkEAwul2oSczWgtTQLOp03qf0K3J5yTjw/kBbJ4vNzUVc3EAFDbFd/RrpGMP\n" +
            "jN03z0uRLKiY/tDf4T2Qsr773E1owQJAIhvkP5v5Zeqm7O7fGcnA8HQs/OQH5PSg\n" +
            "gZE7Z4MomZM4ehmS6P5DxSOuKfmEz61sTRFJFODqmbtZCMAlpUC3IQJBANBewCSt\n" +
            "wPI1XqKMHwnLEPHzJrXhI7xeAdsk7OoAQnfpb8G5KvK3SlB7oiT9u/igqGJ4FeDY\n" +
            "9gRTdZb4BkAv/D0=";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    // 服务端异步通知网址
    public static final String ALI_NOTIFA_URL = "m.alipay.com";

    static {
        ORDER_PARAM = new HashMap<>();
        ORDER_PARAM.put("1", "订单已完成");
        ORDER_PARAM.put("2", "等待支付");
        ORDER_PARAM.put("3", "等待商家确认订单");
        ORDER_PARAM.put("4", "退单");
        ORDER_PARAM.put("5", "订单已取消");
        ORDER_PARAM.put("6", "支付结果确认中");
    }
}

