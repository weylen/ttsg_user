package com.guaigou.cd.minutestohome.activity.confirmorder;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.BasePresenter;

/**
 * Created by weylen on 2016-07-24.
 */
public class ConfirmOrderPresenter implements BasePresenter {

    private ConfirmOrderView confirmOrderView;

    public ConfirmOrderPresenter(ConfirmOrderView confirmOrderView){
        this.confirmOrderView = Preconditions.checkNotNull(confirmOrderView);
    }

    @Override
    public void start() {

    }

    /**
     * 请求订单
     */
    public void onRequestOrder(){

    }
}
