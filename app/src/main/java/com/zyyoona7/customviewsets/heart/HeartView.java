package com.zyyoona7.customviewsets.heart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zyyoona7.customviewsets.R;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zyyoona7 on 2017/7/3.
 * 直播平台进入直播间点亮功能的心形view
 */

public class HeartView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "HeartView";

    private boolean isRunning = false;
    private Paint mPaint;

    private ConcurrentLinkedQueue<Heart> mHearts = null;

    private float mWidth;
    private float mHeight;

    private Matrix mMatrix;

    private SparseArray<Bitmap> mBitmapSparseArray;

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        getHolder().addCallback(this);
        //设置背景透明
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //----
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);

        mHearts = new ConcurrentLinkedQueue<>();
        mMatrix = new Matrix();
        mBitmapSparseArray = new SparseArray<>();
        Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart_default);
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ss_heart1);
        Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ss_heart2);
        Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ss_heart3);
        Bitmap bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ss_heart4);
        Bitmap bitmap6 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ss_heart5);
        mBitmapSparseArray.put(Heart.DEFAULT, bitmap1);
        mBitmapSparseArray.put(Heart.PINK, bitmap2);
        mBitmapSparseArray.put(Heart.CYAN, bitmap3);
        mBitmapSparseArray.put(Heart.GREEN, bitmap4);
        mBitmapSparseArray.put(Heart.YELLOW, bitmap5);
        mBitmapSparseArray.put(Heart.BLUE, bitmap6);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        //回收bitmap
        for (int i = 0; i < mBitmapSparseArray.size(); i++) {
            if (mBitmapSparseArray.valueAt(i) != null) {
                mBitmapSparseArray.valueAt(i).recycle();
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    //清屏~
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    for (Heart heart : mHearts) {
                        if (mBitmapSparseArray.get(heart.getType()) == null) {
                            continue;
                        }
                        mMatrix.setTranslate(0, 0);
                        //位移到x,y
                        mMatrix.postTranslate(heart.getX(), heart.getY());
                        //缩放
//                        mMatrix.postScale()
                        //画bitmap
                        canvas.drawBitmap(mBitmapSparseArray.get(heart.getType()), mMatrix, mPaint);
                        //计算时间
                        if (heart.getT() < 1) {
                            heart.setT(heart.getT() + heart.getSpeed());
                            handleBezierXY(heart);
                        } else {
                            removeHeart(heart);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "run: " + e.getMessage());
            } finally {
                if (canvas != null) {
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }

        }
    }

    /**
     * 计算实时的点坐标
     *
     * @param heart
     */
    private void handleBezierXY(Heart heart) {
        //三阶贝塞尔曲线函数
        //x = (float) (Math.pow((1 - t), 3) * start.x + 3 * t * Math.pow((1 - t), 2) * control1.x + 3 * Math.pow(t, 2) * (1 - t) * control2.x + Math.pow(t, 3) * end.x);
        //y = (float) (Math.pow((1 - t), 3) * start.y + 3 * t * Math.pow((1 - t), 2) * control1.y + 3 * Math.pow(t, 2) * (1 - t) * control2.y + Math.pow(t, 3) * end.y);
        float x = (float) (Math.pow((1 - heart.getT()), 3) * heart.getStartX() + 3 * heart.getT() * Math.pow((1 - heart.getT()), 2) * heart.getControl1X() + 3 * Math.pow(heart.getT(), 2) * (1 - heart.getT()) * heart.getControl2X() + Math.pow(heart.getT(), 3) * heart.getEndX());
        float y = (float) (Math.pow((1 - heart.getT()), 3) * heart.getStartY() + 3 * heart.getT() * Math.pow((1 - heart.getT()), 2) * heart.getControl1Y() + 3 * Math.pow(heart.getT(), 2) * (1 - heart.getT()) * heart.getControl2Y() + Math.pow(heart.getT(), 3) * heart.getEndY());

        heart.setX(x);
        heart.setY(y);
    }

    /**
     * 添加heart
     */
    public void addHeart() {
        Heart heart = new Heart();
        initHeart(heart);
        mHearts.add(heart);
    }

    /**
     * 添加heart
     *
     * @param type
     */
    public void addHeart(int type) {
        Heart heart = new Heart();
        initHeart(heart);
        heart.setType(type);
        mHearts.add(heart);
    }

    /**
     * 重置
     *
     * @param heart
     */
    private void initHeart(Heart heart) {
        heart.initStartAndEnd(mWidth, mHeight);
        heart.initControl(mWidth, mHeight);
        heart.initSpeed();
    }

    /**
     * 移除Heart
     *
     * @param heart
     */
    private void removeHeart(Heart heart) {
        mHearts.remove(heart);
    }

}
