package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.ConstructedGesture;
import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.List;

public abstract class AbstractGestureMetric {
    public AbstractGestureMetric() {
    }

    public double compute(ConstructedGesture gesture) throws ArithmeticException {
        return this.compute(gesture.getTimeline());
    }

    public abstract double compute(List<GestureDataPoint> var1) throws ArithmeticException;

    public double computeNormalized(ConstructedGesture gesture) throws ArithmeticException {
        return this.computeNormalized(gesture.getTimeline());
    }

    public abstract double computeNormalized(List<GestureDataPoint> var1) throws ArithmeticException;
}
