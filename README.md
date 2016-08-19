# 自定义View集合

![Basic_Opreation](https://github.com/zyyoona7/CustomViewSets/blob/master/images/basic_operation.gif)

###ZoomHoverView
**click to zoom in and float view  点击放大悬浮的自定义View**

![ZoomHoverView](https://github.com/zyyoona7/CustomViewSets/blob/master/images/ZoomHoverView.gif)

####Useage

**layout:**
  ```xml
  <com.zyyoona7.customviewsets.overlay_card.ZoomHoverView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/zoom_hover_view"
        zhv:zhv_divider="5dp"
        zhv:zhv_margin_parent="30dp"
        zhv:zhv_column_num="3"
        zhv:zhv_zoom_duration="200"
        zhv:zhv_zoom_to="1.5"
        android:layout_gravity="center"
        android:background="@color/colorPrimary">

    </com.zyyoona7.customviewsets.overlay_card.ZoomHoverView>
  ```
**java**
  ```java
        mAdapter = new TestZoomHoverAdapter(mList);
        final SimpleArrayMap<Integer, Integer> map = new SimpleArrayMap<>();
        map.put(0, 2);
        mAdapter.setSpan(map);
        mZoomHoverView.setAdapter(mAdapter);
        //设置动画监听
        mZoomHoverView.setOnZoomAnimatorListener(new ZoomHoverView.OnZoomAnimatorListener() {
            @Override
            public void onZoomInStart(View view) {
                //放大动画开始
                view.setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
            }

            @Override
            public void onZoomInEnd(View view) {

            }

            @Override
            public void onZoomOutStart(View view) {
                //缩小动画开始
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
        
        //设置放大动画插值器
        mZoomHoverView.setZoomInInterpolator(interpolator);
        //设置缩小动画插值器
        mZoomHoverView.setZoomOutInterpolator(interpolator);
        //同时设置两个动画的插值器
        mZoomHoverView.setZoomInterpolator(interpolator);
        //设置选中的item
        mZoomHoverView.setSelectedItem(position);
  ```
  **自定义属性**
  ```xml
   <declare-styleable name="ZoomHoverView">
        <!--每行多少列-->
        <attr name="zhv_column_num" format="integer"/>
        <!--分割线-->
        <attr name="zhv_divider" format="dimension"/>
        <!--距离父控件的margin-->
        <attr name="zhv_margin_parent" format="dimension"/>
        <!--动画持续时间-->
        <attr name="zhv_zoom_duration" format="integer"/>
        <!--动画缩放的倍数-->
        <attr name="zhv_zoom_to" format="float"/>
    </declare-styleable>
  ```
  
