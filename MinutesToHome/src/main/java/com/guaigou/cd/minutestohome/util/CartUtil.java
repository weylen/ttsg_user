package com.guaigou.cd.minutestohome.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-07.
 */
public enum CartUtil {

    INSTANCE;

    public void remoteCart(OnLoadCompleteListener onLoadCompleteListener){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getCartList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("CartPresenter 购物车列表获取失败：" + e.getMessage());
                        complete(onLoadCompleteListener);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("CartPresenter 购物车数据：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            List<CartEntity> data = parseCartList(jsonObject);
                            CartData.INSTANCE.setData(data);
                        }
                        complete(onLoadCompleteListener);
                    }
                });
    }

    private List<CartEntity> parseCartList(JsonObject jsonObject){
        Gson gson = new Gson();
        return  gson.fromJson(jsonObject.get("data"), new TypeToken<List<CartEntity>>(){}.getType());
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
