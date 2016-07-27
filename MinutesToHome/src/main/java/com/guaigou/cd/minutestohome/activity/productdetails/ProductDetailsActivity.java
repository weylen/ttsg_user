package com.guaigou.cd.minutestohome.activity.productdetails;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.MainActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.rey.material.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-06-18.
 */
public class ProductDetailsActivity extends BaseActivity {

    @Bind(R.id.text_title) TextView mTitleView;
    @Bind(R.id.text_product_name) TextView mNameView;
    @Bind(R.id.Container) View containerView;
    @Bind(R.id.image) ImageView mImageView;
    @Bind(R.id.text_price) TextView mTextPrice;
    @Bind(R.id.text_format) TextView mTextFormat;
    @Bind(R.id.text_stock_num) TextView mTextStockNum;
    @Bind(R.id.text_promotion) TextView mTextPromotionView;
    @Bind(R.id.text_old_price) TextView mOldPriceView;
    @Bind(R.id.button_intocart) TextView productNumView;
    private ProductEntity productEntity;
    private int cartProductNumber = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);
        ButterKnife.bind(this);

        mTitleView.setText("商品详情");
        productEntity = getIntent().getParcelableExtra("Entity");
        setupImageParams();
        setupProductsInfo();
    }

    /**
     * 设置图片参数
     */
    private void setupImageParams(){
        int width = getResources().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        mImageView.setLayoutParams(params);
    }

    /**
     * 设置产品信息
     */
    private void setupProductsInfo(){
        String promotePrice = productEntity.getPromote();// 获取促销价格

        if (TextUtils.isEmpty(promotePrice)){ // 促销价为空
            mTextPromotionView.setVisibility(View.GONE);
            mOldPriceView.setVisibility(View.GONE);

            mTextPrice.setText("￥" + productEntity.getPrice());
        }else {
            mTextPromotionView.setVisibility(View.VISIBLE);
            mOldPriceView.setVisibility(View.VISIBLE);
            mOldPriceView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            // 原价格
            mOldPriceView.setText("￥" + productEntity.getPrice());
            // 促销信息
            mTextPromotionView.setText(productEntity.getBegin()+"到"+productEntity.getEnd()+productEntity.getInfo());
            // 促销价格
            mTextPrice.setText("￥" + promotePrice);
        }
        mNameView.setText(productEntity.getName());
        mTextFormat.setText("规格：" + productEntity.getStandard());
        mTextStockNum.setText("库存：" + productEntity.getReserve()); // 库存

        // 加载图片
        Glide.with(this)
                .load(Constants.BASE_URL + productEntity.getImg())
                .fitCenter()
                .placeholder(R.mipmap.img_load_default)
                .crossFade()
                .dontAnimate()
                .error(R.mipmap.img_load_error)
                .into(mImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartProductNumber = CartData.INSTANCE.getNumberAll();
        productNumView.setText("共" + cartProductNumber + "件商品");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.img_back)
    public void onImgBack() {
        finish();
    }

    @OnClick(R.id.button_intocart)
    public void onIntoCartClick(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("ChooseCart", true);
        startActivity(intent);
    }

    @OnClick(R.id.fab_add)
    void onFabAddClick() {
        //TODO 请求服务器
        // 检查是否还有库存
//        String reserve = productEntity.getReserve();
//        int num = ParseUtil.parseInt(reserve);
//        // 要么库存小于0 要么添加此类的商品已经达到库存的数量
//        if (num <= 0 || CartData.INSTANCE.getNumber(productEntity.getId()) >= num){
//            showSnakeView(containerView, "库存不足，无法添加");
//            return;
//        }
        CartData.INSTANCE.numberAdd(productEntity);
        productNumView.setText("共" + (++cartProductNumber) + "件商品");
    }
}
