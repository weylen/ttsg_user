package com.guaigou.cd.minutestohome.activity.pay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.ExpandedMenuView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.orderdetails.OrderDetailsActivity;
import com.guaigou.cd.minutestohome.entity.OrderProductsEntity;
import com.guaigou.cd.minutestohome.entity.WxPayEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.pay.PayResult;
import com.guaigou.cd.minutestohome.pay.SignUtils;
import com.guaigou.cd.minutestohome.pay.Util;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.util.MD5;
import com.guaigou.cd.minutestohome.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.LinkedList;
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
    private String orderPrice;
    private List<OrderProductsEntity> productsEntityList;
    private String orderName = "商品货款";
    private String orderDetails = "商品货款";

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
        productsEntityList = (List<OrderProductsEntity>) intent.getSerializableExtra(ORDER_PRODUCTS_DETAILS_KEY);
        mTextPrice.setText("￥"+orderPrice);
        mTextOrderName.setText(orderName);
        payPresenter = new PayPresenter(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (WXPayEntryActivity.ACTION.equalsIgnoreCase(action)){
            int errCode = intent.getIntExtra("WXResult", -300);
            DebugUtil.d("PayActivity onNewIntent errCode;" + errCode);

            if(errCode == 0){
                Toast.makeText(this,"支付成功!",Toast.LENGTH_SHORT).show();
                peekInOrderDetailsActivity();
                return;
            }else if(errCode == -1){
                Toast.makeText(this,"支付失败!",Toast.LENGTH_SHORT).show();
                peekInOrderDetailsActivity();
                return;
            }else if(errCode == -2){
                Toast.makeText(this,"取消支付!",Toast.LENGTH_SHORT).show();
            }
        }
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
            payPresenter.requestRsaPrivate();
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
    public void onStartRequestRsaPrivate() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onRequestRsaPrivateSuccess(String rsaPrivate) {
        dismissProgressDialog();
        // TODO.. 修改支付价格为orderPrice
        payByAli(orderName, orderDetails, "0.01", orderId, rsaPrivate);
    }

    @Override
    public void onRequestRasPrivateFailure() {
        dismissProgressDialog();
        showSnakeView(containerView, "连接服务器失败，请重试");
    }

    @Override
    public void onStartWxPay() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onWxPaySuccess(WxPayEntity wxPayEntity) {
        dismissProgressDialog();
        if (wxPayEntity != null && "SUCCESS".equalsIgnoreCase(wxPayEntity.getResult_code())){
            wxPay(wxPayEntity);
        }else {
            String errorMessage = "微信支付异常，请重试";
            if (wxPayEntity != null && "FAIL".equalsIgnoreCase(wxPayEntity.getResult_code())){
                errorMessage = wxPayEntity.getErr_code_des();
            }
            errorMessage = TextUtils.isEmpty(errorMessage) ? "微信支付异常，请重试" : errorMessage;
            showSnakeView(containerView, errorMessage);
        }
    }

    @Override
    public void onWxPayFailure() {
        dismissProgressDialog();
        showSnakeView(containerView, "连接服务器失败，请重试");
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

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     * @param goodsName
     *            商品名
     * @param description
     *            商品描述
     * @param price
     *            商品价格 for:"0.01"
     */
    private void payByAli(String goodsName, String description, String price, String orderId, String rsaPrivate) {
        if (TextUtils.isEmpty(Constants.PARTNER)
                || TextUtils.isEmpty(rsaPrivate)
                || TextUtils.isEmpty(Constants.SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",(dialog, i) -> {
                                finish(); })
                    .show();
            return;
        }
        // 订单
        String orderInfo = getOrderInfo(goodsName, description, price, orderId);
        // 对订单做RSA 签名
        String sign = sign(orderInfo, rsaPrivate);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

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


    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price, String orderId) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constants.PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Constants.SELLER + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";
        // 商品金额 // total_amount total_fee
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + Constants.ALI_NOTIFY_URL + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";
        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     * @param content 待签名订单信息
     */
    private String sign(String content, String rsaPrivate) {
        return SignUtils.sign(content, rsaPrivate);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /*-------------------------------------------微信支付方式---------------------------------------------------------*/
    private IWXAPI api;
    private void checkPayEnvironment(){
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported){
            showSnakeView(containerView, "微信未安装或版本较低，无法进行支付");
            return;
        }
        String describe = "天天闪购-商品货款";
        //TODO 记得修改商品金额 orderPrice
        payPresenter.wxPay(describe, orderId, "0.01");
    }

    private void wxPay(WxPayEntity wxPayEntity){
        PayReq req = new PayReq();
        req.appId			= wxPayEntity.getAppid();
        req.partnerId		= wxPayEntity.getMch_id();
        req.prepayId		= wxPayEntity.getPrepay_id();
        req.nonceStr		= wxPayEntity.getNonce_str();
        req.timeStamp		= String.valueOf(genTimeStamp());
        req.packageValue 	= "Sign=WXPay";
//        req.sign			= wxPayEntity.getSign();
        req.extData			= "app data"; // optional

        List<NameValuePair> signParams = new LinkedList<>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);
        api.registerApp(Constants.APP_ID);
        boolean isSuccess = api.sendReq(req);
        if (!isSuccess){
            showSnakeView(containerView, "微信支付失败");
        }
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append("tiantianshangou20160819Securitys");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }
}
