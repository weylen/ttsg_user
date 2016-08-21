package com.guaigou.cd.minutestohome.http;

import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.SessionUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Converter;
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
                            .addConverterFactory(new StringConverterFactory())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(genericClient())
                            .build();
                }
            }
        }
        return retrofit;
    }

    private static OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .addHeader("Accept-Encoding", "gzip, deflate")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Accept", "*/*")
                            .addHeader("Cookie", SessionUtil.getSessionId())
                            .build();
                    DebugUtil.d("RetrofitFactory 请求的SessionId:" + SessionUtil.getSessionId());
                    return chain.proceed(request);
                })
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        return httpClient;
    }
}
