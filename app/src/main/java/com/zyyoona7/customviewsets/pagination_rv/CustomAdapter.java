package com.zyyoona7.customviewsets.pagination_rv;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zyyoona7.customviewsets.R;

/**
 * Created by zyyoona7 on 2016/9/23.
 */

public class CustomAdapter extends BaseQuickAdapter<String> {


    public CustomAdapter() {
        super(R.layout.item_pagination, null);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int positions) {
        super.onBindViewHolder(holder, positions);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_item, s);
    }
}
