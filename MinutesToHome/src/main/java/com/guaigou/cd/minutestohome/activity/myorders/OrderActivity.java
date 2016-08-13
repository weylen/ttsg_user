package com.guaigou.cd.minutestohome.activity.myorders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.adapter.OnItemViewClickListener;
import com.guaigou.cd.minutestohome.adapter.OrderAdapter;
import com.guaigou.cd.minutestohome.entity.OrderEntity;
import com.guaigou.cd.minutestohome.view.EmptyViewHelper;
import com.guaigou.cd.minutestohome.view.ZListView;
import com.guaigou.cd.minutestohome.view.ZRefreshingView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by weylen on 2016-07-23.
 */
public class OrderActivity extends BaseActivity implements OrderView{

    @Bind(R.id.text_title)
    TextView mTitleView; // 标题视图
    @Bind(R.id.Generic_List)
    ZListView zListView; // 订单列表
    @Bind(R.id.refreshLayout)
    ZRefreshingView zRefreshingView; // 刷新组件

    private OrderAdapter orderAdapter;
    private EmptyViewHelper emptyViewHelper; // 空视图辅助类

    private OrderPresenter orderPresenter; // 业务处理类

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        mTitleView.setText(R.string.Orders);
        // 设置刷新事件
        zRefreshingView.setOnRefreshListener(() -> refresh());
        // 设置列表的加载更多事件
        zListView.setOnLoadmoreListener(new ZListView.OnLoadmoreListener() {
            @Override
            public void onLoadMore() {
                ZListView.State state = zListView.getState();
                if (state == ZListView.State.STATE_COMPLETE || state == ZListView.State.STATE_LOADING){
                    return;
                }
                zListView.setState(ZListView.State.STATE_LOADING);
                orderPresenter.onLoadmore();
            }

            @Override
            public void onError() {
                zListView.setState(ZListView.State.STATE_LOADING);
                orderPresenter.onLoadmore();
            }
        });
        // 添加空视图
        FrameLayout parent = (FrameLayout) findViewById(R.id.parentFrameLayout);
        emptyViewHelper = new EmptyViewHelper(zListView, "没有订单", parent);
        emptyViewHelper.setRefreshListener(() -> refresh());
        emptyViewHelper.setOnEmptyTouchListener(() -> {
            orderPresenter.requestOrder();
        });
        // 设置适配器
        orderAdapter = new OrderAdapter(this, null);
        orderAdapter.setOnItemViewClickListener(itemViewClickListener);
        zListView.setAdapter(orderAdapter);

        // 创建业务处理类对象
        orderPresenter = new OrderPresenter(this);
        orderPresenter.requestOrder();
    }

    private OnItemViewClickListener itemViewClickListener = new OnItemViewClickListener() {
        @Override // 取消操作
        public void onClickView1(int position) {

        }

        @Override // 支付操作
        public void onClickView2(int position) {

        }
    };

    /**
     * 刷新
     */
    void refresh(){
        orderPresenter.onRefresh();
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        finish();
    }

    @OnItemClick(R.id.Generic_List)
    void onItemClick(int position){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void resetRefresh(){
        zRefreshingView.setRefreshing(false);
        emptyViewHelper.setRefreshing(false);
    }

    @Override
    public void onStartRequest() {
        zRefreshingView.setRefreshing(true);
    }

    @Override
    public void onRequestFailure() {
        resetRefresh();
    }

    @Override
    public void onRequestSuccess(List<OrderEntity> data, boolean isFinish) {
        resetRefresh();
        if (isFinish){
            zListView.setShowFooterView(false);
        }else {
            zListView.setShowFooterView(true);
            zListView.setState(ZListView.State.STATE_NORMAL);
        }
        orderAdapter.setData(data);
    }

    @Override
    public void onLoadMoreSuccess(List<OrderEntity> data, boolean isFinish) {
        zListView.setState(isFinish ? ZListView.State.STATE_COMPLETE : ZListView.State.STATE_NORMAL);
        orderAdapter.addData(data);
    }

    @Override
    public void onLoadMoreFailure() {
        zListView.setState(ZListView.State.STATE_ERROR);
    }

    @Override
    public void onRefreshFailure() {
        resetRefresh();
    }
}
