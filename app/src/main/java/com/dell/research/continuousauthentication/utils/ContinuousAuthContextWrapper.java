package com.dell.research.continuousauthentication.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;

public class ContinuousAuthContextWrapper {
    public static final String ACTION_DEVICE_MESSAGE = "ContinuousAuthContextWrapper.ACTION_DEVICE_MESSAGE";
    public static final String ACTION_LOCK_OUT = "ContinuousAuthContextWrapper.ACTION_LOCK_OUT";
    private Context context = null;
    private boolean validContext = false;

    public ContinuousAuthContextWrapper() {
        this.context = null;
        this.validContext = false;
    }

    public ContinuousAuthContextWrapper(Context c) {
        this.context = c;
        this.validContext = c != null;
    }

    public void broadcastLock() {
        if (this.validContext) {
            Intent intent = new Intent("ContinuousAuthContextWrapper.ACTION_LOCK_OUT");
            LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
        }

    }

    public void broadcastText(String tag, String statusMsg) {
        if (this.validContext) {
            Intent intent = new Intent("ContinuousAuthContextWrapper.ACTION_DEVICE_MESSAGE");
            String toSend = String.format(Locale.getDefault(), "(%s) %s [%s]", TimestampLogger.getTimestamp(), statusMsg, tag);
            intent.putExtra("VALUE", toSend);
            LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
        } else {
            TimestampLogger.info(tag, statusMsg);
        }

    }

    public FileInputStream getInternalInputStream(String name) throws FileNotFoundException {
        return this.validContext ? this.context.openFileInput(name) : null;
    }

    public FileOutputStream getInternalOutputStream(String name) throws FileNotFoundException {
        return this.validContext ? this.context.openFileOutput(name, 0) : null;
    }

    public Context getContext() {
        return this.context;
    }
}
