package com.walhalla.build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * versionCode = yyMMdd * 100 + build number for that calendar day.
 * The day uses the build machine timezone, matching the previous yyMMdd scheme.
 * Build number: Gradle property {@code dailyBuildNumber}, otherwise env
 * {@code DAILY_BUILD_NUMBER}, otherwise 1. A non-numeric or negative value falls back to 1.
 */
public final class DailyVersionCode {
    private DailyVersionCode() {
    }

    public static final class Parts {
        public final int datePart;
        public final int buildOfDay;
        public final int versionCode;

        public Parts(int datePart, int buildOfDay) {
            this.datePart = datePart;
            this.buildOfDay = buildOfDay;
            this.versionCode = Math.addExact(Math.multiplyExact(datePart, 100), buildOfDay);
        }
    }

    public static Parts resolve(String propertyValue, String envValue) {
        String raw = firstNonBlank(propertyValue);
        if (raw == null) {
            raw = firstNonBlank(envValue);
        }
        return compute(new Date(), raw, TimeZone.getDefault());
    }

    public static Parts compute(Date now, String buildNumberRaw, TimeZone zone) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd", Locale.US);
        format.setTimeZone(zone == null ? TimeZone.getDefault() : zone);
        int datePart = Integer.parseInt(format.format(now));
        return new Parts(datePart, parseBuildOfDay(buildNumberRaw));
    }

    static int parseBuildOfDay(String raw) {
        if (raw == null) {
            return 1;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return 1;
        }
        try {
            int parsed = Integer.parseInt(trimmed);
            return parsed >= 0 ? parsed : 1;
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    private static String firstNonBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
