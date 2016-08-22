package com.guaigou.cd.minutestohome.activity.addressmgr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.AddressEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.KeyboardUtil;
import com.guaigou.cd.minutestohome.util.ValidateUtil;
import com.rey.material.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-06-18.
 */
public class NewAddressActivity extends BaseActivity {

    @Bind(R.id.text_name) EditText mTextName;
    @Bind(R.id.text_phone) EditText mTextPhone;
    @Bind(R.id.text_community) TextView mTextCommunity;
    @Bind(R.id.text_details) EditText mTextDetails;
    @Bind(R.id.switch_default) Switch mSwitchDefault;
    @Bind(R.id.Container) View containerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_new);
        ButterKnife.bind(this);

        mTextCommunity.setText(RegionPrefs.getRegionData(getApplicationContext()).getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardUtil.hide(this, mTextName);
        ButterKnife.unbind(this);
    }

    @Override
    public void onBackPressed() {
        onBackClick();
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        KeyboardUtil.hide(this, mTextName);
        finish();
    }

    @OnClick(R.id.text_save)
    void onSaveClick(){
        validateAndSave();
    }

    private void validateAndSave() {
        String name = mTextName.getText().toString();
        String phone = mTextPhone.getText().toString();
        String community = mTextCommunity.getText().toString();
        String detail = mTextDetails.getText().toString();
        boolean isDefaultAddr = mSwitchDefault.isChecked();

        if (TextUtils.isEmpty(name)) {
            showSnakeView(containerView, "联系人不能为空");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showSnakeView(containerView, "电话号码不能为空");
            return;
        }

        if (!ValidateUtil.isMobile(phone)) {
            showSnakeView(containerView, "电话号码不正确");
            return;
        }

        if (TextUtils.isEmpty(community)) {
            showSnakeView(containerView, "小区地址不能为空");
            return;
        }

        if (TextUtils.isEmpty(detail)) {
            showSnakeView(containerView, "详细地址不能为空");
            return;
        }

        AddressEntity entity = new AddressEntity();
        entity.setContacts(name);
        entity.setMobilePhone(phone);
        entity.setCommunity(community);
        entity.setDetailsAddress(detail);
        entity.setDefaultAddress(isDefaultAddr);

        Intent intent = new Intent();
        intent.putExtra("Entity", entity);
        setResult(RESULT_OK, intent);

        onBackClick();
    }
}
