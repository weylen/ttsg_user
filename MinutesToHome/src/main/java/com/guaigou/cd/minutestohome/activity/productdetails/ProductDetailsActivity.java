package com.guaigou.cd.minutestohome.activity.productdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-06-18.
 */
public class ProductDetailsActivity extends BaseActivity {

    @Bind(R.id.text_title) TextView mTitleView;
    @Bind(R.id.Container) View containerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);
        ButterKnife.bind(this);

        mTitleView.setText("商品详情");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.img_back)
    public void onImgBack(){
        finish();
    }

    @OnClick(R.id.fab_add)
    void onFabAddClick(){

    }
}
