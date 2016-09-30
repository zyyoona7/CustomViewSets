package com.zyyoona7.customviewsets.pagination_rv;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by zyyoona7 on 2016/9/22.
 */

public class CustomLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "CustomLayoutManager";

    //保存所有的Item的上下左右的偏移量信息
    private SparseArray<Rect> mAllItemsFrames;

    private int mHorizontalScrollOffset = 0;

    private int mTotalWidth = 0;

    //每页的行列数
    private int mSpanColumns = 1;

    private int mSpanRows = 1;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果没有item，直接返回
        if (getItemCount() <= 0) return;
        // 跳过preLayout，preLayout主要用于支持动画
        if (state.isPreLayout()) {
            return;
        }
        //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        detachAndScrapAttachedViews(recycler);
        mAllItemsFrames = new SparseArray<>(getItemCount());
        int currentWidth = 0;
        int currentHeight = 0;
        int currentPage = 1;
        int maxCurrentWidth = 0;
        View scrap = recycler.getViewForPosition(0);
        addView(scrap);
        measureChildWithMargins(scrap, 0, 0);
        int width = getDecoratedMeasuredWidth(scrap);
        int height = getDecoratedMeasuredHeight(scrap);
        detachAndScrapView(scrap, recycler);
        maxCurrentWidth = (getHorizontalSpace() / width) * width;
        Log.e(TAG, "onLayoutChildren: itemCount=" + getItemCount());
        for (int i = 0; i < getItemCount(); i++) {
            //这里就是从缓存里面取出
//            View view = recycler.getViewForPosition(i);
            //将View加入到RecyclerView中
//            addView(view);
//            measureChildWithMargins(view, 0, 0);

            Rect frame = mAllItemsFrames.get(i);
            if (frame == null) {
                frame = new Rect();
            }
            if (currentWidth + width > maxCurrentWidth * currentPage) {
                currentHeight += height;
                currentWidth = maxCurrentWidth * (currentPage - 1);
            }

            if (currentHeight + height > getVerticalSpace()) {
                currentWidth = maxCurrentWidth * currentPage;
                currentHeight = 0;
                currentPage++;
            }
            Log.e(TAG, "onLayoutChildren: currentWidth=" + currentWidth + ",currentHeight=" + currentHeight + ",horizontalSpan=" + getHorizontalSpace() + ",verticalSpace=" + getVerticalSpace() + ",currentPage=" + currentPage + ",maxWidth=" + maxCurrentWidth
            );
            frame.set(currentWidth, currentHeight, currentWidth + width, currentHeight + height);
            // 将当前的Item的Rect边界数据保存
            mAllItemsFrames.put(i, frame);

            currentWidth += width;
            mTotalWidth = maxCurrentWidth * currentPage;
            Log.e(TAG, "onLayoutChildren: totalWidth=" + mTotalWidth);
        }

        mTotalWidth = Math.max(mTotalWidth, getHorizontalSpace());
        recycleAndFillItems(recycler, state);
    }

    /**
     * 回收不需要的Item，并且将需要显示的Item从缓存中取出
     */
    private void recycleAndFillItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) { // 跳过preLayout，preLayout主要用于支持动画
            return;
        }

        // 当前scroll offset状态下的显示区域
        Rect displayFrame = new Rect(mHorizontalScrollOffset, 0, mHorizontalScrollOffset + getHorizontalSpace(), getVerticalSpace());

        /**
         * 将滑出屏幕的Items回收到Recycle缓存中
         */
        Rect childFrame = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            childFrame.left = getDecoratedLeft(child);
            childFrame.top = getDecoratedTop(child);
            childFrame.right = getDecoratedRight(child);
            childFrame.bottom = getDecoratedBottom(child);
            //如果Item没有在显示区域，就说明需要回收
            if (!Rect.intersects(displayFrame, childFrame)) {
                //回收掉滑出屏幕的View
                removeAndRecycleView(child,recycler);

            }
        }

        //重新显示需要出现在屏幕的子View
        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayFrame, mAllItemsFrames.get(i))) {

                View scrap = recycler.getViewForPosition(i);
                measureChildWithMargins(scrap, 0, 0);
                addView(scrap);

                Rect frame = mAllItemsFrames.get(i);
                //将这个item布局出来
                layoutDecorated(scrap,
                        frame.left - mHorizontalScrollOffset,
                        frame.top,
                        frame.right - mHorizontalScrollOffset,
                        frame.bottom);

            }
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //先detach掉所有的子View
        detachAndScrapAttachedViews(recycler);

        //实际要滑动的距离
        int travel = dx;
//
//        如果滑动到最左面
        if (mHorizontalScrollOffset + dx < 0) {
            travel = -mHorizontalScrollOffset;
        } else if (mHorizontalScrollOffset + dx > mTotalWidth - getHorizontalSpace()) {//如果滑动到最右部
            travel = mTotalWidth - getHorizontalSpace() - mHorizontalScrollOffset;
        }
//
//        将水平方向的偏移量+travel
        mHorizontalScrollOffset += travel;
        offsetChildrenHorizontal(-travel);
        recycleAndFillItems(recycler, state);
        Log.e(TAG, "scrollHorizontallyBy: childCount=" + getChildCount());
        return travel;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }


    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }
}
