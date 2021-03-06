package com.guaigou.cd.minutestohome.activity.confirmorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.addressmgr.AddressActivity;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.activity.market.ShopStatusData;
import com.guaigou.cd.minutestohome.activity.orderdetails.OrderDetailsActivity;
import com.guaigou.cd.minutestohome.activity.note.NoteActivity;
import com.guaigou.cd.minutestohome.activity.pay.PayActivity;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.AddressEntity;
import com.guaigou.cd.minutestohome.entity.ConfirmOrderEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.AddressUtil;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.util.MathUtil;
import com.guaigou.cd.minutestohome.view.OrderProductsDetailsView;
import com.rey.material.app.SimpleDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 */
public class ConfirmOrderActivity extends BaseActivity implements ConfirmOrderView{
    /**
     * 请求编辑收获地址
     */
    public static final int REQUEST_ADDRESS_CODE = 100;
    /**
     * 请求编辑备注
     */
    public static final int REQUEST_EDITNOTE_CODE = 101;

    @Bind(R.id.text_title) TextView mTextTitle; // 标题
    @Bind(R.id.text_name) TextView mTextName; // 地址标题 姓名和电话号码
    @Bind(R.id.text_address) TextView mTextAddress; // 地址详情
    @Bind(R.id.text_note) TextView mTextNote; // 备注
    @Bind(R.id.text_delivery_time) TextView mTextDeliveryTime; // 送达时间
    @Bind(R.id.text_score_num) TextView mTextScoreNumView; // 积分数量
    @Bind(R.id.text_product_price) TextView mTextProductPrice; // 商品价格
    @Bind(R.id.text_freight_price) TextView mTextFreightPrice; // 运费
    @Bind(R.id.text_score_price) TextView mTextScorePrice; // 积分减免
    @Bind(R.id.text_real_price) TextView mTextRealPrice; // 实际支付
    @Bind(R.id.layout_address_text) LinearLayout mLayoutAddressTextView;
    @Bind(R.id.text_address_hint) TextView mTextAddressHint;
    @Bind(R.id.layout_products) OrderProductsDetailsView orderProductsDetailsView;
    @Bind(R.id.Container) View containerView;
    @Bind(R.id.text_confirmorder) TextView mConfrimOrderView;
    @Bind(R.id.text_delivery_hint) TextView mDeliveryHintView; // 配送费提示

    private AddressEntity addressEntity;

    private ConfirmOrderPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmorder);
        ButterKnife.bind(this);

        mTextTitle.setText("确认订单");

        // 获取默认的收获地址
        AddressEntity entity = AddressUtil.getDefaultAddress(this);
        setupAddress(entity);
        // 设置商品价格
        String price = MathUtil.calculate();
        // 商品价格
        mTextProductPrice.setText("￥" + price);
        // 配送费提示
        if (LocaleUtil.isOnNightTime()){
            mDeliveryHintView.setText("当前时间属于直营模式，不收取配送费");
        }else{
            mDeliveryHintView.setText(String.format("提示：商品总金额不足%s元会收取%s元的配送费", ShopStatusData.INSTANCE.fareLimit, ShopStatusData.INSTANCE.fare));
        }

        String fare = ShopStatusData.INSTANCE.fare;
        if (LocaleUtil.pareseDouble(fare) == 0){
            mDeliveryHintView.setVisibility(View.GONE);
        }else {
            mDeliveryHintView.setVisibility(View.VISIBLE);
        }
        // 运费
        boolean isNeedFreight = isNeedFreight(price);
        mTextFreightPrice.setText(isNeedFreight ? "￥" + ShopStatusData.INSTANCE.fare : "￥0");
        // 实际支付
        mTextRealPrice.setText(isNeedFreight?getRealPay(price) : price);

        orderProductsDetailsView.setDataAndNotify3(CartData.INSTANCE.getData());

        presenter = new ConfirmOrderPresenter(this);
    }

    private String getRealPay(String price){
        return new BigDecimal(price).add(new BigDecimal(ShopStatusData.INSTANCE.fare)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 判断是否需要运费
     * @return
     */
    private boolean isNeedFreight(String price){
        if (LocaleUtil.isOnNightTime()){ // 如果是在夜间模式 都不要配送费
            DebugUtil.d("ConfirmOrderActivity 当前时间属于夜间模式，不收取配送费：");
            return false;
        }
        boolean isNeedFreight = false;
        try{
            double d = Double.parseDouble(price);
            if (d < LocaleUtil.pareseDouble(ShopStatusData.INSTANCE.fareLimit)){
                isNeedFreight = true;
            }
        }catch (NumberFormatException e){}
        return isNeedFreight;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    // 返回键
    @OnClick(R.id.img_back)
    void onImgBack() {
        finish();
    }

    /**
     * 地址点击
     */
    @OnClick(R.id.layout_address)
    void onLayoutAddressClick() {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra("returndata", true);
        startActivityForResult(intent, REQUEST_ADDRESS_CODE);
    }

    /**
     * 备注点击
     */
    @OnClick(R.id.layout_note)
    void onLayoutNoteClick() {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("returndata", true);
        intent.putExtra("note", mTextNote.getText().toString());
        startActivityForResult(intent, REQUEST_EDITNOTE_CODE);
    }

    /**
     * 送货时间点击
     */
    @OnClick(R.id.layout_delivery_time)
    void onDeliveryTimeClick() {
        showPickerTimeDialog();
    }

    /**
     * 积分提示点击
     */
    @OnClick(R.id.text_score_hint)
    void onScoreHint() {
        showScoreHintDialog();
    }

    /**
     * 积分数量减少点击
     */
    @OnClick(R.id.text_score_les)
    void onScoreLesClick() {
        String text = mTextScoreNumView.getText().toString();
        if (text.matches("^\\d+$")) {
            int num = Integer.parseInt(text);
            num--;
            if (num < 0) {
                num = 0;
            }
            mTextScoreNumView.setText(String.valueOf(num));
        }
    }

    /**
     * 积分数量增加点击
     */
    @OnClick(R.id.text_score_add)
    void onScoreAddClick() {
        showSnakeView(containerView, "积分不足100，不能继续添加");
    }


    /**
     * 确认下单
     */
    @OnClick(R.id.text_confirmorder)
    void onConfrimOrderClick(View view) {
        if (addressEntity == null){
            showSnakeView(view, "请选择收货地址");
            return;
        }

        if(TextUtils.isEmpty(addressEntity.getContacts()) || TextUtils.isEmpty(addressEntity.getContacts())){
            showSnakeView(view, "联系人和电话号码不能为空");
            return;
        }

        RegionEntity entity = RegionPrefs.getRegionData(this);
        if (entity == null || TextUtils.isEmpty(entity.getId())){
            showSnakeView(view, "小区信息出现错误，请退出软件重试");
            return;
        }
        if (!entity.getName().equalsIgnoreCase(addressEntity.getCommunity())){
            showSnakeView(view, "小区地址和收货地址不一样 不能下单");
            return;
        }

        presenter.onRequestOrder(entity.getId(), mTextNote.getText().toString(),
                mTextAddress.getText().toString(),  mTextDeliveryTime.getText().toString(),
                addressEntity.getContacts(), addressEntity.getMobilePhone());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShopStatusData data = ShopStatusData.INSTANCE;
        if (data.status != 1){
            mConfrimOrderView.setText("未营业");
            mConfrimOrderView.setEnabled(false);
        }else if (!LocaleUtil.isOnTime() && !LocaleUtil.isOnNightTime()){
            mConfrimOrderView.setText("不在营业时间");
            mConfrimOrderView.setEnabled(false);
        }else {
            mConfrimOrderView.setText("确认下单");
            mConfrimOrderView.setEnabled(true);
        }
    }

    private void showScoreHintDialog() {
        SimpleDialog scoreDialog = new SimpleDialog(this);
        scoreDialog.title("积分使用规则");
        scoreDialog.message("1.积分与现金的兑换比例为100:1，目前仅支持1元的倍数，如：1元 2元等\n" +
                "2.订单抵扣金额不能超该订单的总金的一半\n" +
                "3.每人每天只能使用一次积分用于购买时抵扣现金")
                .positiveAction("我知道了")
                .positiveActionClickListener(v -> {
                    scoreDialog.dismiss();
                }).showDivider(true)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADDRESS_CODE: // 地址编辑器
                    AddressEntity entity = (AddressEntity) data.getSerializableExtra("AddressEntity");
                    setupAddress(entity);
                    break;
                case REQUEST_EDITNOTE_CODE: // 备注编辑
                    String note = data.getStringExtra("Note");
                    mTextNote.setText(note);
                    break;
            }
        }
    }

    /**
     * 设置地址信息
     *
     * @param entity
     */
    private void setupAddress(AddressEntity entity) {
        this.addressEntity = entity;
        if (entity == null) {
            mLayoutAddressTextView.setVisibility(View.INVISIBLE);
            mTextAddressHint.setVisibility(View.VISIBLE);
        } else {
            mTextName.setText(entity.getContacts() + "   " + entity.getMobilePhone());
            mTextAddress.setText(entity.getCommunity() + "   " + entity.getDetailsAddress());
            mLayoutAddressTextView.setVisibility(View.VISIBLE);
            mTextAddressHint.setVisibility(View.GONE);
        }
    }

    /**
     * 创建送达时间
     *
     * @return
     */
    private ArrayList<String> createTimeList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("尽快送达");
        Calendar calendar = Calendar.getInstance();
        // 获取分钟数
        int minutes = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (minutes <= 30) { // 如果当前时间小于 30 如 21：23 则送达时间改为 21：30 ~ 22：00
            minutes = 30;
        } else {
            minutes = 0;
            hour += 1;
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        while (hour <= 24 && day == calendar.get(Calendar.DAY_OF_MONTH)) {
            String timeStart = format(calendar.getTimeInMillis());
            calendar.add(Calendar.MINUTE, 30);
            String timeEnd = format(calendar.getTimeInMillis());
            list.add(timeStart + "~" + timeEnd);

        }
        return list;
    }

    private String format(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return format.format(new Date(time));
    }

    private void showPickerTimeDialog() {
        ArrayList<String> data = createTimeList();
        OptionsPickerView optionsPickerView = new OptionsPickerView(this);
        optionsPickerView.setTitle("送达时间");
        optionsPickerView.setPicker(data);
        optionsPickerView.setSelectOptions(0);
        optionsPickerView.setCyclic(false);
        optionsPickerView.setOnoptionsSelectListener((options1, options2, options3) -> {
            mTextDeliveryTime.setText(data.get(options1));
        });
        optionsPickerView.show();
    }

    @Override
    public void onStartRequestOrder() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onRequestFailure() {
        dismissProgressDialog();
        showSnakeView(containerView, "下单失败，请重新操作");
    }

    @Override
    public void onRequestSuccess(ConfirmOrderEntity entity) {
        dismissProgressDialog();
        if (entity != null && !TextUtils.isEmpty(entity.getOrderNumber())){
            CartData.INSTANCE.clear();
            Intent intent = new Intent(this, PayActivity.class);
            intent.putExtra(PayActivity.ORDER_ID_KEY, entity.getOrderNumber());
            intent.putExtra(PayActivity.ORDER_PRICE_KEY, entity.getT_price());
            startActivity(intent);
            finish();
        }else {
            showSnakeView(containerView, "下单失败，请重新操作");
        }
    }

}
