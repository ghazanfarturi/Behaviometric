package de.unikl.hci.abbas.behaviometric.Demo.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import de.unikl.hci.abbas.behaviometric.R;

public class TrainModelActivity extends AppCompatActivity {

    public static final String TAG = "TrainerActivity";

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

    }

    @Override
    protected void onStart() {
        super.onStart();

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
        startActivity(intent);
    }


}
