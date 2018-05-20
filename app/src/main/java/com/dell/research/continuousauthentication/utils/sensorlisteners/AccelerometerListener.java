package com.dell.research.continuousauthentication.utils.sensorlisteners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import java.util.LinkedList;
import java.util.List;

public class AccelerometerListener extends AbstractSensorListener {
    private static final List<Integer> SENSOR_PRIORITY_LIST = new LinkedList();
    private static float accelerationX = 0.0F;
    private static float accelerationY = 0.0F;
    private static float accelerationZ = 0.0F;

    public AccelerometerListener(Context c) {
        super(c);
        if (SENSOR_PRIORITY_LIST.isEmpty()) {
            SENSOR_PRIORITY_LIST.add(10);
            SENSOR_PRIORITY_LIST.add(1);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == 10 || event.sensor.getType() == 1) {
            accelerationX = event.values[0];
            accelerationY = event.values[1];
            accelerationZ = event.values[2];
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    protected List<Integer> getSensorPriorityList() {
        return SENSOR_PRIORITY_LIST;
    }

    public final float getAccelerationX() {
        return accelerationX;
    }

    public final float getAccelerationY() {
        return accelerationY;
    }

    public final float getAccelerationZ() {
        return accelerationZ;
    }
}
