package com.zyyoona7.customviewsets.zoom_hover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.view.View;
import android.widget.Toast;

import com.zyyoona7.customviewsets.BaseActivity;
import com.zyyoona7.customviewsets.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ZoomHoverActivity extends BaseActivity {
    private static final String TAG = "ZoomHoverActivity";
    List<String> mList = new ArrayList<>();
    TestZoomHoverAdapter mAdapter;
    @BindView(R.id.zoom_hover_grid_view)
    ZoomHoverGridView mZoomHoverGridView;
    @BindView(R.id.zoom_hover_view)
    ZoomHoverView mZoomHoverView;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_zoom_hover;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList.add("test 1");
        mList.add("test 2");
        mList.add("test 3");
        mList.add("test 4");
        mList.add("test 5");
        mList.add("test 6");
        mList.add("test 7");
        mList.add("test 8");
        mList.add("test 9");
        mList.add("test 10");
        mAdapter = new TestZoomHoverAdapter(mList);
        final SimpleArrayMap<Integer, Integer> map = new SimpleArrayMap<>();
        map.put(0, 2);
        mZoomHoverView.setSpan(map);
        mZoomHoverView.setAdapter(mAdapter);

        mZoomHoverView.setOnZoomAnimatorListener(new OnZoomAnimatorListener() {
            @Override
            public void onZoomInStart(View view) {
                view.setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
            }

            @Override
            public void onZoomInEnd(View view) {

            }

            @Override
            public void onZoomOutStart(View view) {

            }

            @Override
            public void onZoomOutEnd(View view) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });
        mZoomHoverGridView.setOnZoomAnimatorListener(new OnZoomAnimatorListener() {
            @Override
            public void onZoomInStart(View view) {
                view.setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
            }

            @Override
            public void onZoomInEnd(View view) {

            }

            @Override
            public void onZoomOutStart(View view) {

            }

            @Override
            public void onZoomOutEnd(View view) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

        mZoomHoverView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {
                Toast.makeText(ZoomHoverActivity.this, "selected position=" + position, Toast.LENGTH_SHORT).show();
            }
        });

        mZoomHoverView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mZoomHoverView.setSelectedItem(1);
            }
        }, 2000);

        mZoomHoverView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mZoomHoverView.setSelectedItem(3);
            }
        }, 3000);

        mZoomHoverView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mZoomHoverView.setSelectedItem(0);
            }
        }, 4000);
        //
        mZoomHoverGridView.addSpanItem(0, 1, 2);
        mZoomHoverGridView.addSpanItem(3, 2, 2);
        mZoomHoverGridView.addSpanItem(4, 2, 1);
        mZoomHoverGridView.addSpanItem(5, 1, 3);

        mZoomHoverGridView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
