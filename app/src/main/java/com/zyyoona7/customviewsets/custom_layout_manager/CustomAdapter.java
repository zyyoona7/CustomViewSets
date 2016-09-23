package com.zyyoona7.customviewsets.custom_layout_manager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zyyoona7.customviewsets.R;

/**
 * Created by zyyoona7 on 2016/9/23.
 */

public class CustomAdapter extends BaseQuickAdapter<String> {


    public CustomAdapter() {
        super(R.layout.item_overlay_card, null);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_item, s);
    }
}
