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
}
