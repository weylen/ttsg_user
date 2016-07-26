package com.guaigou.cd.minutestohome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;

import java.util.List;

/**
 * Created by Administrator on 2016-06-18.
 */
public class MyRadioGroup extends RadioGroup{

    private List<MarketDataEntity> data;
    private OnCommandChangedListener onCommandChangedListener;
    private LayoutInflater inflater;

    public MyRadioGroup(Context context) {
        super(context);
        setOrientation(RadioGroup.VERTICAL);
    }

    public MyRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(RadioGroup.VERTICAL);
    }

    public void setDataAndNotify(List<MarketDataEntity> data) {
        this.data = data;
        putView();
    }

    private void putView(){
        removeAllViews();
        int size = data == null ? 0 : data.size();
        for (int i = 0; i < size; i++){
            RadioButton rb = createRadioButton(i);
            this.addView(rb);
            this.addView(createDivider());
        }

        View first = getChildAt(0);
        if (first != null && first instanceof RadioButton){
            ((RadioButton)first).setChecked(true);
        }
    }

    public void setOnCommandChangedListener(OnCommandChangedListener onCommandChangedListener) {
        this.onCommandChangedListener = onCommandChangedListener;
    }

    private RadioButton createRadioButton(final int position){
        RadioButton radioButton = (RadioButton) getInflater().inflate(R.layout.view_product_bigtype, this, false);
        final MarketDataEntity entity = data.get(position);
        radioButton.setText(entity.getName());
        int id;
        try {
            id = Integer.parseInt(entity.getId());
        }catch (NumberFormatException e){
            id = 0;
        }
        radioButton.setId(id);
        radioButton.setOnClickListener(v -> {
            if (onCommandChangedListener != null){
                onCommandChangedListener.onChange(entity);
            }
        });
        return radioButton;
    }

    /**
     * 创建分割线
     * @return
     */
    private View createDivider(){
        return getInflater().inflate(R.layout.view_divider, this, false);
    }

    private LayoutInflater getInflater(){
        if (inflater == null){
            inflater = LayoutInflater.from(getContext());
        }
        return inflater;
    }

    public interface OnCommandChangedListener{
        void onChange(MarketDataEntity entity);
    }
}
