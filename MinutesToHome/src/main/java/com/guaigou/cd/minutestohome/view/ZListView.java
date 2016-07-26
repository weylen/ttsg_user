package com.guaigou.cd.minutestohome.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;

/**
 * Created by Weylen on 2016-05-23.
 */
public class ZListView extends ListView implements AbsListView.OnScrollListener{

    private int lastItemIndex;//当前ListView中最后一个Item的索引

    private OnLoadmoreListener onLoadmoreListener; // 刷新监听

    private View footerView; // 尾部视图
    private View loadingView; // 加载视图
    private TextView loadCompleteView; // 加载完成视图

    public ZListView(Context context) {
        super(context);
        init(context);
    }

    public ZListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     * @param context 上下文对象
     */
    private void init(Context context){
        setOnScrollListener(this);
        addFooter(context);
    }

    /**
     * 添加尾部刷新视图
     * @param context 上下文对象
     */
    private void addFooter(Context context){
        footerView = LayoutInflater.from(context).inflate(R.layout.layout_footerview, this, false);
        if (context instanceof Activity){
            Point point = new Point();
            ((Activity)context).getWindowManager().getDefaultDisplay().getSize(point);
            LayoutParams layoutParams = new LayoutParams(point.x, -2);
            footerView.setLayoutParams(layoutParams);
        }
        loadingView = footerView.findViewById(R.id.footerLoading);
        loadCompleteView = (TextView) footerView.findViewById(R.id.footerMessage);

        this.addFooterView(footerView);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && lastItemIndex == getCount() - 1) {
            if (this.onLoadmoreListener != null){
                onLoadmoreListener.onRefresh();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemIndex = firstVisibleItem + visibleItemCount - 1;
    }

    public void setOnLoadmoreListener(OnLoadmoreListener onLoadmoreListener) {
        this.onLoadmoreListener = onLoadmoreListener;
    }

    public interface OnLoadmoreListener {
        void onRefresh();
    }

    /**
     * 设置是否加载完成
     * @param isLoadComplete true表示加载完成 false表示没有加载完成
     */
    public void setLoadComplete(boolean isLoadComplete) {
        if (isLoadComplete){ // 加载完成
            loadCompleteView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }else{
            loadCompleteView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置加载完成的文本
     * @param message
     */
    public void setLoadCompleteText(String message){
        if (loadCompleteView != null){
            loadCompleteView.setText(message);
        }
    }
}
