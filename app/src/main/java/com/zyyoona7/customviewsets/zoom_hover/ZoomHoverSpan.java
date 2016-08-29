package com.zyyoona7.customviewsets.zoom_hover;

/**
 * Created by zyyoona7 on 2016/8/25.
 * <p>
 * 设置对应的rowSpan,columnSpan
 */

public class ZoomHoverSpan {

    private int rowSpan;

    private int columnSpan;

    public ZoomHoverSpan() {
    }

    public ZoomHoverSpan(int rowSpan, int columnSpan) {
        this.rowSpan = rowSpan;
        this.columnSpan = columnSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }
}
