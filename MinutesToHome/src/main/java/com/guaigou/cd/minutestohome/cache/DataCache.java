package com.guaigou.cd.minutestohome.cache;

import android.util.LruCache;

/**
 * 数据缓存类
 */
public enum DataCache {

    INSTANCE;

    private static final int MAX = 1 * 1024 * 1024;

    DataCache() {
        lruCache = new LruCache<String, Data<?>>(MAX){
            @Override
            protected int sizeOf(String key, Data<?> value) {
                return super.sizeOf(key, value);
            }
        };
    }

    private LruCache<String, Data<?>> lruCache = null;

    /**
     * 保存数据
     * @param key key
     * @param data 值
     */
    public void putData(String key, Data<?> data){
        lruCache.put(key, data);
    }

    /**
     * 获取数据
     * @param key key
     * @return Data对象
     */
    public Data<?> getData(String key){
        return lruCache.get(key);
    }

    /**
     * 移除某条数据
     * @param key
     */
    public void remove(String key){
        lruCache.remove(key);
    }

    /**
     * 移除所有数据
     */
    public void removeAll(){
        lruCache.evictAll();
    }
}
