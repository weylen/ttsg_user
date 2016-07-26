package com.guaigou.cd.minutestohome.activity.shoppingcart;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by weylen on 2016-07-24.
 */
public enum CartData {

    INSTANCE;

    private List<ProductEntity> data;

    private Observable<Integer> observable;
    private List<Subscriber> subscriberList;

    CartData(){
        observable = Observable.create((Observable.OnSubscribe<Integer>) subscriber1 -> {
            subscriber1.onNext(data.size());
        });
    }

    private void ensureData(){
        if (data == null){
            data = new ArrayList<>();
        }
    }

    private void ensureSubscriberList(){
        if (subscriberList == null){
            subscriberList = new ArrayList<>();
        }
    }

    public List<ProductEntity> getData() {
        return data;
    }

    public void setData(List<ProductEntity> data) {
        this.data = data;
    }

    public void addData(ProductEntity entity){
        ensureData();
        data.add(entity);
        notifyDataChanged();
    }

    public void addAll(List<ProductEntity> listData){
        ensureData();
        data.addAll(listData);
        notifyDataChanged();
    }

    public void removeAll(List<ProductEntity> deleteData){
        ensureData();
        data.removeAll(deleteData);
        notifyDataChanged();
    }

    public void registerObserver(Subscriber subscriber){
        DebugUtil.d("CartData registerObserver 注册观察者");
        Preconditions.checkNotNull(subscriber, "CartData registerObserver Subscriber can't be null");
        ensureSubscriberList();
        subscriberList.add(subscriber);
    }

    public void unregisterObserver(Subscriber subscriber){
        Preconditions.checkNotNull(subscriber, "CartData unregisterObserver Subscriber can't be null");
        DebugUtil.d("CartData unregisterObserver 解除观察者");
        subscriberList.remove(subscriber);
        subscriber.unsubscribe();
    }

    /**
     * 清除所有观察者
     */
    public void clearObserver(){
        if (subscriberList == null){
            return;
        }
        int size = subscriberList.size();
        for (int i = 0; i < size; i++){
            if (subscriberList.get(i) != null){
                subscriberList.get(i).unsubscribe();
            }
        }
        subscriberList.clear();
        System.gc();
    }

    private void notifyDataChanged(){
        ensureSubscriberList();
        ensureData();
        for (int i = 0; i < subscriberList.size(); i++){
            Subscriber subscriber = subscriberList.get(i);
            if (subscriber != null && !subscriber.isUnsubscribed()){
                observable.subscribe(subscriber);
            }
        }
    }
}
