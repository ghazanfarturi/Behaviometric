package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.List;

public class StartArea extends AbstractGestureMetric {
    public StartArea() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        if (!timeline.isEmpty()) {
            return 1.0D * (double)((GestureDataPoint)timeline.get(0)).getTouchMajor();
        } else {
            throw new ArithmeticException("No ABS_MT_TOUCH_MAJOR data found");
        }
    }

    public double computeNormalized(List<GestureDataPoint> timeline) throws UnimplementedCalculationException {
        throw new UnimplementedCalculationException("StartArea cannot be normalized because devices have different scales for area/pressure");
    }
}