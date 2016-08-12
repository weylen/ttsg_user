package com.guaigou.cd.minutestohome.entity;

/**
 * Created by weylen on 2016-08-12.
 */
public class OrderEntity {

    private String orderNumber;
    private String orderStauts;
    private String t_price;
    private String time;

    public OrderEntity(){}

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderStauts() {
        return orderStauts;
    }

    public void setOrderStauts(String orderStauts) {
        this.orderStauts = orderStauts;
    }

    public String getT_price() {
        return t_price;
    }

    public void setT_price(String t_price) {
        this.t_price = t_price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
