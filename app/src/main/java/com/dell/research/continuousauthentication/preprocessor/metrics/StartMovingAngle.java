package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.List;

public class StartMovingAngle extends AbstractGestureAngleMetric {
    public StartMovingAngle() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        if (timeline.size() >= 2) {
            GestureDataPoint second = (GestureDataPoint)timeline.get(1);
            GestureDataPoint first = (GestureDataPoint)timeline.get(0);
            return MetricUtils.computeMovingAngle(first, second);
        } else {
            throw new ArithmeticException(String.format("Not enough points to compute angle. Expected >= 2, received %d", timeline.size()));
        }
    }
}
