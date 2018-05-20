package de.unikl.hci.abbas.behaviometric.TouchLogger.gestures;

import de.unikl.hci.abbas.behaviometric.TouchLogger.touch.TouchEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class GestureDetectSource {
    private ArrayList<GestureDetectSink> sinks = new ArrayList<>();

    public void registerCallback(GestureDetectSink callback) {
        sinks.add(callback);
    }

    public void unregisterCallback(GestureDetectSink callback) {
        sinks.remove(callback);
    }

    public void unregisterSinks() {
        sinks.clear();
    }

    protected void sendGestureDetect(Gesture gesture, TouchEvent event) {
        for(GestureDetectSink gestureDetectSink : sinks) {
            ArrayList<TouchEvent> list = new ArrayList<>(Arrays.asList(event));
            gestureDetectSink.onGestureDetect(gesture, list);
        }
    }

    protected void sendGestureDetect(Gesture gesture, ArrayList<TouchEvent> events) {
        for(GestureDetectSink gestureDetectSink : sinks) {
            gestureDetectSink.onGestureDetect(gesture, events);
        }
    }
}
