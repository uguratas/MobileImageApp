package com.example.mobileimageapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HistogramView extends View {

    private int[] histogramData;
    private Paint paint;

    public HistogramView(Context context) {
        super(context);
        init();
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
    }

    public void setHistogramData(int[] histogramData) {
        this.histogramData = histogramData;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (histogramData == null) return;

        int maxValue = getMaxValue(histogramData);
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / histogramData.length;

        for (int i = 0; i < histogramData.length; i++) {
            float barHeight = (float) histogramData[i] / maxValue * height * 10;

            // Histogram çubuğunu çiz
            float left = i * barWidth;
            float top = height - barHeight;
            float right = (i + 1) * barWidth;
            float bottom = height;
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private int getMaxValue(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
