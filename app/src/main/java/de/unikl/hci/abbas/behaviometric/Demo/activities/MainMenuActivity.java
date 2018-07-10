package de.unikl.hci.abbas.behaviometric.Demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.unikl.hci.abbas.behaviometric.R;

public class MainMenuActivity extends Activity implements View.OnClickListener {

    private Button mAddNewUser;
    private Button mTrainModel;
    private Button mTestAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mAddNewUser = (Button) findViewById(R.id.btn_add_user);
        mTrainModel = (Button) findViewById(R.id.btn_train_model);
        mTestAuth = (Button) findViewById(R.id.btn_test_auth);

        mAddNewUser.setOnClickListener(this);
        mTrainModel.setOnClickListener(this);
        mTestAuth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_add_user:
                intent = new Intent (MainMenuActivity.this, AddNewUserActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_train_model:
                intent = new Intent (MainMenuActivity.this, TrainModelActivity.class);
                //intent = new Intent(MainMenuActivity.this, NegativeFeatureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_test_auth:
                intent = new Intent (MainMenuActivity.this, TestModelActivity.class);
                //intent = new Intent (MainMenuActivity.this, DemoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

}
