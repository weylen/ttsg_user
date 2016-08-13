package com.guaigou.cd.minutestohome.activity.confirmorder;

import com.guaigou.cd.minutestohome.entity.ConfirmOrderEntity;

/**
 * Created by weylen on 2016-07-24.
 */
public interface ConfirmOrderView{

    void onStartRequestOrder();
    void onRequestFailure();
    void onRequestSuccess(ConfirmOrderEntity entity);
}
