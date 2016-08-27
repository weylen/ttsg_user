package com.guaigou.cd.minutestohome.activity.mine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.prefs.LoginPrefs;
import com.guaigou.cd.minutestohome.util.DimensUtil;
import com.guaigou.cd.minutestohome.util.KeyboardUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-06.
 */
public class UserInfoActivity extends BaseActivity {

    @Bind(R.id.text_title) TextView mTextTitleView;
    @Bind(R.id.text_nickname) TextView mTextNickname;
    @Bind(R.id.text_sex) TextView mTextSexView;
    private AccountEntity accountEntity;
    private String sex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);

        mTextTitleView.setText("账户信息");

        accountEntity = LoginData.INSTANCE.getAccountEntity(getApplicationContext());
        String name = accountEntity.getNickname();
        name = TextUtils.isEmpty(name) ? "设置昵称" : name;
        mTextNickname.setText(name);

        sex = accountEntity.getSex();
        if (TextUtils.isEmpty(sex)){
            mTextSexView.setText("设置性别");
        }else if ("1".equalsIgnoreCase(sex)){
            mTextSexView.setText("男");
        }else {
            mTextSexView.setText("女");
        }
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
        EditText editText = new EditText(this);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("设置昵称")
                .setView(editText, 20, 20, 20, 20)
                .setNegativeButton("取消", (dialog, which) -> {
                    KeyboardUtil.hide(UserInfoActivity.this, editText);
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    KeyboardUtil.hide(UserInfoActivity.this, editText);
                    mTextNickname.setText(editText.getText());
                })
                .create();
        alertDialog.setOnShowListener(dialog -> KeyboardUtil.show(UserInfoActivity.this, editText));
        alertDialog.show();
    }

    /**
     * 点击保存按钮
     */
    @OnClick(R.id.text_save)
    public void onSaveClick(){
        String nickName = mTextNickname.getText().toString();
        if (TextUtils.isEmpty(nickName)){
            showSnakeView(mTextTitleView, "请输入昵称");
            return;
        }

        if (TextUtils.isEmpty(sex)){
            showSnakeView(mTextTitleView, "请选择性别");
            return;
        }

        remote(nickName);
    }

    private ListPopupWindow popupWindow;
    private void showListPopupWindow(View anchor){
        if (popupWindow == null){
            popupWindow = new ListPopupWindow(getApplicationContext());
        }
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        popupWindow.setHeight(-2);
        popupWindow.setWidth(DimensUtil.dp2px(getApplicationContext(), 100));
        String[] gender = new String[]{"男", "女"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.item_text, android.R.id.text1,
                gender);
        popupWindow.setAdapter(adapter);
        popupWindow.setListSelector(getResources().getDrawable(R.drawable.abc_generic_pressed));
        popupWindow.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0){
                sex = "1";
            }else {
                sex = "2";
            }
            mTextSexView.setText(gender[position]);
            popupWindow.dismiss();
        });
        popupWindow.setAnchorView(anchor);
        popupWindow.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    void remote(String nickName){
        showProgressDialog("保存中...");
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .saveAccountInfos(sex, nickName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showSnakeView(mTextTitleView, "保存失败，请重新操作");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            showSnakeView(mTextTitleView, "保存成功");
                            alertAccountInfo(nickName);
                            finish();
                        }else {
                            showSnakeView(mTextTitleView, "保存失败，请重新操作");
                        }
                    }
                });
    }

    private void alertAccountInfo(String nickName){
        accountEntity.setNickname(nickName);
        accountEntity.setSex(sex);
        LoginPrefs.setAccountInfo(this, accountEntity);
        LoginData.INSTANCE.setAccountEntity(accountEntity);
    }
}
