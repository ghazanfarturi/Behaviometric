package com.dell.research.continuousauthentication.preprocessor;

import java.io.Serializable;
import java.util.Locale;

public class GestureDataPoint implements Serializable {
    private static final long serialVersionUID = 2995112323018690265L;
    private double time = -1.0D;
    private int positionX = -1;
    private int positionY = -1;
    private int nativePositionX = -1;
    private int nativePositionY = -1;
    private int touchMajor = -1;
    private GestureDataPoint.DisplayOrientation orientation = null;

    public GestureDataPoint(double time, int positionX, int positionY, int nativePositionX, int nativePositionY, int touchMajor, int displayedOrientation) {
        this.time = time;
        this.positionX = positionX;
        this.positionY = positionY;
        this.nativePositionX = nativePositionX;
        this.nativePositionY = nativePositionY;
        this.touchMajor = touchMajor;
        if (displayedOrientation == 0) {
            this.orientation = GestureDataPoint.DisplayOrientation.PORTRAIT;
        } else if (displayedOrientation == 2) {
            this.orientation = GestureDataPoint.DisplayOrientation.PORTRAIT_FLIP;
        } else if (displayedOrientation == 3) {
            this.orientation = GestureDataPoint.DisplayOrientation.LANDSCAPE_RIGHT;
        } else if (displayedOrientation == 1) {
            this.orientation = GestureDataPoint.DisplayOrientation.LANDSCAPE_LEFT;
        }

    }

    public GestureDataPoint(GestureDataPoint g) {
        this.time = g.time;
        this.positionX = g.positionX;
        this.positionY = g.positionY;
        this.nativePositionX = g.nativePositionX;
        this.nativePositionY = g.nativePositionY;
        this.touchMajor = g.touchMajor;
        this.orientation = g.orientation;
    }

    public double getTime() {
        return this.time;
    }

    public int getPositionX() {
        return this.positionX;
    }

    public int getPositionY() {
        return this.positionY;
    }

    public int getTouchMajor() {
        return this.touchMajor;
    }

    public GestureDataPoint.DisplayOrientation getDisplayOrientation() {
        return this.orientation;
    }

    public String toString() {
        return String.format(Locale.getDefault(), "time=%f,x=%d,y=%d,width=%d,orientation=%s", this.time, this.positionX, this.positionY, this.touchMajor, this.orientation);
    }

    public int getNativePositionX() {
        return this.nativePositionX;
    }

    public int getNativePositionY() {
        return this.nativePositionY;
    }

    public void clearPositionData() {
        this.positionX = 1;
        this.positionY = 1;
        this.nativePositionX = 1;
        this.nativePositionY = 1;
    }

    public static enum DisplayOrientation {
        PORTRAIT,
        LANDSCAPE_LEFT,
        LANDSCAPE_RIGHT,
        PORTRAIT_FLIP;

        private DisplayOrientation() {
        }
    }
}
