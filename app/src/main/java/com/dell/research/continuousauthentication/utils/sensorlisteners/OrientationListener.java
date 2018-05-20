package com.dell.research.continuousauthentication.utils.sensorlisteners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class OrientationListener extends AbstractSensorListener {
    private static final float PI_2 = 1.5707964F;
    private static final List<Integer> SENSOR_PRIORITY_LIST = new LinkedList();
    private static float roll = 0.0F;
    private static float pitch = 0.0F;
    private static float yaw = 0.0F;

    public OrientationListener(Context c) {
        super(c);
        if (SENSOR_PRIORITY_LIST.isEmpty()) {
            SENSOR_PRIORITY_LIST.add(11);
            SENSOR_PRIORITY_LIST.add(15);
            SENSOR_PRIORITY_LIST.add(3);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case 3:
                this.computeOrientationFromOrientationData(event);
                break;
            case 11:
            case 15:
                this.computeOrientationFromRotationVector(event);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    protected List<Integer> getSensorPriorityList() {
        return SENSOR_PRIORITY_LIST;
    }

    private void computeOrientationFromRotationVector(SensorEvent event) {
        float[] rotationVector = event.values;
        float[] quaternion = new float[4];
        SensorManager.getQuaternionFromVector(quaternion, rotationVector);
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, quaternion);
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);
        roll = orientation[0];
        pitch = orientation[1];
        yaw = orientation[2];
    }

    private void computeOrientationFromOrientationData(SensorEvent event) {
        float[] orientation = event.values;
        roll = (float)Math.toRadians((double)orientation[2]) + 1.5707964F;
        pitch = (float)Math.toRadians((double)orientation[1]);
        yaw = (float)Math.toRadians((double)orientation[0]);
    }

    public float getRoll() {
        return roll;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String toString() {
        return String.format(Locale.getDefault(), "(radians) roll=%f,pitch=%f,yaw=%f", roll, pitch, yaw);
    }
}
