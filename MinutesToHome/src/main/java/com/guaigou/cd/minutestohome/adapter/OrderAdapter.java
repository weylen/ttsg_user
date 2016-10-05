package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.OrderEntity;
import com.guaigou.cd.minutestohome.entity.OrderProductsEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.view.OrderProductsDetailsView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderAdapter extends GenericBaseAdapter<OrderEntity> {

    private int blackColor, accentColor, grayColor;
    private OnItemViewClickListener onItemViewClickListener;
    public OrderAdapter(Context context, List<OrderEntity> data) {
        super(context, data);
        blackColor = context.getResources().getColor(R.color.blackText);
        grayColor = context.getResources().getColor(R.color.grayText);
        accentColor = context.getResources().getColor(R.color.colorAccent);
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null || convertView.getTag() == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = getInflater().inflate(R.layout.item_order, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrderEntity entity = getItem(position);
        List<OrderProductsEntity> productsEntities = entity.getProducts();
        OrderProductsEntity productsEntity = productsEntities.get(0);
        String status = productsEntity.getStauts();
        holder.mOrderStatusView.setText(Constants.ORDER_PARAM.get(status));
        holder.mOrderTimeView.setText(productsEntity.getDate());
        holder.mOrderPriceView.setText("￥" + entity.getTotal());
        int size = productsEntities.size();
        holder.mOrderContentView.setText(productsEntity.getName() + " 等"+size+"种商品");


        // 检查订单状态
        if (!"2".equalsIgnoreCase(status)){
            holder.mActionLayout.setVisibility(View.GONE);
            holder.mOrderStatusView.setTextColor(blackColor);
        }else {
            holder.mActionLayout.setVisibility(View.VISIBLE);
            holder.mOrderStatusView.setTextColor(accentColor);
        }

        // 设置删除图标的可见性 订单未支付或者已取消
        if ("1".equalsIgnoreCase(status) || "2".equalsIgnoreCase(status) || "5".equalsIgnoreCase(status)){
            holder.mDeleteView.setVisibility(View.VISIBLE);
        }else {
            holder.mDeleteView.setVisibility(View.GONE);
        }

        // 3：已支付未发货 6：支付确认中 7：商家已结单 8：商家已送达
        if ("3".equalsIgnoreCase(status) || "6".equalsIgnoreCase(status)
                || "7".equalsIgnoreCase(status) || "8".equalsIgnoreCase(status)){
            holder.mActionLayout.setVisibility(View.VISIBLE);
            holder.mConfirmGoodsView.setVisibility(View.VISIBLE);
            holder.mCancelView.setVisibility(View.GONE);
            holder.mPayView.setVisibility(View.GONE);
            holder.mOrderStatusView.setTextColor(blackColor);
        }else {
            // 1：订单完成
            if ("1".equalsIgnoreCase(status)){
                holder.mOrderStatusView.setTextColor(blackColor);
                holder.mActionLayout.setVisibility(View.GONE);
                // 未支付
            }else if ("2".equalsIgnoreCase(status)){
                holder.mActionLayout.setVisibility(View.VISIBLE);
                holder.mOrderStatusView.setTextColor(accentColor);
                holder.mConfirmGoodsView.setVisibility(View.GONE);
                holder.mCancelView.setVisibility(View.VISIBLE);
                holder.mPayView.setVisibility(View.VISIBLE);
                // 5：取消订单 "4"：客户退单
            }else if ("5".equalsIgnoreCase(status) || "4".equalsIgnoreCase(status)){
                holder.mOrderStatusView.setTextColor(grayColor);
                holder.mActionLayout.setVisibility(View.GONE);
            }
        }

        // 设置操作事件  // 取消
        holder.mCancelView.setOnClickListener(v->{
            if (onItemViewClickListener != null){
                onItemViewClickListener.onClickView1(position);
            }
        }); // 支付
        holder.mPayView.setOnClickListener(v->{
            if (onItemViewClickListener != null){
                onItemViewClickListener.onClickView2(position);
            }
        }); // 删除
        holder.mDeleteView.setOnClickListener(v->{
            if (onItemViewClickListener != null){
                onItemViewClickListener.onClickView3(position);
            }
        }); // 确认收货
        holder.mConfirmGoodsView.setOnClickListener(v -> {
            if (onItemViewClickListener != null){
                onItemViewClickListener.onClickView4(position);
            }
        });

        return convertView;
    }

    public static class ViewHolder {

        @Bind(R.id.orderStatusView) TextView mOrderStatusView; // 订单状态
        @Bind(R.id.orderTimeView) TextView mOrderTimeView; // 订单时间
        @Bind(R.id.orderContentView) TextView mOrderContentView; // 订单内容
        @Bind(R.id.orderPriceView) TextView mOrderPriceView; // 订单价格
        @Bind(R.id.action_layout) LinearLayout mActionLayout; // 订单动作布局
        @Bind(R.id.action_cancel) View mCancelView; // 取消订单
        @Bind(R.id.action_pay) View mPayView; // 支付按钮
        @Bind(R.id.action_delete) View mDeleteView; // 删除按钮
        @Bind(R.id.action_confirm_goods) View mConfirmGoodsView; // 确认收货

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
