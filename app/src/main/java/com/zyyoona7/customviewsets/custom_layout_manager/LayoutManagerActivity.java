package com.zyyoona7.customviewsets.custom_layout_manager;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.zyyoona7.customviewsets.BaseActivity;
import com.zyyoona7.customviewsets.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LayoutManagerActivity extends BaseActivity {

    @BindView(R.id.id_layout_manager_recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new CustomLayoutManager());
        CustomAdapter adapter = new CustomAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setNewData(createData());
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_layout_manager;
    }

    private List<String> createData() {
        List<String> list = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            list.add("position=" + i);
        }
        return list;
    }
}
