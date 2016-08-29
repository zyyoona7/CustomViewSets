package com.zyyoona7.customviewsets.zoom_hover;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zyyoona7 on 2016/8/25.
 * 设置span时候需要
 */

public class ZoomHoverItem implements Parcelable {

    //---row,column确定坐标
    //行
    private int row;

    //列
    private int column;

    //---支持跨行,跨列
    //横跨行
    private int rowSpan=1;

    //横跨列
    private int columnSpan=1;

    public ZoomHoverItem(){

    }

    public ZoomHoverItem(int rowSpan, int columnSpan) {
        this.rowSpan = rowSpan;
        this.columnSpan = columnSpan;
    }

    public ZoomHoverItem(int row, int column, int rowSpan, int columnSpan) {
        this.row = row;
        this.column = column;
        this.rowSpan = rowSpan;
        this.columnSpan = columnSpan;
    }

    protected ZoomHoverItem(Parcel in) {
        row = in.readInt();
        column = in.readInt();
        rowSpan = in.readInt();
        columnSpan = in.readInt();
    }

    public static final Creator<ZoomHoverItem> CREATOR = new Creator<ZoomHoverItem>() {
        @Override
        public ZoomHoverItem createFromParcel(Parcel in) {
            return new ZoomHoverItem(in);
        }

        @Override
        public ZoomHoverItem[] newArray(int size) {
            return new ZoomHoverItem[size];
        }
    };

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(row);
        dest.writeInt(column);
        dest.writeInt(rowSpan);
        dest.writeInt(columnSpan);
    }

    @Override
    public boolean equals(Object o) {
        ZoomHoverItem item= (ZoomHoverItem) o;
        return item.getRow()==row && item.getColumn()==column;
    }
}
