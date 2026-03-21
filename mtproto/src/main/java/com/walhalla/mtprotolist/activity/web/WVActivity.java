package com.walhalla.mtprotolist.activity.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.databinding.ActivityMyBinding;

import com.walhalla.ui.DLog;

import com.walhalla.ui.plugins.Launcher;
import com.walhalla.webview.ChromeView;
import com.walhalla.webview.ReceivedError;
import com.walhalla.webview.WVTools;


//import im.delight.android.webview.AdvancedWebView;

import static androidx.browser.browseractions.BrowserActionsIntent.KEY_TITLE;
import static androidx.browser.customtabs.CustomTabsService.KEY_URL;

import java.io.InputStream;

public class WVActivity extends AppCompatActivity
        implements ChromeView, UWVlayout.UWVlayoutCallback {

    private WPresenter var0;
    private boolean doubleBackToExitPressedOnce;
    private String url = ""; // or other values;
    private boolean isInject;


    public WPresenter getPresenter() {
        return var0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        var0.onActivityResult(requestCode, resultCode, data);
        // binding.webview.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void closeApplication() {
        finish();
    }


    //@@@@@@@

    @Override
    public void onPageStarted(String url) {
        hideSwipeRefreshing();
        if (isProgressEnabled()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setIndeterminate(true);
        }
    }

    private boolean isProgressEnabled() {
        return true;
    }

    @Override
    public void onPageFinished(/*WebView view, */String url) {
        if (!isInject) {
            injectCSS();
            isInject = true;
        }
        //getSupportActionBar().setSubtitle("@@"+url);

        hideSwipeRefreshing();
//        if (getSupportActionBar() != null) {
//            String m = view.getTitle();
//            if(TextUtils.isEmpty(m)){
//                getSupportActionBar().setSubtitle(m);
//            }
//        }
        if (isProgressEnabled()) {
            hideProgressBar();
        }
    }

    private void injectCSS() {
        try {
            InputStream inputStream = getAssets().open("style.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            binding.uwlayout.getWebView().loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }

    @Override
    public void webClientError(ReceivedError failure) {

    }

    private void hideSwipeRefreshing() {
    }


    @Override
    public void removeErrorPage() {
        switchViews(false);
    }

    @Override
    public void setErrorPage(ReceivedError receivedError) {
        switchViews(receivedError);
    }

    private void switchViews(ReceivedError receivedError) {
        binding.contentFake.setVisibility(View.GONE);
        binding.errorCode.setText(String.valueOf(receivedError.getDescription()));
        switchViews(true);
    }

    private void switchViews(boolean b) {
        if (b) {
            binding.contentFake.setVisibility(View.VISIBLE);
            binding.uwlayout.setVisibility(View.GONE);
            //getSupportActionBar().setTitle("...");
        } else {
            binding.contentFake.setVisibility(View.GONE);
            binding.uwlayout.setVisibility(View.VISIBLE);
            //getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public void openBrowser(Activity context, String url) {
        Launcher.openBrowser(context, url);
    }

//    @Override
//    public void openOauth2(Activity context, String url) {
//
//    }


    private ActivityMyBinding binding;
    private String title = "";

    public static Intent newInstance(Context context, String url, String title) {
        Intent intent = new Intent(context, WVActivity.class);
        Bundle b = new Bundle();
        b.putString(KEY_URL, url);
        b.putString(KEY_TITLE, title);
        intent.putExtras(b);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler(Looper.getMainLooper());
        var0 = new WPresenter(handler, this);

        binding = ActivityMyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.uwlayout.collapseMenu().setVisibility(View.GONE);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Bundle bundle = getIntent().getExtras();
//        binding.webview.setListener(this, this);
//        binding.webview.setMixedContentAllowed(false);


        if (bundle != null) {
            url = bundle.getString(KEY_URL);
            title = bundle.getString(KEY_TITLE);
            getPresenter().a123(this, binding.uwlayout.getWebView());
            switchViews(false);
            loadUrlRequest(url);
        }

//        if (BuildConfig.DEBUG) {
//            mBinding.adView.setVisibility(View.GONE);
//        } else {
//            binding.adView.loadAd(new AdRequest.Builder().build());
//        }
    }

    public void loadUrlRequest(String url) {
//        if (config.isProgressEnabled()) {
//            binding.progressBar.setVisibility(View.VISIBLE);
//            binding.progressBar.setIndeterminate(true);
//        }
        onPageStarted(url);
        getPresenter().loadUrlRequest(binding.uwlayout.getWebView(), url);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        binding.toolbar.setTitle("WebProxy (" + title + ")");
        //binding.toolbar.setSubtitle("" + url);
        //@        binding.webview.onResume();
//        if (binding.adView != null) {
//            binding.adView.resume();
//        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        //@       binding.webview.onPause();
//        if (binding.adView != null) {
//            binding.adView.pause();
//        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        binding.webview.onDestroy();
//        if (binding.adView != null) {
//            binding.adView.destroy();
//        }
        super.onDestroy();
    }


//    @Override
//    public void onBackPressed() {
//        if (!binding.webview.onBackPressed()) {
//            return;
//        }
//        // ...
//        super.onBackPressed();
//    }

//    @Override
//    public void onPageStarted(String url, Bitmap favicon) {
//        showProgressBar();
//    }
//
//    @Override
//    public void onPageFinished(String url) {
//        hideProgressBar();
//    }


    public void onPageError(int errorCode, String description, String failingUrl) {
        DLog.d(description + errorCode);
        hideProgressBar();
    }

//    @Override
//    public void onDownloadRequested(String url, String suggestedFilename, String mimeType,
//                                    long contentLength, String contentDisposition, String userAgent) {
//    }
//
//    @Override
//    public void onExternalPageRequest(String url) {
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    public void showProgressBar() {
        binding.progressBar.setIndeterminate(true);
    }


    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
        binding.progressBar.setIndeterminate(false);
    }

    @Override
    public void onBackPressed() {


//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }

        if (USE_HISTORY() && (binding.uwlayout.getWebView().canGoBack())) {
            //Toast.makeText(this, "##" + (mWebBackForwardList.getCurrentIndex()), Toast.LENGTH_SHORT).show();
            //historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
            binding.uwlayout.getWebView().goBack();
            return;
        } else {
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;
//        backPressedToast();
//        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1500);
    }

    private boolean USE_HISTORY() {
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.wv_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            String homeUrl = binding.uwlayout.getWebView().getOriginalUrl();
            if (homeUrl != null) {
                binding.uwlayout.getWebView().loadUrl(homeUrl);
            }
            return true;
        } else if (itemId == R.id.action_exit) {
            this.finish();
            return true;
        } else if (itemId == R.id.action_url_copy) {
            String url = binding.uwlayout.getWebView().getUrl();
            WVTools.copyToClipboard0(this, url);
            return true;
        } else if (itemId == R.id.action_wv_refresh) {
            binding.uwlayout.getWebView().reload();
            Toast.makeText(this, R.string.wwPageUpdated, Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_wv_clear_cookies) {
            clearCookies();
            return true;
        } else {
            return false;
        }
    }

    private void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager0 = CookieManager.getInstance();
            cookieManager0.removeAllCookie();
        }
    }
}
