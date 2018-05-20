package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

import java.util.Iterator;
import java.util.List;

public class DisplayOrientation extends AbstractGestureMetric {
    public static final double PORTRAIT = 0.0D;
    public static final double LANDSCAPE_LEFT = 1.0D;
    public static final double LANDSCAPE_RIGHT = 2.0D;
    public static final double AMBIGUOUS = -1.0D;

    public DisplayOrientation() {
    }

    public double compute(List<GestureDataPoint> timeline) {
        double orientation = -1.0D;
        int portrait = 0;
        int landscapeLeft = 0;
        int landscapeRight = 0;
        Iterator i$ = timeline.iterator();

        while(i$.hasNext()) {
            GestureDataPoint data = (GestureDataPoint)i$.next();
            switch(data.getDisplayOrientation()) {
                case LANDSCAPE_LEFT:
                    ++landscapeLeft;
                    break;
                case LANDSCAPE_RIGHT:
                    ++landscapeRight;
                    break;
                case PORTRAIT:
                case PORTRAIT_FLIP:
                    ++portrait;
            }
        }

        if (portrait > landscapeLeft + landscapeRight) {
            orientation = 0.0D;
        } else if (landscapeLeft > portrait + landscapeRight) {
            orientation = 1.0D;
        } else if (landscapeRight > portrait + landscapeLeft) {
            orientation = 2.0D;
        }

        return orientation;
    }

    public double computeNormalized(List<GestureDataPoint> timeline) throws UnimplementedCalculationException {
        throw new UnimplementedCalculationException("DisplayOrientation cannot be normalized, and so shouldn't be used in a feature vector.");
    }
}
