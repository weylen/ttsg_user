package com.guaigou.cd.minutestohome.http;

import android.content.Context;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BaseApplication;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * Created by zhou on 2016/9/14.
 */
public class RespSubscribe extends ErrorSubscriber<JsonObject> {

    private static final int TOKEN_INVALID = -10;

    private Subscriber<JsonObject> realSubscriber;
    public RespSubscribe(Subscriber<JsonObject> subscriber){
        this.realSubscriber = Preconditions.checkNotNull(subscriber, "subscriber can not be null");
    }

    @Override
    public void onCompleted() {}

    @Override
    protected void onError(ApiException ex) {
        if (realSubscriber != null){
            realSubscriber.onError(ex);
        }
        final Context context = BaseApplication.getLast();
        if (context == null){
            DebugUtil.d("RespSubscribe Token Invalid but context == null");
            return;
        }
        Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(JsonObject jsonObject) {
        if (ResponseMgr.getStatus(jsonObject) == TOKEN_INVALID){
            DebugUtil.d("RespSubscribe jsonObject:" + jsonObject);
            ReloginDialog.showDialog();
            onError(new Exception("Token Invalid"));
        }else {
            realSubscriber.onNext(jsonObject);
        }
    }
}
abstract class ErrorSubscriber<T> implements Observer<T> {

    @Override
    public void onError(Throwable e) {
        if(e instanceof ApiException){
            onError((ApiException)e);
        }else{
            onError(new ApiException(e, 123));
        }
    }

    /**
     * 错误回调
     */
    protected abstract void onError(ApiException ex);
}
