package com.guaigou.cd.minutestohome.http;

import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.SessionUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
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
                                .build();
                        Response response = chain.proceed(request);
                        return response;
                    })
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }
}
