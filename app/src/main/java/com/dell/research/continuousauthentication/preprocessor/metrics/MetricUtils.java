package com.dell.research.continuousauthentication.preprocessor.metrics;

import com.dell.research.continuousauthentication.preprocessor.GestureDataPoint;

public class MetricUtils {
    public MetricUtils() {
    }

    public static double computeDistance(GestureDataPoint point1, GestureDataPoint point2) {
        double x1 = (double)point1.getPositionX();
        double y1 = (double)point1.getPositionY();
        double x2 = (double)point2.getPositionX();
        double y2 = (double)point2.getPositionY();
        double xdel = x1 - x2;
        double ydel = y1 - y2;
        return Math.sqrt(xdel * xdel + ydel * ydel);
    }

    public static double computeMovingAngle(GestureDataPoint point1, GestureDataPoint point2) throws ArithmeticException {
        double x1 = (double)point1.getPositionX();
        double y1 = (double)point1.getPositionY();
        double x2 = (double)point2.getPositionX();
        double y2 = (double)point2.getPositionY();
        boolean samePoints = x1 == x2 && y1 == y2;
        if (samePoints) {
            throw new ArithmeticException(String.format("Angle undefined for identical points: [%s] [%s]", point1.toString(), point2.toString()));
        } else {
            double y1prime = -y1;
            double y2prime = -y2;
            double angle = Math.atan2(y2prime - y1prime, x2 - x1);
            if (angle < 0.0D) {
                angle += 6.283185307179586D;
            }

            if (Double.isNaN(angle)) {
                throw new ArithmeticException(String.format("NaN angle between points: [%s] [%s]", point1.toString(), point2.toString()));
            } else {
                return angle;
            }
        }
    }

    public static double computeCurvatureAngle(GestureDataPoint point1, GestureDataPoint point2, GestureDataPoint point3) throws ArithmeticException {
        double dist12 = computeDistance(point1, point2);
        double dist23 = computeDistance(point2, point3);
        double dist13 = computeDistance(point1, point3);
        double a2 = dist23 * dist23;
        double b2 = dist12 * dist12;
        double c2 = dist13 * dist13;
        double angle = Math.acos((a2 + b2 - c2) / (2.0D * dist23 * dist12));
        if (Double.isNaN(angle)) {
            throw new ArithmeticException(String.format("NaN curvature between points: [%s] [%s] [%s]", point1.toString(), point2.toString(), point3.toString()));
        } else {
            return angle;
        }
    }

    public static double computeCurvatureDistance(GestureDataPoint point1, GestureDataPoint point2, GestureDataPoint point3) throws ArithmeticException {
        double x1 = (double)point1.getPositionX();
        double y1 = (double)point1.getPositionY();
        double x2 = (double)point3.getPositionX();
        double y2 = (double)point3.getPositionY();
        double x0 = (double)point2.getPositionX();
        double y0 = (double)point2.getPositionY();
        double xdel = x2 - x1;
        double ydel = y2 - y1;
        double xdel2 = xdel * xdel;
        double ydel2 = ydel * ydel;
        double numerator = Math.abs(ydel * x0 - xdel * y0 + x2 * y1 - y2 * x1);
        double denominator = Math.sqrt(ydel2 + xdel2);
        if (denominator == 0.0D) {
            throw new ArithmeticException(String.format("Line segment is length 0 between points: [%s] [%s]", point1.toString(), point3.toString()));
        } else {
            return numerator / denominator;
        }
    }
}
