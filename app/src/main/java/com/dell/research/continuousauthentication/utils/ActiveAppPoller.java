package com.dell.research.continuousauthentication.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ActiveAppPoller {
    private static final int POLLING_PERIOD_MS = 500;
    private Runnable activeAppPoller = null;
    private volatile boolean killActiveAppPoller = false;
    private volatile String activeApp = "";
    Context context = null;

    public ActiveAppPoller(Context c) {
        this.context = c;
        this.activeAppPoller = new Runnable() {
            public void run() {
                while(true) {
                    if (!ActiveAppPoller.this.killActiveAppPoller) {
                        String app = ActiveAppPoller.this.getActiveAppName();
                        if (!app.isEmpty()) {
                            ActiveAppPoller.this.activeApp = app;
                        }

                        try {
                            Thread.sleep(500L);
                            continue;
                        } catch (InterruptedException var3) {
                            var3.printStackTrace();
                        }
                    }

                    if (ActiveAppPoller.this.killActiveAppPoller) {
                        ActiveAppPoller.this.killActiveAppPoller = false;
                    }

                    return;
                }
            }
        };
    }

    public String getActiveApp() {
        return this.activeApp;
    }

    public void open() {
        Thread thread = new Thread(this.activeAppPoller);
        thread.start();
    }

    public void close() {
        this.killActiveAppPoller = true;
    }

    @SuppressLint({"NewApi"})
    private String getActiveAppName() {
        String activeApp = "";

        try {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager)this.context.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(0, time - 1000000L, time);
            if (stats != null && !stats.isEmpty()) {
                if (stats.size() == 1) {
                    activeApp = ((UsageStats)stats.get(0)).getPackageName();
                } else {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap();
                    Iterator i$ = stats.iterator();

                    while(i$.hasNext()) {
                        UsageStats usageStats = (UsageStats)i$.next();
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }

                    activeApp = ((UsageStats)mySortedMap.get(mySortedMap.lastKey())).getPackageName();
                }
            }
        } catch (NoClassDefFoundError var9) {
            ActivityManager am = (ActivityManager)this.context.getSystemService("activity");
            List<RecentTaskInfo> recentTasks = am.getRecentTasks(1, 1);
            if (!recentTasks.isEmpty()) {
                RecentTaskInfo task = (RecentTaskInfo)recentTasks.get(0);
                Intent base = task.baseIntent;
                activeApp = base.getComponent().getPackageName();
            }
        }

        return activeApp;
    }
}
