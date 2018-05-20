package de.unikl.hci.abbas.behaviometric.TouchLogger.gestures;

import de.unikl.hci.abbas.behaviometric.TouchLogger.gestures.Gesture;
import de.unikl.hci.abbas.behaviometric.TouchLogger.touch.TouchEvent;

import java.util.ArrayList;

public interface GestureDetectSink {
    void onGestureDetect(Gesture gesture, ArrayList<TouchEvent> events);
}
