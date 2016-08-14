package com.guaigou.cd.minutestohome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.entity.OrderDetailsEntity;
import com.guaigou.cd.minutestohome.entity.OrderDetailsProductsEntity;
import com.guaigou.cd.minutestohome.entity.OrderProductsEntity;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.util.MathUtil;

import java.util.List;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderProductsDetailsView extends LinearLayout{

    public OrderProductsDetailsView(Context context) {
        super(context);
        init();
    }

    public OrderProductsDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrderProductsDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setDataAndNotify1(List<OrderProductsEntity> data){
        removeAllViews();
        if (LocaleUtil.isListEmpty(data)){
            return;
        }
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        for (OrderProductsEntity entity : data){
            View view = mInflater.inflate(R.layout.layout_orderproducts_details, this, false);
            // 设置信息
            TextView nameView = (TextView) view.findViewById(R.id.text_name);
            nameView.setText(entity.getName() + "  x" + entity.getAmount());
            TextView priceView = (TextView) view.findViewById(R.id.text_price);
            priceView.setText("￥" + entity.getTotal());
            addView(view);
        }
    }

    public void setDataAndNotify2(List<OrderDetailsProductsEntity> data){
        removeAllViews();
        if (LocaleUtil.isListEmpty(data)){
            return;
        }
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        for (OrderDetailsProductsEntity entity : data){
            View view = mInflater.inflate(R.layout.layout_orderproducts_details, this, false);
            // 设置信息
            TextView nameView = (TextView) view.findViewById(R.id.text_name);
            nameView.setText(entity.getName() + "  x" + entity.getAmount());
            TextView priceView = (TextView) view.findViewById(R.id.text_price);
            priceView.setText("￥" + entity.getTotal());
            addView(view);
        }
    }

    public void setDataAndNotify3(List<CartEntity> data){
        removeAllViews();
        if (LocaleUtil.isListEmpty(data)){
            return;
        }
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        for (CartEntity entity : data){
            View view = mInflater.inflate(R.layout.layout_orderproducts_details, this, false);
            // 设置信息
            TextView nameView = (TextView) view.findViewById(R.id.text_name);
            nameView.setText(entity.getName() + "  x" + entity.getAmount());
            TextView priceView = (TextView) view.findViewById(R.id.text_price);
            if (LocaleUtil.hasPromotion(entity.getPromote())){
                priceView.setText("￥" + MathUtil.mul(entity.getAmount() + "", entity.getPromote()));
            }else {
                priceView.setText("￥" + MathUtil.mul(entity.getAmount() + "", entity.getSalePrice()));
            }
            addView(view);
        }
    }
}
