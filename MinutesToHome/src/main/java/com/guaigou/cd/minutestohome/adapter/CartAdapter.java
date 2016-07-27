package com.guaigou.cd.minutestohome.adapter;

import android.app.Activity;
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

import com.bumptech.glide.Glide;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.util.ParseUtil;
import com.rey.material.app.SimpleDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-06-18.
 */
public class CartAdapter extends GenericBaseAdapter<ProductEntity>{

    private boolean isEditable;
    private SparseBooleanArray statusArray;
    private Activity context;

    public CartAdapter(Activity context, List<ProductEntity> data) {
        super(context, data);
        this.context = context;
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
        holder.formatView.setText(entity.getStandard());
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
            int num = ParseUtil.parseInt(entity.getReserve());
            // 要么库存小于0 要么添加此类的商品已经达到库存的数量
//            if (num <= 0 || CartData.INSTANCE.getNumber(entity.getId()) >= num){
//                Toast.makeText(context, "库存不足，无法添加", Toast.LENGTH_SHORT).show();
//                return;
//            }

            if (holder.lesLayout.getVisibility() != View.VISIBLE){
                holder.lesLayout.setVisibility(View.VISIBLE);
            }
            num = CartData.INSTANCE.numberAdd(entity);
            entity.setNumber(num);
            holder.numView.setText(String.valueOf(num));
        });

        holder.lesNumView.setOnClickListener(v -> {
            // 先检查数量
            int num = CartData.INSTANCE.getNumber(entity.getId());
            if (num == 1){
                showDeleteDialog(entity);
                return;
            }
            num = CartData.INSTANCE.numberLes(entity);
            holder.numView.setText(String.valueOf(num));
        });

        Glide.with(context)
                .load(Constants.BASE_URL+entity.getImg())
                .fitCenter()
                .placeholder(R.mipmap.img_load_default)
                .crossFade()
                .dontAnimate()
                .error(R.mipmap.img_load_error)
                .into(holder.imageView);

        return view;
    }

    private void showDeleteDialog(ProductEntity entity){
        SimpleDialog dialog = new SimpleDialog(context);
        dialog.title("提示");
        dialog.message("确定删除该商品")
                .negativeAction("取消")
                .negativeActionClickListener(v -> dialog.dismiss())
                .positiveAction("确定")
                .positiveActionClickListener(v1 -> {
                    dialog.dismiss();
                    CartData.INSTANCE.numberLes(entity);
                    notifyDataSetChanged();
                })
                .show();
    }

    private class ViewHolder{
        private ImageView imageView, addNumView, lesNumView;
        private TextView titleView, formatView, priceView, numView, promotionView, oldPriceView;
        private View lesLayout;
        private CheckBox checkBox;
    }
}
