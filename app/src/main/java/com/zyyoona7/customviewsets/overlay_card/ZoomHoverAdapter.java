package com.zyyoona7.customviewsets.overlay_card;

import android.support.v4.util.SimpleArrayMap;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zyyoona7 on 2016/8/16.
 */

public abstract class ZoomHoverAdapter<T> {

    private List<T> mDataList;

    //存储需要横跨的下标和跨度
    private SimpleArrayMap<Integer, Integer> mSpanMap = new SimpleArrayMap<>();

    //数据变化回调
    private OnDataChangedListener mOnDataChangedListener;


    /**
     * 数据变化回调
     */
    interface OnDataChangedListener {
        void onChanged();
    }


    public ZoomHoverAdapter(List<T> list) {
        this.mDataList = list;
    }

    public ZoomHoverAdapter(T[] datas) {
        this.mDataList = new ArrayList<>(Arrays.asList(datas));
    }

    /**
     * 设置需要横跨的下标和跨度
     *
     * @param map  key代表下标
     *             value代表跨度
     */
    public void setSpan(SimpleArrayMap<Integer,Integer> map) {
        this.mSpanMap.clear();
        this.mSpanMap.putAll(map);
        if(mOnDataChangedListener!=null){
            mOnDataChangedListener.onChanged();
        }
    }

    public void setSpan(int position,int span){
        this.mSpanMap.clear();
        this.mSpanMap.put(position,span);
        if(mOnDataChangedListener!=null){
            mOnDataChangedListener.onChanged();
        }
    }

    public void setSpan(int position,int span,int position1,int span1){
        this.mSpanMap.clear();
        this.mSpanMap.put(position,span);
        this.mSpanMap.put(position1,span1);
        if(mOnDataChangedListener!=null){
            mOnDataChangedListener.onChanged();
        }
    }


    /**
     * 设置数据变化监听器
     *
     * @param listener
     */
    public void setDataChangedListener(OnDataChangedListener listener) {
        this.mOnDataChangedListener = listener;
    }

    /**
     * 获取数据的总数
     *
     * @return
     */
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    /**
     * 获取对应Item的bean
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        if (mDataList == null) {
            return null;
        } else {
            return mDataList.get(position);
        }
    }

    /**
     * 获取需要横跨的list
     * @return
     */
    public SimpleArrayMap getSpanList() {
        return mSpanMap;
    }

    public abstract View getView(ViewGroup parent, int position, T t);

}
