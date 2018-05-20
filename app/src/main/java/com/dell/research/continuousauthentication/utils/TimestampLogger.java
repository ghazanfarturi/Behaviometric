package com.dell.research.continuousauthentication.utils;

import android.util.Log;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimestampLogger {
    private static boolean enabled = true;

    public TimestampLogger() {
    }

    public static void info(String tag, String message) {
        if (enabled) {
            String out = String.format("(%s) %s", getTimestamp(), message);

            try {
                Log.i(tag, out);
            } catch (NoClassDefFoundError var4) {
                System.out.println(String.format("info -- %s -- %s", tag, out));
            }
        }

    }

    public static void warn(String tag, String message) {
        if (enabled) {
            String out = String.format("(%s) %s", getTimestamp(), message);

            try {
                Log.w(tag, out);
            } catch (NoClassDefFoundError var4) {
                System.out.println(String.format("warn -- %s -- %s", tag, out));
            }
        }

    }

    public static String getTimestamp() {
        long timestamp = System.currentTimeMillis();
        long day = TimeUnit.MILLISECONDS.toDays(timestamp);
        timestamp -= TimeUnit.DAYS.toMillis(day);
        long hour = TimeUnit.MILLISECONDS.toHours(timestamp);
        timestamp -= TimeUnit.HOURS.toMillis(hour);
        long minute = TimeUnit.MILLISECONDS.toMinutes(timestamp);
        timestamp -= TimeUnit.MINUTES.toMillis(minute);
        long second = TimeUnit.MILLISECONDS.toSeconds(timestamp);
        timestamp -= TimeUnit.SECONDS.toMillis(second);
        return String.format(Locale.getDefault(), "%d:%d:%d:%d.%d", day, hour, minute, second, timestamp);
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }
}
