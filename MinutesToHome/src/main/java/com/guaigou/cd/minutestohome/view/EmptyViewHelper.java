package com.guaigou.cd.minutestohome.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.util.DebugUtil;

public class EmptyViewHelper {

    private ListView mListView;
    private View emptyView;
    private Context mContext;
    private String mEmptyText;
    private TextView mTextView;
    private FrameLayout parent;
    private ZRefreshingView refreshLayoutEmptyView;
    private OnEmptyTouchListener onEmptyTouchListener;

    public EmptyViewHelper(ListView listView, String text, FrameLayout parent) {
        mListView = listView;
        mContext = listView.getContext();
        mEmptyText = text;
        this.parent = parent;
        initEmptyView();
    }

    private void initEmptyView() {
        emptyView = View.inflate(mContext, R.layout.layout_empty_view, null);

        refreshLayoutEmptyView = (ZRefreshingView) emptyView.findViewById(R.id.refreshLayoutEmptyView);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        parent.addView(emptyView, lp);

        mListView.setEmptyView(emptyView);

        mTextView = (TextView) emptyView.findViewById(R.id.text_empty);

        mTextView.setText(mEmptyText);
        mTextView.setOnClickListener(v -> {
            if (onEmptyTouchListener != null){
                onEmptyTouchListener.onEmptyTouch();
            }
        });
    }

    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener refreshListener) {
        refreshLayoutEmptyView.setOnRefreshListener(refreshListener);
    }

    public void setOnEmptyTouchListener(OnEmptyTouchListener onEmptyTouchListener) {
        this.onEmptyTouchListener = onEmptyTouchListener;
    }

    public void setEmptyText(String text){
        if (mTextView != null){
            mTextView.setText(text);
        }
    }

    /**
     * 设置是否刷新
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing){
        refreshLayoutEmptyView.setRefreshing(refreshing);
    }

    public interface OnEmptyTouchListener{
        void onEmptyTouch();
    }
}
