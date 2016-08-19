package com.zyyoona7.customviewsets.overlay_card;

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
        mAdapter = new TestZoomHoverAdapter(mList);
        final SimpleArrayMap<Integer, Integer> map = new SimpleArrayMap<>();
        map.put(0, 2);
        mAdapter.setSpan(map);
        mZoomHoverView.setAdapter(mAdapter);

        mZoomHoverView.setOnZoomAnimatorListener(new ZoomHoverView.OnZoomAnimatorListener() {
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

        mZoomHoverView.setOnItemSelectedListener(new ZoomHoverView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {
                Toast.makeText(ZoomHoverActivity.this,"selected position="+position,Toast.LENGTH_SHORT).show();
            }
        });

        mZoomHoverView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mZoomHoverView.setSelectedItem(1);
            }
        },2000);

        mZoomHoverView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mZoomHoverView.setSelectedItem(3);
            }
        },3000);

        mZoomHoverView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mZoomHoverView.setSelectedItem(0);
            }
        },4000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
