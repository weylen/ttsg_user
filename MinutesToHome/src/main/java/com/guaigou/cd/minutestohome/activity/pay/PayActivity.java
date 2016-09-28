package com.guaigou.cd.minutestohome.activity.pay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.orderdetails.OrderDetailsActivity;
import com.guaigou.cd.minutestohome.entity.OrderProductsEntity;
import com.guaigou.cd.minutestohome.entity.WxPayEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.pay.PayResult;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.math.BigDecimal;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 */
public class PayActivity extends BaseActivity implements PayView{

    public static final String ORDER_ID_KEY = "order_id_key";
    public static final String ORDER_PRICE_KEY = "order_price_key";
    public static final String ORDER_PRODUCTS_DETAILS_KEY = "order_products_details_key";
    public static final String ORDER_PREPAY_ID_KEY = "order_prepay_id_key";

    @Bind(R.id.text_title) TextView mTextTitle;
    @Bind(R.id.text_price) TextView mTextPrice;
    @Bind(R.id.text_orderName) TextView mTextOrderName;
    @Bind(R.id.radio_pay_weixin) RadioButton weixinRadio;
    @Bind(R.id.radio_pay_ali) RadioButton aliRadio;
    @Bind(R.id.Container) View containerView;

    private enum PayWay{
        Ali, Weixin;
    }
    private PayWay payWay = PayWay.Ali;

    private String orderId;
    private String prepay_id;
    private String orderPrice;
    private List<OrderProductsEntity> productsEntityList;
    private String orderName = "商品货款";
    private String orderDetails = "天天闪购-商品货款";
    private PayPresenter payPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        mTextTitle.setText(R.string.Pay);

        Intent intent = getIntent();
        orderId = intent.getStringExtra(ORDER_ID_KEY);
        orderPrice = intent.getStringExtra(ORDER_PRICE_KEY);
        prepay_id = intent.getStringExtra(ORDER_PREPAY_ID_KEY);
        productsEntityList = (List<OrderProductsEntity>) intent.getSerializableExtra(ORDER_PRODUCTS_DETAILS_KEY);
        mTextPrice.setText("￥"+ new BigDecimal(orderPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        mTextOrderName.setText(orderName);
        // 创建PayPresenter对象
        payPresenter = new PayPresenter(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int code = intent.getIntExtra("WXResult", 200);
        String action = intent.getAction();

        if (WXPayEntryActivity.ACTION.equalsIgnoreCase(action) && code != 200){
            DebugUtil.d("PayActivity onNewIntent code：" + code);
            if(code == 0){
                showSnakeView(containerView, "支付成功");
                peekInOrderDetailsActivity();
            }else if(code == -1){
                showSnakeView(containerView, "支付失败");
            }else if(code == -2){
                showSnakeView(containerView, "取消支付");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.alipay_layout)
    void onAliPayClick(){
        if (payWay == PayWay.Ali){
            return;
        }
        payWay = PayWay.Ali;
        aliRadio.setChecked(true);
        weixinRadio.setChecked(false);
    }

    @OnClick(R.id.weixin_layout)
    void onWeixinPayClick(){
        if (payWay == PayWay.Weixin){
            return;
        }
        payWay = PayWay.Weixin;
        aliRadio.setChecked(false);
        weixinRadio.setChecked(true);
    }

    @OnClick(R.id.radio_pay_ali)
    void onAliRadioClick(){
        onAliPayClick();
    }

    @OnClick(R.id.radio_pay_weixin)
    void onWeixinRadioClick(){
        onWeixinPayClick();
    }

    // 确认支付点击
    @OnClick(R.id.action_pay)
    void onPayClick(){
        if (payWay == PayWay.Ali){
            payPresenter.aliPay(orderId, orderDetails, orderDetails);
        }else {
            checkPayEnvironment();
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

    @Override
    public void onStartAlertOrderStatus() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onAlertOrderStatusSuccess() {
        dismissProgressDialog();
        peekInOrderDetailsActivity();
    }

    @Override
    public void onAlertOrderStatusFailure() {
        dismissProgressDialog();
        peekInOrderDetailsActivity();
    }

    @Override
    public void onStartRequestAliPay() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onRequestAliPaySuccess(String payInfo) {
        dismissProgressDialog();
        payByAli(payInfo);
    }

    @Override
    public void onRequestAliPayFailure() {
        dismissProgressDialog();
        showSnakeView(containerView, "支付出错，请重新登录");
    }


    @Override
    public void onStartWxPay() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onWxPaySuccess(WxPayEntity wxPayEntity) {
        dismissProgressDialog();
        if (wxPayEntity == null){
            showSnakeView(containerView, "支付出错，请重新登录");
        }else{
            wxPay(wxPayEntity);
        }
    }

    @Override
    public void onWxPayFailure() {
        dismissProgressDialog();
        showSnakeView(containerView, "支付出错，请重新登录");
    }

    /*
      * ---------------------------------阿里支付--------------------------------------
      */
    private final int SDK_PAY_FLAG = 1;

    private final int SDK_CHECK_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//				String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        DebugUtil.d("PayActivity handleMessage 支付成功");
                        peekInOrderDetailsActivity();
//                        payPresenter.onStart(orderId, "3");
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            // Util.show(ConfirmOrderActivity.this, "支付结果确认中");
                            showSnakeView(containerView, "支付结果确认中");
                            peekInOrderDetailsActivity();
//                            payPresenter.onStart(orderId, "6");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                           // Util.show(ConfirmOrderActivity.this, "支付失败");
                            showSnakeView(containerView, "支付取消");
                        }
                    }

                    break;
                }
                case SDK_CHECK_FLAG: {
                  //  Util.show(ConfirmOrderActivity.this, "检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        };
    };

    private void peekInOrderDetailsActivity(){
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra(OrderDetailsActivity.ORDER_KEY, orderId);
        startActivity(intent);
        finish();
    }

    private void payByAli(String payInfo) {
        Runnable payRunnable = () -> {
            // 构造PayTask 对象
            PayTask alipay = new PayTask(PayActivity.this);
            // 调用支付接口，获取支付结果
            String result = alipay.pay(payInfo, true);

            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /*-------------------------------------------微信支付方式---------------------------------------------------------*/
    private IWXAPI api;
    private void checkPayEnvironment(){
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        if (!api.isWXAppInstalled()){
            showSnakeView(containerView, "请先安装微信");
            return;
        }
        if (!api.isWXAppSupportAPI()){
            showSnakeView(containerView, "微信版本较低，无法进行支付");
            return;
        }
        payPresenter.wxPay(orderDetails, orderId, prepay_id);
    }

    private void wxPay(WxPayEntity wxPayEntity){
        PayReq req = new PayReq();
        req.appId			= wxPayEntity.getAppid();
        req.partnerId		= wxPayEntity.getPartnerid();
        req.prepayId		= wxPayEntity.getPrepayid();
        req.nonceStr		= wxPayEntity.getNoncestr();
        req.timeStamp		= wxPayEntity.getTimestamp();
        req.packageValue 	= "Sign=WXPay";
        req.sign			= wxPayEntity.getSign();
        // req.extData			= "app data"; // optional
        api.registerApp(Constants.APP_ID);
        boolean isSuccess = api.sendReq(req);
        if (!isSuccess){
            showSnakeView(containerView, "微信支付失败");
        }
    }
}
