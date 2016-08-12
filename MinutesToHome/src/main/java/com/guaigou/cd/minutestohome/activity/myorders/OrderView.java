package com.guaigou.cd.minutestohome.activity.myorders;

/**
 * Created by weylen on 2016-07-23.
 */
public interface OrderView{

    void onStartRequest();
    void onRequestFailure();
    void onRequestSuccess();
    void onLoadMoreSuccess();
    void onLoadMoreFailure();
    void onRefreshSuccess();
    void onRefreshFailure();
}
