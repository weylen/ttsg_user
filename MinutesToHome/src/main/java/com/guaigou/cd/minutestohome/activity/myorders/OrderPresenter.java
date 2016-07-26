package com.guaigou.cd.minutestohome.activity.myorders;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.BasePresenter;

/**
 * Created by weylen on 2016-07-23.
 */
public class OrderPresenter implements BasePresenter{

    private OrderView orderView;
    public OrderPresenter(OrderView orderView){
        this.orderView = Preconditions.checkNotNull(orderView, "OrderView can not be null");
    }

    @Override
    public void start() {

    }

    // 刷新事件
    void onRefresh(){

    }

    // 加载更多
    void onLoadmore(){

    }
}
