package com.guaigou.cd.minutestohome.http;

import com.guaigou.cd.minutestohome.util.SessionUtil;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by weylen on 2016-07-20.
 */
public class RetrofitFactory {

    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (RetrofitFactory.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
//                            .addConverterFactory(new StringConverterFactory())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(genericClient())
//                            .callbackExecutor()
                            .build();
                }
            }
        }
        return retrofit;
    }

    private static OkHttpClient genericClient() {
        if (okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Cookie", SessionUtil.getSessionId())
                                .addHeader("DeviceType", "Android")
                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .addHeader("Accept-Encoding", "gzip, deflate")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "*/*")
                                .build();
                        return chain.proceed(request);
                    })
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }
}
