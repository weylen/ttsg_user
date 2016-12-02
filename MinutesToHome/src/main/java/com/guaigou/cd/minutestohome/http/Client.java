package com.guaigou.cd.minutestohome.http;

/**
 * Created by weylen on 2016-12-02.
 */
public class Client {
    public static HttpService request(){
        return RetrofitFactory.getRetrofit().create(HttpService.class);
    }
}
