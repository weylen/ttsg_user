package com.guaigou.cd.minutestohome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.guaigou.cd.minutestohome.R;

/**
 * Created by Weylen on 2016-05-23.
 */
public class ZListView extends ListView implements AbsListView.OnScrollListener{

    private int lastItemIndex;//当前ListView中最后一个Item的索引

    private OnLoadmoreListener onLoadmoreListener; // 刷新监听

    private View footerView; // 尾部视图
    private View loadingView; // 加载视图
    private View errorView; // 错误视图
    private View completeView; // 加载完成视图
    private boolean isShowFooterView;
    private State state = State.STATE_NORMAL;

    public enum State{
        STATE_LOADING,
        STATE_NORMAL,
        STATE_COMPLETE,
        STATE_ERROR;
    }

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
        setFooterDividersEnabled(false);
    }

    /**
     * 添加尾部刷新视图
     * @param context 上下文对象
     */
    private void addFooter(Context context){
        footerView = LayoutInflater.from(context).inflate(R.layout.layout_footerview, null, false);
        loadingView = footerView.findViewById(R.id.layout_loading);
        errorView = footerView.findViewById(R.id.layout_error);
        errorView.setOnClickListener(v->{
            if (this.onLoadmoreListener != null){
                onLoadmoreListener.onError();
            }
        });
        completeView = footerView.findViewById(R.id.layout_complete);
        this.addFooterView(footerView);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && lastItemIndex == getCount() - 1) {
            if (this.onLoadmoreListener != null){
                onLoadmoreListener.onLoadMore();
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
        void onLoadMore();
        void onError();
    }

    public void setState(State state){
        this.state = state;

        loadingView.setVisibility(View.GONE);
        completeView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        switch (state){
            case STATE_COMPLETE:
                completeView.setVisibility(View.VISIBLE);
                break;
            case STATE_LOADING:
                loadingView.setVisibility(View.VISIBLE);
                break;
            case STATE_ERROR:
                errorView.setVisibility(View.VISIBLE);
                break;
            case STATE_NORMAL:
                loadingView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public State getState() {
        return state;
    }

    /**
     * 设置是否加载完成
     * @param isLoadComplete true表示加载完成 false表示没有加载完成
     */
    public void setLoadComplete(boolean isLoadComplete) {
        if (!isFooterVisible() && isShowFooterView){
            footerView.setVisibility(View.VISIBLE);
        }
        setState(isLoadComplete ? State.STATE_COMPLETE : State.STATE_NORMAL);
    }

    public void setFooterVisible(boolean isVisible){
        footerView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean isFooterVisible(){
        return footerView.getVisibility() == View.VISIBLE;
    }

    public void setShowFooterView(boolean isShowFooterView){
        this.isShowFooterView = isShowFooterView;
        setFooterVisible(isShowFooterView);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(w, -2);
        footerView.setLayoutParams(params);
    }
}
