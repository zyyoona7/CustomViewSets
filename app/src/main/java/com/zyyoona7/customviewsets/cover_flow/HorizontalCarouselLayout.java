package com.zyyoona7.customviewsets.cover_flow;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;

public class HorizontalCarouselLayout
        extends ViewGroup {
    private final float SCALE_RATIO = 0.9F;
    private int mGestureSensitivity = 80;
    private int DURATION = 200;
    private int mSpaceBetweenViews = 20;
    private int mRotation;
    private boolean mRotationEnabled = false;
    private int mTranslate;
    private boolean mTranslatateEnbabled = false;
    private float mSetInactiveViewTransparency = 1.0F;
    private int mHowManyViews = 99;
    private float mChildSizeRatio = 0.6F;
    private BaseAdapter mAdapter = null;
    private int mCurrentItem = 0;
    private int mCenterView = 0;
    private int mChildrenWidth;
    private int mChildrenWidthMiddle;
    private int mChildrenHeight;
    private int mChildrenHeightMiddle;
    private int mHeightCenter;
    private int mWidthCenter;
    private int mMaxChildUnderCenter;
    private float mViewZoomOutFactor = 0.0F;
    private int mCoverflowRotation = 0;
    private Collector mCollector = new Collector();
    private Matrix mMatrix = new Matrix();
    private Context mContext;
    private float mGap;
    private boolean mIsAnimating = false;
    private long mCurTime;
    private long mStartTime;
    private int mItemtoReach = 0;
    private CarouselInterface mCallback;
    private Runnable animationTask = new Runnable() {
        public void run() {
            HorizontalCarouselLayout.this.mCurTime = SystemClock.uptimeMillis();
            long totalTime = HorizontalCarouselLayout.this.mCurTime - HorizontalCarouselLayout.this.mStartTime;
            if (totalTime > HorizontalCarouselLayout.this.DURATION) {
                if (HorizontalCarouselLayout.this.mItemtoReach > HorizontalCarouselLayout.this.mCurrentItem) {
                    HorizontalCarouselLayout.this.fillBottom();
                } else {
                    HorizontalCarouselLayout.this.fillTop();
                }
                HorizontalCarouselLayout.this.mCurrentItem = HorizontalCarouselLayout.this.mItemtoReach;
                HorizontalCarouselLayout.this.mGap = 0.0F;
                HorizontalCarouselLayout.this.mIsAnimating = false;

                HorizontalCarouselLayout.this.mCenterView = HorizontalCarouselLayout.this.mCurrentItem;
                if (HorizontalCarouselLayout.this.mCurrentItem >= HorizontalCarouselLayout.this.mMaxChildUnderCenter) {
                    HorizontalCarouselLayout.this.mCenterView = HorizontalCarouselLayout.this.mMaxChildUnderCenter;
                }
                HorizontalCarouselLayout.this.removeCallbacks(HorizontalCarouselLayout.this.animationTask);
                if (HorizontalCarouselLayout.this.mCallback != null) {
                    HorizontalCarouselLayout.this.mCallback.onItemChangedListener(mAdapter.getView(mCurrentItem, null, HorizontalCarouselLayout.this), mCurrentItem);
                }
            } else {
                float perCent = (float) totalTime / HorizontalCarouselLayout.this.DURATION;
                HorizontalCarouselLayout.this.mGap = ((HorizontalCarouselLayout.this.mCurrentItem - HorizontalCarouselLayout.this.mItemtoReach) * perCent);
                HorizontalCarouselLayout.this.post(this);
            }
            HorizontalCarouselLayout.this.childrenLayout(HorizontalCarouselLayout.this.mGap);
            HorizontalCarouselLayout.this.invalidate();
        }
    };
    private GestureDetector mGestureDetector = new GestureDetector(this.mContext,
            new GestureDetector.SimpleOnGestureListener() {
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if ((!HorizontalCarouselLayout.this.mIsAnimating) && (HorizontalCarouselLayout.this.mAdapter != null)) {
                        int dx = (int) (e2.getX() - e1.getX());
                        if ((Math.abs(dx) > HorizontalCarouselLayout.this.mGestureSensitivity) &&
                                (Math.abs(velocityY) < Math.abs(velocityX))) {
                            if (velocityX > 0.0F) {
                                if (HorizontalCarouselLayout.this.mCurrentItem > 0) {
                                    HorizontalCarouselLayout.this.mItemtoReach = (HorizontalCarouselLayout.this.mCurrentItem - 1);
                                    HorizontalCarouselLayout.this.mStartTime = SystemClock.uptimeMillis();
                                    HorizontalCarouselLayout.this.mIsAnimating = true;
                                    HorizontalCarouselLayout.this.post(HorizontalCarouselLayout.this.animationTask);
                                    return true;
                                }
                            } else if (HorizontalCarouselLayout.this.mCurrentItem < HorizontalCarouselLayout.this.mAdapter.getCount() - 1) {
                                HorizontalCarouselLayout.this.mItemtoReach = (HorizontalCarouselLayout.this.mCurrentItem + 1);
                                HorizontalCarouselLayout.this.mStartTime = SystemClock.uptimeMillis();
                                HorizontalCarouselLayout.this.mIsAnimating = true;
                                HorizontalCarouselLayout.this.post(HorizontalCarouselLayout.this.animationTask);
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
    );

    public HorizontalCarouselLayout(Context context) {
        super(context);
        this.mContext = context;
        initSlidingAnimation();
    }

    public HorizontalCarouselLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initSlidingAnimation();
    }

    public HorizontalCarouselLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initSlidingAnimation();
    }

    public void disableRotation() {
        this.mRotationEnabled = false;
    }

    public void disableTranslate() {
        this.mTranslatateEnbabled = false;
    }

    public void setOnCarouselViewChangedListener(CarouselInterface carouselInterface) {
        this.mCallback = carouselInterface;
    }

    public void setGestureSensitivity(int gestureSensitivity) {
        this.mGestureSensitivity = gestureSensitivity;
    }

    public void setStyle(HorizontalCarouselStyle style) {
        this.mSetInactiveViewTransparency = style.getInactiveViewTransparency();
        this.mSpaceBetweenViews = style.getSpaceBetweenViews();
        this.mRotation = style.getRotation();
        this.mRotationEnabled = style.isRotationEnabled();
        this.mTranslate = style.getTranslate();
        this.mTranslatateEnbabled = style.isTranslatateEnbabled();
        this.mHowManyViews = style.getHowManyViews();
        this.mChildSizeRatio = style.getChildSizeRatio();
        this.mCoverflowRotation = style.getCoverflowRotation();
        this.mViewZoomOutFactor = style.getViewZoomOutFactor();
        this.DURATION = style.getAnimationTime();
    }

    public void setAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            this.mAdapter = adapter;
            this.mCenterView = (this.mCurrentItem = 0);
            if (this.mHowManyViews % 2 == 0) {
                this.mMaxChildUnderCenter = (this.mHowManyViews / 2);
            } else {
                this.mMaxChildUnderCenter = (this.mHowManyViews / 2);
            }
            for (int i = 0; i <= this.mMaxChildUnderCenter; i++) {
                if (i > this.mAdapter.getCount() - 1) {
                    break;
                }
                View v = this.mAdapter.getView(i, null, this);
                addView(v);
            }
            childrenLayout(0.0F);
            invalidate();
        }
    }

    private void fillTop() {
        if ((this.mCenterView < this.mMaxChildUnderCenter) &&
                (getChildCount() > this.mMaxChildUnderCenter + 1)) {
            View old = getChildAt(getChildCount() - 1);
            detachViewFromParent(old);
            this.mCollector.collect(old);
        }
        if (getChildCount() >= this.mHowManyViews) {
            View old = getChildAt(this.mHowManyViews - 1);
            detachViewFromParent(old);
            this.mCollector.collect(old);
        }
        int indexToRequest = this.mCurrentItem - (this.mMaxChildUnderCenter + 1);
        if (indexToRequest >= 0) {
            Log.v("UITEST", "Fill top with " + indexToRequest);
            View recycled = this.mCollector.retrieve();
            View v = this.mAdapter.getView(indexToRequest, recycled, this);
            if (recycled != null) {
                attachViewToParent(v, 0, generateDefaultLayoutParams());
                v.measure(this.mChildrenWidth, this.mChildrenHeight);
            } else {
                addView(v, 0);
            }
        }
    }

    private void fillBottom() {
        if (this.mCenterView >= this.mMaxChildUnderCenter) {
            View old = getChildAt(0);
            detachViewFromParent(old);
            this.mCollector.collect(old);
        }
        if (getChildCount() >= this.mHowManyViews) {
            View old = getChildAt(0);
            detachViewFromParent(old);
            this.mCollector.collect(old);
        }
        int indexToRequest = this.mCurrentItem + (this.mMaxChildUnderCenter + 1);
        if (indexToRequest < this.mAdapter.getCount()) {
            Log.v("UITEST", "Fill bottom with " + indexToRequest);
            View recycled = this.mCollector.retrieve();
            View v = this.mAdapter.getView(indexToRequest, recycled, this);
            if (recycled != null) {
                Log.v("UITEST", "view attached");
                attachViewToParent(v, -1, generateDefaultLayoutParams());
                v.measure(this.mChildrenWidth, this.mChildrenHeight);
            } else {
                Log.v("UITEST", "view added");
                addView(v, -1);
            }
        }
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(-1,
                -1);
    }

    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ViewGroup.LayoutParams(p);
    }

    private void initSlidingAnimation() {
        setChildrenDrawingOrderEnabled(true);
        setStaticTransformationsEnabled(true);
        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                HorizontalCarouselLayout.this.mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int specWidthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int specHeightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        this.mWidthCenter = (specWidthSize / 2);
        this.mHeightCenter = (specHeightSize / 2);
        this.mChildrenWidth = ((int) (specWidthSize * this.mChildSizeRatio));
        this.mChildrenHeight = ((int) (specHeightSize * this.mChildSizeRatio));
        this.mChildrenWidthMiddle = (this.mChildrenWidth / 2);
        this.mChildrenHeightMiddle = (this.mChildrenHeight / 2);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, this.mChildrenWidth, this.mChildrenHeight);
        }
        setMeasuredDimension(specWidthSize, specHeightSize);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        childrenLayout(0.0F);
    }

    private void childrenLayout(float gap) {
        int leftCenterView = this.mWidthCenter - this.mChildrenWidth / 2;
        int topCenterView = this.mHeightCenter - this.mChildrenHeight / 2;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            float offset = this.mCenterView - i - gap;
            int left = (int) (leftCenterView - this.mSpaceBetweenViews * offset);
            child.layout(left, topCenterView, left + this.mChildrenWidth,
                    topCenterView + this.mChildrenHeight);
        }
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        int centerView = this.mCenterView;
        if (this.mGap > 0.5F) {
            centerView--;
        } else if (this.mGap < -0.5F) {
            centerView++;
        }
        if (i < centerView) {
            return i;
        }
        if (i > centerView) {
            return centerView + (childCount - 1) - i;
        }
        return childCount - 1;
    }

    protected boolean getChildStaticTransformation(View child, Transformation t) {
        Camera camera = new Camera();
        int leftCenterView = this.mWidthCenter - this.mChildrenWidthMiddle;
        float offset = (-child.getLeft() + leftCenterView) /
                this.mSpaceBetweenViews;
        if (offset != 0.0F) {
            float absOffset = Math.abs(offset);
            float scale = (float) Math.pow(0.8999999761581421D, absOffset);
            t.clear();
            t.setTransformationType(Transformation.TYPE_MATRIX);
            t.setAlpha(this.mSetInactiveViewTransparency);
            Matrix m = t.getMatrix();
            m.setScale(scale, scale);
            if (this.mTranslatateEnbabled) {
                m.setTranslate(0.0F, this.mTranslate * absOffset);
            }
            if (offset > 0.0F) {
                camera.save();
                camera.translate(0.0F, 0.0F, this.mViewZoomOutFactor * offset);
                camera.rotateY(this.mCoverflowRotation);
                camera.getMatrix(m);
                camera.restore();
                m.preTranslate(-this.mChildrenWidthMiddle, -this.mChildrenHeight);
                m.postTranslate(this.mChildrenWidthMiddle, this.mChildrenHeight);
            } else {
                camera.save();
                camera.translate(0.0F, 0.0F, -(this.mViewZoomOutFactor * offset));
                camera.rotateY(-this.mCoverflowRotation);
                camera.getMatrix(m);
                camera.restore();
                m.preTranslate(-this.mChildrenWidthMiddle, -this.mChildrenHeight);
                m.postTranslate(this.mChildrenWidthMiddle, this.mChildrenHeight);
            }
            this.mMatrix.reset();
            if (this.mRotationEnabled) {
                this.mMatrix.setRotate(this.mRotation * offset);
            }
            this.mMatrix.preTranslate(-this.mChildrenWidthMiddle, -this.mChildrenHeightMiddle);
            this.mMatrix.postTranslate(this.mChildrenWidthMiddle, this.mChildrenHeightMiddle);
            m.setConcat(m, this.mMatrix);
        }
        return true;
    }

    public static abstract interface CarouselInterface {
        public abstract void onItemChangedListener(View paramView, int paramInt);
    }
}