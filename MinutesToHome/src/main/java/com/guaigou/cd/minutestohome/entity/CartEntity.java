package com.guaigou.cd.minutestohome.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by weylen on 2016-08-06.
 */
public class CartEntity implements Parcelable, Serializable{
    private String id;
    private String name;
    private String standard;
    private String kind;
    private String imgPath;
    private String buyPrice;
    private String salePrice;
    private String stock;
    private String info;
    private String promote;
    private String stauts;
    private String begin;
    private String end;
    private int amount;
    private String time;
    private String storeName;
    private String shoperid;

    public CartEntity() {
    }

    protected CartEntity(Parcel in) {
        id = in.readString();
        name = in.readString();
        standard = in.readString();
        kind = in.readString();
        imgPath = in.readString();
        buyPrice = in.readString();
        salePrice = in.readString();
        stock = in.readString();
        info = in.readString();
        promote = in.readString();
        stauts = in.readString();
        begin = in.readString();
        end = in.readString();
        amount = in.readInt();
        time = in.readString();
        storeName = in.readString();
        shoperid = in.readString();
    }

    public static final Creator<CartEntity> CREATOR = new Creator<CartEntity>() {
        @Override
        public CartEntity createFromParcel(Parcel in) {
            return new CartEntity(in);
        }

        @Override
        public CartEntity[] newArray(int size) {
            return new CartEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPromote() {
        return promote;
    }

    public void setPromote(String promote) {
        this.promote = promote;
    }

    public String getStauts() {
        return stauts;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getShoperid() {
        return shoperid;
    }

    public void setShoperid(String shoperid) {
        this.shoperid = shoperid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(standard);
        dest.writeString(kind);
        dest.writeString(imgPath);
        dest.writeString(buyPrice);
        dest.writeString(salePrice);
        dest.writeString(stock);
        dest.writeString(info);
        dest.writeString(promote);
        dest.writeString(stauts);
        dest.writeString(begin);
        dest.writeString(end);
        dest.writeInt(amount);
        dest.writeString(time);
        dest.writeString(storeName);
        dest.writeString(shoperid);
    }
}
