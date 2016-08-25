package com.guaigou.cd.minutestohome.http;

import java.util.HashMap;

/**
 * Created by weylen on 2016-07-20.
 */
public class Constants {

    public static final String BASE_URL = "http://s-280082.gotocdn.com/";

    public static final String EMPTY_STRING = new String();

    public static HashMap<String, String> ORDER_PARAM = null;
    // 微信支付 appid
    public static final String APP_ID = "wx0f894d6426a78405";

    public static int WX_RESP = -200;

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

