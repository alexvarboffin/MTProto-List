package com.walhalla.build;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DailyVersionCodeTest {
    @Test
    public void october30_2026_defaultsToBuild1() {
        DailyVersionCode.Parts parts = DailyVersionCode.compute(
                utc(2026, Calendar.OCTOBER, 30, 15),
                null,
                TimeZone.getTimeZone("UTC"));
        assertEquals(261030, parts.datePart);
        assertEquals(1, parts.buildOfDay);
        assertEquals(26103001, parts.versionCode);
    }

    @Test
    public void explicitBuildNumberIsAppended() {
        DailyVersionCode.Parts parts = DailyVersionCode.compute(
                utc(2026, Calendar.OCTOBER, 27, 8),
                "2",
                TimeZone.getTimeZone("UTC"));
        assertEquals(261027, parts.datePart);
        assertEquals(26102702, parts.versionCode);
    }

    @Test
    public void zeroIsAValidBuildNumber() {
        DailyVersionCode.Parts parts = DailyVersionCode.compute(
                utc(2026, Calendar.JANUARY, 1, 0),
                "0",
                TimeZone.getTimeZone("UTC"));
        assertEquals(260101, parts.datePart);
        assertEquals(26010100, parts.versionCode);
    }

    @Test
    public void invalidBuildNumberFallsBackToOne() {
        assertEquals(1, DailyVersionCode.parseBuildOfDay(null));
        assertEquals(1, DailyVersionCode.parseBuildOfDay(""));
        assertEquals(1, DailyVersionCode.parseBuildOfDay("abc"));
        assertEquals(1, DailyVersionCode.parseBuildOfDay("-3"));
        assertEquals(4, DailyVersionCode.parseBuildOfDay(" 4 "));
    }

    @Test
    public void propertyWinsOverEnv() {
        DailyVersionCode.Parts parts = DailyVersionCode.resolve("7", "3");
        assertEquals(7, parts.buildOfDay);
        assertEquals(parts.datePart * 100 + 7, parts.versionCode);
    }

    @Test
    public void blankPropertyFallsThroughToEnv() {
        DailyVersionCode.Parts parts = DailyVersionCode.resolve("  ", "5");
        assertEquals(5, parts.buildOfDay);
    }

    private static java.util.Date utc(int year, int month, int day, int hour) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(year, month, day, hour, 0, 0);
        return calendar.getTime();
    }
}
