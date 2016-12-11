package com.guaigou.cd.minutestohome.http;

import java.io.IOException;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-12-02.
 */
public class Transformer {
    public static <T> Observable.Transformer<Response<T>, T> sTransformer() {

        return responseObservable -> responseObservable.map(tResponse -> {
            if (!tResponse.isSuccessful()) try {
                throw new RuntimeException(tResponse.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tResponse.body();
        }).onErrorResumeNext(new HttpResponseFunc<>());
    }

    public static <T> Observable.Transformer<T, T> switchSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override public Observable<T> call(Throwable throwable) {
            throwable.printStackTrace();
            //ExceptionEngine为处理异常的驱动器
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }
}
