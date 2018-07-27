package com.dell.research.continuousauthentication.nativedevice;

import com.dell.research.continuousauthentication.nativedevice.NativeDeviceEnums.ABSCode;
import com.dell.research.continuousauthentication.nativedevice.NativeDeviceEnums.SYNCode;
import com.dell.research.continuousauthentication.nativedevice.NativeDeviceEnums.Type;

import java.io.Serializable;
import java.util.Locale;

public class NativeDeviceData implements Serializable {
    private static final long serialVersionUID = 7082024149878502090L;
    private int time = -1;
    private int timeMicro = -1;
    private short typeNum = -1;
    private short codeNum = -1;
    private int value = -1;
    private boolean valid = false;
    private Type type = null;
    private ABSCode absCode = null;
    private SYNCode synCode = null;
    private static int touchMaxX = -1;
    private static int touchMaxY = -1;
    private static int displayMaxX = -1;
    private static int displayMaxY = -1;
    private static boolean validMaxValues = false;
    private static boolean hasMismatch = false;

    public NativeDeviceData(int time, int timeMicro, short type, short code, int value) {
        this.time = time;
        this.timeMicro = timeMicro;
        this.typeNum = type;
        this.codeNum = code;
        this.value = value;
        this.valid = this.storeTypeAndCode(type, code);
        if (validMaxValues && hasMismatch) {
            this.reconcileCoordinates();
        }

    }

    public int getTime() {
        return this.time;
    }

    public int getTimeMicro() {
        return this.timeMicro;
    }

    public Type getType() {
        return this.type;
    }

    public SYNCode getSynCode() {
        return this.synCode;
    }

    public ABSCode getAbsCode() {
        return this.absCode;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isValid() {
        return this.valid;
    }

    private boolean storeTypeAndCode(short type, short code) {
        boolean success = false;
        Type[] arr$ = Type.values();
        int len$ = arr$.length;

        int i$;
        for(i$ = 0; i$ < len$; ++i$) {
            Type t = arr$[i$];
            if (t.getValue() == type) {
                this.type = t;
                break;
            }
        }

        if (this.type != null) {
            switch(this.type) {
                case EV_ABS:
                    ABSCode[] arr_abscode$ = ABSCode.values();
                    len$ = arr_abscode$.length;

                    for(i$ = 0; i$ < len$; ++i$) {
                        ABSCode a = arr_abscode$[i$];
                        if (a.getValue() == code) {
                            this.absCode = a;
                            success = true;
                            return success;
                        }
                    }

                    return success;
                case EV_SYN:
                    SYNCode[] arr_syncode$ = SYNCode.values();
                    len$ = arr_syncode$.length;

                    for(i$ = 0; i$ < len$; ++i$) {
                        SYNCode s = arr_syncode$[i$];
                        if (s.getValue() == code) {
                            this.synCode = s;
                            success = true;
                            break;
                        }
                    }
            }
        }

        return success;
    }

    private void reconcileCoordinates() {
        double proportion;
        double newValue;
        if (this.isXCoordinate()) {
            proportion = (0.0D + (double)this.value) / (double)touchMaxX;
            newValue = (double)Math.round(proportion * (double)displayMaxX);
            this.value = (int)newValue;
        } else if (this.isYCoordinate()) {
            proportion = (0.0D + (double)this.value) / (double)touchMaxY;
            newValue = (double)Math.round(proportion * (double)displayMaxY);
            this.value = (int)newValue;
        }

    }

    public boolean isXCoordinate() {
        return this.type == Type.EV_ABS && this.absCode == ABSCode.ABS_MT_POSITION_X;
    }

    public boolean isYCoordinate() {
        return this.type == Type.EV_ABS && this.absCode == ABSCode.ABS_MT_POSITION_Y;
    }

    public void clearPositionData() {
        if (this.isXCoordinate() || this.isYCoordinate()) {
            this.value = 1;
        }

    }

    public String toString() {
        //return String.format(Locale.getDefault(), "%s=%d.%06d,%s=0x%02x,%s=0x%02x,%s=0x%08x", NativeDeviceData.DataLabels.time.toString(), this.time, this.timeMicro, NativeDeviceData.DataLabels.type.toString(), this.typeNum, NativeDeviceData.DataLabels.code.toString(), this.codeNum, NativeDeviceData.DataLabels.value.toString(), this.value);
        return String.format("%d.%06d 0x%02x 0x%02x 0x%08x", this.time, this.timeMicro, this.typeNum, this.codeNum, this.value);
    }

    public static void setInputOutputDimensions(int touchMaxX, int touchMaxY, int displayMaxX, int displayMaxY) {
        touchMaxX = touchMaxX;
        touchMaxY = touchMaxY;
        displayMaxX = displayMaxX;
        displayMaxY = displayMaxY;
        validMaxValues = touchMaxX > 0 && touchMaxY > 0 && displayMaxX > 0 && displayMaxY > 0;
        hasMismatch = touchMaxX != displayMaxX || touchMaxY != displayMaxY;
    }

    public static enum DataLabels {
        time,
        type,
        code,
        value;

        private DataLabels() {
        }
    }

}
