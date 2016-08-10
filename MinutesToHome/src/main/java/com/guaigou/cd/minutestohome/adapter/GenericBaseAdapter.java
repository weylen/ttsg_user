package com.guaigou.cd.minutestohome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 泛型的类型即表示数据的类型
 * @param <T>
 */
public abstract class GenericBaseAdapter<T> extends BaseAdapter{

	private List<T> listData = new ArrayList<>();
	private LayoutInflater mInflater;
	
	protected GenericBaseAdapter(Context context, List<T> data){
		mInflater = LayoutInflater.from(context);
		addData(data);
	}
	
	@Override
	public int getCount() {
		return listData.size();
	}
	
	@Override
	public T getItem(int position) {
		return listData.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}


	/**
	 * 更新一条数据
	 */
	public void updateData(int position,T t){
		if(listData == null || position < 0 || position >= listData.size()){
			return;
		}
		listData.set(position, t);
		notifyDataSetChanged();
	}
	
	/**
	 * 重新设定数据
	 * @param newData
	 */
	public void setData(List<T> newData){
		DebugUtil.d("GenericBaseAdapter size:" + listData.size());
		listData.clear();
		if (!LocaleUtil.isListEmpty(newData)){
			listData.addAll(newData);
		}
		DebugUtil.d("GenericBaseAdapter size:" + listData.size());
		notifyDataSetChanged();
	}
	
	/**
	 * 添加一条数据
	 * @param t
	 */
	public void addData(T t){
		listData.add(t);
		notifyDataSetChanged();
	}
	
	/**
	 * 添加一组数据
	 * @param newData
	 */
	public void addData(List<T> newData){
		if (!LocaleUtil.isListEmpty(newData)){
			listData.addAll(newData);
			notifyDataSetChanged();
		}
	}

	/**
	 * 获取数据
	 * @return
	 */
	public List<T> getData(){
		return listData;
	}
	
	/**
	 * 获取LayoutInflater对象
	 * @return
	 */
	public LayoutInflater getInflater(){
		return mInflater;
	}
}
