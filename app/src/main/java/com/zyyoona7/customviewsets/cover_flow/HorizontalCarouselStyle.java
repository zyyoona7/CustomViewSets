package com.zyyoona7.customviewsets.cover_flow;

import android.content.Context;
import android.util.TypedValue;

public class HorizontalCarouselStyle
{
  public static final int NO_STYLE = 0;
  public static final int STYLE_ZOOMED_OUT = 1;
  public static final int STYLE_COVERFLOW = 2;
  public static final int STYLE_RIGHT_ALIGNED_ROTATION = 3;
  public static final int STYLE_LEFT_ALIGNED_ROTATION = 4;
  private int mCoverflowRotation;
  private float mViewZoomOutFactor;
  private float mInactiveViewTransparency;
  private int mSpaceBetweenViews;
  private int mRotation;
  private boolean mRotationEnabled;
  private int mTranslate;
  private boolean mTranslatateEnbabled;
  private int mHowManyViews;
  private float mChildSizeRatio;
  private int mAnimationTime;
  private int dip;
  
  public HorizontalCarouselStyle(Context context, int style)
  {
    this.dip = ((int) TypedValue.applyDimension(1, 1.0F, context.getResources().getDisplayMetrics()));
    this.mInactiveViewTransparency = 1.0F;
    this.mHowManyViews = 99;
    this.mChildSizeRatio = 0.6F;
    this.mAnimationTime = 200;
    setStyle(style);
  }
  
  private void setStyle(int style)
  {
    switch (style)
    {
    case 1: 
      this.mSpaceBetweenViews = (80 * this.dip);
      this.mRotation = 0;
      this.mRotationEnabled = false;
      this.mTranslate = 50;
      this.mCoverflowRotation = 0;
      this.mViewZoomOutFactor = 30.0F;
      this.mTranslatateEnbabled = true;
      break;
    case 2: 
      this.mSpaceBetweenViews = (80 * this.dip);
      this.mRotation = 0;
      this.mRotationEnabled = false;
      this.mTranslate = -50;
      this.mCoverflowRotation = 45;
      this.mViewZoomOutFactor = 60.0F;
      this.mTranslatateEnbabled = true;
      break;
    case 3: 
      this.mSpaceBetweenViews = (80 * this.dip);
      this.mRotation = 30;
      this.mRotationEnabled = true;
      this.mTranslate = 50;
      this.mCoverflowRotation = 0;
      this.mViewZoomOutFactor = 0.0F;
      this.mTranslatateEnbabled = true;
      break;
    case 4: 
      this.mSpaceBetweenViews = (80 * this.dip);
      this.mRotation = -30;
      this.mRotationEnabled = true;
      this.mTranslate = -50;
      this.mCoverflowRotation = 0;
      this.mViewZoomOutFactor = 0.0F;
      this.mTranslatateEnbabled = true;
      break;
    default: 
      this.mSpaceBetweenViews = (50 * this.dip);
      this.mRotation = 0;
      this.mRotationEnabled = false;
      this.mTranslate = 0;
      this.mCoverflowRotation = 0;
      this.mViewZoomOutFactor = 0.0F;
      this.mTranslatateEnbabled = false;
    }
  }
  
  public void setInactiveViewTransparency(float mSetInactiveViewTransparency)
  {
    this.mInactiveViewTransparency = mSetInactiveViewTransparency;
  }
  
  public void setSpaceBetweenViews(int spaceInPixel)
  {
    this.mSpaceBetweenViews = (spaceInPixel * this.dip);
  }
  
  public void setRotation(int rotation)
  {
    this.mRotationEnabled = true;
    this.mRotation = rotation;
  }
  
  public void setTranslate(int translate)
  {
    this.mTranslatateEnbabled = true;
    this.mTranslate = translate;
  }
  
  public boolean setHowManyViews(int howMany)
  {
    if (howMany % 2 != 0) {
      return false;
    }
    this.mHowManyViews = howMany;
    return true;
  }
  
  public boolean setChildSizeRation(float parentPerCent)
  {
    if ((parentPerCent > 1.0F) && (parentPerCent < 1.0F)) {
      return false;
    }
    this.mChildSizeRatio = parentPerCent;
    return true;
  }
  
  public void setAnimationTime(int mAnimationTime)
  {
    this.mAnimationTime = mAnimationTime;
  }
  
  public void setCoverflowRotation(int mCoverflowRotation)
  {
    this.mCoverflowRotation = mCoverflowRotation;
  }
  
  public void setViewZoomOutFactor(float mViewZoomOutFactor)
  {
    this.mViewZoomOutFactor = mViewZoomOutFactor;
  }
  
  public float getInactiveViewTransparency()
  {
    return this.mInactiveViewTransparency;
  }
  
  public int getSpaceBetweenViews()
  {
    return this.mSpaceBetweenViews;
  }
  
  public int getRotation()
  {
    return this.mRotation;
  }
  
  public boolean isRotationEnabled()
  {
    return this.mRotationEnabled;
  }
  
  public int getTranslate()
  {
    return this.mTranslate;
  }
  
  public boolean isTranslatateEnbabled()
  {
    return this.mTranslatateEnbabled;
  }
  
  public int getHowManyViews()
  {
    return this.mHowManyViews;
  }
  
  public float getChildSizeRatio()
  {
    return this.mChildSizeRatio;
  }
  
  public int getAnimationTime()
  {
    return this.mAnimationTime;
  }
  
  public int getCoverflowRotation()
  {
    return this.mCoverflowRotation;
  }
  
  public float getViewZoomOutFactor()
  {
    return this.mViewZoomOutFactor;
  }
}