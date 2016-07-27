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
            subscriber1.onNext(getNumberAll());
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

    /**
     * 获取指定id的数量
     * @param id
     * @return
     */
    public int getNumber(String id){
        int number = 0;
        if (data != null && data.size() > 0){
            for (ProductEntity entity : data){
                if (entity.getId().equalsIgnoreCase(id)){
                    number += entity.getNumber();
                }
            }
        }
        return number;
    }

    /**
     * 获取所有商品的数量
     * @return
     */
    public int getNumberAll(){
        ensureData();
        int number = 0;
        for (ProductEntity entity : data){
            number += entity.getNumber();
        }
        return number;
    }

    /**
     * 减少指定商品的数量
     * @param entity
     */
    public int numberLes(ProductEntity entity){
        int num = 0;
        for (ProductEntity productEntity : data){
            if (productEntity.getId().equalsIgnoreCase(entity.getId())){
                num = productEntity.getNumber();
                num--;
                num = num <= 0 ? 0 : num;
                productEntity.setNumber(num);
                if (num == 0){
                    data.remove(productEntity);
                }
                break;
            }
        }
        notifyDataChanged();
        return num;
    }

    /**
     * 增加商品的数量
     * @param entity
     */
    public int numberAdd(ProductEntity entity){
        ensureData();
        int num = 0;
        if (data.size() == 0){ // 如果没有列表数据
            entity.setNumber(1);
            data.add(entity);
            num = 1;
        }else {
            boolean isSame = false; // 判断是否有相同的数据
            for (ProductEntity productEntity : data){
                if (productEntity.getId().equalsIgnoreCase(entity.getId())){
                    isSame = true;
                    num = productEntity.getNumber() + 1;
                    productEntity.setNumber(num);
                    break;
                }
            }
            if (!isSame){
                entity.setNumber(1);
                data.add(entity);
                num = 1;
            }
        }
        notifyDataChanged();
        return num;
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
