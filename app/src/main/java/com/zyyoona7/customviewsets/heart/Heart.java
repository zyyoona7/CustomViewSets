package com.zyyoona7.customviewsets.heart;

/**
 * Created by zyyoona7 on 2017/7/4.
 */

public class Heart {

    public static final int DEFAULT = 0;
    public static final int PINK = 1;
    public static final int CYAN = 2;
    public static final int GREEN = 3;
    public static final int YELLOW = 4;
    public static final int BLUE = 5;

    //实时坐标
    private float x;
    private float y;

    //起始点坐标
    private float startX;
    private float startY;

    //结束点坐标
    private float endX;
    private float endY;

    //三阶贝塞尔曲线（两个控制点）
    //控制点1坐标
    private float control1X;
    private float control1Y;

    //控制点2坐标
    private float control2X;
    private float control2Y;

    //实时的时间
    private float t=0;
    //速率
    private float speed;

    //bitmap图片类型
    private int type=DEFAULT;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public float getControl1X() {
        return control1X;
    }

    public void setControl1X(float control1X) {
        this.control1X = control1X;
    }

    public float getControl1Y() {
        return control1Y;
    }

    public void setControl1Y(float control1Y) {
        this.control1Y = control1Y;
    }

    public float getControl2X() {
        return control2X;
    }

    public void setControl2X(float control2X) {
        this.control2X = control2X;
    }

    public float getControl2Y() {
        return control2Y;
    }

    public void setControl2Y(float control2Y) {
        this.control2Y = control2Y;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 重置下x，y坐标
     * 位置在最底部的中间
     *
     * @param x
     * @param y
     */
    public void initXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 重置起始点和结束点
     *
     * @param width
     * @param height
     */
    public void initStartAndEnd(float width, float height) {
        //起始点和结束点为view的正下方和正上方
        this.startX = width / 2;
        this.startY = height;
        this.endX = width / 2;
        this.endY = 0;
        initXY(startX,startY);
    }

    /**
     * 重置控制点坐标
     *
     * @param width
     * @param height
     */
    public void initControl(float width, float height) {
        //随机生成控制点1
        this.control1X = (float) (Math.random() * width);
        this.control1Y = (float) (Math.random() * height);

        //随机生成控制点2
        this.control2X = (float) (Math.random() * width);
        this.control2Y = (float) (Math.random() * height);

        //如果两个点重合，重新生成控制点
        if (this.control1X == this.control2X && this.control1Y == this.control2Y) {
            initControl(width, height);
        }
    }

    /**
     * 重置速率
     */
    public void initSpeed() {
        //随机速率
        this.speed = (float) (Math.random() * 0.01 + 0.003);
    }

//    /**
//     * 重置时间
//     * t=0-1;
//     */
//    public void resetT() {
//        this.t = 0;
//    }
}
