package de.unikl.hci.abbas.behaviometric.TouchLogger.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import de.unikl.hci.abbas.behaviometric.R;
import de.unikl.hci.abbas.behaviometric.TouchLogger.capture.CaptureIntentMessage;
import de.unikl.hci.abbas.behaviometric.TouchLogger.capture.CaptureService;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<CaptureIntentMessage, Intent> intents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intents = new HashMap<>();
        for (CaptureIntentMessage message : CaptureIntentMessage.values()) {
            Intent intent = new Intent(this, CaptureService.class);
            intent.setAction(message.name());
            intents.put(message, intent);
        }

        // startService(intents.get(CaptureIntentMessage.START));
    }

    public void startButtonClick(View v) {
        startService(intents.get(CaptureIntentMessage.START));
    }

    public void stopButtonClick(View v) {
        startService(intents.get(CaptureIntentMessage.STOP));
    }
}
