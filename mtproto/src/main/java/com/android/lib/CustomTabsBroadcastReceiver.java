package com.android.lib;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CustomTabsBroadcastReceiver extends BroadcastReceiver {


    public static String ACTION_COPY_URL = "action.copy";
    public static final String ACTION_PRIVACY = "action.privacy";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_COPY_URL.equals(action)) {
                String url = intent.getDataString();
                Toast.makeText(context, "Copy link pressed. URL = " + url, Toast.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", url);
                clipboard.setPrimaryClip(clip);
            } else if (ACTION_PRIVACY.equals(action)) {
                //AboutBox.showPolicy(context);
            }
        }

    }
}