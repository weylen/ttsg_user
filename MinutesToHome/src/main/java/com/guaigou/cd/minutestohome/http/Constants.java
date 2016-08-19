package com.guaigou.cd.minutestohome.http;

import java.util.HashMap;

/**
 * Created by weylen on 2016-07-20.
 */
public class Constants {

    public static final String BASE_URL = "http://s-280082.gotocdn.com/";

    public static final String EMPTY_STRING = new String();

    public static HashMap<String, String> ORDER_PARAM = null;
    // 商户PID
    public static final String PARTNER = "2088901028180245";
    // 商户收款账号
    public static final String SELLER = "870508809@qq.com";
    // 支付宝服务端异步通知网址
    public static final String ALI_NOTIFY_URL = BASE_URL + "payat-alipaysync";
    // 微信支付 appid
    public static final String APP_ID = "wx0f894d6426a78405";

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

