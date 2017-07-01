package com.lwq.richedittext.super_editext;

import android.graphics.Typeface;

/**
 * User:lwq
 * Date:2017-05-12
 * Time:15:50
 * introduction:
 */

public class PaintBrush {
    public PaintBrush() {
    }

    public PaintBrush(int textSize, int typeface, int textColor) {
        this.textSize = textSize;
        this.typeface = typeface;
        this.textColor = textColor;
    }

    private int textSize = 18;
    private int typeface = Typeface.NORMAL;
    private int textColor = 0xff404040;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTypeface() {
        return typeface;
    }

    public void setTypeface(int typeface) {
        this.typeface = typeface;
    }

    @Override
    public String toString() {
        return "PaintBrush{" +
                "textSize=" + textSize +
                ", typeface=" + typeface +
                ", textColor=" + textColor +
                '}';
    }
}
