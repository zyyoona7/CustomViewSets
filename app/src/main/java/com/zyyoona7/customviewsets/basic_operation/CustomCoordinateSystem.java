package com.zyyoona7.customviewsets.basic_operation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zyyoona7.customviewsets.utils.DensityUtils;

/**
 * Created by zyyoona7 on 2016/8/9.
 *
 * 自定义坐标系
 */

public class CustomCoordinateSystem extends View {
    private static final int LINE_WITH=5;
    private static final int LINE_COLOR=0xffff0000;
    private static final int TEXT_SIZE=16;
    //坐标系类型
    //数学坐标系
    private static final int TYPE_MATH=0;
    //手机坐标系
    private static final int TYPE_PHONE=1;

    //宽
    private int mWith=0;
    //高
    private int mHeight=0;

    //画笔对象
    private Paint mPaint;

    //和线的间距
    private int mMarginLine=15;

    //和定点的间距
    private int mMarginPoint=30;

    private int padding=0;

    private int mType=1;

    public CustomCoordinateSystem(Context context) {
        super(context);
        initPaint();
    }

    public CustomCoordinateSystem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        mMarginLine=DensityUtils.dip2px(context,8);
        mMarginPoint=DensityUtils.dip2px(context,16);
        padding=DensityUtils.dip2px(context,5);
    }

    private void initPaint(){
        mPaint=new Paint();
        //设置是否使用抗锯齿功能
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(LINE_WITH);
        mPaint.setColor(LINE_COLOR);
        //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式Cap.ROUND,或方形样式Cap.SQUARE
        mPaint.setStrokeCap(Paint.Cap.ROUND);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWith=w;
        mHeight=h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if(mType==TYPE_MATH) {
            //画x轴的线
            canvas.drawLine(0, mHeight / 2, mWith - LINE_WITH, mHeight / 2, mPaint);
            //画x轴的上箭头线
            canvas.drawLine(mWith - mMarginPoint, mHeight / 2 - mMarginLine, mWith - LINE_WITH, mHeight / 2, mPaint);
            //画x轴的下箭头线
            canvas.drawLine(mWith - mMarginPoint, mHeight / 2 + mMarginLine, mWith - LINE_WITH, mHeight / 2, mPaint);
            //画y轴的线
            canvas.drawLine(mWith / 2, 0, mWith / 2, mHeight - LINE_WITH, mPaint);
            //画y轴的左箭头线
            canvas.drawLine(mWith / 2 - mMarginLine, mHeight - mMarginPoint, mWith / 2, mHeight - LINE_WITH, mPaint);
            //画y轴的右箭头线
            canvas.drawLine(mWith / 2 + mMarginLine, mHeight - mMarginPoint, mWith / 2, mHeight - LINE_WITH, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(DensityUtils.sp2px(getContext(),TEXT_SIZE));
            mPaint.setColor(LINE_COLOR);
            //画坐标
            //原点坐标
            String oCoordinate = "(0,0)";
            //x轴最大坐标
            String xCoordinate = "(" + mWith/2 + ",0)";
            //y轴最大坐标
            String yCoordinate = "(0," + mHeight/2 + ")";
            //计算原点坐标文字的宽度
            float oPointWidth = mPaint.measureText(oCoordinate);
            //画原点坐标
            canvas.drawText(oCoordinate, mWith / 2 - padding - oPointWidth, mHeight / 2 + DensityUtils.sp2px(getContext(), TEXT_SIZE)+10, mPaint);
            //计算x轴坐标的宽度
            float xPointWidth = mPaint.measureText(xCoordinate);
            //画x轴坐标
            canvas.drawText(xCoordinate, mWith - padding - xPointWidth, mHeight / 2 + DensityUtils.sp2px(getContext(), TEXT_SIZE) + mMarginLine, mPaint);
            //画y轴坐标
            canvas.drawText(yCoordinate, mWith / 2 + mMarginLine, mHeight - padding, mPaint);
        }else {
            //画x轴的线
            canvas.drawLine(padding+mMarginLine,padding+mMarginLine,mWith-LINE_WITH,padding+mMarginLine,mPaint);
            //画x轴的上箭头线
            canvas.drawLine(mWith-mMarginPoint,padding,mWith-LINE_WITH,padding+mMarginLine,mPaint);
            //画x轴的下箭头线
            canvas.drawLine(mWith-mMarginPoint,padding+2*mMarginLine,mWith-LINE_WITH,padding+mMarginLine,mPaint);
            //画y轴的线
            canvas.drawLine(padding+mMarginLine,padding+mMarginLine,padding+mMarginLine,mHeight-LINE_WITH,mPaint);
            //画y轴的左箭头线
            canvas.drawLine(padding,mHeight-mMarginPoint,padding+mMarginLine,mHeight-LINE_WITH,mPaint);
            //画y轴的右箭头线
            canvas.drawLine(padding+2*mMarginLine,mHeight-mMarginPoint,padding+mMarginLine,mHeight-LINE_WITH,mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(DensityUtils.sp2px(getContext(),TEXT_SIZE));
            mPaint.setColor(LINE_COLOR);
            //画坐标
            //原点坐标
            String oCoordinate="(0,0)";
            //x轴最大坐标
            String xCoordinate="("+mWith+",0)";
            //y轴最大坐标
            String yCoordinate="(0,"+mHeight+")";
            //画原点坐标
            //drawText的y轴坐标的基线为底部，所以要减去字体的高度
            canvas.drawText(oCoordinate,2*padding+mMarginLine,2*padding+mMarginLine+DensityUtils.sp2px(getContext(),TEXT_SIZE),mPaint);
            //计算x轴坐标的宽度
            float xPointWidth=mPaint.measureText(xCoordinate);
            //画x轴坐标
            canvas.drawText(xCoordinate,mWith-padding-xPointWidth,2*padding+2*mMarginLine+DensityUtils.sp2px(getContext(),TEXT_SIZE),mPaint);
            //画y轴坐标
            canvas.drawText(yCoordinate,2*padding+2*mMarginLine,mHeight-padding,mPaint);

        }
        canvas.restore();
    }
}
