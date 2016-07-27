package com.guaigou.cd.minutestohome.activity.market;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.adapter.GenericBaseAdapter;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.http.Constants;

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
            holder.lesLayout = view.findViewById(R.id.layout_les);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        final ProductEntity entity = getItem(position);
        holder.titleView.setText(entity.getName());
        holder.formatView.setText(entity.getStandard());
        holder.priceView.setText(entity.getPrice());

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
            if (num <= 0){
                holder.lesLayout.setVisibility(View.GONE);
                num = 0;
            }
            entity.setNumber(num);
            holder.numView.setText(String.valueOf(num));
            // TODO 总价格和购物车的改变
        });

        Glide.with(context)
                .load(Constants.BASE_URL+entity.getImg())
                .centerCrop()
                .placeholder(R.mipmap.img_load_default)
                .crossFade()
                .error(R.mipmap.img_load_error)
                .into(holder.imageView);
        return view;
    }

    private class ViewHolder{
        private ImageView imageView, addNumView, lesNumView;
        private TextView titleView, formatView, priceView, numView;
        private View lesLayout;
    }
}
