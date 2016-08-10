package com.zyyoona7.customviewsets.basic_operation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zyyoona7.customviewsets.utils.DensityUtils;

/**
 * Created by zyyoona7 on 2016/8/10.
 * <p>
 * view基本操作图形集合
 */

public class CustomBasicOpView extends View {

    private static final int PAINT_COLOR = 0xff0000ff;
    private static final int TEXT_SIZE = 14;
    private static final int STOKE_WIDTH = 5;
    //长方形的宽高
    private int rectWidth = 0;
    private int rectHeight = 0;
    //间距
    private int margin = 0;

    //同坐标系的padding
    private int padding = 0;

    //画笔
    private Paint mPaint;

    //宽高
    private int mWidth = 0;
    private int mHeight = 0;
    /**
     * 利用这三个点来划分画图的范围（防止和坐标轴的图象重叠）
     */
    //起始xy点
    private Point startXYPoint = null;
    //结束的x轴点
    private Point endXPoint = null;
    //结束的y轴点
    private Point endYPoint = null;

    private int mTextHeight = 0;

    public CustomBasicOpView(Context context) {
        super(context);
        initPaint();
    }

    public CustomBasicOpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        mTextHeight = DensityUtils.sp2px(context, TEXT_SIZE);
        rectWidth = DensityUtils.dip2px(context, 150);
        rectHeight = DensityUtils.dip2px(context, 75);
        margin = DensityUtils.dip2px(context, 8);
        padding = DensityUtils.dip2px(context, 5);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STOKE_WIDTH);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(PAINT_COLOR);

    }

    /**
     * 改变成画字体
     */
    private void changeToTextPaint() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextHeight);
        mPaint.setColor(Color.BLACK);
    }

    private void changeStyleFill() {
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void changeStyleStroke() {
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void changeColorRed() {
        mPaint.setColor(Color.RED);
    }

    private void changeColorBlue() {
        mPaint.setColor(Color.BLUE);
    }

    private void changeColorGray() {
        mPaint.setColor(Color.GRAY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        startXYPoint = new Point(mWidth/2-rectWidth/2, 3 * margin + padding + DensityUtils.sp2px(getContext(), 16));
        endXPoint = new Point(mWidth - (3 * margin + padding), 3 * margin + padding + DensityUtils.sp2px(getContext(), 16));
        endYPoint = new Point(3 * margin + padding, mHeight - (3 * margin + padding + DensityUtils.sp2px(getContext(), 16)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画点
        canvas.save();
        changeColorBlue();
        canvas.drawPoint(startXYPoint.x + margin, startXYPoint.y + margin, mPaint);
        canvas.drawPoint(startXYPoint.x + 3 * margin, startXYPoint.y + margin, mPaint);
        canvas.drawPoint(startXYPoint.x + 5 * margin, startXYPoint.y + margin, mPaint);
        canvas.restore();

        //画线
        canvas.save();
        canvas.drawLine(startXYPoint.x, startXYPoint.y + 3 * margin, startXYPoint.x + rectWidth, startXYPoint.y + 3 * margin, mPaint);
        canvas.drawLine(startXYPoint.x, startXYPoint.y + 4 * margin, startXYPoint.x + 2 * rectWidth / 3, startXYPoint.y + 4 * margin, mPaint);
        canvas.drawLine(startXYPoint.x, startXYPoint.y + 5 * margin, startXYPoint.x + rectWidth / 2, startXYPoint.y + 5 * margin, mPaint);
        canvas.restore();

        //画长方形
        canvas.save();
        //空心长方形
        Rect rect = new Rect(startXYPoint.x, startXYPoint.y + 6 * margin, startXYPoint.x + rectWidth + 10, startXYPoint.y + rectHeight + 6 * margin);
        Rect rect1 = new Rect(startXYPoint.x, startXYPoint.y + rectHeight + 7 * margin, startXYPoint.x + rectWidth + 10, startXYPoint.y + 2 * rectHeight + 7 * margin);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect, mPaint);
        changeToTextPaint();
        String strHollowRect = "我是空心长方形，Paint的style为STROKE";
        int hollowRectTextWidth = (int) mPaint.measureText(strHollowRect);
        //画文字
        canvas.drawText(strHollowRect, (rectWidth - hollowRectTextWidth) / 2 + startXYPoint.x, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 6 * margin + mTextHeight , mPaint);
        //实心长方形
        changeColorBlue();
        canvas.drawRect(rect1, mPaint);
        changeToTextPaint();
        String strRect = "我是实心长方形，Paint的style为FILL";
        int rectWidth1 = (int) mPaint.measureText(strRect);
        canvas.drawText(strRect, (rectWidth - rectWidth1) / 2 + startXYPoint.x + 10, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 7 * margin + rectHeight + mTextHeight , mPaint);
        canvas.restore();

        //画正方形
        changeColorBlue();
        Rect rect2 = new Rect(startXYPoint.x+rectHeight/2, startXYPoint.y + 8 * margin + 2 * rectHeight, startXYPoint.x + rectHeight+rectHeight/2, startXYPoint.y + 8 * margin + 3 * rectHeight);
        canvas.drawRect(rect2, mPaint);
        changeToTextPaint();
        String strSquare = "我是实心正方形";
        int squareWidth = (int) mPaint.measureText(strSquare);
        canvas.drawText(strSquare, (rectHeight - squareWidth) / 2 + startXYPoint.x+rectHeight/2, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 8 * margin + 2 * rectHeight + mTextHeight , mPaint);

        //画椭圆
        RectF rect3 = new RectF(startXYPoint.x, startXYPoint.y + 9 * margin + 3 * rectHeight, startXYPoint.x + rectWidth, startXYPoint.y + 9 * margin + 4 * rectHeight);
        changeColorGray();
        canvas.drawRect(rect3, mPaint);
        changeColorBlue();
        canvas.drawOval(rect3, mPaint);
        changeToTextPaint();
        String strOval = "我是椭圆，根据我的外围长方形画出";
        int ovalWidth = (int) mPaint.measureText(strOval);
        canvas.drawText(strOval, (rectWidth - ovalWidth) / 2 + startXYPoint.x, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 9 * margin + 3 * rectHeight + mTextHeight , mPaint);

        //画圆
        Rect rect4 = new Rect(startXYPoint.x+rectHeight/2, startXYPoint.y + 10 * margin + 4 * rectHeight, startXYPoint.x+rectHeight/2 + rectHeight, startXYPoint.y + 10 * margin + 5 * rectHeight);
        changeColorGray();
        canvas.drawRect(rect4, mPaint);
        changeColorBlue();
        canvas.drawCircle(startXYPoint.x+rectHeight/2 + rectHeight / 2, startXYPoint.y + 10 * margin + 4 * rectHeight + rectHeight / 2, rectHeight / 2, mPaint);
        changeToTextPaint();
        String strCircle = "我是圆";
        int circleWidth = (int) mPaint.measureText(strCircle);
        canvas.drawText(strCircle, (rectHeight - circleWidth) / 2 + startXYPoint.x+rectHeight/2, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 10 * margin + 4 * rectHeight + mTextHeight , mPaint);

        //画椭圆圆弧
        //useCenter=false
        RectF rect5 = new RectF(startXYPoint.x , startXYPoint.y + 11 * margin + 5 * rectHeight, startXYPoint.x + rectWidth, startXYPoint.y + 11 * margin + 6 * rectHeight);
        changeColorGray();
        canvas.drawRect(rect5, mPaint);
        changeColorBlue();
        canvas.drawArc(rect5, 0, 90, false, mPaint);
        changeToTextPaint();
        String strOvalArc = "我(蓝色区域)是椭圆圆弧，useCenter=false";
        int ovalArcWidth = (int) mPaint.measureText(strOvalArc);
        canvas.drawText(strOvalArc, (rectWidth - ovalArcWidth) / 2 + startXYPoint.x, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 11 * margin + 5 * rectHeight + mTextHeight , mPaint);

        //useCenter=true
        RectF rect6 = new RectF(startXYPoint.x , startXYPoint.y + 12 * margin + 6 * rectHeight, startXYPoint.x + rectWidth, startXYPoint.y + 12 * margin + 7 * rectHeight);
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(rect6, mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawArc(rect6, 0, 90, true, mPaint);
        changeToTextPaint();
        String strOvalArc1 = "我(蓝色区域)是椭圆圆弧，useCenter=true";
        int ovalArcWidth1 = (int) mPaint.measureText(strOvalArc1);
        canvas.drawText(strOvalArc1, (rectWidth - ovalArcWidth1) / 2 + startXYPoint.x , (rectHeight - mTextHeight) / 2 + startXYPoint.y + 12 * margin + 6 * rectHeight + mTextHeight , mPaint);

        //画圆的圆弧
        //useCenter=true
        RectF rect7 = new RectF(startXYPoint.x+rectHeight/2, startXYPoint.y + 13 * margin + 7 * rectHeight, startXYPoint.x +rectHeight/2+ rectHeight, startXYPoint.y + 13 * margin + 8 * rectHeight);
        changeColorGray();
        canvas.drawRect(rect7, mPaint);
        changeColorBlue();
        canvas.drawArc(rect7, 0, 90, true, mPaint);
        changeToTextPaint();
        String strCircleArc1 = "我(蓝色区域)是圆的圆弧，useCenter=true";
        int circleArcWidth1 = (int) mPaint.measureText(strCircleArc1);
        canvas.drawText(strCircleArc1, (rectHeight - circleArcWidth1) / 2 + startXYPoint.x+rectHeight/2, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 13 * margin + 7 * rectHeight + mTextHeight , mPaint);

        //useCenter=false
        RectF rect8 = new RectF(startXYPoint.x+rectHeight/2, startXYPoint.y + 14 * margin + 8 * rectHeight, startXYPoint.x+rectHeight/2 + rectHeight, startXYPoint.y + 14 * margin + 9 * rectHeight);
        changeColorGray();
        canvas.drawRect(rect8, mPaint);
        changeColorBlue();
        canvas.drawArc(rect8, 0, 90, false, mPaint);
        changeToTextPaint();
        String strCircleArc = "我(蓝色区域)是圆的圆弧，useCenter=false";
        int circleArcWidth = (int) mPaint.measureText(strCircleArc);
        canvas.drawText(strCircleArc, (rectHeight - circleArcWidth) / 2 + startXYPoint.x+rectHeight/2, (rectHeight - mTextHeight) / 2 + startXYPoint.y + 14 * margin + 8 * rectHeight + mTextHeight , mPaint);


        //画饼状图
        RectF rect9 = new RectF(startXYPoint.x+rectHeight/2, startXYPoint.y + 15 * margin + 9 * rectHeight, startXYPoint.x +rectHeight/2+ rectHeight, startXYPoint.y + 15 * margin + 10 * rectHeight);
        changeColorGray();
        canvas.drawArc(rect9,0,60,true,mPaint);
        changeColorBlue();
        canvas.drawArc(rect9,60,90,true,mPaint);
        changeColorRed();
        canvas.drawArc(rect9,90,150,true,mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(rect9,240,120,true,mPaint);

        //画饼状图
        RectF rect10 = new RectF(startXYPoint.x+rectHeight/2, startXYPoint.y + 16 * margin + 10 * rectHeight, startXYPoint.x +rectHeight/2+ rectHeight, startXYPoint.y + 16 * margin + 11 * rectHeight);
        changeColorGray();
        canvas.drawArc(rect10,0,90,false,mPaint);
        changeColorBlue();
        canvas.drawArc(rect10,90,90,false,mPaint);
        changeColorRed();
        canvas.drawArc(rect10,180,90,false,mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(rect10,270,90,false,mPaint);
    }
}
