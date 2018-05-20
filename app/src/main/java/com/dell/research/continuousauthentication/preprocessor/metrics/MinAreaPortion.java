package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.Iterator;
import java.util.List;

public class MinAreaPortion extends AbstractGestureMetric {
    public MinAreaPortion() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        int minTouchMajor = 2147483647;
        double minTouchMajorDistance = 0.0D;
        double totalDistance = 0.0D;
        GestureDataPoint previous = null;

        GestureDataPoint point;
        for(Iterator i$ = timeline.iterator(); i$.hasNext(); previous = point) {
            point = (GestureDataPoint)i$.next();
            int touchMajor = point.getTouchMajor();
            if (previous != null) {
                totalDistance += MetricUtils.computeDistance(previous, point);
                if (touchMajor < minTouchMajor) {
                    minTouchMajor = touchMajor;
                    minTouchMajorDistance = totalDistance;
                }
            } else {
                minTouchMajor = touchMajor;
            }
        }

        if (totalDistance > 0.0D) {
            return minTouchMajorDistance / totalDistance;
        } else {
            throw new ArithmeticException("Gesture length is 0, require non-0 length gesture");
        }
    }

    public double computeNormalized(List<GestureDataPoint> timeline) throws ArithmeticException {
        return this.compute(timeline);
    }
}
