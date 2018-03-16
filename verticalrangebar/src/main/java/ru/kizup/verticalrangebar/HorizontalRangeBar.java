package ru.kizup.verticalrangebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by Neuron on 16.03.2018.
 */

public class HorizontalRangeBar extends View implements RangeBar {

    public static final String TAG = VerticalRangeBar.class.getSimpleName();

    private static final String START_VALUE_PARAM = "start_value_param";
    private static final String END_VALUE_PARAM = "end_value_param";

    private Paint mLinePaint;
    private Paint mDisabledLinePaint;
    private Paint mStartThumbPaint;
    private Paint mEndThumbPaint;

//    private Rect mStartThumbRect;
//    private Rect mEndThumbRect;

    private ThumbHorizontal mStartThumb;
    private ThumbHorizontal mEndThumb;

    private int viewHeight;

    private int width;
    private int startThumbSize;
    private int endThumbSize;

    private int lineStartY;
    private int lineStopY;

    private int minValue;
    private int maxValue;

    // значние верхнего ползунка
    private int startValue;
    // значение нижнего ползунка
    private int endValue;

    private boolean isCanDragStart = false;
    private boolean isCanDragEnd = false;

    private boolean debug = false;

    private Paint mDebugPaint;

    private int realStartValue;
    private int realEndValue;

    private OnRangeChangeListener mOnRangeChangeListener;
    private boolean firstMeasure = true;

    public HorizontalRangeBar(Context context) {
        this(context, null);
    }

    public HorizontalRangeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public HorizontalRangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HorizontalRangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet set, int defStyleAttr, int defStyleRes) {
        @ColorRes int startThumbColor = Color.RED;
        @ColorRes int endThumbColor = Color.GREEN;
        @ColorRes int lineColor = Color.BLUE;
        @ColorRes int disableLineColor = Color.GRAY;

        if (set != null) {
            TypedArray ta = context.getTheme()
                    .obtainStyledAttributes(set, R.styleable.VerticalRangeBar, defStyleAttr, defStyleRes);
            try {
                startValue = ta.getResourceId(R.styleable.VerticalRangeBar_startProgress, 0);
                endValue = ta.getResourceId(R.styleable.VerticalRangeBar_endProgress, 100);
                startThumbColor = ta.getResourceId(R.styleable.VerticalRangeBar_startThumbColor, Color.RED);
                endThumbColor = ta.getResourceId(R.styleable.VerticalRangeBar_endThumbColor, Color.GREEN);
                lineColor = ta.getResourceId(R.styleable.VerticalRangeBar_lineColor, Color.BLUE);
                disableLineColor = ta.getResourceId(R.styleable.VerticalRangeBar_disableLineColor, Color.GRAY);

                setStartValue(startValue);
                setEndValue(endValue);
            } finally {
                ta.recycle();
            }
        }

        mDebugPaint = new Paint();
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setStrokeWidth(3f);
        mDebugPaint.setColor(Color.BLACK);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(4f);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setColor(getResources().getColor(lineColor));

        mDisabledLinePaint = new Paint();
        mDisabledLinePaint.setStrokeWidth(4f);
        mDisabledLinePaint.setStyle(Paint.Style.STROKE);
        mDisabledLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mDisabledLinePaint.setColor(getResources().getColor(disableLineColor));

        mStartThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStartThumbPaint.setStyle(Paint.Style.FILL);
        mStartThumbPaint.setColor(getResources().getColor(startThumbColor));
        mStartThumbPaint.setTextSize(24);

        mEndThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEndThumbPaint.setStyle(Paint.Style.FILL);
        mEndThumbPaint.setColor(getResources().getColor(endThumbColor));
        mEndThumbPaint.setTextSize(24);

        startThumbSize = context.getResources().getDimensionPixelSize(R.dimen.start_thumb_size);
        endThumbSize = context.getResources().getDimensionPixelSize(R.dimen.end_thumb_size);
    }

    public void setRange(int start, int end) {
        if (start > end) {
            throw new IllegalStateException("Start value " + start + " can't be bigger than end value " + end);
        }

        if (start == end) {
            throw new IllegalStateException("Start value " + start + " can't be equals end value " + end);
        }

        realStartValue = start;
        realEndValue = end;
        invalidate();
    }

    public int getRealStartValue() {
        int value = Math.round(startValue);
        return (int) (realStartValue + (value * getRange() / 100f));
    }

    public int getRealEndValue() {
        int value = Math.round(endValue);
        return (int) (realStartValue + (value * getRange() / 100f));
    }

    public void setRealStartValue(int value) {
        int sv = (int) (((value * 100f) - (realStartValue * 100f)) / getRange());
        setStartValue(sv);
    }

    public void setRealEndValue(int value) {
        int ev = (int) (((realEndValue * 100f) + (value * 100f)) / getRange());
        setEndValue(ev - 100);
    }

    int getRange() {
        return realEndValue - realStartValue;
    }

    public void setOnRangeChangeListener(OnRangeChangeListener onRangeChangeListener) {
        mOnRangeChangeListener = onRangeChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getStartValue() {
        return mStartThumb.getValue();
    }

    public void setStartValue(int startValue) {
        if (startValue < 0) startValue = 0;
        this.startValue = startValue;
        if (mStartThumb != null) {
            mStartThumb.setValue(startValue);
            checkThumbs();
            invalidate();
        }
        if (mOnRangeChangeListener != null) {
            mOnRangeChangeListener.onStartProgressChange(startValue);
        }
    }

    public int getEndValue() {
        return mEndThumb.getValue();
    }

    public void setEndValue(int endValue) {
        if (endValue > 100) endValue = 100;
        this.endValue = endValue;
        if (mEndThumb != null) {
            mEndThumb.setValue(endValue);
            checkThumbs();
            invalidate();
        }
        if (mOnRangeChangeListener != null) {
            mOnRangeChangeListener.onEndProgressChange(endValue);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mStartThumb == null || mEndThumb == null) return false;

        int action = event.getAction();
        int touchY = (int) event.getY();
        int touchX = (int) event.getX();

        switch (action) {
            case ACTION_DOWN: {
                isCanDragStart = mStartThumb.contains(touchX, touchY);
                isCanDragEnd = mEndThumb.contains(touchX, touchY);
                break;
            }
            case ACTION_MOVE: {
                int y = touchX;

                if (y <= getPaddingStart()) y = getPaddingStart();
                if (y >= width) y = width;

                if (isCanDragStart) {
                    if (y >= (mEndThumb.getRect().left - (startThumbSize / 2))) {
                        y = (mEndThumb.getRect().left - (startThumbSize / 2));
                    }
                    mStartThumb.setCenterY(y);
                    startValue = mStartThumb.getValue();
                    if (mOnRangeChangeListener != null) {
                        mOnRangeChangeListener.onStartProgressChange(startValue);
                    }
                    invalidate();
                }

                if (isCanDragEnd) {
                    if (y >= (mStartThumb.getRect().right + endThumbSize / 2)) {
                        y = mStartThumb.getRect().right + endThumbSize / 2;
                    }

                    mEndThumb.setCenterY(y);
                    endValue = mEndThumb.getValue();
                    if (mOnRangeChangeListener != null) {
                        mOnRangeChangeListener.onEndProgressChange(endValue);
                    }
                    invalidate();
                }

                break;
            }
            case ACTION_UP: {
                isCanDragStart = false;
                isCanDragEnd = false;
                break;
            }
        }
        return true;
    }

    private void checkThumbs() {
        if (mStartThumb.getRect().bottom > mEndThumb.getRect().top) {
            if (mStartThumb.getValue() > 0) {
                mStartThumb.decrease();
                setStartValue(mStartThumb.getValue());
            }
            if (mEndThumb.getValue() < 100) {
                mEndThumb.increase();
                setEndValue(mEndThumb.getValue());
            }
//            checkThumbs();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewHeight = getPaddingStart() + getPaddingEnd() + endThumbSize;

        width = getWidth();

        width = width - (getPaddingEnd());

        lineStartY = getPaddingStart();
        lineStopY = width;

        if (mStartThumb == null) {
            mStartThumb = new ThumbHorizontal(getHeightCenter(), lineStartY, startThumbSize, this, true);
            mStartThumb.setValue(startValue);
        }

        if (mEndThumb == null) {
            mEndThumb = new ThumbHorizontal(getHeightCenter(), lineStopY, endThumbSize, this, false);
            mEndThumb.setValue(endValue);
        }
    }

    public int getStartThumbY() {
        if (mStartThumb == null) return 0;
        return mStartThumb.getCenterY();
    }

    public int getEndThumbY() {
        if (mEndThumb == null) return 0;
        return mEndThumb.getCenterY();
    }

    @Override
    public float getStepPx() {
        float stepPx = (float) getLineSize() / 100f;
        return stepPx;
    }

    int getLineSize() {
        int size = lineStopY - lineStartY;
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mStartThumb == null || mEndThumb == null) return;
        checkThumbs();
        viewHeight = getHeight();

        mStartThumb.setCenterX(getHeightCenter());
        mEndThumb.setCenterX(getHeightCenter());

        canvas.drawLine(mStartThumb.getCenterY(), getHeightCenter(), mEndThumb.getCenterY(), getHeightCenter(), mLinePaint);
        canvas.drawLine(lineStartY, getHeightCenter(), mStartThumb.getCenterY(), getHeightCenter(), mDisabledLinePaint);
        canvas.drawLine(lineStopY, getHeightCenter(), mEndThumb.getCenterY(), getHeightCenter(), mDisabledLinePaint);

        canvas.drawCircle(mStartThumb.getCenterY(), mStartThumb.getCenterX(), startThumbSize / 2, mStartThumbPaint);
        canvas.drawCircle(mEndThumb.getCenterY(), mEndThumb.getCenterX(), endThumbSize / 2, mEndThumbPaint);

        // debug draw
        if (debug) {
            canvas.drawRect(mStartThumb.getRect(), mDebugPaint);
            canvas.drawRect(mEndThumb.getRect(), mDebugPaint);

            canvas.drawText("-> " + mStartThumb.getValue(), mStartThumb.getCenterX() + 100, mStartThumb.getCenterY(), mStartThumbPaint);
            canvas.drawText("-> " + mEndThumb.getValue(), mEndThumb.getCenterX() + 100, mEndThumb.getCenterY(), mStartThumbPaint);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        mOnRangeChangeListener = null;
        mStartThumb = null;
        mEndThumb = null;
        mDebugPaint = null;
        mLinePaint = null;
        mStartThumbPaint = null;
        mEndThumbPaint = null;
        mDisabledLinePaint = null;
        super.onDetachedFromWindow();
    }

    public int getHeightCenter(){
        return viewHeight / 2;
    }
}