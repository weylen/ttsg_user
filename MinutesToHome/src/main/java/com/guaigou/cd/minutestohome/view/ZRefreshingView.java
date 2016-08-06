package com.guaigou.cd.minutestohome.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.guaigou.cd.minutestohome.R;

/**
 * Created by Administrator on 2016-05-28.
 */
public class ZRefreshingView extends SwipeRefreshLayout{

    public ZRefreshingView(Context context) {
        super(context);
        init();
    }

    public ZRefreshingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init(){
        setColorSchemeResources(R.color.themeColor);
    }
}
