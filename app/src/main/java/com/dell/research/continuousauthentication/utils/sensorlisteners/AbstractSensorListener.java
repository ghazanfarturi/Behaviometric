package com.dell.research.continuousauthentication.utils.sensorlisteners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.dell.research.continuousauthentication.utils.TimestampLogger;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSensorListener implements SensorEventListener {
    private final String TAG = this.getClass().getName();
    private static SensorManager sensorManager = null;

    protected AbstractSensorListener(Context c) {
        if (sensorManager == null) {
            sensorManager = (SensorManager)c.getSystemService("sensor");
        }
    }

    public void open() {
        boolean success = false;
        Iterator i$ = this.getSensorPriorityList().iterator();

        while(i$.hasNext()) {
            Integer type = (Integer)i$.next();
            Sensor s = sensorManager.getDefaultSensor(type);
            if (s != null) {
                sensorManager.registerListener(this, s, 3);
                TimestampLogger.info(this.TAG, "Successfully subscribed to sensor " + s.getName());
                success = true;
                break;
            }
            TimestampLogger.warn(this.TAG, "Sensor " + type + " unavailable");
        }

        if (!success) {
            TimestampLogger.warn(this.TAG, "No valid sensors found");
        }
    }

    public void close() {
        sensorManager.unregisterListener(this);
    }

    protected abstract List<Integer> getSensorPriorityList();
}
