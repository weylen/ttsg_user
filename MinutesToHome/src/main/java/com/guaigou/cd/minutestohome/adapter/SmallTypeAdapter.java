package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-04.
 */
public class SmallTypeAdapter extends RecyclerView.Adapter<SmallTypeAdapter.TypeHolder>{

    private LayoutInflater layoutInflater;
    private List<MarketDataEntity> dataEntities;
    private OnItemClickListener onItemClickListener;
    private int checkedPosition = 0;

    public SmallTypeAdapter(Context context, List<MarketDataEntity> dataEntities){
        this.layoutInflater = LayoutInflater.from(context);
        this.dataEntities = dataEntities;
    }

    @Override
    public TypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_product_smalltype, parent, false);
        return new TypeHolder(view);
    }

    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }

    public int getCheckedPosition() {
        return checkedPosition;
    }

    public void setDataEntities(List<MarketDataEntity> dataEntities) {
        this.dataEntities = dataEntities;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(TypeHolder holder, int position) {
        MarketDataEntity entity = dataEntities.get(position);
        holder.radioButton.setText(entity.getName());
        holder.radioButton.setChecked(position == checkedPosition);
        holder.radioButton.setOnClickListener(v -> {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(position, entity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataEntities == null ? 0:dataEntities.size();
    }

    public static class TypeHolder extends RecyclerView.ViewHolder{

        private RadioButton radioButton;
        public TypeHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position, MarketDataEntity entity);
    }
}
