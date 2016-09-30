package com.zyyoona7.customviewsets.pagination_rv;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zyyoona7 on 2016/9/29.
 * <p>
 * code from http://stackoverflow.com/questions/29134094/recyclerview-horizontal-scroll-snap-in-center
 */

public class PaginationSnapHelper extends LinearSnapHelper {

    private static final String TAG = "PaginationSnapHelper";


    private int mPageSize = 1;

    public PaginationSnapHelper(int pageSize) {
        this.mPageSize = pageSize;
    }


    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        View centerView = findSnapView(layoutManager);
        if (centerView == null) {
            return RecyclerView.NO_POSITION;
        }

        int position = layoutManager.getPosition(centerView);
        int targetPosition = -1;
        if (layoutManager.canScrollHorizontally()) {
            if (velocityX < 0) {
                targetPosition = position - mPageSize;
            } else {
                targetPosition = position + mPageSize;
            }
        }

        if (layoutManager.canScrollVertically()) {
            if (velocityY < 0) {
                targetPosition = position - mPageSize;
            } else {
                targetPosition = position + mPageSize;
            }
        }

        final int firstItem = 0;
        final int lastItem = layoutManager.getItemCount() - 1;
        targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
        return targetPosition;
    }
}
