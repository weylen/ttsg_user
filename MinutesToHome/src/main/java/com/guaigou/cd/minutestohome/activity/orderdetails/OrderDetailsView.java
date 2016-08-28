package com.guaigou.cd.minutestohome.activity.orderdetails;

import com.guaigou.cd.minutestohome.entity.OrderDetailsEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-14.
 */
public interface OrderDetailsView {
    void onStartRequestOrderDetails();
    void onRequestOrderDetailsSuccess(List<OrderDetailsEntity> data);
    void onRequestOrderDetailsFailure();

    void onStartValidateOrder();
    void onValidateOrderFailure(String message);
    void onValidateOrderSuccess();

    void onStartAlertStatus();
    void onAlertStatusSuccess(int status);
    void onAlertStatusFailure(int status);
}
