package com.dell.research.continuousauthentication.demotransmitter;

import org.json.JSONException;
import org.json.JSONObject;

public class DemoSignals {
    public static final DemoTransmittable LOCK_OUT = new DemoTransmittable() {
        public String getVisualizationJSON() {
            JSONObject lockout = new JSONObject();

            try {
                lockout.put("msgtype", "lockout");
            } catch (JSONException var3) {
                var3.printStackTrace();
            }

            return lockout.toString();
        }
    };

    public DemoSignals() {
    }
}
