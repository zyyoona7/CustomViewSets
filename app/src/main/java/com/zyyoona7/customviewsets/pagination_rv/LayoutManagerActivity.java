package com.zyyoona7.customviewsets.pagination_rv;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zyyoona7.customviewsets.BaseActivity;
import com.zyyoona7.customviewsets.R;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class LayoutManagerActivity extends BaseActivity {

    private static final String TAG = "LayoutManagerActivity";

    @BindView(R.id.id_layout_manager_recycler_view)
    RecyclerView mRecyclerView;

    int currentVisiblePosition = 0;
    LinkedList<String> list = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.HORIZONTAL, false));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        CustomAdapter adapter = new CustomAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setNewData(createData());
//        SnapHelper snapHelper = new PaginationSnapHelper(4);
//        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e(TAG, "onScrollStateChanged: state=" + newState);
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    int visiblePosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    int visibleCount = ((GridLayoutManager) recyclerView.getLayoutManager()).getChildCount();
                    int itemCount = recyclerView.getLayoutManager().getItemCount();
                    if (visibleCount + visiblePosition >= itemCount) {
                        Log.e(TAG, "onScrolled: end");
                        ((BaseQuickAdapter) recyclerView.getAdapter()).addData(createData());
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e(TAG, "onScrolled: dx=" + dx + ",dy=" + dy);
                int visiblePosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (visiblePosition != currentVisiblePosition && visiblePosition > currentVisiblePosition) {
                    //next page
                    currentVisiblePosition = visiblePosition;
//                    onNextPage(currentVisiblePosition);
                    Log.e(TAG, "onScrolled: next page");
                } else if (visiblePosition != currentVisiblePosition && visiblePosition < currentVisiblePosition) {
                    //pre page
                    currentVisiblePosition = visiblePosition;
//                    onPrePage(currentVisiblePosition);
                    Log.e(TAG, "onScrolled: pre page");
                }

            }
        });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_layout_manager;
    }

    private List<String> createData() {
        for (int i = 0; i < 50; i++) {
            list.add("position=" + i);
        }
        return list;
    }

    private void onNextPage(int currentPosition) {
        for (int i = list.size(); i < list.size() + 4; i++) {
            list.add("position=" + i);
        }

        for (int i = 0; i < 4; i++) {
            list.removeFirst();
        }
    }

    private void onPrePage(int currentPosition) {
        for (int i = 0; i < 4; i++) {
            list.addFirst("position=" + i);
        }

        for (int i = list.size() - 1; i > list.size() - 5; i--) {
            list.removeLast();
        }
    }
}
