package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.Iterator;
import java.util.List;

public class MaxAreaPortion extends AbstractGestureMetric {
    public MaxAreaPortion() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        int maxTouchMajor = -2147483648;
        double maxTouchMajorDistance = 0.0D;
        double totalDistance = 0.0D;
        GestureDataPoint previous = null;

        GestureDataPoint point;
        for(Iterator i$ = timeline.iterator(); i$.hasNext(); previous = point) {
            point = (GestureDataPoint)i$.next();
            int touchMajor = point.getTouchMajor();
            if (previous != null) {
                totalDistance += MetricUtils.computeDistance(previous, point);
                if (touchMajor > maxTouchMajor) {
                    maxTouchMajor = touchMajor;
                    maxTouchMajorDistance = totalDistance;
                }
            } else {
                maxTouchMajor = touchMajor;
            }
        }

        if (totalDistance > 0.0D) {
            return maxTouchMajorDistance / totalDistance;
        } else {
            throw new ArithmeticException("Gesture length is 0, require non-0 length gesture");
        }
    }

    public double computeNormalized(List<GestureDataPoint> timeline) throws ArithmeticException {
        return this.compute(timeline);
    }
}
