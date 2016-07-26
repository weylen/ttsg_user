package com.guaigou.cd.minutestohome.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Created by Administrator on 2016/3/1.
 */
public class ContentPagerAdapter extends FragmentPagerAdapter{

    private List<Fragment> fragmentList;
    private int viewId;
    public ContentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, int containerId) {
        super(fm);
        this.fragmentList = fragmentList;
        this.viewId = containerId;
        clearCache(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    public Fragment getCurrentFragment(int index){
        return fragmentList.get(index);
    }

    public static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private void clearCache(FragmentManager fm) {
        if(this.fragmentList != null){
            FragmentTransaction ft = fm.beginTransaction();
            for(int i = 0; i < getCount(); i++){
                String name = makeFragmentName(viewId, i);
                Fragment fragment = fm.findFragmentByTag(name);
                if(fragment != null){
                    ft.remove(fragment);
                }
            }
            ft.commit();
        }
    }
}
