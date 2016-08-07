package com.guaigou.cd.minutestohome.activity.myorders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.view.EmptyViewHelper;
import com.guaigou.cd.minutestohome.view.ZListView;
import com.guaigou.cd.minutestohome.view.ZRefreshingView;

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

            }

            @Override
            public void onError() {

            }
        });
        // 添加空视图
        FrameLayout parent = (FrameLayout) findViewById(R.id.parentFrameLayout);
        emptyViewHelper = new EmptyViewHelper(zListView, "没有订单", parent);
        emptyViewHelper.setRefreshListener(() -> refresh());

        // 创建业务处理类对象
        orderPresenter = new OrderPresenter(this);
    }

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

    @Override
    public void startLoading() {
        showProgressDialog("加载中");
    }

    @Override
    public void onLoadFailure() {
        dismissProgressDialog();
    }

    @Override
    public void onLoadSuccess() {
        dismissProgressDialog();
    }

    @Override
    public void onRefresh() {
        zRefreshingView.setRefreshing(false);
        emptyViewHelper.setRefreshing(false);
    }

    @Override
    public void setPresenter(OrderPresenter presenter) {

    }
}
