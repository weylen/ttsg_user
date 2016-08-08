package com.guaigou.cd.minutestohome.util;

import android.content.Context;

import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.prefs.CartPrefs;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-07.
 */
public enum CartUtil {

    INSTANCE;

    public void remoteCart(Context context, OnLoadCompleteListener onLoadCompleteListener){
        Observable.create((Observable.OnSubscribe<List<CartEntity>>) subscriber -> {
            subscriber.onNext(CartPrefs.getCartData(context));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    CartData.INSTANCE.setData(o);
                    complete(onLoadCompleteListener);
                });
    }

    private void complete(OnLoadCompleteListener onLoadCompleteListener){
        if (onLoadCompleteListener != null){
            onLoadCompleteListener.onComplete();
        }
    }

    public interface OnLoadCompleteListener{
        void onComplete();
    }
}
