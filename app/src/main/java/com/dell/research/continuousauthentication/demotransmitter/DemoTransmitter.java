package com.dell.research.continuousauthentication.demotransmitter;

import android.os.AsyncTask;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class DemoTransmitter extends AsyncTask<DemoTransmittable, Void, Boolean> {
    private static final String USER_AGENT = "Dell Continuous Authentication Prototype";
    private String demoServer = "";
    private HttpClient client = null;
    private HttpPost post = null;

    public DemoTransmitter(String demoServer) {
        this.demoServer = demoServer;
        this.init();
    }

    public DemoTransmitter(DemoTransmitter dt) {
        this.demoServer = dt.demoServer;
        this.init();
    }

    private void init() {
        this.client = new DefaultHttpClient();
        this.post = new HttpPost(this.demoServer);
        this.post.setHeader("User-Agent", "Dell Continuous Authentication Prototype");
    }

    private String transmit(DemoTransmittable data) throws IOException {
        String json = data.getVisualizationJSON();
        StringEntity se = new StringEntity(json, "UTF-8");
        se.setContentType("application/json");
        this.post.setEntity(se);
        HttpResponse response = this.client.execute(this.post);
        HttpEntity responseEntity = response.getEntity();
        return EntityUtils.toString(responseEntity);
    }

    protected Boolean doInBackground(DemoTransmittable... params) {
        if (params.length <= 0) {
            return false;
        } else {
            boolean success = true;
            DemoTransmittable[] arr$ = params;
            int len$ = params.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                DemoTransmittable dt = arr$[i$];

                try {
                    this.transmit(dt);
                } catch (IOException var8) {
                    var8.printStackTrace();
                    success = false;
                }
            }

            return success;
        }
    }
}
