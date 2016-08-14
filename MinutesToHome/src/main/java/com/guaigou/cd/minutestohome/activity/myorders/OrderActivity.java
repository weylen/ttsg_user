package com.guaigou.cd.minutestohome.activity.myorders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.orderdetails.OrderDetailsActivity;
import com.guaigou.cd.minutestohome.activity.pay.PayActivity;
import com.guaigou.cd.minutestohome.adapter.OnItemViewClickListener;
import com.guaigou.cd.minutestohome.adapter.OrderAdapter;
import com.guaigou.cd.minutestohome.entity.OrderEntity;
import com.guaigou.cd.minutestohome.entity.OrderProductsEntity;
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

    @Bind(R.id.text_title) TextView mTitleView; // 标题视图
    @Bind(R.id.Generic_List) ZListView zListView; // 订单列表
    @Bind(R.id.refreshLayout) ZRefreshingView zRefreshingView; // 刷新组件

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
        emptyViewHelper.setOnEmptyTouchListener(() -> orderPresenter.requestOrder());
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
            showDeleteOrderDialog(position, orderAdapter.getItem(position).getOrderId());
        }

        @Override // 支付操作
        public void onClickView2(int position) {
            orderPresenter.validatePayment(position, orderAdapter.getItem(position).getOrderId());
        }
    };

    private void showDeleteOrderDialog(int position, String orderId){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定要取消该订单？")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    orderPresenter.cancelOrder(position, orderId);
                })
                .show();
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
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra(OrderDetailsActivity.ORDER_KEY, orderAdapter.getItem(position).getOrderId());
        startActivity(intent);
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
        zListView.setState(isFinish ? ZListView.State.STATE_COMPLETE : ZListView.State.STATE_NORMAL);
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

    @Override
    public void onStartCancelOrder() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onCancelOrderSuccess(int position) {
        dismissProgressDialog();
        showSnakeView(zListView, "取消订单成功");
        OrderEntity entity = orderAdapter.getItem(position);
        List<OrderProductsEntity> orderProductsEntities = entity.getProducts();
        for (OrderProductsEntity orderProductsEntity : orderProductsEntities){
            orderProductsEntity.setStauts("5");
        }
        orderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelOrderFailure() {
        dismissProgressDialog();
        showSnakeView(zListView, "取消订单失败，请重新操作");
    }

    @Override
    public void onStartValidateOrder() {
        showProgressDialog("请求中，请稍后...");
    }

    @Override
    public void onValidateOrderFailure(String message) {
        dismissProgressDialog();
        if (TextUtils.isEmpty(message)){
            showSnakeView(zListView, "请求失败，请重新操作");
        }else {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton("确定", (dialog, which) -> {
                       dialog.dismiss();
                    })
                    .show();
        }
    }

    @Override
    public void oNValidateOrderSuccess(int position) {
        dismissProgressDialog();
        OrderEntity entity = orderAdapter.getItem(position);
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra(PayActivity.ORDER_ID_KEY, entity.getOrderId());
        intent.putExtra(PayActivity.ORDER_PRICE_KEY, entity.getTotal());
        intent.putExtra(PayActivity.ORDER_PRODUCTS_DETAILS_KEY, entity.getProducts());
        startActivity(intent);
    }
}
