package com.walhalla.mtprotolist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.walhalla.ui.DLog.d
import androidx.core.net.toUri

class IntentUtils {
    private fun info(activity: Activity, format: String?) {
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(format));
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = format.toUri()
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setPackage(packageInfo.packageName);
        //org.telegram.ui.LaunchActivity
        val pm = activity.packageManager
        val appList = pm.queryIntentActivities(intent, 0)
        if (appList.isEmpty()) {
            //mainCallback.makeInstaller();
        } else {
            for (info in appList) {
                d("{*} " + info.activityInfo.name)
            }
            activity.startActivity(intent)
        }
    }
}
