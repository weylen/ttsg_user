package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.AddressEntity;

import java.util.List;

/**
 * Created by Administrator on 2016-06-18.
 */
public class AddressAdapter extends GenericBaseAdapter<AddressEntity>{

    private int themeColor;
    private int grayColor;

    public AddressAdapter(Context context, List<AddressEntity> data) {
        super(context, data);
        themeColor = context.getResources().getColor(R.color.themeColor);
        grayColor = context.getResources().getColor(R.color.grayText);
    }

    private OnEditActionListener onEditActionListener;

    public void setOnEditActionListener(OnEditActionListener onEditActionListener) {
        this.onEditActionListener = onEditActionListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = getInflater().inflate(R.layout.item_address, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.text_name);
        TextView phoneView = (TextView) convertView.findViewById(R.id.text_phone);
        TextView addressView = (TextView) convertView.findViewById(R.id.text_address);
        ImageView img_arrow = (ImageView) convertView.findViewById(R.id.img_arrow);

        final AddressEntity entity = getItem(position);
        nameView.setText(entity.getContacts());
        phoneView.setText(entity.getMobilePhone());
        addressView.setText(entity.getCommunity()+entity.getDetailsAddress());
        // 编辑
        convertView.findViewById(R.id.img_edit).setOnClickListener(v -> {
            if (onEditActionListener != null){
                onEditActionListener.onEditAction(position, entity);
            }
        });
        // 判断是否为默认地址
        if (entity.isDefaultAddress()){
            img_arrow.setVisibility(View.VISIBLE);
            nameView.setTextColor(themeColor);
            phoneView.setTextColor(themeColor);
        }else {
            img_arrow.setVisibility(View.GONE);
            nameView.setTextColor(grayColor);
            phoneView.setTextColor(grayColor);
        }

        return convertView;
    }

    public interface OnEditActionListener{
        void onEditAction(int position, AddressEntity entity);
    }
}
