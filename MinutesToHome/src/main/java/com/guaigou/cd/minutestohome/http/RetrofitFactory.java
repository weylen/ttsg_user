package com.guaigou.cd.minutestohome.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by weylen on 2016-07-20.
 */
public class RetrofitFactory {

    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (RetrofitFactory.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(OkHttpMgr.defaultOkHttpClient())
                            .build();
                }
            }
        }
        return retrofit;
    }
}
