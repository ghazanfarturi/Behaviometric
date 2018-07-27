package de.unikl.hci.abbas.behaviometric.Demo.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.dell.research.continuousauthentication.nativedevice.NativeDeviceData;
import com.dell.research.continuousauthentication.nativedevice.NativeDeviceEnums.SYNCode;
import com.dell.research.continuousauthentication.nativedevice.NativeDeviceEnums.Type;
import com.dell.research.continuousauthentication.nativedevice.NativeTouchscreenDeviceReader;
import com.dell.research.continuousauthentication.preprocessor.ConstructedGesture;
import com.dell.research.continuousauthentication.preprocessor.GestureManager;
import com.dell.research.continuousauthentication.utils.ActiveAppPoller;
import com.dell.research.continuousauthentication.utils.sensorlisteners.AccelerometerListener;
import com.dell.research.continuousauthentication.utils.sensorlisteners.OrientationListener;

import de.unikl.hci.abbas.behaviometric.Demo.activities.TrainModelActivity;
import de.unikl.hci.abbas.behaviometric.Demo.util.DataWriter;
import de.unikl.hci.abbas.behaviometric.Demo.util.LoggerData;
import de.unikl.hci.abbas.behaviometric.R;



public class DataLoggerService extends Service {
    private static class Constants {
        public static final int NOTIFICATION_ID = 100;
        public static final int LOG_BUFFER_MAX_SIZE_LINES = 25000;
        public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddhhmmss", Locale.US);
        public static final int LOG_FLUSH_TIMEOUT_MINUTES = 15;
        public static final int LOG_FLUSH_TIMEOUT_MILLIS = 1000 * 60 * LOG_FLUSH_TIMEOUT_MINUTES;

        public static final String LOG_SUFFIX = ".log.gz";
        public static final String INFO_SUFFIX = "_info.txt";
        public static final FilenameFilter LOG_AND_INFO_FILTER = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                boolean isLogFile = filename.toLowerCase(Locale.US).endsWith(LOG_SUFFIX);
                boolean isInfoFile = filename.toLowerCase(Locale.US).endsWith(INFO_SUFFIX);

                return isLogFile || isInfoFile;
            }
        };
    }

    public static final String EXTRA_START_SERVICE = "GestureLoggerService_START_SERVICE";

    // Touchscreen, orientation, display, active app, and accelerometer data
    // interfaces
    private NativeTouchscreenDeviceReader touchscreen = null;
    private OrientationListener orientation = null;
    private ActiveAppPoller activeApp = null;
    private AccelerometerListener acceleration = null;

    // Put the device polling operation on its own thread with a boolean sentinel
    private Runnable devicePoller = null;
    private volatile boolean killDevicePoller = false;

    // Locally saved log file
    private GestureManager gestures = null;
    private LinkedList<NativeDeviceData> tapCheckBuffer = null;
    private LinkedList<LoggerData> holdingBuffer = null;

    private LinkedList<LoggerData> logBuffer = null;

    private long nextLogWrite = 0;

    private Display display = null;

    private boolean setupSuccess = false;
    private Exception setupException = null;

    private DataWriter dataWriter = null;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Get the screen resolution in its native orientation
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            display.getRealMetrics(dm);
            int displayMaxX = 0;
            int displayMaxY = 0;

            boolean isPortrait = display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180;
            if (isPortrait) {
                displayMaxX = dm.widthPixels;
                displayMaxY = dm.heightPixels;
            } else {
                displayMaxX = dm.heightPixels;
                displayMaxY = dm.widthPixels;
            }

            // Initialize the touchscreen reader
            touchscreen = new NativeTouchscreenDeviceReader(displayMaxX, displayMaxY);

            // Thread to poll the touchscreen reader for data
            devicePoller = new Runnable() {
                @Override
                public void run() {
                    while (!killDevicePoller) {
                        try {
                            NativeDeviceData data = touchscreen.read();

                            if (data != null) {
                                logData(data);
                            }
                        } catch (IOException e) {
                            // This exception happens when the data stream times
                            // out
                            if (System.currentTimeMillis() >= nextLogWrite) {
                                writeTimestampNamedLogFile(logBuffer);
                            }
                        }
                    }

                    if (killDevicePoller) {
                        // Flush log to file before killing the device polling
                        // thread
                        writeTimestampNamedLogFile(logBuffer);

                        // Set this to false to allow a new thread with this
                        // Runnable to poll the device
                        killDevicePoller = false;
                    }
                }
            };

            activeApp = new ActiveAppPoller(this);
            orientation = new OrientationListener(this);
            acceleration = new AccelerometerListener(this);

            gestures = new GestureManager(displayMaxX, displayMaxY);
            tapCheckBuffer = new LinkedList<NativeDeviceData>();
            holdingBuffer = new LinkedList<LoggerData>();
            logBuffer = new LinkedList<LoggerData>();

            setupSuccess = true;

            dataWriter = new DataWriter(this);
        } catch (IOException e) {
            setupException = e;
            sendException(e);
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        // Write out the log
        writeTimestampNamedLogFile(logBuffer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Write out the log
        writeTimestampNamedLogFile(logBuffer);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (setupSuccess) {
            boolean startService = intent.getBooleanExtra(DataLoggerService.EXTRA_START_SERVICE, false);

            if (startService) {
                try {
                    startLogging();
                    startForeground(DataLoggerService.Constants.NOTIFICATION_ID, buildNotification().build());
                    sendServiceSuccess(true, true);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    sendServiceSuccess(true, false);
                }

                return super.onStartCommand(intent, flags, startId);
            } else {
                try {
                    stopLogging();
                    stopForeground(true);
                    sendServiceSuccess(false, true);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    sendServiceSuccess(false, false);
                }

                return START_STICKY_COMPATIBILITY;
            }
        } else {
            sendException(setupException);

            return START_STICKY_COMPATIBILITY;
        }
    }

    /**
     * Starts the background logging thread
     *
     * @throws InterruptedException
     * @throws IOException
     */
    private void startLogging() throws IOException, InterruptedException {
        nextLogWrite = System.currentTimeMillis() + DataLoggerService.Constants.LOG_FLUSH_TIMEOUT_MILLIS;

        touchscreen.open();
        activeApp.open();
        orientation.open();
        acceleration.open();

        Thread devicePollThread = new Thread(devicePoller);
        devicePollThread.start();

        sendStatusMessage("Activated");
    }

    /**
     * Stop the logging thread
     *
     * @throws InterruptedException
     * @throws IOException
     */
    private void stopLogging() throws IOException, InterruptedException {
        killDevicePoller = true;

        acceleration.close();
        orientation.close();
        activeApp.close();
        touchscreen.close();

        sendStatusMessage("Deactivated");
        sendServiceSuccess(false, true);
    }

    /**
     * Saves the native device data + metadata to a log buffer
     *
     * @param data
     *            Native device data (SYN/EVT codes, values, time)
     */
    private void logData(NativeDeviceData data) {
        String appName = activeApp.getActiveApp();

        int rotation = display.getRotation();

        float roll = orientation.getRoll();
        float pitch = orientation.getPitch();
        float yaw = orientation.getYaw();

        float accX = acceleration.getAccelerationX();
        float accY = acceleration.getAccelerationY();
        float accZ = acceleration.getAccelerationZ();

        LoggerData loggerData = new LoggerData(data, rotation, roll, pitch, yaw, accX, accY, accZ);

        // Process touchscreen messages through the gesture manager to identify
        // taps. The gesture manager will remove position data from tap events
        // for privacy.
        holdingBuffer.add(loggerData);
        tapCheckBuffer.add(data);
        if (data.getType() == Type.EV_SYN && data.getSynCode() == SYNCode.SYN_REPORT) {
            List<ConstructedGesture> finishedGestures = gestures.process(tapCheckBuffer, rotation, appName);
            tapCheckBuffer.clear();

            if (!finishedGestures.isEmpty()) {
                logBuffer.addAll(holdingBuffer);
                for (ConstructedGesture swipe : finishedGestures) {
                    sendStatusMessage(String.format("duration=%f,direction=%f,distance=%f", swipe.getDuration(), swipe.getSwipeDirection(), swipe.getDistance()));
                }

                holdingBuffer.clear();
            }
        }

        // Flush log to file when it meets or exceeds the max size
        if (logBuffer.size() >= DataLoggerService.Constants.LOG_BUFFER_MAX_SIZE_LINES) {
            LinkedList<LoggerData> writeBuffer = logBuffer;

            // Clear the log buffer and saved active app name to ensure the
            // first entry in the next log contains app data
            logBuffer = new LinkedList<LoggerData>();

            writeTimestampNamedLogFile(writeBuffer);
        }
    }

    /**
     * Flushes the specified log buffer to a file named with the current time.
     * The log buffer will be cleared.
     *
     * @param logBuffer
     */
    private void writeTimestampNamedLogFile(final LinkedList<LoggerData> logBuffer) {
        if (!logBuffer.isEmpty()) {
            String timeTag = DataLoggerService.Constants.DATE_FORMAT.format(Calendar.getInstance().getTime());
            StringBuffer logLines = new StringBuffer();

            dataWriter.writeCompressedLoggerData(logBuffer, timeTag + ".txt");
            nextLogWrite = System.currentTimeMillis() + DataLoggerService.Constants.LOG_FLUSH_TIMEOUT_MILLIS;
        }
    }

    /**
     * Sends an exception message and stack trace to the app's console
     *
     * @param e
     */
    private void sendException(Exception e) {
        sendStatusMessage(e.getMessage());
        for (StackTraceElement trace : e.getStackTrace()) {
            sendStatusMessage(trace.toString());
        }
    }

    /**
     * Sends a status message to be printed out on the app's console
     *
     * @param message
     */
    private void sendStatusMessage(String message) {
        Intent status = new Intent(getResources().getString(R.string.action_status_message));
        status.putExtra(getResources().getString(R.string.extra_value_name), message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(status);
    }

    /**
     * Sends an intent signifying if the service has been successfully toggled
     *
     * @param started
     *            Set to true if attempting to start, false if attempting to
     *            stop
     * @param success
     *            Set to true if successfully started, false otherwise
     */
    private void sendServiceSuccess(boolean started, boolean success) {
        Intent startSuccess = new Intent(getResources().getString(R.string.action_status_service));
        startSuccess.putExtra(getResources().getString(R.string.extra_value_started), started);
        startSuccess.putExtra(getResources().getString(R.string.extra_value_success), success);
        LocalBroadcastManager.getInstance(this).sendBroadcast(startSuccess);
    }

    /**
     * Create a tray notification entry
     *
     * @return Builder for the notification
     */
    private NotificationCompat.Builder buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.notification_title));
        builder.setContentText("");
        builder.setSmallIcon(R.drawable.ic_stat_lock);

        Intent openIntent = new Intent(this, TrainModelActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(openPendingIntent);

        return builder;
    }
}
