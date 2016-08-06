package com.guaigou.cd.minutestohome.activity.generic;

/**
 * Created by weylen on 2016-08-06.
 */
public interface GenericAddCardView {

    void onStartRequest();
    void onRequestSuccess();
    void onRequestFailure(String message);
    void noLogin();
}
