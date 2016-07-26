package com.guaigou.cd.minutestohome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.List;

/**
 * Created by Administrator on 2016-06-18.
 */
public class MyHorizontalRadioGroup extends RadioGroup{

    private List<MarketDataEntity> data;
    private OnCommandChangedListener onCommandChangedListener;

    private LayoutInflater inflater;

    public MyHorizontalRadioGroup(Context context) {
        super(context);
    }

    public MyHorizontalRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
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
            if (i == 0){
                rb.setChecked(true);
            }
        }
    }

    public void setOnCommandChangedListener(OnCommandChangedListener onCommandChangedListener) {
        this.onCommandChangedListener = onCommandChangedListener;
    }

    private RadioButton createRadioButton(final int position){
        if (inflater == null){
            inflater = LayoutInflater.from(getContext());
        }
        RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_product_smalltype, this, false);
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
     * 设置某一个RadioButton被选中
     * @param position
     */
    public void check(int position){
        if (!validatePosition(position)){
            DebugUtil.d("传入的下标有错误--> position:" + position);
            return;
        }
        View view = getChildAt(position);
        if (view instanceof RadioButton){
            ((RadioButton)view).setChecked(true);
        }
    }

    /**
     * 验证下标是否在子控件个数范围内
     * @param position
     * @return
     */
    private boolean validatePosition(int position){
        int childCount = getChildCount();
        if (position >= 0 && position < childCount){
            return true;
        }
        return false;
    }

    public interface OnCommandChangedListener{
        void onChange(MarketDataEntity entity);
    }
}
