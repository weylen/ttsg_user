package com.guaigou.cd.minutestohome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016-06-18.
 */
public class Fragment_ProductDetails extends BaseFragment{

    public static final String TAG = Fragment_ProductDetails.class.getName();

    private View addView;
    private View controllerNumView;
    private TextView numView;
    private int num = 0;

    @Override
    public int layoutId() {
        return R.layout.fragment_productdetails;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupHeadInfo(view, "商品详情");

        addView = view.findViewById(R.id.layout_price);
        controllerNumView = view.findViewById(R.id.layout_num);
        numView = (TextView) view.findViewById(R.id.text_num);

        view.findViewById(R.id.img_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = 1;
                numView.setText(String.valueOf(num));
                addView.setVisibility(View.GONE);
                controllerNumView.setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.img_num_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num++;
                numView.setText(String.valueOf(num));
            }
        });

        view.findViewById(R.id.img_num_les).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num--;
                if (num <= 0){
                    num = 0;
                    addView.setVisibility(View.VISIBLE);
                    controllerNumView.setVisibility(View.GONE);
                }
                numView.setText(String.valueOf(num));

            }
        });
    }
}
