package com.guaigou.cd.minutestohome.activity.pay;

/**
 * Created by weylen on 2016-08-14.
 */
public interface PayView {

    void onStartAlertOrderStatus();
    void onAlertOrderStatusSuccess();
    void onAlertOrderStatusFailure();
    void onStartRequestRsaPrivate();
    void onRequestRsaPrivateSuccess(String rsaPrivate);
    void onRequestRasPrivateFailure();
    void onStartWxPay();
    void onWxPaySuccess();
    void onWxPayFailure();
}
