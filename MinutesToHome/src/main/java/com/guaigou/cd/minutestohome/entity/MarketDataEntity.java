package com.guaigou.cd.minutestohome.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weylen on 2016-07-23.
 */
public class MarketDataEntity implements Parcelable{
    private String id;
    private String pid;
    private String name;
    private int number;

    public MarketDataEntity() {
    }

    public MarketDataEntity(String id, String pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }

    protected MarketDataEntity(Parcel in) {
        id = in.readString();
        pid = in.readString();
        name = in.readString();
        number = in.readInt();
    }

    public static final Creator<MarketDataEntity> CREATOR = new Creator<MarketDataEntity>() {
        @Override
        public MarketDataEntity createFromParcel(Parcel in) {
            return new MarketDataEntity(in);
        }

        @Override
        public MarketDataEntity[] newArray(int size) {
            return new MarketDataEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(pid);
        dest.writeString(name);
        dest.writeInt(number);
    }
}
