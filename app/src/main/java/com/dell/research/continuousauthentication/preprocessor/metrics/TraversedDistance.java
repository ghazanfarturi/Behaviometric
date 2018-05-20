package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;
import java.util.Iterator;
import java.util.List;

public class TraversedDistance extends AbstractGestureDistanceMetric {
    public TraversedDistance() {
    }

    public double compute(List<GestureDataPoint> timeline) {
        if (!timeline.isEmpty()) {
            double distance = 0.0D;
            GestureDataPoint previous = null;

            GestureDataPoint data;
            for(Iterator i$ = timeline.iterator(); i$.hasNext(); previous = data) {
                data = (GestureDataPoint)i$.next();
                if (previous != null) {
                    distance += MetricUtils.computeDistance(previous, data);
                }
            }

            return distance;
        } else {
            return 0.0D;
        }
    }

    protected double computeNormalized(List<GestureDataPoint> timeline, int apparentDisplaySizeX, int apparentDisplaySizeY) {
        double proportion = this.compute(timeline) / (double)Math.max(apparentDisplaySizeX, apparentDisplaySizeY);
        return Math.min(proportion, 1.0D);
    }
}
