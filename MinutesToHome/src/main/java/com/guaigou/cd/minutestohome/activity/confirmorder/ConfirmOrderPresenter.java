package com.guaigou.cd.minutestohome.activity.confirmorder;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.entity.ConfirmOrderEntity;
import com.guaigou.cd.minutestohome.http.Client;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.RespSubscribe;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.http.Transformer;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.List;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-24.
 */
public class ConfirmOrderPresenter implements BasePresenter {

    private ConfirmOrderView confirmOrderView;

    public ConfirmOrderPresenter(ConfirmOrderView confirmOrderView){
        this.confirmOrderView = Preconditions.checkNotNull(confirmOrderView);
    }

    @Override
    public void start() {

    }

    /**
     * 请求订单
     */
    public void onRequestOrder(String shopId, String note, String address, String time, String name, String phone){
        confirmOrderView.onStartRequestOrder();

        StringBuilder builder = new StringBuilder();
        builder.append(shopId+":");
        List<CartEntity> data = CartData.INSTANCE.getData();
        for (CartEntity entity : data){
            builder.append(entity.getId());
            builder.append("-");
            builder.append(entity.getAmount());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        String orderInfo = builder.toString();
        DebugUtil.d("ConfirmOrderPresenter 订单请求信息：Key:" + orderInfo+"  note:" + note+"  address:" + address +"  time:" + time
            + " name:" + name + " phone:" + phone);
        Client.request()
                .requestOrder(orderInfo, note, address, time, name, phone)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        confirmOrderView.onRequestFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("ConfirmOrderPresenter 订单获取成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            confirmOrderView.onRequestSuccess(new Gson().fromJson(jsonObject.get("data").getAsJsonObject(), ConfirmOrderEntity.class));
                        }else {
                            confirmOrderView.onRequestFailure();
                        }
                    }
                }));
    }
}
