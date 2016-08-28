package com.guaigou.cd.minutestohome.activity.myorders;

import com.guaigou.cd.minutestohome.entity.OrderEntity;

import java.util.List;

/**
 * Created by weylen on 2016-07-23.
 */
public interface OrderView{

    void onStartRequest();
    void onRequestFailure();
    void onRequestSuccess(List<OrderEntity> data, boolean isFinish);
    void onLoadMoreSuccess(List<OrderEntity> data, boolean isFinish);
    void onLoadMoreFailure();
    void onRefreshFailure();
    void onStartCancelOrder();
    void onCancelOrderSuccess(int position);
    void onCancelOrderFailure();
    void onStartValidateOrder();
    void onValidateOrderFailure(String message);
    void oNValidateOrderSuccess(int position);
    void onStartDeleteOrder();
    void onDeleteOrderSuccess(int position);
    void onDeleteOrderFailure();
    void onStartAlertStatus();
    void onAlertStatusSuccess(int position, int status);
    void onAlertStatusFailure(int position, int status);
}
