package com.guaigou.cd.minutestohome.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by weylen on 2016-07-24.
 */
public class KeybordUtil {

    public static void hide(Activity context){
        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void show(Activity context){
        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.showSoftInput(view, 0);
        }
    }
}
