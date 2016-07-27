package com.guaigou.cd.minutestohome.activity.market;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.adapter.GenericBaseAdapter;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.util.ParseUtil;

import java.util.List;

/**
 * Created by Administrator on 2016-06-17.
 */
public class MarketProductAdapter extends GenericBaseAdapter<ProductEntity> {

    private Activity context;
    public MarketProductAdapter(Activity context, List<ProductEntity> data) {
        super(context, data);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = getInflater().inflate(R.layout.item_market_product, parent, false);
            holder.imageView = (ImageView) view.findViewById(R.id.img_product);
            holder.titleView = (TextView) view.findViewById(R.id.text_title);
            holder.formatView = (TextView) view.findViewById(R.id.text_format);
            holder.priceView = (TextView) view.findViewById(R.id.text_price);
            holder.numView = (TextView) view.findViewById(R.id.text_num);
            holder.addNumView = (ImageView) view.findViewById(R.id.img_num_add);
            holder.lesNumView = (ImageView) view.findViewById(R.id.img_num_les);
            holder.promotionView = (TextView) view.findViewById(R.id.text_promotion);
            holder.oldPriceView = (TextView) view.findViewById(R.id.text_old_price);
            holder.lesLayout = view.findViewById(R.id.layout_les);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        final ProductEntity entity = getItem(position);
        holder.titleView.setText(entity.getName());
        holder.formatView.setText(entity.getStandard() );
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

        int n = CartData.INSTANCE.getNumber(entity.getId());
        if (n <= 0){
            n = 0;
            holder.lesLayout.setVisibility(View.GONE);
        }else {
            holder.lesLayout.setVisibility(View.VISIBLE);
        }

        holder.numView.setText(String.valueOf(n));

        // 添加数量
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

        // 减少数量
        holder.lesNumView.setOnClickListener(v -> {
            int num = CartData.INSTANCE.numberLes(entity);
            if (num == 0){
                holder.lesLayout.setVisibility(View.GONE);
            }
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

    private class ViewHolder{
        private ImageView imageView, addNumView, lesNumView;
        private TextView titleView, formatView, priceView, numView, promotionView, oldPriceView;
        private View lesLayout;
    }
}
