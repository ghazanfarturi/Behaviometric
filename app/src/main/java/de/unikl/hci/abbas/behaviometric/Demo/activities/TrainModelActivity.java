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
import de.unikl.hci.abbas.behaviometric.R;
import de.unikl.hci.abbas.behaviometric.TouchLogger.utils.DeviceInfo;

public class TrainModelActivity extends AppCompatActivity {

    private static final class Constants {
        public static final int MAX_DISPLAYED_LINES = 50;
        public static final String INFO_SUFFIX = "_info.txt";
        public static final String TAG = "TrainerActivity";
    }

    private ShareActionProvider shareProvider = null;
    private static final LinkedList<String> LOG_BUFFER = new LinkedList<String>();

    private static DataWriter dataWriter = null;
    private static boolean serviceStarted = false;

    ImageView mFirstScreen;
    Button mButtonNext;
    Spinner spinner;
    static final String[] users = new String[] {"User0001", "User0002", "User0003","User0004","User0005"};

    boolean mFileState;
    int resourceInc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_model);

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
        monitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchMonitor(buttonView, isChecked);
            }
        });

        mFirstScreen = (ImageView) findViewById(R.id.mlFirstScreen);
        mFirstScreen.setImageResource(R.drawable.backward);

        LinearLayout ll = (LinearLayout) findViewById(R.id.mlPaintLayout);

        View view = new PaintView(this);
        ll.addView(view);

        mButtonNext = (Button) findViewById(R.id.mlbtnNext);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mFirstScreen.setImageResource(R.drawable.circle);
            }
        });

        spinner = (Spinner) findViewById(R.id.spinnerUsers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, users);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
    private void switchMonitor(CompoundButton monitor, boolean isChecked) {
        if (isChecked && !serviceStarted) {
            writeDeviceInfo();
            startLoggerService();
        } else if (!isChecked && serviceStarted) {
            stopLoggerService();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Initialize the data writer if it hasn't already been
        if(dataWriter == null) {
            try {
                dataWriter = new DataWriter(this);
            } catch(IOException e) {
                dataWriter = null;

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("ERROR");
                dialog.setMessage(e.getMessage());
            }
        }

        mFirstScreen = (ImageView) findViewById(R.id.mlFirstScreen);

        mButtonNext = (Button) findViewById(R.id.mlbtnNext);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            int i = 2;
            @Override
            public void onClick(View v) {
                final int[] resourceId = new int[17];
                resourceId[0] = getResources().getIdentifier("backward", "drawable", getPackageName());
                resourceId[1] = getResources().getIdentifier("circle", "drawable", getPackageName());
                resourceId[2] = getResources().getIdentifier("cloud", "drawable", getPackageName());
                resourceId[3] = getResources().getIdentifier("diamond", "drawable", getPackageName());
                resourceId[4] = getResources().getIdentifier("down", "drawable", getPackageName());
                resourceId[5] = getResources().getIdentifier("eight", "drawable", getPackageName());
                resourceId[6] = getResources().getIdentifier("flash", "drawable", getPackageName());
                resourceId[7] = getResources().getIdentifier("forward", "drawable", getPackageName());
                resourceId[8] = getResources().getIdentifier("fourgon", "drawable", getPackageName());
                resourceId[9] = getResources().getIdentifier("hexagon", "drawable", getPackageName());
                resourceId[10] = getResources().getIdentifier("pentagon", "drawable", getPackageName());
                resourceId[11] = getResources().getIdentifier("rectangle", "drawable", getPackageName());
                resourceId[12] = getResources().getIdentifier("square", "drawable", getPackageName());
                resourceId[13] = getResources().getIdentifier("star", "drawable", getPackageName());
                resourceId[14] = getResources().getIdentifier("tilde", "drawable", getPackageName());
                resourceId[15] = getResources().getIdentifier("triangle", "drawable", getPackageName());
                resourceId[16] = getResources().getIdentifier("up", "drawable", getPackageName());

                if ((v.getId() == R.id.mlbtnNext) && i < 16) {
                    mFirstScreen.setImageResource(resourceId[i]);
                    i++;
                }
            }
        });

    }

    public void returnToMain(View v) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stopLoggerService();
        startActivity(intent);

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
