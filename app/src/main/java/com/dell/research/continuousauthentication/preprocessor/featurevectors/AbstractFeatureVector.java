package com.dell.research.continuousauthentication.preprocessor.featurevectors;

import com.dell.research.continuousauthentication.preprocessor.ConstructedGesture;

import java.util.LinkedHashMap;

public abstract class AbstractFeatureVector {
    public AbstractFeatureVector() {
    }

    public abstract LinkedHashMap<String, Double> generateFeatureVector(ConstructedGesture var1, int var2, int var3, double var4) throws ArithmeticException;
}
