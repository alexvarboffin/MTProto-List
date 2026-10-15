package com.walhalla.mtprotocompose.util

import android.text.format.DateUtils

fun formatRelativeUpdatedAt(epochMs: Long): String {
    if (epochMs <= 0L) return "—"
    return DateUtils.getRelativeTimeSpanString(
        epochMs,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE,
    ).toString()
}

fun formatUtcOffsetSeconds(offsetSeconds: Int?): String {
    if (offsetSeconds == null) return "—"
    val totalMinutes = offsetSeconds / 60
    val sign = if (totalMinutes >= 0) "+" else "-"
    val abs = kotlin.math.abs(totalMinutes)
    val hours = abs / 60
    val minutes = abs % 60
    return if (minutes == 0) {
        "UTC$sign$hours"
    } else {
        "UTC$sign$hours:${minutes.toString().padStart(2, '0')}"
    }
}
