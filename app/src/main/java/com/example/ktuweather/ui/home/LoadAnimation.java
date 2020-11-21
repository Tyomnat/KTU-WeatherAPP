package com.example.ktuweather.ui.home;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.ktuweather.IndicatingView;

public class LoadAnimation extends Animation {
    private IndicatingView indicatingView;
    private int callsCount;

    public LoadAnimation(IndicatingView indicatingView) {
        this.indicatingView = indicatingView;
        this.callsCount = 0;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        callsCount++;
        if(callsCount > 10) {
            indicatingView.incrementStrokes();
            if(indicatingView.getStrokes() > 1) {
                indicatingView.setStrokes(0);
            }
            indicatingView.requestLayout();
            callsCount = 0;
        }
    }
}