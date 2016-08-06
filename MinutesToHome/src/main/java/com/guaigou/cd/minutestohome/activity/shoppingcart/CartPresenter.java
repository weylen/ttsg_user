package com.guaigou.cd.minutestohome.activity.shoppingcart;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-06.
 */
public class CartPresenter {

    private CartView cartView;
    public CartPresenter(CartView cartView){
        this.cartView = cartView;
    }

    void refresh(){
        cartView.onStartRefresh();
        remote();
    }

    void requestList(){
        cartView.onStartLoading();
        remote();
    }

    private void remote(){
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
                        requestFailure("请求失败");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("CartPresenter 购物车数据：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            requestFailure("购物车为空");
                        }else {
                            cartView.onLoadSuccess(parseCartList(jsonObject));
                        }
                    }
                });
    }

    private List<CartEntity> parseCartList(JsonObject jsonObject){
        Gson gson = new Gson();
        return  gson.fromJson(jsonObject.get("data"), new TypeToken<List<CartEntity>>(){}.getType());
    }

    private void requestFailure(String message){
        cartView.onLoadFailure(message);
    }
}
