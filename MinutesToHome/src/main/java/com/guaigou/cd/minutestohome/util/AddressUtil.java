package com.guaigou.cd.minutestohome.util;

import android.app.Activity;
import android.content.Context;

import com.guaigou.cd.minutestohome.entity.AddressEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * Created by Administrator on 2016-06-18.
 */
public class AddressUtil {

    private static final String ADDRESS_FILENAME = "address.db";

    public static void saveAddressList(Context context, List<AddressEntity> addressEntityList){
        try {
            OutputStream outputStream = context.openFileOutput(ADDRESS_FILENAME, Activity.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(addressEntityList);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<AddressEntity> getAddressList(Context context){
        List<AddressEntity> data = null;
        try {
            InputStream inputStream = context.openFileInput(ADDRESS_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            data = (List<AddressEntity>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 获取默认的收获地址
     * @param context
     * @return
     */
    public static AddressEntity getDefaultAddress(Context context){
        List<AddressEntity> data = getAddressList(context);
        if (data == null || data.size() == 0){
            return null;
        }
        int size = data.size();
        for (int i = 0; i < size; i++){
            AddressEntity entity = data.get(i);
            if (entity.isDefaultAddress()){
                return entity;
            }
        }
        return null;
    }
}
