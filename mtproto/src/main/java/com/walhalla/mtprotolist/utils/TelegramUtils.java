package com.walhalla.mtprotolist.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.activity.HomeActivity;
import com.walhalla.mtprotolist.entity.MtprotoProxy;
import com.walhalla.ui.UConst;

import java.util.HashMap;
import java.util.Map;

public class TelegramUtils {
    public static void makeInstaller(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("Telegram", "org.telegram.messenger");
        map.put("Telegram X", "org.thunderdog.challegram");
        map.put("BGram", "org.telegram.BifToGram");
        map.put("Vidogram", "org.vidogram.messenger");
        map.put("Plus Messenger", "org.telegram.plus");
        map.put("Graph Messenger", "ir.ilmili.telegraph");
        map.put("Pentagram", "top.pentagram");
        String[] sites = map.keySet().toArray(new String[0]);
        String[] values = map.values().toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.err_telegram_client_not_unstalled);// add a list
        builder.setItems(sites, (dialog, which) -> {
            final String packageName = values[which];
            String raw = UConst.GOOGLE_PLAY_CONSTANT + packageName;
            Uri uri = Uri.parse(raw);
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (android.content.ActivityNotFoundException anfe0) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //Toast.makeText(this, R.string.couldnt_rate, Toast.LENGTH_LONG).show();
            }
        });// create and show the alert dialog
        AlertDialog dialog = builder.setPositiveButton(
                android.R.string.cancel, null
        ).create();
        dialog.show();
    }

    public static void handleProxy(Context context, MtprotoProxy data) {
        try {
            String format;
//            try {
//                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
//                intent.setData(Uri.parse("package:" + getContext().getApplicationContext().getPackageName()));
//                startActivity(intent);
//            } catch (Exception e) {
//                //FileLog.m4025e(e);
//            }
            format = String.format(Config.PROXY_HANDLER_TG, data.host.toLowerCase().trim(), data.port.trim(),
                    data.secret.trim()
            );
            //format = String.format(PROXY_HANDLER, data.host, data.port, data.secret);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(format));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);//info(getActivity(),format);

        } catch (android.content.ActivityNotFoundException anfe) {
            Toast.makeText(context, R.string.err_telegram_client_not_unstalled, Toast.LENGTH_LONG).show();

//            format = String.format(PROXY_HANDLER, data.getHost(), data.getPort(), data.getSecret());
//
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(format)));
//            } catch (Exception e) {
//                Toast.makeText(getContext(),
//                        "Telegram Client not found...", Toast.LENGTH_LONG).show();
//            }
            makeInstaller(context);
        }
    }
}
