package com.walhalla.mtprotolist.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

import com.android.lib.CustomTabsBroadcastReceiver;
import com.walhalla.mtprotolist.R;


public class CustomTabUtils {

    public static CustomTabsIntent customWeb(Activity context) {
        //Configure the color of the address bar
        CustomTabColorSchemeParams defaultColors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(context.getResources().getColor(R.color.colorPrimaryDark))
                //.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark))
                .setNavigationBarDividerColor(Color.BLACK).setSecondaryToolbarColor(Color.BLACK).build();

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder
                //.setShowTitle(true)
                .setDefaultColorSchemeParams(defaultColors);

        //Configure a custom action button
        //24dp
        //@PandingIntent pandingIntent = new PendingI
        //@builder.setActionButton(icon, description, pendingIntent, tint);

        //Configure a custom menu
        //builder.addMenuItem(menuItemTitle, menuItemPendingIntent);

        //Android 12
        final int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        Intent intent = new Intent(context, CustomTabsBroadcastReceiver.class);
        intent.setAction(CustomTabsBroadcastReceiver.ACTION_COPY_URL);
        String label = "Copy link...";
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flag);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//            pendingIntent = PendingIntent.getBroadcast(
//                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        } else {
//            //PendingIntent.FLAG_UPDATE_CURRENT
//            pendingIntent = PendingIntent.getBroadcast(
//                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
        builder.addMenuItem(label, pendingIntent).build();
        CustomTabsIntent customTabsIntent = builder.build();
        //customTabsIntent.intent.setPackage("com.android.chrome"); // Указываем пакет Chrome


        return customTabsIntent;
    }
}
