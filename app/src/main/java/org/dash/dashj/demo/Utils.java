package org.dash.dashj.demo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static PackageInfo packageInfo(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException x) {
            throw new RuntimeException(x);
        }
    }

    /**
     * Formats a given date+time value to an ISO 8601 string.
     *
     * @param dateTime value to format, as a Date
     */
    public static String dateTimeFormat(Date dateTime) {
        DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        iso8601.setTimeZone(UTC);
        return iso8601.format(dateTime);
    }
}
