package de.unikl.hci.abbas.behaviometric.Demo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.IOException;
import java.util.LinkedList;

import de.unikl.hci.abbas.behaviometric.Demo.services.DataLoggerService;
import de.unikl.hci.abbas.behaviometric.Demo.util.DataWriter;
import de.unikl.hci.abbas.behaviometric.TouchLogger.utils.DeviceInfo;

import de.unikl.hci.abbas.behaviometric.R;

public class TestModelActivity extends AppCompatActivity {

    private static final class Constants {
        public static final int MAX_DISPLAYED_LINES = 50;
        public static final String INFO_SUFFIX = "_info.txt";
        public static final String TAG = "TestActivity";
    }

    private ShareActionProvider shareProvider = null;
    private static final LinkedList<String> LOG_BUFFER = new LinkedList<String>();

    private static DataWriter dataWriter = null;
    private static boolean serviceStarted = false;

    Button mButtonTest;
    Spinner spinnerUser;
    Spinner spinnerTestModel;
    public static String mNameTest;
    public static String mModeTest;
    static final String[] users = new String[] {"User0001", "User0002", "User0003","User0004","User0005"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_model);

        // Store the device ID, model, and manufacturer
        if (!DeviceInfo.isSet()) {
            DeviceInfo.init(this);

            // Disable the on/off switch if the device is not rooted
            final Switch monitor = (Switch) findViewById(R.id.recordSwitch);
            monitor.setEnabled(DeviceInfo.getRooted());
        }

        // The "Monitor" on/off switch will control whether the logging service is running
        final Switch monitor = (Switch) findViewById(R.id.recordSwitch);
        monitor.setChecked(serviceStarted);
        monitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recordSwitch(buttonView, isChecked);
            }
        });

        LinearLayout ll = (LinearLayout) findViewById(R.id.mlPaintLayout);

        View view = new PaintView(this);
        ll.addView(view);

        mButtonTest = (Button) findViewById(R.id.mlbtnTest);

        spinnerUser = (Spinner) findViewById(R.id.spinnerTestUsers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, users);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerUser.setAdapter(adapter);

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
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStartedStoppedReceiver, new IntentFilter(getResources().getString(R.string.action_status_service)));

    }

    /**
     * Click action associated with the monitor toggle widget. Starts and stops
     * the touchscreen device logger.
     */
    private void recordSwitch(CompoundButton monitor, boolean isChecked) {
        if (isChecked && !serviceStarted) {
            // writeDeviceInfo();
            startLoggerService();
        } else if (!isChecked && serviceStarted) {
            stopLoggerService();
        }
    }

    public void returnToMain(View v) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stopLoggerService();
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize the data writer if it hasn't already been
        if(dataWriter == null) {
            try {
                mNameTest = spinnerUser.getSelectedItem().toString();
                mModeTest = "test";
                dataWriter = new DataWriter(this);
            } catch(IOException e) {
                dataWriter = null;

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("ERROR");
                dialog.setMessage(e.getMessage());
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onClickAuth(View view) {

    }

    /**
     * Activate the background logging service
     */
    private void startLoggerService() {
        Intent intent = new Intent(this, DataLoggerService.class);
        intent.putExtra(DataLoggerService.EXTRA_START_SERVICE, true);
        startService(intent);
    }

    /**
     * Stop the background logging service
     */
    private void stopLoggerService() {
        Intent intent = new Intent(this, DataLoggerService.class);
        intent.putExtra(DataLoggerService.EXTRA_START_SERVICE, false);
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
     *
     * @return A multi-line report containing basic information about the logger software and device hardware
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
