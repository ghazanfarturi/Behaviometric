package de.unikl.hci.abbas.behaviometric.Demo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.unikl.hci.abbas.behaviometric.R;

public class AddNewUserActivity extends Activity {

    private static final String AddUserTAG = "AddNewUser";

    private Button mButtonOK, mButtonCancel;
    private EditText mEditTextUserID, mEditTextFirstName, mEditTextLastName;
    private String userId, fname, lname;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

    private FileOutputStream fileOutput;
    private OutputStreamWriter writer;
    private StringBuffer strBuffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        mButtonOK = (Button) findViewById(R.id.btn_add_user_ok);
        mEditTextUserID = (EditText) findViewById(R.id.txtUserID);
        mEditTextFirstName = (EditText) findViewById(R.id.txtFirstName);
        mEditTextLastName = (EditText) findViewById(R.id.txtLastName);

        verifyStoragePermissions(this);
    }

    public void initialize() {
        userId = mEditTextUserID.getText().toString().trim();
        fname  = mEditTextFirstName.getText().toString().trim();
        lname  = mEditTextLastName.getText().toString().trim();
    }

    public boolean validateInput() {

        if (userId.isEmpty() || fname.isEmpty() || lname.isEmpty() ) {
            return false;
        }
        return true;
    }

    public void saveNewUser(View v) {
        initialize();

        if(!validateInput()) {
            Toast.makeText(this, "Please fill out all mandatory fields!", Toast.LENGTH_SHORT).show();
        } else {
            strBuffer = new StringBuffer();
            strBuffer.append(mEditTextUserID.getText());
            strBuffer.append("          ");
            strBuffer.append(mEditTextFirstName.getText());
            strBuffer.append("          ");
            strBuffer.append(mEditTextLastName.getText());
            strBuffer.append("\n");
            writeFile();
            clearField();
        }

    }

    public void clearField() {
        mEditTextUserID.getText().clear();
        mEditTextFirstName.getText().clear();
        mEditTextLastName.getText().clear();
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void writeFile () {

        File file = new File(Environment.getExternalStorageDirectory(), "Behaviometric/users.csv");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            fileOutput = new FileOutputStream(file, true);
            writer = new OutputStreamWriter(fileOutput);
            writer.append(strBuffer);
            writer.close();
            fileOutput.close();

        } catch (IOException e) {
            Log.e("Exception", "Unable to open file: " + e.toString());
        }
    }

}
