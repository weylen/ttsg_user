package com.guaigou.cd.minutestohome.activity.pay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.pay.PayResult;
import com.guaigou.cd.minutestohome.pay.SignUtils;
import com.guaigou.cd.minutestohome.pay.Util;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
            payByAli(orderName, orderDetails, orderPrice, orderId);
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
                        payPresenter.onStart(orderId, "3");
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            // Util.show(ConfirmOrderActivity.this, "支付结果确认中");
                            showSnakeView(containerView, "支付结果确认中");
                            payPresenter.onStart(orderId, "6");
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
    private void payByAli(String goodsName, String description, String price, String orderId) {
        if (TextUtils.isEmpty(Constants.PARTNER)
                || TextUtils.isEmpty(Constants.RSA_PRIVATE)
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
        String sign = sign(orderInfo);
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
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + Constants.ALI_NOTIFA_URL + "\"";
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
    private String sign(String content) {
        return SignUtils.sign(content, Constants.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
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

    /*-------------------------------------------微信支付方式---------------------------------------------------------*/

    private IWXAPI api;
    private void checkPayEnvironment(){
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported){
            showSnakeView(containerView, "当前版本不支持微信支付");
            return;
        }
        wxPay();
    }

    private void wxPay(){
        String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
        showProgressDialog("获取订单中...");
        try{
            byte[] buf = Util.httpGet(url);
            if (buf != null && buf.length > 0) {
                String content = new String(buf);
                DebugUtil.e("PayActivity content:" + content);
                JSONObject json = new JSONObject(content);
                if(null != json && !json.has("retcode") ){
                    PayReq req = new PayReq();
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
                    req.extData			= "app data"; // optional
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    boolean isSend = api.sendReq(req);
                    if (!isSend){
                        showSnakeView(containerView, "请先安装微信");
                    }
                }else{
                    showSnakeView(containerView, "返回错误"+json.getString("retmsg"));
                }
            }else{
                showSnakeView(containerView, "服务器请求错误");
            }
        }catch(Exception e){
            showSnakeView(containerView, "异常："+e.getMessage());
        }
    }
}
