package com.guaigou.cd.minutestohome.activity.generic;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-06.
 */
public class GenericAddCartPresenter {

    private GenericAddCardView genericAddCardView;
    private RegionEntity regionEntity;
    private Context context;
    public GenericAddCartPresenter(Context context, GenericAddCardView genericAddCardView){
        this.genericAddCardView = genericAddCardView;
        regionEntity = RegionPrefs.getRegionData(context);
        this.context = context;
    }

    /**
     * 添加购物车请求，添加的商品
     * @param entity
     */
    public void request(ProductEntity entity){
        List<ProductEntity> entities = new ArrayList<>();
        entities.add(entity);
        request(entities);
    }

    /**
     * 添加到购物车请求
     * @param entities
     */
    public void request(List<ProductEntity> entities){
        // 检查区域id
        if (regionEntity == null || TextUtils.isEmpty(regionEntity.getId())){
            requestFailure("区域id不存在");
            return;
        }
        // 检查是否登录
        if (!LoginData.INSTANCE.isLogin(context)){
            genericAddCardView.noLogin();
            return;
        }
        genericAddCardView.onStartRequest();
        map(entities);
    }

    /**
     * 转换请求参数
     * @param data
     */
    private void map(List<ProductEntity> data){
        Observable.just(data)
                .subscribeOn(Schedulers.io())
                .map(o -> {
                    String param = "";
                    for (ProductEntity entity : data){
                        param += regionEntity.getId() + "-" + entity.getId() + "-" + entity.getNumber() + ",";
                    }
                    if (param.endsWith(",")){
                        param = param.substring(0, param.length() - 1);
                    }
                    return param;
                })
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                    remote(s);
                });
    }

    /**
     * 具体请求操作
     * @param param
     */
    private void remote(String param){
        DebugUtil.d("GenericAddCartPresenter remote 添加商品请求参数：" + param);
        RetrofitFactory.getRetrofit()
                .create(HttpService.class)
                .addProductToCart(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("GenericAddCartPresenter 添加商品失败：" + e.getMessage());
                        requestFailure("请求错误");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("GenericAddCartPresenter 添加商品结果：" + jsonObject);
                        int status = ResponseMgr.getStatus(jsonObject);
                        if (status == 1){
                            genericAddCardView.onRequestSuccess();
                        }else {
                            String message = jsonObject.get("data").getAsString();
                            requestFailure(message);
                        }
                    }
                });
    }

    /**
     * 请求失败
     * @param message
     */
    private void requestFailure(String message){
        genericAddCardView.onRequestFailure(message);
    }
}
