package de.unikl.hci.abbas.behaviometric.TouchLogger.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Switch;

import de.unikl.hci.abbas.behaviometric.R;
import de.unikl.hci.abbas.behaviometric.TouchLogger.gestures.GestureDataWriter;
import de.unikl.hci.abbas.behaviometric.TouchLogger.gestures.GestureLoggerService;
import de.unikl.hci.abbas.behaviometric.TouchLogger.utils.DeviceInfo;

public class GestureLoggerActivity extends Activity {

    private static final class Constants {
        public static final int MAX_DISPLAYED_LINES = 50;
        public static final String INFO_SUFFIX = "_info.txt";
        public static final String TAR_SUFFIX = ".tar";
    }

    private ShareActionProvider shareProvider = null;
    private static final LinkedList<String> LOG_BUFFER = new LinkedList<String>();

    private static GestureDataWriter dataWriter = null;
    private static boolean serviceStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_logger);

        // Store the device ID, model, and manufacturer
        if (!DeviceInfo.isSet()) {
            DeviceInfo.init(this);

            // Disable the on/off switch if the device is not rooted
            final Switch monitor = (Switch) findViewById(R.id.monitorSwitch);
            monitor.setEnabled(DeviceInfo.getRooted());
        }

        // The "Monitor" on/off switch will control whether the logging service is running
        final Switch monitor = (Switch) findViewById(R.id.monitorSwitch);
        monitor.setChecked(serviceStarted);
        monitor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchMonitor(buttonView, isChecked);
            }
        });

        // The status box will receive messages from the service
        final EditText statusBox = (EditText) findViewById(R.id.statusBox);
        final String nameValue = getResources().getString(R.string.extra_value_name);
        BroadcastReceiver statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String statusMessage = intent.getStringExtra(nameValue);
                LOG_BUFFER.addFirst(statusMessage);

                if (LOG_BUFFER.size() > Constants.MAX_DISPLAYED_LINES) {
                    LOG_BUFFER.removeLast();
                }

                StringBuffer buffer = new StringBuffer();
                for (String line : LOG_BUFFER) {
                    buffer.append(String.format("%s%n", line));
                }

                statusBox.setText(buffer);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, new IntentFilter(getResources().getString(R.string.action_status_message)));

        // Modify the service started flag based on the start/stop success messages from the service
        BroadcastReceiver serviceStartedStoppedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean startAttempt = intent.getBooleanExtra(getResources().getString(R.string.extra_value_started), false);
                boolean success = intent.getBooleanExtra(getResources().getString(R.string.extra_value_success), false);

                if (success) {
                    if (startAttempt) {
                        serviceStarted = true;
                    } else {
                        serviceStarted = false;
                    }
                } else {
                    monitor.setChecked(serviceStarted);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStartedStoppedReceiver, new IntentFilter(getResources().getString(R.string.action_status_service)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize the data writer if it hasn't already been
        if(dataWriter == null) {
            try {
                dataWriter = new GestureDataWriter(this);
            } catch(IOException e) {
                dataWriter = null;

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("ERROR");
                dialog.setMessage(e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gesture_logger, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        shareProvider = (ShareActionProvider) item.getActionProvider();

        updateShareIntent();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    /**
     * Display dialog when the user clicks the "About" button
     *
     * @param view
     */
    public void aboutOnClick(View view) {
        // Configure "About" dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.about_title));
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, just dismiss the dialog
            }
        });

        dialog.setMessage(getDeviceReport());

        dialog.show();
    }

    /**
     * Display the "Apps with usage access" menu when the user clicks this button
     *
     * @param view
     */
    public void appLoggingOnClick(View view) {
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$UsageAccessSettingsActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Click action associated with the monitor toggle widget. Starts and stops
     * the touchscreen device logger.
     */
    private void switchMonitor(CompoundButton monitor, boolean isChecked) {
        EditText text = (EditText) findViewById(R.id.statusBox);

        if (isChecked && !serviceStarted) {
            text.setText("");
            writeDeviceInfo();
            updateShareIntent();
            startLoggerService();
        } else if (!isChecked && serviceStarted) {
            stopLoggerService();
            updateShareIntent();
        }
    }

    /**
     * Activate the background logging service
     */
    private void startLoggerService() {
        Intent intent = new Intent(this, GestureLoggerService.class);
        intent.putExtra(GestureLoggerService.EXTRA_START_SERVICE, true);

        startService(intent);
    }

    /**
     * Stop the background logging service
     */
    private void stopLoggerService() {
        Intent intent = new Intent(this, GestureLoggerService.class);
        intent.putExtra(GestureLoggerService.EXTRA_START_SERVICE, false);

        startService(intent);
    }

    /**
     * Writes out a text file containing device info such as the model, the
     * screen resolution, the list of devices returned by "getevent -p" and the
     * sensors list as reported by the Android API
     */
    private void writeDeviceInfo() {
        StringBuffer text = new StringBuffer(getDeviceReport());
        dataWriter.writeTextData(text, "info.txt");
    }

    /**
     * Updates the share intent with the latest files
     *
     * @return True if all attachments have been put on the share Intent
     */
    private boolean updateShareIntent() {
        boolean foundAllAttachments = false;

        if (shareProvider != null) {
            String to = getResources().getString(R.string.email_to);
            String subject = String.format(getResources().getString(R.string.email_subject), DeviceInfo.getDeviceID());
            String text = getResources().getString(R.string.email_body);

            // Set text fields
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, text);

            // Set attachments
            File tarAttachment = dataWriter.getDataTar();
            ArrayList<Uri> uris = new ArrayList<Uri>();

            if (tarAttachment.exists()) {
                Uri uri = FileProvider.getUriForFile(this, "de.unikl.hci.abbas.touchscreenlogger.fileprovider", tarAttachment);
                uris.add(uri);
            }

            if (!uris.isEmpty()) {
                foundAllAttachments = tarAttachment.exists();

                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                shareProvider.setShareIntent(emailIntent);
            }
        }

        return foundAllAttachments;
    }

    /**
     *
     * @return A multi-line report containing basic informationa about the logger software and device hardware
     */
    private String getDeviceReport() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("caUtilsBuild : " + com.dell.research.continuousauthentication.version.Version.BUILD + "\n");
        buffer.append("caUtilsDate : " + com.dell.research.continuousauthentication.version.Version.DATE+ "\n");
        buffer.append("loggerBuild : " + de.unikl.hci.abbas.behaviometric.TouchLogger.utils.Version.BUILD + "\n");
        buffer.append("loggerDate : " + de.unikl.hci.abbas.behaviometric.TouchLogger.utils.Version.DATE+ "\n");
        buffer.append(DeviceInfo.getSummary());

        return buffer.toString();
    }
}
