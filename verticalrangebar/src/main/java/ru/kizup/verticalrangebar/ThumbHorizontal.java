package ru.kizup.verticalrangebar;

import android.graphics.Rect;

/**
 * Created by Neuron on 16.03.2018.
 */

public class ThumbHorizontal {

    private int centerX;
    private int centerY;
    private Rect rect;
    private int value;
    private int size;
    private RangeBar rangeBar;
    private boolean isStartThumb;

    ThumbHorizontal(int centerX, int centerY, int size, RangeBar rangeBar, boolean isStartThumb) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.size = size;
        this.rangeBar = rangeBar;
        rect = new Rect();
        this.isStartThumb = isStartThumb;
        setRect();
    }

    boolean contains(int x, int y) {
        return rect.contains(y, x);
    }

    private void setRect() {
        int halfSize = size / 2;
        rect.set(centerX - halfSize, centerY - halfSize, centerX + halfSize, centerY + halfSize);
//        rect.set(centerY - halfSize, centerX - halfSize, centerY + halfSize, centerX + halfSize);
    }

    int getCenterX() {
        return centerX;
    }

    void setCenterX(int centerX) {
        this.centerX = centerX;
        setRect();
    }

    int getCenterY() {
        return centerY;
    }

    void setCenterY(int centerY) {
        this.centerY = centerY;
        int padding = isStartThumb ? rangeBar.getPaddingStart() : rangeBar.getPaddingEnd();
        float value = ((float) (centerY - padding) / rangeBar.getStepPx());
        this.value = Math.round(value);
        setRect();
    }

    Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
        int offset = isStartThumb ? rangeBar.getPaddingStart() : rangeBar.getPaddingEnd();
        int y = (int) (rangeBar.getStepPx() * value) + offset;
        setCenterY(y);
    }

    void decrease() {
        setValue(value - 1);
    }

    void increase() {
        setValue(value + 1);
    }
}