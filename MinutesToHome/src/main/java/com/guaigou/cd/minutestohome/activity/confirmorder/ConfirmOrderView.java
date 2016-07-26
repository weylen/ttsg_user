package com.guaigou.cd.minutestohome.activity.confirmorder;

import com.guaigou.cd.minutestohome.BaseView;

/**
 * Created by weylen on 2016-07-24.
 */
public interface ConfirmOrderView extends BaseView<ConfirmOrderPresenter>{

    void onRequestOrder();
    void onRequestFailure();
    void onRequestSuccess();
}
