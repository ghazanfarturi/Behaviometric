package de.unikl.hci.abbas.behaviometric.Demo.util;

import android.util.Log;

import com.dell.research.continuousauthentication.nativedevice.NativeDeviceData;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggerData implements Serializable {
    private static final long serialVersionUID = 8827144848295422965L;
    private static final Pattern META_FIELDS = Pattern.compile("meta=\\((.*?)\\)");
    private static final Pattern DATA_FIELDS = Pattern.compile("data=\\((.*?)\\)");
    private static final String TAG = "LoggerData";
    private static final String COMMA = ",";
    private static final String EQUALS = "=";
    private static final String DECIMAL = "\\.";
    private NativeDeviceData data = null;
    //private String app = null;
    private long epochMillis = 0L;
    private int rotation = 0;
    private float roll;
    private float pitch;
    private float yaw = 0.0F;
    private float accX;
    private float accY;
    private float accZ = 0.0F;

    public LoggerData(String line) {
        Matcher metaMatch = META_FIELDS.matcher(line);
        String metaLine = "";
        if (metaMatch.find()) {
            metaLine = metaMatch.group(1);
        }

        String[] arr$ = metaLine.split(",");
        int len$ = arr$.length;

        int seconds;
        for(seconds = 0; seconds < len$; ++seconds) {
            String fieldAndValue = arr$[seconds];
            String[] tokens = fieldAndValue.split("=");
            String field;
            if (tokens.length == 2) {
                field = tokens[0];
                String value = tokens[1];
                /* if (field.equals(LoggerData.MetaLabels.app.toString())) {
                    this.app = value;
                } else */
                if (field.equals(LoggerData.MetaLabels.mil.toString())) {
                    this.epochMillis = Long.parseLong(value);
                } else if (field.equals(LoggerData.MetaLabels.rot.toString())) {
                    this.rotation = Integer.parseInt(value);
                } else if (field.equals(LoggerData.MetaLabels.roll.toString())) {
                    this.roll = Float.parseFloat(value);
                } else if (field.equals(LoggerData.MetaLabels.pitch.toString())) {
                    this.pitch = Float.parseFloat(value);
                } else if (field.equals(LoggerData.MetaLabels.yaw.toString())) {
                    this.yaw = Float.parseFloat(value);
                } else if (field.equals(LoggerData.MetaLabels.accX.toString())) {
                    this.accX = Float.parseFloat(value);
                } else if (field.equals(LoggerData.MetaLabels.accY.toString())) {
                    this.accY = Float.parseFloat(value);
                } else if (field.equals(LoggerData.MetaLabels.accZ.toString())) {
                    this.accZ = Float.parseFloat(value);
                }
            } /* else if (tokens.length == 1) {
                field = tokens[0];
                if (field.equals(LoggerData.MetaLabels.app.toString())) {
                    this.app = "";
                }
            } */
        }

        Matcher dataMatch = DATA_FIELDS.matcher(line);
        String dataLine = "";
        if (dataMatch.find()) {
            dataLine = dataMatch.group(1);
        }

        seconds = 0;
        int micros = 0;
        short type = 0;
        short code = 0;
        int dataValue = 0;
        arr$ = dataLine.split(",");
        len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String fieldAndValue = arr$[i$];
            String[] tokens = fieldAndValue.split("=");
            if (tokens.length == 2) {
                String field = tokens[0];
                String value = tokens[1];
                if (field.equals(NativeDeviceData.DataLabels.time.toString())) {
                    seconds = (int)(this.epochMillis / 1000L);
                    micros = (int)(this.epochMillis % 1000L * 1000L);
                } else if (field.equals(NativeDeviceData.DataLabels.type.toString())) {
                    type = Short.decode(value);
                } else if (field.equals(NativeDeviceData.DataLabels.code.toString())) {
                    code = Short.decode(value);
                } else if (field.equals(NativeDeviceData.DataLabels.value.toString())) {
                    dataValue = Long.decode(value).intValue();
                }
            }
        }

        this.data = new NativeDeviceData(seconds, micros, type, code, dataValue);
    }

    public LoggerData(NativeDeviceData data, int rotation, float roll, float pitch, float yaw, float accX, float accY, float accZ) {
        this.data = data;
        //this.app = app;
        this.epochMillis = System.currentTimeMillis();
        this.rotation = rotation;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
    }

    public final NativeDeviceData getData() {
        return this.data;
    }

    /*
    public final String getApp() {
        return this.app;
    }
    */
    public final long getEpochMillis() {
        return this.epochMillis;
    }

    public final int getRotation() {
        return this.rotation;
    }

    public final float getRoll() {
        return this.roll;
    }

    public final float getPitch() {
        return this.pitch;
    }

    public final float getYaw() {
        return this.yaw;
    }

    public String toString() {
        //return String.format(Locale.getDefault(), "meta=(%s=%s,%s=%d,%s=%d,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f),data=(%s)", LoggerData.MetaLabels.app.toString(), this.app, LoggerData.MetaLabels.mil.toString(), this.epochMillis, LoggerData.MetaLabels.rot.toString(), this.rotation, LoggerData.MetaLabels.roll.toString(), this.roll, LoggerData.MetaLabels.pitch.toString(), this.pitch, LoggerData.MetaLabels.yaw.toString(), this.yaw, LoggerData.MetaLabels.accX.toString(), this.accX, LoggerData.MetaLabels.accY.toString(), this.accY, LoggerData.MetaLabels.accZ.toString(), this.accZ, this.data.toString());
        //return String.format(Locale.getDefault(), "meta=(%s=%d,%s=%d,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f),data=(%s)", LoggerData.MetaLabels.mil.toString(), this.epochMillis, LoggerData.MetaLabels.rot.toString(), this.rotation, LoggerData.MetaLabels.roll.toString(), this.roll, LoggerData.MetaLabels.pitch.toString(), this.pitch, LoggerData.MetaLabels.yaw.toString(), this.yaw, LoggerData.MetaLabels.accX.toString(), this.accX, LoggerData.MetaLabels.accY.toString(), this.accY, LoggerData.MetaLabels.accZ.toString(), this.accZ, this.data.toString());
        //Log.i(TAG, String.format(Locale.getDefault(), "%s %d %f %f %f %f %f %f %s", LoggerData.MetaLabels.mil.toString(), this.epochMillis, LoggerData.MetaLabels.rot.toString(), this.rotation, LoggerData.MetaLabels.roll.toString(), this.roll, LoggerData.MetaLabels.pitch.toString(), this.pitch, LoggerData.MetaLabels.yaw.toString(), this.yaw, LoggerData.MetaLabels.accX.toString(), this.accX, LoggerData.MetaLabels.accY.toString(), this.accY, LoggerData.MetaLabels.accZ.toString(), this.accZ, this.data.toString()));
        Log.i(TAG, String.format(Locale.getDefault(),"%d %d %f %f %f %f %f %f %s", this.epochMillis, this.rotation, this.roll, this.pitch, this.yaw, this.accX, this.accY, this.accZ, this.data));
        //Log.i(TAG, String.format(Locale.getDefault(), "meta=(%s=%d,%s=%d,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f),data=(%s)", LoggerData.MetaLabels.mil.toString(), this.epochMillis, LoggerData.MetaLabels.rot.toString(), this.rotation, LoggerData.MetaLabels.roll.toString(), this.roll, LoggerData.MetaLabels.pitch.toString(), this.pitch, LoggerData.MetaLabels.yaw.toString(), this.yaw, LoggerData.MetaLabels.accX.toString(), this.accX, LoggerData.MetaLabels.accY.toString(), this.accY, LoggerData.MetaLabels.accZ.toString(), this.accZ, this.data.toString()));
        //return String.format(Locale.getDefault(), "meta=(%s=%d,%s=%d,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f,%s=%f),data=(%s)", LoggerData.MetaLabels.mil.toString(), this.epochMillis, LoggerData.MetaLabels.rot.toString(), this.rotation, LoggerData.MetaLabels.roll.toString(), this.roll, LoggerData.MetaLabels.pitch.toString(), this.pitch, LoggerData.MetaLabels.yaw.toString(), this.yaw, LoggerData.MetaLabels.accX.toString(), this.accX, LoggerData.MetaLabels.accY.toString(), this.accY, LoggerData.MetaLabels.accZ.toString(), this.accZ, this.data.toString());
        return String.format(String.format(Locale.getDefault(),"%d %d %f %f %f %f %f %f %s", this.epochMillis, this.rotation, this.roll, this.pitch, this.yaw, this.accX, this.accY, this.accZ, this.data));
    }

    public final float getAccX() {
        return this.accX;
    }

    public final float getAccY() {
        return this.accY;
    }

    public final float getAccZ() {
        return this.accZ;
    }

    private static enum MetaLabels {
        //app,
        mil,
        rot,
        roll,
        pitch,
        yaw,
        accX,
        accY,
        accZ;

        private MetaLabels() {}
    }
}
