package com.walhalla.mtprotolist

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.walhalla.ui.plugins.Module_U.actionWirelessSettings
import java.util.Locale

object MyTextUtils {
    //    public class CustomWebViewClient extends com.google.androidbrowserhelper.trusted.WebViewFallbackActivity {
    //        @Override
    //        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
    //            Uri uri = request.getUrl();
    //            // Ваша логика обработки URL здесь
    //            return super.shouldOverrideUrlLoading(view, request);
    //        }
    //    }
    @JvmStatic
    fun changeString(context: Activity, textView: TextView) {
        val text = context.getString(R.string.msg_no_notes1)

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
        val spannableString = SpannableString(text)

        val mm = context.getString(R.string.part1).lowercase(Locale.getDefault())
        val bb = context.getString(R.string.part2).lowercase(Locale.getDefault())
        val lower = text.lowercase(Locale.getDefault())
        val startQuote1 = lower.indexOf(mm)
        val endQuote1 = startQuote1 + mm.length - 1 //text.indexOf("@", startQuote1 + 1);
        val startQuote2 = lower.indexOf(bb) //text.indexOf(bb, endQuote1 + 1);
        val endQuote2 = startQuote2 + bb.length - 1 //text.indexOf("@", startQuote2 + 1);

        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                actionWirelessSettings(context)
            }
        }, startQuote1, endQuote1 + 1, 0)

        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                try {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.setComponent(
                        ComponentName(
                            "com.android.settings",
                            "com.android.settings.Settings\$DataUsageSummaryActivity"
                        )
                    )
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    actionWirelessSettings(context)
                }
            }
        }, startQuote2, endQuote2 + 1, 0)
        // Устанавливаем SpannableString в TextView
        textView.setText(spannableString)
        // Для обработки кликов в TextView нужно добавить следующий код:
        textView.setMovementMethod(LinkMovementMethod.getInstance())
    }
}
