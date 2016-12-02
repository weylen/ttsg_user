package com.guaigou.cd.minutestohome.activity.pay;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.entity.WxPayEntity;
import com.guaigou.cd.minutestohome.http.Client;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.RespSubscribe;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.http.Transformer;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-14.
 */
public class PayPresenter {

    private PayView payView;
    public PayPresenter(PayView payView){
        this.payView = payView;
    }

    /**
     * 修改订单状态
     * @param orderId 订单号
     * @param status "1"："订单完成" "2"："订单未支付" "3"："订单已支付未发货" "4"："客户退货" "5"："客户取消订单" "6"："支付结果确认中"
     */
    void onStart(String orderId, String status){
        payView.onStartAlertOrderStatus();
        Client.request()
                .alertOrderStatus(orderId, status)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        payView.onAlertOrderStatusFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("PayPresenter 修改支付状态成功");
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            payView.onAlertOrderStatusSuccess();
                        }else {
                            payView.onAlertOrderStatusFailure();
                        }
                    }
                }));
    }

    void aliPay(String order, String details, String desc){
        payView.onStartRequestAliPay();
        Client.request()
                .aliPay(order, details, desc)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        payView.onRequestAliPayFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            payView.onRequestAliPaySuccess(jsonObject.get("data").getAsString());
                        }else {
                            payView.onRequestAliPayFailure();
                        }
                    }
                }));
    }

    void wxPay(String describe, String orderNum, String payId){
        payView.onStartWxPay();
        Client.request()
                .wxPay(describe, orderNum, payId)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        payView.onWxPayFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            payView.onWxPaySuccess(parseWxResult(jsonObject));
                        }else {
                            payView.onWxPayFailure();
                        }

                    }
                }));
    }

    /**
     * 解析微信下单结果
     * @param jsonObject
     * @return
     */
    private WxPayEntity parseWxResult(JsonObject jsonObject){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.get("data").getAsJsonObject(), WxPayEntity.class);
    }
}
