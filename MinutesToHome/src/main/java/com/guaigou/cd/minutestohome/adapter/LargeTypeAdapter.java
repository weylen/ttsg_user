package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.List;

/**
 * Created by weylen on 2016-08-04.
 */
public class LargeTypeAdapter extends GenericBaseAdapter<MarketDataEntity>{

    private int checkedPosition = 0;

    public LargeTypeAdapter(Context context, List<MarketDataEntity> data) {
        super(context, data);
    }

    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
        DebugUtil.d("LargeTypeAdapter setCheckedPosition ，，，，position:" + checkedPosition);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RadioButton radioButton;
        if (convertView == null){
            radioButton = (RadioButton) getInflater().inflate(R.layout.view_product_bigtype, parent, false);
        }else {
            radioButton = (RadioButton) convertView;
        }

        radioButton.setChecked(position == checkedPosition);
        radioButton.setText(getItem(position).getName());

        return radioButton;
    }
}
