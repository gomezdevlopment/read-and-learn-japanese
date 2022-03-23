package com.readandlearn.japanese;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;

class RoundedHighlightSpan extends ReplacementSpan {

    private static final int CORNER_RADIUS = 15;

    private final float PADDING_X = dpToPx(1);
    private final float PADDING_Y = dpToPx(1);
    private int mBackgroundColor;
    private int mTextColor;
    private float mTextSize;

    public RoundedHighlightSpan(int backgroundColor, int textColor, float textSize) {
        mBackgroundColor = backgroundColor;
        mTextColor = textColor;
        mTextSize = textSize;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        float magicNumber = .2115F;
        float extra = mTextSize * magicNumber;
        paint = new Paint(paint);

        paint.setTextSize(mTextSize);

        paint.setColor(mBackgroundColor);
        float textHeightWrapping = dpToPx(1);
        float tagBottom = top + PADDING_Y + mTextSize + textHeightWrapping;
        float tagRight = x + getTagWidth(text, start, end, paint);
        RectF rect = new RectF(x, top - PADDING_Y, tagRight, tagBottom);
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);

        paint.setColor(mTextColor);
        canvas.drawText(text, start, end, x + PADDING_X, tagBottom-PADDING_Y- extra, paint);
    }

    private int getTagWidth(CharSequence text, int start, int end, Paint paint) {
        return Math.round(PADDING_X + paint.measureText(text.subSequence(start, end).toString()) + PADDING_X);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        paint = new Paint(paint);
        paint.setTextSize(mTextSize);
        return getTagWidth(text, start, end, paint);
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = MainActivity.displayMetrics;
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
