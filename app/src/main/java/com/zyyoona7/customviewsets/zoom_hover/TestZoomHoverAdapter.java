package com.zyyoona7.customviewsets.zoom_hover;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyyoona7.customviewsets.R;

import java.util.List;

/**
 * Created by User on 2016/8/16.
 */

public class TestZoomHoverAdapter extends ZoomHoverAdapter<String> {


    public TestZoomHoverAdapter(List<String> list) {
        super(list);
    }

    @Override
    public View getView(ViewGroup parent, int position, String s) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_overlay_card, parent, false);
        TextView textView= (TextView) contentView.findViewById(R.id.tv_item);
        textView.setText(s);
        return contentView;
    }
}
