package com.guaigou.cd.minutestohome.activity.shoppingcart;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by weylen on 2016-07-24.
 */
public enum CartData {

    INSTANCE;

    private List<CartEntity> data;

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

    public List<CartEntity> getData() {
        return data;
    }

    public void setData(List<CartEntity> data) {
        this.data = data;
    }

    /**
     * 获取指定id的数量
     * @param id
     * @return
     */
    public int getNumber(String id){
        int number = 0;
        if (data != null && data.size() > 0){
            for (CartEntity entity : data){
                if (entity.getId().equalsIgnoreCase(id)){
                    number += entity.getAmount();
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
        for (CartEntity entity : data){
            number += entity.getAmount();
        }
        return number;
    }

    /**
     * 减少指定商品的数量
     * @param entity
     */
    public int numberLes(ProductEntity entity){
        int num = 0;
        for (CartEntity cartEntity : data){
            if (cartEntity.getId().equalsIgnoreCase(entity.getId())){
                num = cartEntity.getAmount();
                num--;
                num = num <= 0 ? 0 : num;
                cartEntity.setAmount(num);
                if (num == 0){
                    data.remove(cartEntity);
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
            data.add(LocaleUtil.format2CartEntity(entity));
            num = 1;
        }else {
            boolean isSame = false; // 判断是否有相同的数据
            for (CartEntity cartEntity : data){
                if (cartEntity.getId().equalsIgnoreCase(entity.getId())){
                    isSame = true;
                    num = cartEntity.getAmount() + 1;
                    cartEntity.setAmount(num);
                    entity.setNumber(num);
                    break;
                }
            }
            if (!isSame){
                entity.setNumber(1);
                data.add(LocaleUtil.format2CartEntity(entity));
                num = 1;
            }
        }
        notifyDataChanged();
        return num;
    }

    public void removeAll(List<CartEntity> deleteData){
        ensureData();
        data.removeAll(deleteData);
        notifyDataChanged();
    }

    public void registerObserver(Subscriber subscriber){
        Preconditions.checkNotNull(subscriber, "CartData registerObserver Subscriber can't be null");
        ensureSubscriberList();
        subscriberList.add(subscriber);
    }

    public void unregisterObserver(Subscriber subscriber){
        Preconditions.checkNotNull(subscriber, "CartData unregisterObserver Subscriber can't be null");
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
