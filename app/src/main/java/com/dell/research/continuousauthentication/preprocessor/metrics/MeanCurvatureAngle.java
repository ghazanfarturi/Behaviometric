package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.Iterator;
import java.util.List;

public class MeanCurvatureAngle extends AbstractGestureAngleMetric {
    public MeanCurvatureAngle() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        GestureDataPoint data2Before = null;
        GestureDataPoint data1Before = null;
        double totalCurvature = 0.0D;
        int curvatureCount = 0;

        GestureDataPoint data;
        for(Iterator i$ = timeline.iterator(); i$.hasNext(); data1Before = data) {
            data = (GestureDataPoint)i$.next();
            if (data2Before != null && data1Before != null) {
                try {
                    double curvatureAngle = MetricUtils.computeCurvatureAngle(data2Before, data1Before, data);
                    totalCurvature += curvatureAngle;
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
            return totalCurvature / (double)curvatureCount;
        } else {
            throw new ArithmeticException("No valid curvatures calculated for this mean");
        }
    }
}
