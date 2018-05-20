package com.dell.research.continuousauthentication.nativedevice;

import java.io.Serializable;

public class NativeDeviceEnums implements Serializable {
    private static final long serialVersionUID = -20494974085158317L;

    public NativeDeviceEnums() {
    }

    public static enum ABSCode {
        ABS_MT_SLOT(47),
        ABS_MT_TOUCH_MAJOR(48),
        ABS_MT_TOUCH_MINOR(49),
        ABS_MT_WIDTH_MAJOR(50),
        ABS_MT_WIDTH_MINOR(51),
        ABS_MT_POSITION_X(53),
        ABS_MT_POSITION_Y(54),
        ABS_MT_TRACKING_ID(57),
        ABS_MT_TOOL_X(60),
        ABS_MT_TOOL_Y(61);

        private int value;

        private ABSCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static enum SYNCode {
        SYN_REPORT(0),
        SYN_CONFIG(1),
        SYN_MT_REPORT(2);

        private int value;

        private SYNCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static enum Type {
        EV_SYN(0),
        EV_ABS(3);

        private int value;

        private Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
