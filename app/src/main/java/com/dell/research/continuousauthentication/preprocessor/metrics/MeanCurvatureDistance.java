package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.Iterator;
import java.util.List;

public class MeanCurvatureDistance extends AbstractGestureDistanceMetric {
    public MeanCurvatureDistance() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        GestureDataPoint data2Before = null;
        GestureDataPoint data1Before = null;
        double totalCurvatureDistance = 0.0D;
        int curvatureCount = 0;

        GestureDataPoint data;
        for(Iterator i$ = timeline.iterator(); i$.hasNext(); data1Before = data) {
            data = (GestureDataPoint)i$.next();
            if (data2Before != null && data1Before != null) {
                try {
                    double curvatureDistance = MetricUtils.computeCurvatureDistance(data2Before, data1Before, data);
                    totalCurvatureDistance += curvatureDistance;
                    ++curvatureCount;
                } catch (ArithmeticException var11) {
                    ;
                }
            }

            if (data1Before != null) {
                data2Before = data1Before;
            }
        }

        if (curvatureCount > 0) {
            return totalCurvatureDistance / (double)curvatureCount;
        } else {
            throw new ArithmeticException("No curvatures computed");
        }
    }

    protected double computeNormalized(List<GestureDataPoint> timeline, int apparentDisplaySizeX, int apparentDisplaySizeY) throws ArithmeticException {
        return this.compute(timeline) / (double)Math.max(apparentDisplaySizeX, apparentDisplaySizeY);
    }
}
