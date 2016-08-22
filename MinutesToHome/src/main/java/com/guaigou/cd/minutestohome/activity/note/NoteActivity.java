package com.guaigou.cd.minutestohome.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.util.KeyboardUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 * 编辑备注页面
 */
public class NoteActivity extends BaseActivity {

    @Bind(R.id.text_edit) EditText mTextEdit; // 输入框

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);

        mTextEdit.setText(getIntent().getStringExtra("note"));
        mTextEdit.setSelection(mTextEdit.getText().length());
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
        String note = mTextEdit.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("Note", note);
        setResult(RESULT_OK, intent);
        onBackClick();
    }
}
