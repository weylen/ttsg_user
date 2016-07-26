package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.ProductEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-06-18.
 */
public class CartAdapter extends GenericBaseAdapter<ProductEntity>{

    private boolean isEditable;
    private SparseBooleanArray statusArray;

    public CartAdapter(Context context, List<ProductEntity> data) {
        super(context, data);
        statusArray = new SparseBooleanArray();
    }

    public void setEditable(boolean isEditable){
        this.isEditable = isEditable;
        this.notifyDataSetChanged();
    }

    public void setSelectAll(boolean isSelectAll){
        int count = getCount();
        for (int i = 0; i < count; i++){
            statusArray.put(i, isSelectAll);
        }
        notifyDataSetChanged();
    }

    public void resetStatusArray(){
        statusArray.clear();
    }

    /**
     * 获取需要删除的数据
     * @return
     */
    public List<ProductEntity> getDeleteData(){
        List<ProductEntity> deleteData = new ArrayList<>();
        List<ProductEntity> data = getData();
        int count = statusArray.size();
        for (int i = 0; i < count; i++){
            if (statusArray.valueAt(i)){ // 表示要删除此商品
                deleteData.add(data.get(statusArray.keyAt(i)));
            }
        }
        return deleteData;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = getInflater().inflate(R.layout.item_cart_product, parent, false);
            holder.imageView = (ImageView) view.findViewById(R.id.img_product);
            holder.titleView = (TextView) view.findViewById(R.id.text_title);
            holder.formatView = (TextView) view.findViewById(R.id.text_format);
            holder.priceView = (TextView) view.findViewById(R.id.text_price);
            holder.numView = (TextView) view.findViewById(R.id.text_num);
            holder.addNumView = (ImageView) view.findViewById(R.id.img_num_add);
            holder.lesNumView = (ImageView) view.findViewById(R.id.img_num_les);
            holder.checkBox = (CheckBox) view.findViewById(R.id.item_box);
            holder.promotionView = (TextView) view.findViewById(R.id.text_promotion);
            holder.oldPriceView = (TextView) view.findViewById(R.id.text_old_price);
            holder.lesLayout = view.findViewById(R.id.layout_les);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        final ProductEntity entity = getItem(position);
        holder.titleView.setText(entity.getName());
        holder.formatView.setText("(" + entity.getStandard() +")");
        holder.priceView.setText(entity.getPrice());

        // 促销信息
        String promotionPrice = entity.getPromote();
        String promotionMessage = entity.getInfo();
        if (!TextUtils.isEmpty(promotionPrice)){
            holder.oldPriceView.setVisibility(View.VISIBLE);
            holder.oldPriceView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            holder.oldPriceView.setText("￥" + entity.getPrice());
            holder.priceView.setText(entity.getPromote());
            holder.promotionView.setText(entity.getBegin()+"到"+entity.getEnd()+promotionMessage);
        }else {
            holder.oldPriceView.setVisibility(View.GONE);
        }

        // 设置CheckBox的显示状态
        holder.checkBox.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        // 设置CheckBox的点击事件
        holder.checkBox.setOnClickListener(v -> statusArray.put(position, holder.checkBox.isChecked()));
        // 设置CheckBox的选择状态
        holder.checkBox.setChecked(statusArray.get(position));

        int n = entity.getNumber();
        if (n <= 0){
            n = 0;
            holder.lesLayout.setVisibility(View.GONE);
        }else {
            holder.lesLayout.setVisibility(View.VISIBLE);
        }

        holder.numView.setText(String.valueOf(n));

        holder.addNumView.setOnClickListener(v -> {
            if (holder.lesLayout.getVisibility() != View.VISIBLE){
                holder.lesLayout.setVisibility(View.VISIBLE);
            }
            int num = entity.getNumber() + 1;
            entity.setNumber(num);
            holder.numView.setText(String.valueOf(num));
            // TODO 总价格和购物车的改变
        });

        holder.lesNumView.setOnClickListener(v -> {
            int num = entity.getNumber() - 1;
            if (num <= 1){
                num = 1;
            }
            entity.setNumber(num);
            holder.numView.setText(String.valueOf(num));
            if (num <= 0){
                getData().remove(position);
                notifyDataSetChanged();
            }
            // TODO 总价格和购物车的改变
        });

        return view;
    }

    private class ViewHolder{
        private ImageView imageView, addNumView, lesNumView;
        private TextView titleView, formatView, priceView, numView, promotionView, oldPriceView;
        private View lesLayout;
        private CheckBox checkBox;
    }
}
