package com.walhalla.mtprotolist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.walhalla.ui.DLog;

import java.util.List;

public class IntentUtils {
//    private void info(Activity activity, String format) {
//        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(format));
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(format));
//        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        //intent.setPackage(packageInfo.packageName);
//        //org.telegram.ui.LaunchActivity
//        PackageManager pm = activity.getPackageManager();
//        List<ResolveInfo> appList = pm.queryIntentActivities(intent, 0);
//        if (appList.isEmpty()) {
//            mainCallback.makeInstaller();
//        } else {
//            for (ResolveInfo info : appList) {
//                DLog.d("{*} " + info.activityInfo.name);
//            }
//            activity.startActivity(intent);
//        }
//    }
}
