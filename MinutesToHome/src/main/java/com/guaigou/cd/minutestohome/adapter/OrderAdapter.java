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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderAdapter extends GenericBaseAdapter<OrderEntity> {

    private OnItemViewClickListener onItemViewClickListener;
    public OrderAdapter(Context context, List<OrderEntity> data) {
        super(context, data);
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
        StringBuilder builder = new StringBuilder();
        for (OrderProductsEntity productsEntity1 : productsEntities){
            builder.append(productsEntity1.getName());
            builder.append("&nbsp;&nbsp;x");
            builder.append(productsEntity1.getAmount());
            builder.append("<br>");
        }
        builder.deleteCharAt(builder.length()-1);
        holder.mOrderContentView.setText(Html.fromHtml(builder.toString()));

        // 检查订单状态
        if (!"2".equalsIgnoreCase(status)){
            holder.mActionLayout.setVisibility(View.GONE);
        }else {
            holder.mActionLayout.setVisibility(View.VISIBLE);
        }
        // 设置操作事件
        holder.mCancelView.setOnClickListener(v->{
            if (onItemViewClickListener != null){
                onItemViewClickListener.onClickView1(position);
            }
        });
        holder.mPayView.setOnClickListener(v->{
            if (onItemViewClickListener != null){
                onItemViewClickListener.onClickView2(position);
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

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
