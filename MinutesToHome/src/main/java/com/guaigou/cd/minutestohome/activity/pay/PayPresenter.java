package com.guaigou.cd.minutestohome.activity.pay;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.entity.WxPayEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
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
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .alertOrderStatus(orderId, status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("PayPresenter 修改支付状态失败：" + e.getMessage());
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
                });
    }

    void requestRsaPrivate(){
        payView.onStartRequestRsaPrivate();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getRsaPrivate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        payView.onRequestRasPrivateFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            payView.onRequestRsaPrivateSuccess(jsonObject.get("data").getAsString());
                        }else {
                            payView.onRequestRasPrivateFailure();
                        }
                    }
                });
    }

    void wxPay(String describe, String orderNum, String money){
        payView.onStartWxPay();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .wxPay(describe, orderNum, money)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("PayPresenter 微信下单失败：" + e.getMessage());
                        payView.onWxPayFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("PayPresenter 微信下单成功：" + jsonObject);
                        payView.onWxPaySuccess(parseWxResult(jsonObject));
                    }
                });
    }

    /**
     * 解析微信下单结果
     * @param jsonObject
     * @return
     */
    private WxPayEntity parseWxResult(JsonObject jsonObject){
        String result = jsonObject.get("xml").getAsJsonObject().get("return_code").getAsString();
        WxPayEntity wxPayEntity;
        if ("SUCCESS".equalsIgnoreCase(result)){
            Gson gson = new Gson();
            wxPayEntity = gson.fromJson(jsonObject.get("xml").getAsJsonObject(), WxPayEntity.class);
        }else {
            wxPayEntity = new WxPayEntity();
            wxPayEntity.setReturn_code("FAIL");
        }
        return wxPayEntity;
    }
}
