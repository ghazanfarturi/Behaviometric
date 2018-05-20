package com.dell.research.continuousauthentication.preprocessor;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SerializedGesture implements Serializable {
    private static final long serialVersionUID = 6700288762964640906L;
    private List<GestureDataPoint> timeline = null;
    private double startTime = -1.0D;
    private double finishTime = -1.0D;
    private int displaySizeX = -1;
    private int displaySizeY = -1;

    public SerializedGesture(List<GestureDataPoint> timeline, double startTime, double finishTime, int displaySizeX, int displaySizeY) {
        this.timeline = new LinkedList();
        Iterator i$ = timeline.iterator();

        while(i$.hasNext()) {
            GestureDataPoint data = (GestureDataPoint)i$.next();
            this.timeline.add(new GestureDataPoint(data));
        }

        this.startTime = startTime;
        this.finishTime = finishTime;
        this.displaySizeX = displaySizeX;
        this.displaySizeY = displaySizeY;
    }

    public final List<GestureDataPoint> getTimeline() {
        return this.timeline;
    }

    public final double getStartTime() {
        return this.startTime;
    }

    public final double getFinishTime() {
        return this.finishTime;
    }

    public final int getDisplaySizeX() {
        return this.displaySizeX;
    }

    public final int getDisplaySizeY() {
        return this.displaySizeY;
    }
}
