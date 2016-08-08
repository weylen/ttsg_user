package com.guaigou.cd.minutestohome.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.entity.CartEntity;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by weylen on 2016-08-08.
 */
public class CartPrefs {

    private static final String CART_FILE_NAME = "cart_files.db";
    private static final String CART_PREFS_NAME = "cart_prefs.db";

    public static void saveCartData(Context context, List<CartEntity> cartEntityList){
        setCartName(context);
        try {
            OutputStream os = context.openFileOutput(CART_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(cartEntityList);
            oos.flush();
            oos.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<CartEntity> getCartData(Context context){
        List<CartEntity> data = null;
        try {
            InputStream in = context.openFileInput(CART_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(in);
            data = (List<CartEntity>) ois.readObject();
            ois.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getCartName(Context context){
        SharedPreferences shared = context.getSharedPreferences(CART_PREFS_NAME, Context.MODE_PRIVATE);
        return shared.getString("cart_name", "");
    }

    public static void setCartName(Context context){
        SharedPreferences shared = context.getSharedPreferences(CART_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        AccountEntity entity = LoginData.INSTANCE.getAccountEntity(context);
        String name = entity == null ? "" : entity.getUname();
        editor.putString("cart_name", name);
        editor.commit();
    }
}
