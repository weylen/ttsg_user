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

    static {
        ORDER_PARAM = new HashMap<>();
        ORDER_PARAM.put("1", "订单已完成");
        ORDER_PARAM.put("2", "等待支付");
        ORDER_PARAM.put("3", "等待商家确认订单");
        ORDER_PARAM.put("4", "退单");
        ORDER_PARAM.put("5", "订单已取消");
    }
}

