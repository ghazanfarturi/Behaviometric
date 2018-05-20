package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;
import android.annotation.SuppressLint;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"RtlHardcoded"})
public class SwipeDirection extends AbstractGestureMetric {
    public static final double UP = 0.0D;
    public static final double DOWN = 1.0D;
    public static final double LEFT = 2.0D;
    public static final double RIGHT = 3.0D;
    public static final double AMBIGUOUS = -1.0D;
    private static final double PI_4 = 0.7853981633974483D;
    private static final double TOP_RIGHT = 0.7853981633974483D;
    private static final double TOP_LEFT = 2.356194490192345D;
    private static final double BOTTOM_LEFT = 3.9269908169872414D;
    private static final double BOTTOM_RIGHT = 5.497787143782138D;

    public SwipeDirection() {
    }

    public double compute(List<GestureDataPoint> timeline) {
        double direction = -1.0D;
        if (timeline.size() >= 2) {
            double up = 0.0D;
            double down = 0.0D;
            double left = 0.0D;
            double right = 0.0D;
            GestureDataPoint previous = null;
            Iterator i$ = timeline.iterator();

            while(true) {
                GestureDataPoint data;
                while(true) {
                    if (!i$.hasNext()) {
                        if (up > down + left + right) {
                            direction = 0.0D;
                            return direction;
                        } else if (down > up + left + right) {
                            direction = 1.0D;
                            return direction;
                        } else {
                            if (left > up + down + right) {
                                direction = 2.0D;
                            } else if (right > up + down + left) {
                                direction = 3.0D;
                                return direction;
                            }

                            return direction;
                        }
                    }

                    data = (GestureDataPoint)i$.next();
                    if (previous == null) {
                        break;
                    }

                    double distance = MetricUtils.computeDistance(previous, data);
                    double angle = 0.0D;

                    try {
                        angle = MetricUtils.computeMovingAngle(previous, data);
                    } catch (ArithmeticException var20) {
                        continue;
                    }

                    if (angle >= 0.7853981633974483D && angle <= 2.356194490192345D) {
                        up += distance;
                        break;
                    }

                    if (angle >= 3.9269908169872414D && angle <= 5.497787143782138D) {
                        down += distance;
                        break;
                    }

                    if (angle >= 2.356194490192345D && angle <= 3.9269908169872414D) {
                        left += distance;
                        break;
                    }

                    if (angle >= 0.0D && angle <= 0.7853981633974483D || angle >= 5.497787143782138D && angle <= 6.283185307179586D) {
                        right += distance;
                    }
                    break;
                }

                previous = data;
            }
        } else {
            return direction;
        }
    }

    public double computeNormalized(List<GestureDataPoint> timeline) throws UnimplementedCalculationException {
        throw new UnimplementedCalculationException("SwipeDirection cannot be normalized, and so shouldn't be used in a feature vector.");
    }
}
