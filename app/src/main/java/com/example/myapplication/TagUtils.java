package com.example.myapplication;

import android.text.TextUtils;

import java.util.LinkedHashSet;
import java.util.Locale;

public class TagUtils {

    public static String normalizeTag(String tag) {
        if (tag == null) return "";
        String trimmed = tag.trim();
        if (trimmed.isEmpty()) return "";
        String lower = trimmed.toLowerCase(Locale.getDefault());
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    public static String normalizeTagsString(String tagsCsv) {
        if (tagsCsv == null || tagsCsv.trim().isEmpty()) return "";
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String tag : tagsCsv.split(",")) {
            String norm = normalizeTag(tag);
            if (!norm.isEmpty()) {
                normalized.add(norm);
            }
        }
        return TextUtils.join(", ", normalized);
    }
}
