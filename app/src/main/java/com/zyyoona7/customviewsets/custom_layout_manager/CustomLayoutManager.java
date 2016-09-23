package com.zyyoona7.customviewsets.custom_layout_manager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by zyyoona7 on 2016/9/22.
 */

public class CustomLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "CustomLayoutManager";

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //分离所有的item
        detachAndScrapAttachedViews(recycler);
        int currentWidth = 0;
        int currentHeight = 0;
        for (int i = 0; i < state.getItemCount(); i++) {
            View itemView = recycler.getViewForPosition(i);

            addView(itemView);

            measureChildWithMargins(itemView, 0, 0);

            int width = getDecoratedMeasuredWidth(itemView);
            int height = getDecoratedMeasuredHeight(itemView);

            if (currentWidth + width > getWidth()) {
                currentWidth = 0;
                currentHeight += height;
            }
            Log.e(TAG, "onLayoutChildren: mCurrentWidth=" + currentWidth + ",mCurrentHeight=" + currentHeight + ",width=" + width + ",height=" + height);
            layoutDecoratedWithMargins(itemView, currentWidth, currentHeight, currentWidth + width, currentHeight + height);
            currentWidth += width;
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e(TAG, "scrollVerticallyBy: dy=" + dy);
        offsetChildrenVertical(-dy);
        return dy;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return super.canScrollHorizontally();
    }
}
