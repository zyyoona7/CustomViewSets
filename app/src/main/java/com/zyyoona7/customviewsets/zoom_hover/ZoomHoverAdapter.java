package com.zyyoona7.customviewsets.zoom_hover;

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
     * 设置数据变化监听器
     *
     * @param listener
     */
    public void setDataChangedListener(OnDataChangedListener listener) {
        this.mOnDataChangedListener = listener;
    }

    /**
     * 通知adapter数据发生变化
     */
    public void notifyDataChanged() {
        if (this.mOnDataChangedListener != null) {
            this.mOnDataChangedListener.onChanged();
        }
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

    public abstract View getView(ViewGroup parent, int position, T t);

}
