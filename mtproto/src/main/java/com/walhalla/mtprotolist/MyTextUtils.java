package com.walhalla.mtprotolist;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.walhalla.ui.plugins.Module_U;


public class MyTextUtils {
//    public class CustomWebViewClient extends com.google.androidbrowserhelper.trusted.WebViewFallbackActivity {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            Uri uri = request.getUrl();
//            // Ваша логика обработки URL здесь
//            return super.shouldOverrideUrlLoading(view, request);
//        }
//    }
    public static void changeString(Activity context, TextView textView) {

        String text = context.getString(R.string.msg_no_notes1);
//        textView.setOnClickListener(v -> {
//        });

//        int start = 46;
//        SpannableString spannableString = new SpannableString(text);
//        spannableString.setSpan(
//                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent)),
//                start, text.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        );
//        textView.setText(spannableString);

        SpannableString spannableString = new SpannableString(text);

        String mm = context.getString(R.string.part1).toLowerCase();
        String bb = context.getString(R.string.part2).toLowerCase();
        String lower = text.toLowerCase();
        int startQuote1 = lower.indexOf(mm);
        int endQuote1 = startQuote1 + mm.length() - 1;//text.indexOf("@", startQuote1 + 1);
        int startQuote2 = lower.indexOf(bb);//text.indexOf(bb, endQuote1 + 1);
        int endQuote2 = startQuote2 + bb.length() - 1;//text.indexOf("@", startQuote2 + 1);

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Module_U.actionWirelessSettings(context);
            }
        }, startQuote1, endQuote1 + 1, 0);

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Module_U.actionWirelessSettings(context);
                }
            }
        }, startQuote2, endQuote2 + 1, 0);
        // Устанавливаем SpannableString в TextView
        textView.setText(spannableString);
        // Для обработки кликов в TextView нужно добавить следующий код:
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
