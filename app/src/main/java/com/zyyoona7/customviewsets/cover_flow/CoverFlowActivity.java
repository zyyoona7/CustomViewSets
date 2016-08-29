package com.zyyoona7.customviewsets.cover_flow;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zyyoona7.customviewsets.BaseActivity;
import com.zyyoona7.customviewsets.R;

import butterknife.BindView;

public class CoverFlowActivity extends BaseActivity {

    @BindView(R.id.horizontal_carousel)
    HorizontalCarouselLayout layout;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_cover_flow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout.setAdapter(new ImageAdapter());
        layout.setStyle(new HorizontalCarouselStyle(this,HorizontalCarouselStyle.NO_STYLE));
    }

    public class ImageAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView=new ImageView(CoverFlowActivity.this);
            imageView.setImageResource(R.mipmap.yoona);

            return imageView;
        }
    }
}