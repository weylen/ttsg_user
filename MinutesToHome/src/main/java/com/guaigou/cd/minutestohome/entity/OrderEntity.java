package com.guaigou.cd.minutestohome.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderEntity implements Serializable{

    private String orderId;
    private String total;
    private ArrayList<OrderProductsEntity> products;

    public OrderEntity(){}

    public OrderEntity(String orderId, String total, ArrayList<OrderProductsEntity> products) {
        this.orderId = orderId;
        this.total = total;
        this.products = products;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<OrderProductsEntity> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<OrderProductsEntity> products) {
        this.products = products;
    }

}
