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

    public static final String TAG = ProductDetailsActivity.class.getName();

    @Bind(R.id.text_title) TextView mTitleView;
    @Bind(R.id.layout_price) View addView;
    @Bind(R.id.layout_num) View controllerNumView;
    @Bind(R.id.text_num) TextView numView;

    private int num = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_productdetails);
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

    @OnClick(R.id.img_add)
    void onImgAddClick(){
        num = 1;
        numView.setText(String.valueOf(num));
        addView.setVisibility(View.GONE);
        controllerNumView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.img_num_add)
    public void onAddClick(){
        num++;
        numView.setText(String.valueOf(num));
    }

    @OnClick(R.id.img_num_les)
    public void onLesClick(){
        num--;
        if (num <= 0){
            num = 0;
            addView.setVisibility(View.VISIBLE);
            controllerNumView.setVisibility(View.GONE);
        }
        numView.setText(String.valueOf(num));
    }
}
