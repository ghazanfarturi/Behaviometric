package de.unikl.hci.abbas.behaviometric.TouchLogger.touch;

import java.util.ArrayList;

public interface TouchEventsSink {
    void onTouchEvents(ArrayList<TouchEvent> touchEvents);
}
