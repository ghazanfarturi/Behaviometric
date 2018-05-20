package com.dell.research.continuousauthentication.preprocessor.featurevectors;

import com.dell.research.continuousauthentication.preprocessor.ConstructedGesture;
import com.dell.research.continuousauthentication.preprocessor.metrics.AbstractGestureMetric;
import com.dell.research.continuousauthentication.preprocessor.metrics.MaxAreaPortion;

import java.util.LinkedHashMap;

public class LiFeatureVector extends LiNoAreaFeatureVector {
    private static final AbstractGestureMetric CALC_MAX_AREA_PORTION = new MaxAreaPortion();

    public LiFeatureVector() {
    }

    public LinkedHashMap<String, Double> generateFeatureVector(ConstructedGesture gesture, int displaySizeX, int displaySizeY, double displayOrientation) throws ArithmeticException {
        LinkedHashMap<String, Double> values = super.generateFeatureVector(gesture, displaySizeX, displaySizeY, displayOrientation);
        double maxAreaPortion = CALC_MAX_AREA_PORTION.computeNormalized(gesture);
        values.put("maxAreaPortion", maxAreaPortion);
        return values;
    }
}
