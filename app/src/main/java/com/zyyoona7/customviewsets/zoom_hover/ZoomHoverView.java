package com.zyyoona7.customviewsets.zoom_hover;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.zyyoona7.customviewsets.R;
import com.zyyoona7.customviewsets.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * Created by zyyoona7 on 2016/8/16.
 */

public class ZoomHoverView extends RelativeLayout implements View.OnClickListener, ZoomHoverAdapter.OnDataChangedListener {

    private static final String TAG = "ZoomHoverView";

    //adapter
    private ZoomHoverAdapter mZoomHoverAdapter;

    // 需要的列数
    private int mColumnNum = 3;

    //记录当前列
    private int mCurrentColumn = 0;

    //记录当前行
    private int mCurrentRow = 1;

    //记录每行第一列的下标（row First column position）
    //K--所在行数   V--当前view的下标
    private SimpleArrayMap<Integer, Integer> mRFColPosMap = new SimpleArrayMap<>();

    //子view距离父控件的外边距宽度
    private int mMarginParent = 20;

    //行列的分割线宽度
    private int mDivider = 10;

    //当前放大动画
    private AnimatorSet mCurrentZoomInAnim = null;

    //当前缩小动画
    private AnimatorSet mCurrentZoomOutAnim = null;

    //缩放动画监听器
    private OnZoomAnimatorListener mOnZoomAnimatorListener = null;

    //动画持续时间
    private int mAnimDuration;

    //动画缩放倍数
    private float mAnimZoomTo;

    //缩放动画插值器
    private Interpolator mZoomInInterpolator;
    private Interpolator mZoomOutInterpolator;

    //上一个ZoomOut的view(为了解决快速切换时，上一个被缩小的view缩放大小不正常的情况)
    private View mPreZoomOutView;

    //当前被选中的view
    private View mCurrentView = null;

    //item选中监听器
    private OnItemSelectedListener mOnItemSelectedListener;

    //存储当前layout中所有子view
    private List<View> mViewList;

    //需要横跨的map
    private SimpleArrayMap<Integer, Integer> mNeedSpanMap = new SimpleArrayMap<>();

    public ZoomHoverView(Context context) {
        this(context, null);
    }

    public ZoomHoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZoomHoverView);
        mDivider = typedArray.getDimensionPixelOffset(R.styleable.ZoomHoverView_zhv_divider, DensityUtils.dip2px(context, 5));
        mMarginParent = typedArray.getDimensionPixelOffset(R.styleable.ZoomHoverView_zhv_margin_parent, DensityUtils.dip2px(context, 5));
        mColumnNum = typedArray.getInt(R.styleable.ZoomHoverView_zhv_column_num, 3);
        mAnimDuration = typedArray.getInt(R.styleable.ZoomHoverView_zhv_zoom_duration, getResources().getInteger(android.R.integer.config_shortAnimTime));
        mAnimZoomTo = typedArray.getFloat(R.styleable.ZoomHoverView_zhv_zoom_to, 1.2f);
        typedArray.recycle();
        mZoomOutInterpolator = mZoomInInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.e(TAG, "onMeasure: 执行了..." + getWidth() + "," + getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: 执行了...");
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */

    public void setAdapter(ZoomHoverAdapter adapter) {
        this.mZoomHoverAdapter = adapter;
        mZoomHoverAdapter.setDataChangedListener(this);
        changeAdapter();
    }


    /**
     * 根据adapter添加view
     */
    private void changeAdapter() {
        removeAllViews();
        //重置参数（因为changeAdapter可能调用多次）
        mColumnNum = 3;
        mCurrentRow = 1;
        mCurrentColumn = 0;
        mRFColPosMap.clear();
        requestLayout();
        mViewList = new ArrayList<>(mZoomHoverAdapter.getCount());
        //需要拉伸的下标的参数K-下标，V-跨度
        SimpleArrayMap<Integer, Integer> needSpanMap = mNeedSpanMap;
        for (int i = 0; i < mZoomHoverAdapter.getCount(); i++) {
            //获取子view
            View childView = mZoomHoverAdapter.getView(this, i, mZoomHoverAdapter.getItem(i));
            mViewList.add(childView);
            childView.setId(i + 1);

            //判断当前view是否设置了跨度
            int span = 1;
            if (needSpanMap.containsKey(i)) {
                span = needSpanMap.get(i);
            }

            //获取AdapterView的的布局参数
            RelativeLayout.LayoutParams childViewParams = (LayoutParams) childView.getLayoutParams();

            if (childViewParams == null) {
                childViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            //如果view的宽高设置了wrap_content或者match_parent则span无效
            if (childViewParams.width <= 0) {
                span = 1;
            }

            //如果跨度有变，重新设置view的宽
            if (span > 1 && span <= mColumnNum) {
                childViewParams.width = childViewParams.width * span + (span - 1) * mDivider;
            } else if (span < 1) {
                span = 1;
            } else if (span > mColumnNum) {
                span = mColumnNum;
                childViewParams.width = childViewParams.width * span + (span - 1) * mDivider;
            }

            //设置右下左上的边距
            int rightMargin = 0;
            int bottomMargin = 0;
            int leftMargin = 0;
            int topMargin = 0;

            //如果跨度+当前的列>设置的列数，换行
            if (span + mCurrentColumn > mColumnNum) {
                //换行当前行数+1
                mCurrentRow++;
                //当前列等于当前view的跨度
                mCurrentColumn = span;
                //换行以后肯定是第一个
                mRFColPosMap.put(mCurrentRow, i);
                //换行操作
                //因为换行，肯定不是第一行
                //换行操作后将当前view添加到上一行第一个位置的下面
                childViewParams.addRule(RelativeLayout.BELOW,
                        mViewList.get(mRFColPosMap.get(mCurrentRow - 1)).getId());
                //不是第一行，所以上边距为分割线的宽度
                topMargin = mDivider;
                //换行后位置在左边第一个，所以左边距为距离父控件的边距
                leftMargin = mMarginParent;
            } else {
                if (mCurrentColumn <= 0 && mCurrentRow <= 1) {
                    //第一行第一列的位置保存第一列信息，同时第一列不需要任何相对规则
                    mRFColPosMap.put(mCurrentRow, i);
                    //第一行第一列上边距和左边距都是距离父控件的边距
                    topMargin = mMarginParent;
                    leftMargin = mMarginParent;
                } else {
                    //不是每一行的第一个，就添加到前一个的view的右面，并且和前一个顶部对齐
                    childViewParams.addRule(RelativeLayout.RIGHT_OF,
                            mViewList.get(i - 1).getId());
                    childViewParams.addRule(ALIGN_TOP, mViewList.get(i - 1).getId());

                }
                //移动到当前列
                mCurrentColumn += span;
            }

            if (mCurrentColumn >= mColumnNum || i >= mZoomHoverAdapter.getCount() - 1) {
                //如果当前列为列总数或者当前view的下标等于最后一个view的下标那么就是最右边的view，设置父边距
                rightMargin = mMarginParent;
            } else {
                rightMargin = mDivider;
            }

            //如果当前view是最后一个那么他肯定是最后一行
            if (i >= (mZoomHoverAdapter.getCount() - 1)) {
                bottomMargin = mMarginParent;
            }

            //设置外边距
            childViewParams.setMargins(leftMargin, topMargin, rightMargin,
                    bottomMargin);
            //添加view
            addView(childView, childViewParams);
            //添加点击事件
            childView.setOnClickListener(this);
        }
    }


    @Override
    public void onChanged() {
        changeAdapter();
    }


    @Override
    public void onClick(View view) {
        if (mCurrentView == null) {
            //如果currentView为null，证明第一次点击
            zoomInAnim(view);
            mCurrentView = view;
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(mCurrentView, mCurrentView.getId() - 1);
            }
        } else {
            if (view.getId() != mCurrentView.getId()) {
                //点击的view不是currentView
                //currentView执行缩小动画
                zoomOutAnim(mCurrentView);
                //当前点击的view赋值给currentView
                mCurrentView = view;
                //执行放大动画
                zoomInAnim(mCurrentView);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(mCurrentView, mCurrentView.getId() - 1);
                }
            }
        }
    }

    /**
     * 放大动画
     *
     * @param view
     */
    private void zoomInAnim(final View view) {
        //将view放在其他view之上
        view.bringToFront();
        //按照bringToFront文档来的，暂没测试
        if (Build.VERSION.SDK_INT < KITKAT) {
            requestLayout();
        }
        if (mCurrentZoomInAnim != null) {
            //如果当前有放大动画执行，cancel调
            mCurrentZoomInAnim.cancel();
        }
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, mAnimZoomTo);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, mAnimZoomTo);
        objectAnimatorX.setDuration(mAnimDuration);
        objectAnimatorX.setInterpolator(mZoomInInterpolator);
        objectAnimatorY.setDuration(mAnimDuration);
        objectAnimatorY.setInterpolator(mZoomInInterpolator);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(objectAnimatorX, objectAnimatorY);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                //放大动画开始
                if (mOnZoomAnimatorListener != null) {
                    mOnZoomAnimatorListener.onZoomInStart(view);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //放大动画结束
                if (mOnZoomAnimatorListener != null) {
                    mOnZoomAnimatorListener.onZoomInEnd(view);
                }
                mCurrentZoomInAnim = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                //放大动画退出
                mCurrentZoomInAnim = null;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
        mCurrentZoomInAnim = set;
    }

    /**
     * 缩小动画
     *
     * @param view
     */
    private void zoomOutAnim(final View view) {
        if (mCurrentZoomOutAnim != null) {
            //如果当前有缩小动画执行，cancel调
            mCurrentZoomOutAnim.cancel();
            //动画cancel后，上一个缩小view的scaleX不是1.0，就手动设置scaleX，Y到1.0
            if (mPreZoomOutView != null && mPreZoomOutView.getScaleX() > 1.0) {
                mPreZoomOutView.setScaleX(1.0f);
                mPreZoomOutView.setScaleY(1.0f);
            }
        }
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", mAnimZoomTo, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", mAnimZoomTo, 1.0f);
        objectAnimatorX.setDuration(mAnimDuration);
        objectAnimatorX.setInterpolator(mZoomOutInterpolator);
        objectAnimatorY.setDuration(mAnimDuration);
        objectAnimatorY.setInterpolator(mZoomOutInterpolator);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(objectAnimatorX, objectAnimatorY);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                //缩小动画开始
                if (mOnZoomAnimatorListener != null) {
                    mOnZoomAnimatorListener.onZoomOutStart(view);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //缩小动画结束
                if (mOnZoomAnimatorListener != null) {
                    mOnZoomAnimatorListener.onZoomOutEnd(view);
                }
                mCurrentZoomOutAnim = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                //缩小动画退出
                mCurrentZoomOutAnim = null;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
        mCurrentZoomOutAnim = set;
        //把当前缩放的view作为上一个缩放的view
        mPreZoomOutView = view;
    }

    /**
     * 设置缩放动画监听器
     *
     * @param listener
     */
    public void setOnZoomAnimatorListener(OnZoomAnimatorListener listener) {
        this.mOnZoomAnimatorListener = listener;
    }

    /**
     * 同时设置插值器
     *
     * @param interpolator
     */
    public void setZoomInterpolator(Interpolator interpolator) {
        this.mZoomInInterpolator = this.mZoomOutInterpolator = interpolator;
    }

    /**
     * 设置放大动画插值器
     *
     * @param interpolator
     */
    public void setZoomInInterpolator(Interpolator interpolator) {
        this.mZoomInInterpolator = interpolator;
    }

    /**
     * 设置缩小动画的插值器
     *
     * @param interpolator
     */
    public void setZoomOutInterpolator(Interpolator interpolator) {
        this.mZoomOutInterpolator = interpolator;
    }

    /**
     * 设置item选中监听器
     *
     * @param listener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    /**
     * 设置选中的条目
     *
     * @param position
     */
    public void setSelectedItem(int position) {
        //list为空或者size<position不执行操作
        if (mViewList == null || mViewList.size() <= position) {
            return;
        }

        //同onClick事件处理
        if (mCurrentView != null) {
            if (mCurrentView.getId() - 1 != position) {
                zoomOutAnim(mCurrentView);
                mCurrentView = mViewList.get(position);
                zoomInAnim(mCurrentView);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(mCurrentView, mCurrentView.getId() - 1);
                }
            }
        } else {
            zoomInAnim(mViewList.get(position));
            mCurrentView = mViewList.get(position);
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(mCurrentView, mCurrentView.getId() - 1);
            }
        }
    }

    /**
     * 设置动画持续时长
     *
     * @param duration
     */
    public void setZoomDuration(int duration) {
        this.mAnimDuration = duration;
    }

    /**
     * 设置放大的倍数
     *
     * @param zoomTo
     */
    public void setZoomTo(float zoomTo) {
        this.mAnimZoomTo = zoomTo;
    }

    /**
     * 设置列数
     *
     * @param columnNum
     */
    public void setColumnNum(int columnNum) {
        this.mColumnNum = columnNum;
    }

    /**
     * 设置分割线宽度
     *
     * @param divider
     */
    public void setZoomDivider(int divider) {
        this.mDivider = DensityUtils.dip2px(getContext(), divider);
    }

    /**
     * 设置距离父边框的宽度
     *
     * @param marginParent
     */
    public void setZoomMarginParent(int marginParent) {
        this.mMarginParent = DensityUtils.dip2px(getContext(), marginParent);
    }

    /**
     * 设置需要横跨的下标和跨度
     *
     * @param map key代表下标
     *            value代表跨度
     */
    public void setSpan(SimpleArrayMap<Integer, Integer> map) {
        this.mNeedSpanMap.clear();
        this.mNeedSpanMap.putAll(map);
        if (this.mZoomHoverAdapter != null) {
            changeAdapter();
        }
    }

    public void addSpan(int position, int span) {
        this.mNeedSpanMap.clear();
        this.mNeedSpanMap.put(position, span);
        if (this.mZoomHoverAdapter != null) {
            changeAdapter();
        }
    }

}
