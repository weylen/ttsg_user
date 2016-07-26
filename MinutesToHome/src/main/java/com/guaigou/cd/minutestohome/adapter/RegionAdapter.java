package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.RegionEntity;

import java.util.List;

/**
 * Created by weylen on 2016-07-22.
 */
public class RegionAdapter extends GenericBaseAdapter<RegionEntity>{

    public RegionAdapter(Context context, List<RegionEntity> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = getInflater().inflate(R.layout.item_region, parent, false);
            holder.nameView = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        RegionEntity entity = getItem(position);
        holder.nameView.setText(entity.getName());

        return convertView;
    }

    private class ViewHolder{
        private TextView nameView;
    }
}
