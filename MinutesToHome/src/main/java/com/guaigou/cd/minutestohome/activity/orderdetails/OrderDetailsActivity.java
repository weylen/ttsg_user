package com.guaigou.cd.minutestohome.activity.orderdetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.pay.PayActivity;
import com.guaigou.cd.minutestohome.entity.OrderDetailsEntity;
import com.guaigou.cd.minutestohome.entity.OrderDetailsProductsEntity;
import com.guaigou.cd.minutestohome.entity.OrderEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.DimensUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.view.OrderProductsDetailsView;
import com.guaigou.cd.minutestohome.view.ZRefreshingView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 */
public class OrderDetailsActivity extends BaseActivity implements OrderDetailsView{

    public static final String ORDER_KEY = "order_key";

    @Bind(R.id.text_title) TextView mTextTitleView;
    @Bind(R.id.orderCodeView) TextView mOrderCodeView;
    @Bind(R.id.orderStatusView) TextView mOrderStatusView;
    @Bind(R.id.orderTimeView) TextView mOrderTimeView;
    @Bind(R.id.orderDeliveryView) TextView mOrderDeliveryView;
    @Bind(R.id.orderNoteView) TextView mOrderNoteView;
    @Bind(R.id.orderProductsPriceView) TextView mOrderProductsPriceView;
    @Bind(R.id.text_pay) TextView mPayPriceView;
    @Bind(R.id.orderFreightPriceView) TextView mOrderFreightPriceView; // 运费
    @Bind(R.id.orderScorePriceView) TextView mOrderScorePriceView; // 积分 暂时不用
    @Bind(R.id.orderPaymentView) TextView mOrderPaymentView;
    @Bind(R.id.orderContactsView) TextView mOrderContactsView;
    @Bind(R.id.orderAddressView) TextView mOrderAddressView;
    @Bind(R.id.payment_layout) View paymentView;
    @Bind(R.id.layout_real_pay) View mLayoutRealPayView;
    @Bind(R.id.layout_products) OrderProductsDetailsView orderProductsDetailsView;
    @Bind(R.id.layout_parent) FrameLayout parentLayout;
    @Bind(R.id.layout_main) View mainView;
    @Bind(R.id.layout_empty) View emptyView;
    @Bind(R.id.refreshLayout) ZRefreshingView zRefreshingView;
    @Bind(R.id.layout_note) View noteLayoutView;

    private String orderNumber = null; // 订单号
    private List<OrderDetailsEntity> detailsEntities;
    private OrderDetailsPresenter orderDetailsPresenter;

    private boolean isFirstRequest = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyorder);
        ButterKnife.bind(this);

        mTextTitleView.setText(R.string.OrderDetails);

        orderNumber = getIntent().getStringExtra(ORDER_KEY);
        if (TextUtils.isEmpty(orderNumber)) {
            return;
        }
        zRefreshingView.setOnRefreshListener(() -> orderDetailsPresenter.onRefresh());
        emptyView.setOnClickListener(v -> orderDetailsPresenter.onStartRequestOrderDetails(orderNumber));

        // 隐藏所有的布局
        mainView.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.GONE);
        orderDetailsPresenter = new OrderDetailsPresenter(this);

        ViewTreeObserver vto2 = mOrderNoteView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mOrderNoteView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (isFirstRequest){
                    orderDetailsPresenter.onStartRequestOrderDetails(orderNumber);
                    isFirstRequest = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onBackPressed() {
        onImgBack();
    }

    // 返回键
    @OnClick(R.id.img_back)
    void onImgBack(){
        finish();
    }

    /**
     * 设置订单信息
     */
    private void setupMessages(){
        OrderDetailsEntity detailsEntity = detailsEntities.get(0);
        // 订单号
        mOrderCodeView.setText(detailsEntity.getOrderId());
        // 订单总金额
        mOrderPaymentView.setText(detailsEntity.getTotal());
        // 获取商品列表信息
        List<OrderDetailsProductsEntity> products = detailsEntity.getProducts();
        OrderDetailsProductsEntity productsEntity = products.get(0);
        // 订单状态
        mOrderStatusView.setText(Constants.ORDER_PARAM.get(productsEntity.getStauts()));
        // 下单时间
        mOrderTimeView.setText(productsEntity.getDate());
        // 送达时间
        mOrderDeliveryView.setText(productsEntity.getShsj());
        // 备注
        String text = productsEntity.getNote();
        String note = text == null ? "" : text;
        DebugUtil.d("OrderDetailsActivity 备注：" + note);
        float textWidth = mOrderNoteView.getPaint().measureText(note);
        Drawable d = getResources().getDrawable(R.mipmap.abc_arrow_right);
        if (textWidth >= mOrderNoteView.getWidth()){
            mOrderNoteView.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
            mOrderNoteView.setCompoundDrawablePadding(DimensUtil.dp2px(this, 10));
            noteLayoutView.setOnClickListener(v-> showNoteDialog(note));
        }else {
            mOrderNoteView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        mOrderNoteView.setText(note);
        // 联系人
        mOrderContactsView.setText(productsEntity.getFname() + "   " + productsEntity.getTele());
        // 地址
        mOrderAddressView.setText(productsEntity.getAddr());
        // 商品价格
        mOrderProductsPriceView.setText("￥" + detailsEntity.getTotal());
        orderProductsDetailsView.setDataAndNotify2(products);
        // 设置支付状态
        String status = productsEntity.getStauts();
        if ("2".equalsIgnoreCase(status)){
            paymentView.setVisibility(View.VISIBLE);
            mLayoutRealPayView.setVisibility(View.GONE);
        }else if ("1".equalsIgnoreCase(status) || "3".equalsIgnoreCase(status)){
            mLayoutRealPayView.setVisibility(View.VISIBLE);
            paymentView.setVisibility(View.GONE);
        }else {
            mLayoutRealPayView.setVisibility(View.GONE);
            paymentView.setVisibility(View.GONE);
        }
        mPayPriceView.setText(detailsEntity.getTotal());

    }

    private void showNoteDialog(String note){
        new AlertDialog.Builder(this)
                .setTitle("备注信息")
                .setMessage(note)
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void showEmptyView(){
        emptyView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
    }

    /**
     * 去支付
     */
    @OnClick(R.id.text_payment)
    void onConfrimOrderClick(){
        orderDetailsPresenter.validatePayment(orderNumber);
    }

    @Override
    public void onStartRequestOrderDetails() {
        zRefreshingView.setRefreshing(true);
    }

    @Override
    public void onRequestOrderDetailsSuccess(List<OrderDetailsEntity> data) {
        zRefreshingView.setRefreshing(false);
        this.detailsEntities = data;
        if (data == null){
            showEmptyView();
        }else {
            emptyView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
        setupMessages();
    }

    @Override
    public void onRequestOrderDetailsFailure() {
        zRefreshingView.setRefreshing(false);
        showEmptyView();
    }

    @Override
    public void onStartValidateOrder() {
        showProgressDialog("请求中，请稍后...");
    }

    @Override
    public void onValidateOrderFailure(String message) {
        dismissProgressDialog();
        if (TextUtils.isEmpty(message)){
            showSnakeView(parentLayout, "请求失败，请重新操作");
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
    public void onValidateOrderSuccess() {
        dismissProgressDialog();
        OrderDetailsEntity detailsEntity = detailsEntities.get(0);
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra(PayActivity.ORDER_ID_KEY, detailsEntity.getOrderId());
        intent.putExtra(PayActivity.ORDER_PRICE_KEY, detailsEntity.getTotal());
        startActivity(intent);
    }
}
