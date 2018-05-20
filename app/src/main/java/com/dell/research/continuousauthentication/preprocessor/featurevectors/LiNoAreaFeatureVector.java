package com.dell.research.continuousauthentication.preprocessor.featurevectors;

import com.dell.research.continuousauthentication.preprocessor.ConstructedGesture;
import com.dell.research.continuousauthentication.preprocessor.metrics.AbstractGestureAngleMetric;
import com.dell.research.continuousauthentication.preprocessor.metrics.MeanMovingAngle;
import com.dell.research.continuousauthentication.preprocessor.metrics.StartX;
import com.dell.research.continuousauthentication.preprocessor.metrics.AbstractGestureDistanceMetric;
import com.dell.research.continuousauthentication.preprocessor.metrics.MeanCurvatureAngle;
import com.dell.research.continuousauthentication.preprocessor.metrics.StartMovingAngle;
import com.dell.research.continuousauthentication.preprocessor.metrics.StartY;

import java.util.LinkedHashMap;

public class LiNoAreaFeatureVector extends AbstractFeatureVector {
    private static final AbstractGestureDistanceMetric CALC_START_X = new StartX();
    private static final AbstractGestureDistanceMetric CALC_START_Y = new StartY();
    private static final AbstractGestureAngleMetric CALC_START_MOVING_ANGLE = new StartMovingAngle();
    private static final AbstractGestureAngleMetric CALC_MEAN_MOVING_ANGLE = new MeanMovingAngle();
    private static final AbstractGestureAngleMetric CALC_MEAN_CURVATURE_ANGLE = new MeanCurvatureAngle();

    public LiNoAreaFeatureVector() {
    }

    public LinkedHashMap<String, Double> generateFeatureVector(ConstructedGesture gesture, int displaySizeX, int displaySizeY, double displayOrientation) throws ArithmeticException {
        double firstX = CALC_START_X.computeNormalized(gesture, displaySizeX, displaySizeY, displayOrientation);
        double firstY = CALC_START_Y.computeNormalized(gesture, displaySizeX, displaySizeY, displayOrientation);
        double firstMovingDirectionCos = CALC_START_MOVING_ANGLE.computeCosNormalized(gesture);
        double firstMovingDirectionSin = CALC_START_MOVING_ANGLE.computeSinNormalized(gesture);
        double movingDistance = gesture.getNormalizedDistance();
        double duration = gesture.getNormalizedDuration();
        double averageMovingDirectionCos = CALC_MEAN_MOVING_ANGLE.computeCosNormalized(gesture);
        double averageMovingDirectionSin = CALC_MEAN_MOVING_ANGLE.computeSinNormalized(gesture);
        double averageMovingCurvatureCos = CALC_MEAN_CURVATURE_ANGLE.computeCosNormalized(gesture);
        double averageMovingCurvatureSin = CALC_MEAN_CURVATURE_ANGLE.computeSinNormalized(gesture);
        LinkedHashMap<String, Double> values = new LinkedHashMap();
        values.put("firstX", firstX);
        values.put("firstY", firstY);
        values.put("firstMovingDirectionCos", firstMovingDirectionCos);
        values.put("firstMovingDirectionSin", firstMovingDirectionSin);
        values.put("movingDistance", movingDistance);
        values.put("duration", duration);
        values.put("averageMovingDirectionCos", averageMovingDirectionCos);
        values.put("averageMovingDirectionSin", averageMovingDirectionSin);
        values.put("averageMovingCurvatureCos", averageMovingCurvatureCos);
        values.put("averageMovingCurvatureSin", averageMovingCurvatureSin);
        return values;
    }
}
