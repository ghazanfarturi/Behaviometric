package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.Iterator;
import java.util.List;

public class MeanArea extends AbstractGestureMetric {
    public MeanArea() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        double totalTouchMajor = 0.0D;
        int touchMajorCount = 0;

        for(Iterator i$ = timeline.iterator(); i$.hasNext(); ++touchMajorCount) {
            GestureDataPoint data = (GestureDataPoint)i$.next();
            totalTouchMajor += (double)data.getTouchMajor();
        }

        if (touchMajorCount > 0) {
            return totalTouchMajor / (double)touchMajorCount;
        } else {
            throw new ArithmeticException("No ABS_MT_TOUCH_MAJOR data found");
        }
    }

    public double computeNormalized(List<GestureDataPoint> timeline) throws UnimplementedCalculationException {
        throw new UnimplementedCalculationException("MeanArea cannot be normalized because devices have different scales for area/pressure");
    }
}
