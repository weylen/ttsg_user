package com.guaigou.cd.minutestohome.activity.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.util.KeybordUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 * 意见反馈页面
 */
public class FeedbackActivity extends BaseActivity {

    @Bind(R.id.text_edit) EditText mTextEdit; // 输入框
    @Bind(R.id.text_title) TextView mTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);

        mTitleView.setText(R.string.Feedback);
        mTextEdit.setHint(R.string.FeedbackHint);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        KeybordUtil.hide(this);
        finish();
    }

    @OnClick(R.id.text_complete)
    void onCompleteClick(){
        //TODO..
    }
}
