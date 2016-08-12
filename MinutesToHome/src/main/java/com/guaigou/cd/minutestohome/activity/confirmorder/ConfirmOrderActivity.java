package com.guaigou.cd.minutestohome.activity.confirmorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.addressmgr.AddressActivity;
import com.guaigou.cd.minutestohome.activity.buyorder.BuyOrderActivity;
import com.guaigou.cd.minutestohome.activity.note.NoteActivity;
import com.guaigou.cd.minutestohome.entity.AddressEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.AddressUtil;
import com.guaigou.cd.minutestohome.util.MathUtil;
import com.rey.material.app.SimpleDialog;

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
    @Bind(R.id.Container) ScrollView mContainer; // 容器
    @Bind(R.id.layout_address_text) LinearLayout mLayoutAddressTextView;
    @Bind(R.id.text_address_hint) TextView mTextAddressHint;

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
        mTextProductPrice.setText(price);
        mTextRealPrice.setText(price);

        presenter = new ConfirmOrderPresenter(this);
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
        showSnakeView(mContainer, "积分不足100，不能继续添加");
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
        RegionEntity entity = RegionPrefs.getRegionData(this);
        if (entity == null || TextUtils.isEmpty(entity.getId())){
            showSnakeView(view, "小区信息出现错误，请退出软件重试");
            return;
        }
        presenter.onRequestOrder(entity.getId(), mTextNote.getText().toString(),
                mTextAddress.getText().toString(),  mTextDeliveryTime.getText().toString());
//
//        Intent intent = new Intent(this, BuyOrderActivity.class);
//        startActivity(intent);
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
            mTextAddress.setText(entity.getCommunity() + "\n" + entity.getDetailsAddress());
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
    }

    @Override
    public void onRequestSuccess() {
        dismissProgressDialog();
    }

}
