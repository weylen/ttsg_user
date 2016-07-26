package com.guaigou.cd.minutestohome.activity.myorders;

import com.guaigou.cd.minutestohome.BaseView;

/**
 * Created by weylen on 2016-07-23.
 */
public interface OrderView extends BaseView<OrderPresenter>{

    void startLoading();

    void onLoadFailure();

    void onLoadSuccess();

    void onRefresh();
}
