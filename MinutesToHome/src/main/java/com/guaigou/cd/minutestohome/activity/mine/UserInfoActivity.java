package com.guaigou.cd.minutestohome.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.util.DimensUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-08-06.
 */
public class UserInfoActivity extends BaseActivity {

    @Bind(R.id.text_title) TextView mTextTitleView;
    @Bind(R.id.text_nickname) TextView mTextNickname;
    @Bind(R.id.text_sex) TextView mTextSexView;
    private AccountEntity accountEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);

        mTextTitleView.setText("账户信息");

        accountEntity = LoginData.INSTANCE.getAccountEntity(getApplicationContext());
        String name = accountEntity.getName();
        name = TextUtils.isEmpty(name) ? "设置昵称" : name;
        mTextNickname.setText(name);
    }

    @OnClick(R.id.img_back)
    public void onImgBackClick(){
        finish();
    }

    @OnClick(R.id.layout_sex)
    public void onSexLayoutClick(){
        if (popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }else {
            showListPopupWindow(mTextSexView);
        }

    }
    @OnClick(R.id.layout_nickname)
    public void onNickLayoutClick(){

    }

    private ListPopupWindow popupWindow;
    private void showListPopupWindow(View anchor){
        if (popupWindow == null){
            popupWindow = new ListPopupWindow(getApplicationContext());
        }
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        popupWindow.setHeight(-2);
        popupWindow.setWidth(DimensUtil.dp2px(getApplicationContext(), 100));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.item_text, android.R.id.text1,
                new String[]{"男", "女"});
        popupWindow.setAdapter(adapter);
        popupWindow.setListSelector(getResources().getDrawable(R.drawable.abc_generic_pressed));
        popupWindow.setOnItemClickListener((parent, view, position, id) -> {

        });
        popupWindow.setAnchorView(anchor);
        popupWindow.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
