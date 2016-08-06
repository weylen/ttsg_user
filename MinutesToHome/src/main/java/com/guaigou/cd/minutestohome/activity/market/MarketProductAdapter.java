package com.guaigou.cd.minutestohome.activity.market;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.generic.GenericAddCardView;
import com.guaigou.cd.minutestohome.activity.generic.GenericAddCartPresenter;
import com.guaigou.cd.minutestohome.activity.login.LoginActivity;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.adapter.GenericBaseAdapter;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.DialogUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.util.ParseUtil;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016-06-17.
 */
public class MarketProductAdapter extends GenericBaseAdapter<ProductEntity> implements GenericAddCardView{

    private ProgressDialog progressDialog;
    private Activity context;
    private GenericAddCartPresenter presenter;
    public MarketProductAdapter(Activity context, List<ProductEntity> data) {
        super(context, data);
        this.context = context;
        presenter = new GenericAddCartPresenter(context, this);
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
        if (LocaleUtil.hasPromotion(promotionPrice)){
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

        RxView.clicks(holder.addNumView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    map(entity, 1);
                });

        RxView.clicks(holder.lesNumView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    map(entity, -1);
                });
        /**
        // 添加数量
        holder.addNumView.setOnClickListener(v -> {
            int num = ParseUtil.parseInt(entity.getReserve());
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
         */

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

    private void map(ProductEntity entity, int number){
        try {
            ProductEntity newEntity = (ProductEntity) entity.clone();
            newEntity.setNumber(number);
            presenter.request(newEntity);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            DebugUtil.d("MarketProductAdapter map 克隆不被支持");
        }
    }

    @Override
    public void onStartRequest() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "处理中");
    }

    @Override
    public void onRequestSuccess() {
        dismissDialog();
//        showSnakeView();
    }

    @Override
    public void onRequestFailure(String message) {
        dismissDialog();
        showSnakeView(message);
    }

    @Override
    public void noLogin() {
        dismissDialog();
        DialogUtil.showLoginDialog(context);
    }

    private void dismissDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showSnakeView(String message){
        View view = context.findViewById(R.id.Container);
        if (view != null){
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private class ViewHolder{
        private ImageView imageView, addNumView, lesNumView;
        private TextView titleView, formatView, priceView, numView, promotionView, oldPriceView;
        private View lesLayout;
    }
}
