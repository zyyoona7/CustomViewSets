package com.zyyoona7.customviewsets.zoom_hover;

import android.view.View;

/**
 * 缩放动画回调
 */
public interface OnZoomAnimatorListener {
    /**
     * 放大动画开始
     *
     * @param view
     */
    void onZoomInStart(View view);

    /**
     * 放大动画结束
     *
     * @param view
     */
    void onZoomInEnd(View view);

    /**
     * 缩小动画开始
     *
     * @param view
     */
    void onZoomOutStart(View view);

    /**
     * 缩小动画结束
     *
     * @param view
     */
    void onZoomOutEnd(View view);
}