package com.guaigou.cd.minutestohome.activity.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.RespSubscribe;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.KeyboardUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-24.
 * 意见反馈页面
 */
public class FeedbackActivity extends BaseActivity {

    @Bind(R.id.text_edit) EditText mTextEdit; // 输入框
    @Bind(R.id.text_title) TextView mTitleView;
    @Bind(R.id.Container) View containerView;

    private boolean isRemeberContent = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);

        mTitleView.setText(R.string.Feedback);
        mTextEdit.setHint(R.string.FeedbackHint);
        mTextEdit.setText(FeedbackData.INSTANCE.content);
        mTextEdit.setSelection(mTextEdit.length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        KeyboardUtil.hide(this, mTextEdit);
        finish();
    }

    @OnClick(R.id.text_complete)
    void onCompleteClick(){
        String text = mTextEdit.getText().toString();
        if (TextUtils.isEmpty(text)){
            showSnakeView(containerView, "请输入反馈内容");
            return;
        }

        RegionEntity entity = RegionPrefs.getRegionData(getApplicationContext());
        if (entity == null || TextUtils.isEmpty(entity.getId())){
            showSnakeView(containerView, "小区选择出现错误，请退出后重试");
            return;
        }
        showProgressDialog("提交中...");
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .feedBack(entity.getId(), mTextEdit.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showSnakeView(containerView, "请求失败");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            showSnakeView(containerView, "反馈成功");
                            isRemeberContent = false;
                            FeedbackData.INSTANCE.content = "";
                        }else {
                            showSnakeView(containerView, "反馈失败，请重新操作");
                        }
                    }
                }));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRemeberContent){
            FeedbackData.INSTANCE.content = mTextEdit.getText().toString();
        }
    }
}
