package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.Enumeration;
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = getInflater().inflate(R.layout.view_product_bigtype, parent, false);
        }
        MarketDataEntity entity = getItem(position);

        final RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.item_radio);
        radioButton.setChecked(position == checkedPosition);
        radioButton.setText(entity.getName());

        final TextView numberView = (TextView) convertView.findViewById(R.id.item_num);
        int num = entity.getNumber();
        numberView.setText(String.valueOf(num));
        numberView.setVisibility(num <= 0 ? View.GONE : View.VISIBLE);

        return convertView;
    }
}
