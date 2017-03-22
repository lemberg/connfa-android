package com.ls.ui.view;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

import com.ls.utils.L;

public class TagBadgeSpannable extends ReplacementSpan {

    private static int CORNER_RADIUS = 30;
    private final int textColor;
    private final int backgroundColor;

    public TagBadgeSpannable(int textColor, int backgroundColor) {
        super();
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
        L.e("Top = " + top + " Bottom = " + bottom);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);

        paint.setColor(textColor);
//        canvas.drawText(text, start, end, x, (bottom + top)/2, paint);
        canvas.drawText(text, start, end, x, y, paint);
        L.e("Y = " + y);
        L.e("(bottom + top)/2 = " + (bottom + top) / 2);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end));
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }


}