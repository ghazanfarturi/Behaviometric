package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.List;

public class StartX extends AbstractGestureDistanceMetric {
    public StartX() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        if (!timeline.isEmpty()) {
            return (double)((GestureDataPoint)timeline.get(0)).getPositionX();
        } else {
            throw new ArithmeticException("No gesture points received");
        }
    }

    protected double computeNormalized(List<GestureDataPoint> timeline, int apparentDisplaySizeX, int apparentDisplaySizeY) throws ArithmeticException {
        return this.compute(timeline) / (double)apparentDisplaySizeX;
    }
}
