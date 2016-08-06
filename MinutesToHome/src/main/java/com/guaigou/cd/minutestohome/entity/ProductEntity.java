package com.guaigou.cd.minutestohome.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weylen on 2016-07-23.
 */
public class ProductEntity implements Parcelable, Cloneable{
    private String id;
    private String name;
    /**
     * 规格
     */
    private String standard;
    /**
     * 种类
     */
    private String kind;
    private String img;
    private String price;
    /**
     * 库存
     */
    private String reserve;
    /**
     * 促销价格
     */
    private String promote;
    /**
     * 促销开始时间
     */
    private String begin;
    /**
     * 促销结束时间
     */
    private String end;
    /**
     * 促销信息
     */
    private String info;

    /**
     * 购买数量
     */
    private int number;

    public ProductEntity() {
    }

    protected ProductEntity(Parcel in) {
        id = in.readString();
        name = in.readString();
        standard = in.readString();
        kind = in.readString();
        img = in.readString();
        price = in.readString();
        reserve = in.readString();
        promote = in.readString();
        begin = in.readString();
        end = in.readString();
        info = in.readString();
        number = in.readInt();
    }

    public static final Creator<ProductEntity> CREATOR = new Creator<ProductEntity>() {
        @Override
        public ProductEntity createFromParcel(Parcel in) {
            return new ProductEntity(in);
        }

        @Override
        public ProductEntity[] newArray(int size) {
            return new ProductEntity[size];
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getPromote() {
        return promote;
    }

    public void setPromote(String promote) {
        this.promote = promote;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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
        dest.writeString(img);
        dest.writeString(price);
        dest.writeString(reserve);
        dest.writeString(promote);
        dest.writeString(begin);
        dest.writeString(end);
        dest.writeString(info);
        dest.writeInt(number);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
