package com.example.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat STORAGE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault());

    public static String formatForDisplay(String storedDate) {
        if (storedDate == null) return "";
        try {
            Date date = STORAGE_FORMAT.parse(storedDate);
            return date != null ? DISPLAY_FORMAT.format(date) : storedDate;
        } catch (ParseException e) {
            return storedDate;
        }
    }
}
