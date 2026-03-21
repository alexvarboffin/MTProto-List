package com.walhalla.mtprotoloader.activity;

import static com.walhalla.mtprotolist.Config.KEY_TKT_LOADER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotoloader.R;
import com.walhalla.mtprotoloader.databinding.ActivityLinkBinding;
import com.walhalla.mtprotoloader.services.ClipboardMonitor;
import com.walhalla.ui.DLog;

import java.io.File;
//https://riptutorial.com/android/example/12833/multiple-domains-and-multiple-paths

public class LinkActivity extends AppCompatActivity {

    private ActivityLinkBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLinkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        StringBuilder keyValue = new StringBuilder();
        Bundle bundle = getIntent().getExtras();

        String textBundle = "";

        if (bundle != null) {
            textBundle = bundle.toString();
            for (String key : bundle.keySet()) {


                Object object = bundle.get(key);
                if (Intent.EXTRA_STREAM.equals(key)) {
                    Uri uri1 = (Uri) object;
                    File file = null;//create path from uri
                    if (uri1 != null) {
                        file = new File(uri1.getPath());
                    }
                    //final String[] split = file.getPath().split(":");//split the path.
                    //String filePath = split[1];//assign it to action keyValue(your choice).

                    if (object != null) {
                        Log.d("@@@", object.getClass().getSimpleName());
                        Log.d("@@@", "" + object);
                        Log.d("@@@", file.getAbsolutePath());

                        String path = getFilesDir().getAbsolutePath();
                        DLog.d("@@@" + path);
                    }
                }

                Log.d("@@@", object.getClass().getSimpleName());
                Log.d("@@@", "" + object);
                String path = getFilesDir().getAbsolutePath();
                DLog.d("@@@" + path);

                keyValue.append(" ").append(key).append("=>").append(bundle.get(key)).append(";");
            }
        }


        SpannableStringBuilder xxx = new SpannableStringBuilder(
                appLinkAction + "\n\n" + appLinkData + "\n\n" + keyValue
                        + "\n\n@@@" + textBundle + "@@@"
        );
        binding.textView.setMovementMethod(LinkMovementMethod.getInstance());
        binding.textView.setText(xxx.toString(), TextView.BufferType.SPANNABLE);
        setColor(binding.textView, appLinkAction, Color.MAGENTA);
        if (appLinkData != null) {
            setColor(binding.textView, appLinkData.toString(), Color.RED);
        }
        setColor(binding.textView, keyValue.toString(), Color.GREEN);
        setColor(binding.textView, textBundle, Color.RED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setColor(TextView view, String subtext, int color) {
        Spannable str = (Spannable) view.getText();
        int index = view.getText().toString().indexOf(subtext);
        str.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                DLog.d(subtext);
                Toast.makeText(LinkActivity.this, subtext, Toast.LENGTH_SHORT).show();
            }
        }, index, index + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(color), index, index + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new BackgroundColorSpan(Color.BLACK), index, index + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(KEY_TKT_LOADER, MODE_PRIVATE);
        prefs.edit().putBoolean(Config.KEY_CLIPBOARD_MONITOR, true).apply();
        Toast.makeText(this, "@@@", Toast.LENGTH_SHORT).show();
        ClipboardMonitor.startClipboardMonitor(this);
        //this.finish();
    }
}