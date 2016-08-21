package com.guaigou.cd.minutestohome.activity.pay;

import com.guaigou.cd.minutestohome.entity.WxPayEntity;

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
    void onWxPaySuccess(WxPayEntity wxPayEntity);
    void onWxPayFailure();
}
