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
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.GridLayout;

import com.zyyoona7.customviewsets.R;
import com.zyyoona7.customviewsets.utils.DensityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * Created by zyyoona7 on 2016/8/29.
 */

public class ZoomHoverGridView extends GridLayout implements View.OnClickListener, ZoomHoverAdapter.OnDataChangedListener {

    private static final String TAG = "ZoomHoverGridView";

    //adapter
    private ZoomHoverAdapter mZoomHoverAdapter;

    // 需要的列数
    private int mColumnNum = 3;

    // 需要的行数
    private int mRowNum = 3;

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
    private List<View> mViewList = new ArrayList<>();

    //存储Span信息的list
    private SimpleArrayMap<Integer, ZoomHoverSpan> mSpanMap = new SimpleArrayMap<>();

    //存储每个item
    private List<ZoomHoverItem> mItemList = new ArrayList<>();

    //矩阵，用来存储位置信息
    private boolean mMatrix[][];

    //基准的宽高
    private int mBaseWidth = 0;
    private int mBaseHeight = 0;
    //是否使用基准宽高
    private boolean mUseBaseWH = false;

    //确定位置时起始地行列值，防止每次从0开始循环
    private int mStartColumn = 0;
    private int mStartRow = 0;

    public ZoomHoverGridView(Context context) {
        this(context, null);
    }

    public ZoomHoverGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZoomHoverView);
        mDivider = typedArray.getDimensionPixelOffset(R.styleable.ZoomHoverView_zhv_divider, DensityUtils.dip2px(context, 5));
        mMarginParent = typedArray.getDimensionPixelOffset(R.styleable.ZoomHoverView_zhv_margin_parent, DensityUtils.dip2px(context, 5));
        mColumnNum = typedArray.getInt(R.styleable.ZoomHoverView_zhv_column_num, 3);
        mAnimDuration = typedArray.getInt(R.styleable.ZoomHoverView_zhv_zoom_duration, getResources().getInteger(android.R.integer.config_shortAnimTime));
        mAnimZoomTo = typedArray.getFloat(R.styleable.ZoomHoverView_zhv_zoom_to, 1.2f);
        mUseBaseWH = typedArray.getBoolean(R.styleable.ZoomHoverView_zhv_use_baseWH, false);
        mBaseWidth = typedArray.getDimensionPixelSize(R.styleable.ZoomHoverView_zhv_base_width, DensityUtils.dip2px(context, 50));
        mBaseHeight = typedArray.getDimensionPixelSize(R.styleable.ZoomHoverView_zhv_base_height, DensityUtils.dip2px(context, 50));
        typedArray.recycle();
        mZoomOutInterpolator = mZoomInInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        //移除所有view
        removeAllViews();
        //计算子view并保存到list中
        calculateChild();
        //计算每个view的下标并设置位置
        calculatePosition();
        //计算子view的实际位置
        calculatePositionExact();
        //添加view
        addItem();
    }

    /**
     * 计算子view的个数并添加到list中
     */
    private void calculateChild() {
        mViewList.clear();
        mItemList.clear();

        for (int i = 0; i < mZoomHoverAdapter.getCount(); i++) {
            View childView = mZoomHoverAdapter.getView(this, i, mZoomHoverAdapter.getItem(i));
            //添加view到mViewList
            mViewList.add(childView);
            childView.setId(i + 1);
            if (mSpanMap.containsKey(i)) {
                int tempRowSpan = mSpanMap.get(i).getRowSpan();
                int tempColumnSpan = mSpanMap.get(i).getColumnSpan();
                tempRowSpan = tempRowSpan >= 1 ? tempRowSpan : 1;
                tempColumnSpan = tempColumnSpan >= 1 ? tempColumnSpan : 1;
                if (tempColumnSpan > mColumnNum) {
                    tempColumnSpan = mColumnNum;
                }
                //添加item到mItemList
                mItemList.add(new ZoomHoverItem(tempRowSpan, tempColumnSpan));
            } else {
                //添加item到mItemList
                mItemList.add(new ZoomHoverItem(1, 1));
            }
        }
    }

    /**
     * 计算每个item的下标
     */
    private void calculatePosition() {
        //计算最大的行数和列数
        calculateMaxRowColumn();
        setColumnCount(mColumnNum);
        setRowCount(mRowNum);
        //初始化数组
        mMatrix = new boolean[mRowNum][mColumnNum];
        //循环确定每个item的位置
        for (ZoomHoverItem item : mItemList) {
            setPosition(item);
        }
    }

    /**
     * 测量最差的情况下行数
     */
    private void calculateMaxRowColumn() {
        for (ZoomHoverItem item : mItemList) {
            mRowNum += item.getRowSpan();
        }
    }

    /**
     * 设置每个item的位置
     *
     * @param item
     */
    private void setPosition(ZoomHoverItem item) {
        //遍历每行
        for (int i = mStartRow; i < mRowNum; i++) {
            //换行以后重置起始列
            mStartColumn = 0;
            //遍历每列
            for (int j = mStartColumn; j < mColumnNum; j++) {
                if (mRowNum < i + item.getRowSpan() || mColumnNum < j + item.getColumnSpan()) {
                    continue;
                }
                //判断是否可以放在该区域
                if (!canPutPosition(item, i, j)) {
                    continue;
                }
                item.setRow(i);
                item.setColumn(j);
                mStartColumn = j;
                mStartRow = i;
                //标记已使用的位置
                markPosition(item, i, j);
                return;
            }
        }
    }

    /**
     * 确定是否可以放在此位置
     *
     * @param item
     * @param row
     * @param column
     * @return
     */
    private boolean canPutPosition(ZoomHoverItem item, int row, int column) {
        //从当前位置到span的区域判断
        for (int i = row; i < row + item.getRowSpan(); i++) {
            for (int j = column; j < column + item.getColumnSpan(); j++) {
                if (mMatrix[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 标记使用过的位置
     *
     * @param item
     * @param row
     * @param column
     */
    private void markPosition(ZoomHoverItem item, int row, int column) {
        //从当前位置到span的区域判断
        for (int i = row; i < row + item.getRowSpan(); i++) {
            for (int j = column; j < column + item.getColumnSpan(); j++) {
                mMatrix[i][j] = true;
            }
        }
    }

    /**
     * 计算布局精确的行数和列数
     */
    private void calculatePositionExact() {
        mColumnNum = 1;
        mRowNum = 1;
        //由行数为key，value为每行的列数
        HashMap<Integer, Integer> rowMap = new HashMap<>();
        //由列数为key，value为每列的行数
        HashMap<Integer, Integer> columnMap = new HashMap<>();

        for (ZoomHoverItem item : mItemList) {
            int row = item.getRow();
            int column = item.getColumn();

            if (rowMap.containsKey(row)) {
                //如果为同一行则列数相加
                rowMap.put(row, rowMap.get(row) + item.getColumnSpan());
            } else {
                rowMap.put(row, item.getColumnSpan());
            }

            if (columnMap.containsKey(column)) {
                //如果同一列，则行数相加
                columnMap.put(column, columnMap.get(column) + item.getRowSpan());
            } else {
                columnMap.put(column, item.getRowSpan());
            }
        }

        Set<Integer> rowKeySet = rowMap.keySet();
        //遍历取出最大列数
        for (Integer i : rowKeySet) {
            int rows = rowMap.get(i);
            if (rows < mColumnNum) {
                continue;
            }
            mColumnNum = rows;
        }

        Set<Integer> columnKeySet = columnMap.keySet();
        //遍历取出最大行数
        for (Integer i : columnKeySet) {
            int columns = columnMap.get(i);
            if (columns < mRowNum) {
                continue;
            }
            mRowNum = columns;
        }

    }

    /**
     * 将view添加进GridLayout
     */
    private void addItem() {
        for (int i = 0; i < mItemList.size(); i++) {
            View childView = mViewList.get(i);
            ZoomHoverItem item = mItemList.get(i);
            GridLayout.LayoutParams childViewLp = (LayoutParams) childView.getLayoutParams();
            if (childViewLp == null) {
                childViewLp = new GridLayout.LayoutParams();
            }
            //设置子view的宽高
            if (mUseBaseWH) {
                childViewLp.width = mBaseWidth * item.getColumnSpan() + (item.getColumnSpan() > 1 ? (item.getColumnSpan() - 1) * mDivider : 0);
                childViewLp.height = mBaseHeight * item.getRowSpan() + (item.getRowSpan() > 1 ? (item.getRowSpan() - 1) * mDivider : 0);
            } else {
                if (childViewLp.width > 0) {
                    childViewLp.width = childViewLp.width * item.getColumnSpan() + (item.getColumnSpan() > 1 ? (item.getColumnSpan() - 1) * mDivider : 0);
                } else {
                    childViewLp.width = LayoutParams.WRAP_CONTENT;
                }

                if (childViewLp.height > 0) {
                    childViewLp.height = childViewLp.height * item.getRowSpan() + (item.getRowSpan() > 1 ? (item.getRowSpan() - 1) * mDivider : 0);
                } else {
                    childViewLp.height = LayoutParams.WRAP_CONTENT;
                }
            }
            //确定子view的行列位置
            childViewLp.rowSpec = GridLayout.spec(item.getRow(), item.getRowSpan());
            childViewLp.columnSpec = GridLayout.spec(item.getColumn(), item.getColumnSpan());
            int right = 0;
            int top = 0;
            int bottom = 0;
            int left = 0;
            if (item.getColumn() > 0) {
                //除了第一列左边距为分割线的宽度
                left = mDivider;
            } else if (item.getColumn() == 0) {
                //第一列的左边距=距离父布局的边距
                left = mMarginParent;
            }

            if (item.getRow() > 0) {
                //不是第一行上边距为分割线的宽度
                top = mDivider;
            } else if (item.getRow() == 0) {
                //第一行的上边距=距离父布局的边距
                top = mMarginParent;
            }

            //最后一列的右边距=距离父布局的边距
            if (item.getColumn() + item.getColumnSpan() >= mColumnNum) {
                right = mMarginParent;
            }

            //最后一行的下边距=距离父布局的边距
            if (item.getRow() + item.getRowSpan() >= mRowNum) {
                bottom = mMarginParent;
            }
            //设置margin
            childViewLp.setMargins(left, top, right, bottom);
            addView(childView, childViewLp);
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
     * 设置跨度列表信息
     *
     * @param map
     */
    public void setSpanList(SimpleArrayMap<Integer, ZoomHoverSpan> map) {
        this.mSpanMap.clear();
        this.mSpanMap.putAll(map);
        if (mZoomHoverAdapter != null) {
            //如果adapter不为null重新布局
            changeAdapter();
        }
    }

    /**
     * 添加跨度item
     *
     * @param item
     */
    public void addSpanItem(int position, ZoomHoverSpan item) {
        this.mSpanMap.put(position, item);
        if (mZoomHoverAdapter != null) {
            //如果adapter不为null重新布局
            changeAdapter();
        }
    }

    /**
     * 添加跨度信息
     *
     * @param position
     * @param rowSpan
     * @param columnSpan
     */
    public void addSpanItem(int position, int rowSpan, int columnSpan) {
        this.mSpanMap.put(position, new ZoomHoverSpan(rowSpan, columnSpan));
        if (mZoomHoverAdapter != null) {
            //如果adapter不为null重新布局
            changeAdapter();
        }
    }

    /**
     * 设置是否使用基础宽高
     *
     * @param useBaseWH
     */
    public void setUseBaseWH(boolean useBaseWH) {
        this.mUseBaseWH = useBaseWH;
    }

    /**
     * 设置基础的宽度
     *
     * @param baseWidth
     */
    public void setBaseWidth(int baseWidth) {
        this.mBaseWidth = baseWidth;
    }

    /**
     * 设置基础高度
     *
     * @param baseHeight
     */
    public void setBaseHeight(int baseHeight) {
        this.mBaseHeight = baseHeight;
    }

}
