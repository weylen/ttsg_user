package com.guaigou.cd.minutestohome.cache;

import java.util.List;

/**
 * Created by Administrator on 2016-05-27.
 */
public class Data<T> {

    private int pageNum = 1;

    private List<T> listData;

    private boolean isLoadComplete;

    private Object tag;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<T> getListData() {
        return listData;
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    public boolean isLoadComplete() {
        return isLoadComplete;
    }

    public void setLoadComplete(boolean loadComplete) {
        isLoadComplete = loadComplete;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
