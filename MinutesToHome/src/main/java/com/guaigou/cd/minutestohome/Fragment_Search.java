package com.guaigou.cd.minutestohome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.GridView;

import com.guaigou.cd.minutestohome.activity.market.MarketProductAdapter;

/**
 * Created by Administrator on 2016-06-18.
 */
public class Fragment_Search extends BaseFragment{

    public static final String TAG = Fragment_Search.class.getName();

    private MarketProductAdapter adapter;
    private GridView gridView;

    @Override
    public int layoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // 取消
        view.findViewById(R.id.text_cancel).setOnClickListener(v -> getFragmentManager().popBackStack());
        // 搜索
        view.findViewById(R.id.img_search).setOnClickListener(v -> {

        });


        gridView = (GridView) view.findViewById(R.id.grid_product);
        adapter = new MarketProductAdapter(getContext(), null);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view1, position, id) -> ((MainActivity)getActivity()).replaceFragment(new Fragment_ProductDetails(), Fragment_ProductDetails.TAG, true));
    }
}
