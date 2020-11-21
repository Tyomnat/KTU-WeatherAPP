package com.example.ktuweather.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomProgressBarView extends View {

    public static final int NOTSTARTED = 0;
    public static final int INPROGRESS = 1;
    public static final int FINISHED = 2;

    int state = NOTSTARTED;
    int progress = 0;

    public CustomProgressBarView(Context context) {
        super(context);
    }

    public CustomProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getProgress() { return progress; }

    public void setProgress(int progress) { this.progress = progress; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint();;
        paint.setStrokeWidth(height);
        paint.setColor(Color.parseColor("#bee8c4"));
        canvas.drawLine(0, height / 2, width * 0.2f, height / 2, paint);
        paint.setColor(Color.parseColor("#86cf91"));
        canvas.drawLine(width * 0.2f, height / 2, width * 0.4f, height / 2, paint);
        paint.setColor(Color.parseColor("#65a86f"));
        canvas.drawLine(width * 0.4f, height / 2, width * 0.6f, height / 2, paint);
        paint.setColor(Color.parseColor("#468750"));
        canvas.drawLine(width * 0.6f, height / 2, width * 0.8f, height / 2, paint);
        paint.setColor(Color.parseColor("#2e6135"));
        canvas.drawLine(width * 0.8f, height / 2, width, height / 2, paint);

        switch(state) {
            case NOTSTARTED:
                paint.setColor(Color.WHITE);
                canvas.drawLine(width, height / 2, 0, height / 2, paint);
                break;

            case INPROGRESS:
                paint.setColor(Color.WHITE);
                canvas.drawLine(width, height / 2, width * (progress) / 100, height / 2, paint);
                break;

            case FINISHED:
                break;

            default:
                break;
        }
    }
}