package com.walhalla.mtprotocompose.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.startActivitySafely(intent: Intent) {
    val activity = findActivity()
    if (activity != null) {
        activity.startActivity(intent)
    } else {
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
