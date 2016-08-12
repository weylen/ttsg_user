package com.guaigou.cd.minutestohome.activity.myorders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.ParseUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-23.
 */
public class OrderPresenter{

    private OrderView orderView;
    public OrderPresenter(OrderView orderView){
        this.orderView = Preconditions.checkNotNull(orderView, "OrderView can not be null");
    }

    // 刷新事件
    void onRefresh(){
        remote(1);
    }

    // 加载更多
    void onLoadmore(){
        remote(OrderData.INSTANCE.pageNum + 1);
    }

    void requestOrder(){
        orderView.onStartRequest();
        remote(1);
    }

    private void remote(int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .requestOrderList(Constants.EMPTY_STRING, Constants.EMPTY_STRING, pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("OrderPresenter 订单列表获取失败：" + e.getMessage());
                        orderView.onRequestFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("OrderPresenter 订单列表结果：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            orderView.onRequestFailure();
                        }else {
                            //TODO 解析数据
                        }
                    }
                });
    }
}
