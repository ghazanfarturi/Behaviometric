package com.dell.research.continuousauthentication.preprocessor;

import com.dell.research.continuousauthentication.preprocessor.metrics.DisplayOrientation;
import com.dell.research.continuousauthentication.preprocessor.metrics.SwipeDirection;
import com.dell.research.continuousauthentication.preprocessor.metrics.TraversedDistance;
import com.dell.research.continuousauthentication.demotransmitter.DemoTransmittable;
import com.dell.research.continuousauthentication.nativedevice.NativeDeviceData;
import com.dell.research.continuousauthentication.preprocessor.featurevectors.AbstractFeatureVector;
import com.dell.research.continuousauthentication.preprocessor.metrics.AbstractGestureMetric;
import com.dell.research.continuousauthentication.utils.TimestampLogger;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConstructedGesture implements DemoTransmittable {
    private final String TAG = this.getClass().getName();
    private static final double TAP_THRESHOLD = 25.0D;
    private static final AbstractGestureMetric CALC_TRAVERSED_DISTANCE = new TraversedDistance();
    private static final AbstractGestureMetric CALC_SWIPE_DIRECTION = new SwipeDirection();
    private static final AbstractGestureMetric CALC_SWIPE_DISPLAY_ORIENTATION = new DisplayOrientation();
    private static int gestureCount = 0;
    private int id = 0;
    private List<GestureDataPoint> timeline = null;
    private List<NativeDeviceData> sourceData = null;
    private int displaySizeX = -1;
    private int displaySizeY = -1;
    private double startTime = -1.0D;
    private double finishTime = -1.0D;
    private double duration = 0.0D;
    private double distance = 0.0D;
    private double swipeDirection = -1.0D;
    private double displayOrientation = -1.0D;
    private String activeApp = "";
    private LinkedHashMap<String, Double> featureVector = null;

    public ConstructedGesture(int displaySizeX, int displaySizeY) {
        this.id = gestureCount++;
        this.timeline = new LinkedList();
        this.sourceData = new LinkedList();
        this.displaySizeX = displaySizeX;
        this.displaySizeY = displaySizeY;
    }

    public ConstructedGesture(SerializedGesture gesture) {
        this.id = gestureCount++;
        this.timeline = gesture.getTimeline();
        this.sourceData = new LinkedList();
        this.displaySizeX = gesture.getDisplaySizeX();
        this.displaySizeY = gesture.getDisplaySizeY();
        this.startTime = gesture.getStartTime();
        this.finish(gesture.getFinishTime());
    }

    public void add(GestureDataPoint data) {
        if (this.timeline.isEmpty()) {
            this.startTime = data.getTime();
        }

        this.timeline.add(data);
    }

    public void addNative(NativeDeviceData data) {
        this.sourceData.add(data);
    }

    public void finish(double time) {
        if (!this.isFinished()) {
            this.finishTime = time;
            this.duration = this.finishTime - this.startTime;
            this.distance = CALC_TRAVERSED_DISTANCE.compute(this.timeline);
            this.swipeDirection = CALC_SWIPE_DIRECTION.compute(this.timeline);
            this.displayOrientation = CALC_SWIPE_DISPLAY_ORIENTATION.compute(this.timeline);
            if (this.isTap()) {
                Iterator i$ = this.sourceData.iterator();

                while(i$.hasNext()) {
                    NativeDeviceData data = (NativeDeviceData)i$.next();
                    data.clearPositionData();
                }

                i$ = this.timeline.iterator();

                while(i$.hasNext()) {
                    GestureDataPoint data = (GestureDataPoint)i$.next();
                    data.clearPositionData();
                }
            }
        } else {
            TimestampLogger.warn(this.TAG, "id=" + this.id + " already finished at finishTime=" + this.finishTime);
        }

    }

    public boolean isFinished() {
        return this.finishTime > 0.0D;
    }

    public double getDuration() {
        return this.duration;
    }

    public double getNormalizedDuration() {
        return Math.min(this.getDuration(), 1.0D);
    }

    public double getDistance() {
        return this.distance;
    }

    public double getNormalizedDistance() {
        double longEdge = (double)Math.max(this.displaySizeX, this.displaySizeY);
        return Math.min(this.getDistance() / longEdge, 1.0D);
    }

    public double getDisplayOrientation() {
        return this.displayOrientation;
    }

    public double getSwipeDirection() {
        return this.swipeDirection;
    }

    public List<GestureDataPoint> getTimeline() {
        return this.timeline;
    }

    public LinkedHashMap<String, Double> getFeatureVector(AbstractFeatureVector featureVector) throws ArithmeticException {
        this.featureVector = featureVector.generateFeatureVector(this, this.displaySizeX, this.displaySizeY, this.displayOrientation);
        return this.featureVector;
    }

    public boolean equals(Object o) {
        ConstructedGesture other = (ConstructedGesture)o;
        return this.id == other.id;
    }

    public void setActiveApp(String app) {
        this.activeApp = app;
    }

    public String getActiveApp() {
        return this.activeApp;
    }

    public boolean isTap() {
        return this.timeline.size() == 1 || this.distance < 25.0D;
    }

    public boolean isDirectionalSwipe() {
        return !this.isTap() && this.swipeDirection != -1.0D;
    }

    public GestureDataPoint getLastDataPoint() {
        return !this.timeline.isEmpty() ? (GestureDataPoint)this.timeline.get(this.timeline.size() - 1) : null;
    }

    public String toString() {
        return String.format(Locale.getDefault(), "id=%d,size=%d,start=%f,finish=%f,duration=%f,distance=%f,swipeDirection=%f,displayOrientation=%f,app=%s", this.id, this.timeline.size(), this.startTime, this.finishTime, this.duration, this.distance, this.swipeDirection, this.displayOrientation, this.activeApp);
    }

    public SerializedGesture generateSerialized() {
        return this.isFinished() ? new SerializedGesture(this.timeline, this.startTime, this.finishTime, this.displaySizeX, this.displaySizeY) : null;
    }

    public String getVisualizationJSON() {
        JSONObject gesture = new JSONObject();

        try {
            gesture.put("msgtype", MessageType.GESTURE.toString());
            gesture.put("id", this.id);
            gesture.put("duration", this.duration);
            gesture.put("distance", this.distance);
            gesture.put("direction", this.swipeDirection);
            gesture.put("orientation", this.displayOrientation);
            JSONArray points = new JSONArray();
            Iterator i$ = this.timeline.iterator();

            while(i$.hasNext()) {
                GestureDataPoint point = (GestureDataPoint)i$.next();
                JSONObject jsonPoint = new JSONObject();
                jsonPoint.put("time", point.getTime());
                jsonPoint.put("x", point.getPositionX());
                jsonPoint.put("y", point.getPositionY());
                jsonPoint.put("pres", point.getTouchMajor());
                points.put(jsonPoint);
            }

            gesture.put("points", points);
            if (this.featureVector != null) {
                JSONArray features = new JSONArray();
                List<String> keys = new LinkedList(this.featureVector.keySet());
                Collections.sort(keys);
                i$ = keys.iterator();

                while(i$.hasNext()) {
                    String label = (String)i$.next();
                    double metric = (Double)this.featureVector.get(label);
                    JSONObject feature = new JSONObject();
                    feature.put("name", label);
                    feature.put("value", metric);
                    features.put(feature);
                }

                gesture.put("features", features);
            }
        } catch (JSONException var10) {
            var10.printStackTrace();
        }

        return gesture.toString();
    }

    public boolean hasPoints() {
        return !this.timeline.isEmpty();
    }

    public int getDisplaySizeX() {
        return this.displaySizeX;
    }

    public int getDisplaySizeY() {
        return this.displaySizeY;
    }
}
