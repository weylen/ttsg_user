package com.guaigou.cd.minutestohome.activity.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 */
public class PayActivity extends BaseActivity {

    @Bind(R.id.text_title) TextView mTextTitle;
    @Bind(R.id.text_price) TextView mTextPrice;
    @Bind(R.id.Container) View containerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        mTextTitle.setText(R.string.Pay);
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

    @OnClick(R.id.alipay_layout)
    void onAlipayClick(){ // 支付宝支付
        showSnakeView(containerView, "支付宝");
    }

    @OnClick(R.id.weixin_layout)
    void onWeixinpayClick(){ // 微信支付
        showSnakeView(containerView, "微信");
    }
}
