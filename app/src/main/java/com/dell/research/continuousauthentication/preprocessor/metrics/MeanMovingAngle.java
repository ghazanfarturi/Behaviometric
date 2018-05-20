package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;
import java.util.Iterator;
import java.util.List;

public class MeanMovingAngle extends AbstractGestureAngleMetric {
    public MeanMovingAngle() {
    }

    public double compute(List<GestureDataPoint> timeline) throws ArithmeticException {
        int numAngles = 0;
        double totalAngle = 0.0D;
        GestureDataPoint previous = null;

        GestureDataPoint point;
        for(Iterator i$ = timeline.iterator(); i$.hasNext(); previous = point) {
            point = (GestureDataPoint)i$.next();
            if (previous != null) {
                try {
                    double angle = MetricUtils.computeMovingAngle(previous, point);
                    totalAngle += angle;
                    ++numAngles;
                } catch (ArithmeticException var10) {
                    ;
                }
            }
        }

        if (numAngles > 0) {
            return totalAngle / (double)numAngles;
        } else {
            throw new ArithmeticException("No valid angles calculated for this mean");
        }
    }
}
