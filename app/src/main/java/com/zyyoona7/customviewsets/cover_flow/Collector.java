package com.zyyoona7.customviewsets.cover_flow;

import android.view.View;

import java.util.ArrayList;

public class Collector
{
  ArrayList<View> mOldViews = new ArrayList();
  
  protected void collect(View v)
  {
    this.mOldViews.add(v);
  }
  
  protected View retrieve()
  {
    if (this.mOldViews.size() == 0) {
      return null;
    }
    return (View)this.mOldViews.remove(0);
  }
}
