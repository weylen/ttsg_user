package com.guaigou.cd.minutestohome;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016-06-17.
 */
public abstract class BaseFragment extends Fragment{

    private Toast mToast;

    protected void showToast(String message){
        if (mToast == null){
            mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.show();
    }

    private ProgressDialog progressDialog;
    protected void showProgressDialog(String message){
        dismissProgressDialog();
        progressDialog = ProgressDialog.show(getActivity(), "", message);
    }

    protected void dismissProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * 设置返回键和标题
     * @param view
     * @param title
     */
    protected void setupHeadInfo(View view, String title){
        // 返回键
        view.findViewById(R.id.img_back).setOnClickListener(v -> getFragmentManager().popBackStack());
        // 标题
        TextView titleView = (TextView) view.findViewById(R.id.text_title);
        titleView.setText(title);
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), container, false);
    }

    public void showSnakeView(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 返回此fragment加载的布局的id
     * @return
     */
    public abstract int layoutId();


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
