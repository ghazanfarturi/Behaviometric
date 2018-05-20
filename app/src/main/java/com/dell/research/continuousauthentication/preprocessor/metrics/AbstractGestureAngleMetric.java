package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.ConstructedGesture;
import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;
import java.util.List;

public abstract class AbstractGestureAngleMetric extends AbstractGestureMetric {
    public AbstractGestureAngleMetric() {
    }

    public final double computeNormalized(List<GestureDataPoint> timeline) throws ArithmeticException {
        double angle = this.compute(timeline);

        for(double var4 = 6.283185307179586D; angle > 6.283185307179586D; angle -= 6.283185307179586D) {
            ;
        }

        while(angle < 0.0D) {
            angle += 6.283185307179586D;
        }

        double normalized = angle / 6.283185307179586D;
        return normalized;
    }

    public final double computeCos(ConstructedGesture gesture) throws ArithmeticException {
        return this.computeCos(gesture.getTimeline());
    }

    public final double computeCos(List<GestureDataPoint> timeline) throws ArithmeticException {
        double angle = this.compute(timeline);
        return Math.cos(angle);
    }

    public final double computeSin(ConstructedGesture gesture) throws ArithmeticException {
        return this.computeSin(gesture.getTimeline());
    }

    public final double computeSin(List<GestureDataPoint> timeline) throws ArithmeticException {
        double angle = this.compute(timeline);
        return Math.sin(angle);
    }

    public final double computeCosNormalized(ConstructedGesture gesture) throws ArithmeticException {
        return this.computeCosNormalized(gesture.getTimeline());
    }

    public final double computeCosNormalized(List<GestureDataPoint> timeline) throws ArithmeticException {
        double cos = this.computeCos(timeline);
        double normalizedCos = (cos + 1.0D) / 2.0D;
        return normalizedCos;
    }

    public final double computeSinNormalized(ConstructedGesture gesture) throws ArithmeticException {
        return this.computeSinNormalized(gesture.getTimeline());
    }

    public final double computeSinNormalized(List<GestureDataPoint> timeline) throws ArithmeticException {
        double sin = this.computeSin(timeline);
        double normalizedSin = (sin + 1.0D) / 2.0D;
        return normalizedSin;
    }
}
