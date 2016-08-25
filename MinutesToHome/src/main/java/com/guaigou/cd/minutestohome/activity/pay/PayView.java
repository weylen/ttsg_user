package com.guaigou.cd.minutestohome.activity.pay;

import com.guaigou.cd.minutestohome.entity.WxPayEntity;

/**
 * Created by weylen on 2016-08-14.
 */
public interface PayView {

    void onStartAlertOrderStatus();
    void onAlertOrderStatusSuccess();
    void onAlertOrderStatusFailure();
    void onStartRequestAliPay();
    void onRequestAliPaySuccess(String rsaPrivate);
    void onRequestAliPayFailure();
    void onStartWxPay();
    void onWxPaySuccess(WxPayEntity wxPayEntity);
    void onWxPayFailure();
}
