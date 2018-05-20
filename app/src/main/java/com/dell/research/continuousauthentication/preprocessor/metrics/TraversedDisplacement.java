package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;
import java.util.List;

public class TraversedDisplacement extends AbstractGestureDistanceMetric {
    public TraversedDisplacement() {
    }

    protected double computeNormalized(List<GestureDataPoint> timeline, int apparentDisplaySizeX, int apparentDisplaySizeY) throws ArithmeticException {
        double proportion = this.compute(timeline) / (double)Math.max(apparentDisplaySizeX, apparentDisplaySizeY);
        return Math.min(proportion, 1.0D);
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        if (!timeline.isEmpty()) {
            GestureDataPoint start = (GestureDataPoint)timeline.get(0);
            GestureDataPoint end = (GestureDataPoint)timeline.get(timeline.size() - 1);
            return MetricUtils.computeDistance(start, end);
        } else {
            return 0.0D;
        }
    }
}
