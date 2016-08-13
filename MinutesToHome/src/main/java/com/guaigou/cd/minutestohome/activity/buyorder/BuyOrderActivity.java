package com.guaigou.cd.minutestohome.activity.buyorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.pay.PayActivity;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 */
public class BuyOrderActivity extends BaseActivity {

    public static final String ORDER_KEY = "order_key";

    @Bind(R.id.text_title) TextView mTextTitleView;
    @Bind(R.id.orderCodeView) TextView mOrderCodeView;
    @Bind(R.id.orderStatusView) TextView mOrderStatusView;
    @Bind(R.id.orderTimeView) TextView mOrderTimeView;
    @Bind(R.id.orderDeliveryView) TextView mOrderDeliveryView;
    @Bind(R.id.orderNoteView) TextView mOrderNoteView;
    @Bind(R.id.CostDetailsContainer) LinearLayout mCostDetailsContainer;
    @Bind(R.id.orderProductsPriceView) TextView mOrderProductsPriceView;
    @Bind(R.id.orderFreightPriceView) TextView mOrderFreightPriceView;
    @Bind(R.id.orderScorePriceView) TextView mOrderScorePriceView;
    @Bind(R.id.orderPaymentView) TextView mOrderPaymentView;
    @Bind(R.id.orderContactsView) TextView mOrderContactsView;
    @Bind(R.id.orderAddressView) TextView mOrderAddressView;

    private String orderNumber = null; // 订单号

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyorder);
        ButterKnife.bind(this);

        mTextTitleView.setText(R.string.OrderDetails);

        orderNumber = getIntent().getStringExtra(ORDER_KEY);
        if (TextUtils.isEmpty(orderNumber)){
            DebugUtil.e("BuyOrderActivity onCreate 订单号出现异常");
            return;
        }
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
     * 去支付
     */
    @OnClick(R.id.text_payment)
    void onConfrimOrderClick(){
        Intent intent = new Intent(this, PayActivity.class);
        startActivity(intent);
    }
}
