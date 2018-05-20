package com.dell.research.continuousauthentication.nativedevice;

import android.os.Build.VERSION;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NativeTouchscreenDeviceReader {
    private static final String INPUT_LIB = "input-event";
    private static final String[] GETEVENT_LIST_COMMAND = new String[]{"su", "-c", "getevent -lp"};
    private static final String[] BYPASS_COMMAND = new String[]{"su", "-c", "supolicy --live 'allow appdomain input_device dir { ioctl read getattr search open }' 'allow appdomain input_device chr_file { ioctl read write getattr lock append open }'"};
    private static final String[] REVERT_COMMAND = new String[]{"su", "-c", "supolicy --live 'deny appdomain input_device dir { ioctl read getattr search open }' 'deny appdomain input_device chr_file { ioctl read write getattr lock append open }'"};
    private static final String ID_COMMAND = "id";
    private static final Pattern ID_PATTERN = Pattern.compile("uid=(\\d+)");
    private static final int INPUT_GID = 1004;
    private static final String USER_INPUT_PATTERN_FORMAT = "\\s+%d\\s+1004\\s+";
    private static final Pattern ROOT_INPUT_PATTERN;
    private Pattern uidInputPattern = null;
    private static final String TOUCHSCREEN_X_LABEL = "ABS_MT_POSITION_X";
    private static final String TOUCHSCREEN_Y_LABEL = "ABS_MT_POSITION_Y";
    private static final String TOUCHSCREEN_PRESSURE_LABEL = "ABS_MT_PRESSURE";
    private static final String INPUT_PROP_LABEL = "INPUT_PROP_DIRECT";
    private static final Pattern MAX_VALUE_PATTERN;
    private static final Pattern DEVICE_PATH_PATTERN;
    private String[] lsCommand = null;
    private String[] chownAllowCommand = null;
    private String[] chownDenyCommand = null;
    private String devicePath = "";
    private boolean deviceOpened = false;
    private boolean androidNewerThanKitKat = false;
    private boolean selinuxBypassed = false;
    private int touchMaxX = 0;
    private int touchMaxY = 0;
    private boolean hasPressureField = false;

    public NativeTouchscreenDeviceReader(int displaySizeX, int displaySizeY) throws IOException {
        this.devicePath = this.getTouchscreenDeviceFile();
        NativeDeviceData.setInputOutputDimensions(this.touchMaxX, this.touchMaxY, displaySizeX, displaySizeY);
        int uid = this.getUID();
        this.chownAllowCommand = new String[]{"su", "-c", String.format("chown %d %s", uid, this.devicePath)};
        this.chownDenyCommand = new String[]{"su", "-c", String.format("chown 0 %s", this.devicePath)};
        this.lsCommand = new String[]{"ls", "-n", this.devicePath};
        this.uidInputPattern = Pattern.compile(String.format("\\s+%d\\s+1004\\s+", uid));
        this.androidNewerThanKitKat = VERSION.SDK_INT > 19;
    }

    private native boolean openDevice(String var1);
    //public static native boolean openDevice(String var1);

    private native NativeDeviceData readDevice();

    private native boolean closeDevice(String var1);

    public void open() throws IOException, InterruptedException {
        this.bypassSELinux();
        if (this.rootOrAppOwnsDevice()) {
            Process p = Runtime.getRuntime().exec(this.chownAllowCommand);
            p.waitFor();
            boolean success = this.openDevice(this.devicePath);
            //boolean success = this.isDeviceOpened(this.devicePath);
            if (success) {
                this.deviceOpened = true;
            } else {
                throw new IOException("Unable to open " + this.devicePath);
            }
        } else {
            throw new IOException("Non-root owns " + this.devicePath);
        }
    }

    public void close() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(this.chownDenyCommand);
        p.waitFor();
        boolean success = this.closeDevice(this.devicePath);
        this.revertSELinux();
        if (success) {
            this.deviceOpened = false;
        } else {
            throw new IOException("Unable to close " + this.devicePath);
        }
    }

    public NativeDeviceData read() throws IOException {
        NativeDeviceData data = this.readDevice();
        if (data != null) {
            return data;
        } else {
            throw new IOException("Timed out reading " + this.devicePath);
        }
    }

    public boolean isOpened() {
        return this.deviceOpened;
    }

    public String getDevicePath() {
        return this.devicePath;
    }

    public boolean hasPressureField() {
        return this.hasPressureField;
    }

    private String getTouchscreenDeviceFile() throws IOException {
        Process geteventList = Runtime.getRuntime().exec(GETEVENT_LIST_COMMAND);

        try {
            geteventList.waitFor();
        } catch (InterruptedException var14) {
            throw new IOException(var14.getMessage());
        }

        InputStreamReader input = new InputStreamReader(geteventList.getInputStream());
        BufferedReader reader = new BufferedReader(input);
        boolean xIsSet = false;
        boolean yIsSet = false;
        String devicePath = "";
        String line = "";

        boolean endOfTouchscreen;
        do {
            if ((line = reader.readLine()) == null) {
                reader.close();
                input.close();
                throw new IOException("No touchscreen found");
            }

            Matcher match = DEVICE_PATH_PATTERN.matcher(line);
            boolean lineContainsDevicePath = match.find();
            boolean hasXPosition = line.contains("ABS_MT_POSITION_X");
            boolean hasYPosition = line.contains("ABS_MT_POSITION_Y");
            boolean hasPressureField = line.contains("ABS_MT_PRESSURE");
            endOfTouchscreen = line.contains("INPUT_PROP_DIRECT");
            if (lineContainsDevicePath) {
                devicePath = match.group();
                this.touchMaxX = 0;
                xIsSet = false;
                this.touchMaxY = 0;
                yIsSet = false;
                this.hasPressureField = false;
            } else if (hasXPosition) {
                this.touchMaxX = this.getMaxFromLine(line);
                xIsSet = this.touchMaxX > 0;
            } else if (hasYPosition) {
                this.touchMaxY = this.getMaxFromLine(line);
                yIsSet = this.touchMaxY > 0;
            } else if (hasPressureField) {
                this.hasPressureField = true;
            }
        } while(!xIsSet || !yIsSet || devicePath.isEmpty() || !endOfTouchscreen);

        reader.close();
        input.close();
        return devicePath;
    }

    private int getUID() throws IOException {
        Process getID = Runtime.getRuntime().exec("id");

        try {
            getID.waitFor();
        } catch (InterruptedException var8) {
            throw new IOException(var8.getMessage());
        }

        InputStreamReader input = new InputStreamReader(getID.getInputStream());
        BufferedReader reader = new BufferedReader(input);
        String line = "";
        int id = -1;

        while((line = reader.readLine()) != null) {
            Matcher match = ID_PATTERN.matcher(line);
            boolean lineContainsID = match.find();
            if (lineContainsID) {
                id = Integer.parseInt(match.group(1));
                break;
            }
        }

        reader.close();
        input.close();
        if (id > 0) {
            return id;
        } else {
            throw new IOException("No app UID found");
        }
    }

    private boolean rootOrAppOwnsDevice() throws IOException, InterruptedException {
        Process ls = Runtime.getRuntime().exec(this.lsCommand);
        ls.waitFor();
        InputStreamReader input = new InputStreamReader(ls.getInputStream());
        BufferedReader reader = new BufferedReader(input);
        String line = "";

        boolean validOwner;
        Matcher matchRoot;
        Matcher matchApp;
        for(validOwner = false; (line = reader.readLine()) != null; validOwner = validOwner || matchRoot.find() || matchApp.find()) {
            matchRoot = ROOT_INPUT_PATTERN.matcher(line);
            matchApp = this.uidInputPattern.matcher(line);
        }

        reader.close();
        input.close();
        return validOwner;
    }

    private int getMaxFromLine(String line) {
        Matcher match = MAX_VALUE_PATTERN.matcher(line);
        if (match.find()) {
            String value = match.group(1);
            return Integer.parseInt(value);
        } else {
            return -1;
        }
    }

    private void bypassSELinux() throws IOException, InterruptedException {
        if (this.androidNewerThanKitKat && !this.selinuxBypassed) {
            Process p = Runtime.getRuntime().exec(BYPASS_COMMAND);
            p.waitFor();
            this.selinuxBypassed = true;
        }

    }

    private void revertSELinux() throws IOException, InterruptedException {
        if (this.androidNewerThanKitKat && this.selinuxBypassed) {
            Process p = Runtime.getRuntime().exec(REVERT_COMMAND);
            p.waitFor();
            this.selinuxBypassed = false;
        }

    }

    static {
        ROOT_INPUT_PATTERN = Pattern.compile(String.format(Locale.US, "\\s+%d\\s+1004\\s+", 0));
        MAX_VALUE_PATTERN = Pattern.compile("max (\\d+)");
        DEVICE_PATH_PATTERN = Pattern.compile("/dev/input/event\\d+");
        System.loadLibrary("input-event");
    }
}
