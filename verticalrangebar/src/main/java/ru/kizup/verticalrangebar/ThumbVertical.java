package ru.kizup.verticalrangebar;

import android.graphics.Rect;

/**
 * Created by Neuron on 16.03.2018.
 */

public class ThumbVertical {

    private int centerX;
    private int centerY;
    private Rect rect;
    private int value;
    private int size;
    private VerticalRangeBar view;
    private boolean isStartThumb;

    ThumbVertical(int centerX, int centerY, int size, VerticalRangeBar verticalRangeBar, boolean isStartThumb) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.size = size;
        this.view = verticalRangeBar;
        rect = new Rect();
        this.isStartThumb = isStartThumb;
        setRect();
    }

    boolean contains(int x, int y) {
        return rect.contains(x, y);
    }

    private void setRect() {
        int halfSize = size / 2;
        rect.set(centerX - halfSize, centerY - halfSize, centerX + halfSize, centerY + halfSize);
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
        int padding = isStartThumb ? view.getPaddingTop() : view.getPaddingBottom();
        float value = ((float) (centerY - padding) / view.getStepPx());
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
        int offset = isStartThumb ? view.getPaddingTop() : view.getPaddingBottom();
        int y = (int) (view.getStepPx() * value) + offset;
        setCenterY(y);
    }

    void decrease() {
        setValue(value - 1);
    }

    void increase() {
        setValue(value + 1);
    }
}
