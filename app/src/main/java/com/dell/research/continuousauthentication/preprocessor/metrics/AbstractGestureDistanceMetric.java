package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.ConstructedGesture;
import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.List;

public abstract class AbstractGestureDistanceMetric extends AbstractGestureMetric {
    public AbstractGestureDistanceMetric() {
    }

    public final double computeNormalized(List<GestureDataPoint> timeline) throws UnimplementedCalculationException {
        throw new UnimplementedCalculationException("Distance metrics require display dimensions and orientation");
    }

    public final double computeNormalized(ConstructedGesture gesture, int displaySizeX, int displaySizeY, double displayOrientation) throws ArithmeticException {
        return this.computeNormalized(gesture.getTimeline(), displaySizeX, displaySizeY, displayOrientation);
    }

    public final double computeNormalized(List<GestureDataPoint> timeline, int displaySizeX, int displaySizeY, double displayOrientation) throws ArithmeticException {
        if (!this.validDisplayInfo(displaySizeX, displaySizeY, displayOrientation)) {
            String message = String.format("Display size must both be positive (x=%d, y=%d) and display orientation (orientation=%f) must be a valid DisplayOrientation static value", displaySizeX, displaySizeY, displayOrientation);
            throw new ArithmeticException(message);
        } else {
            int apparentDisplaySizeX;
            int apparentDisplaySizeY;
            if (displayOrientation != 2.0D && displayOrientation != 1.0D) {
                apparentDisplaySizeX = displaySizeX;
                apparentDisplaySizeY = displaySizeY;
            } else {
                apparentDisplaySizeX = displaySizeY;
                apparentDisplaySizeY = displaySizeX;
            }

            return this.computeNormalized(timeline, apparentDisplaySizeX, apparentDisplaySizeY);
        }
    }

    protected abstract double computeNormalized(List<GestureDataPoint> var1, int var2, int var3) throws ArithmeticException;

    private boolean validDisplayInfo(int displaySizeX, int displaySizeY, double displayOrientation) {
        boolean validDisplaySizes = displaySizeX > 0 && displaySizeY > 0;
        boolean validDisplayOrientation = displayOrientation == 1.0D || displayOrientation == 2.0D || displayOrientation == 0.0D;
        return validDisplaySizes && validDisplayOrientation;
    }
}
