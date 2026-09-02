package com.walhalla.mtprotolist.activity.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.browseractions.BrowserActionsIntent
import androidx.browser.customtabs.CustomTabsService
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.activity.web.UWVlayout.UWVlayoutCallback
import com.walhalla.mtprotolist.databinding.ActivityMyBinding
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.plugins.Launcher.openBrowser
import com.walhalla.webview.ChromeView
import com.walhalla.webview.ReceivedError
import com.walhalla.webview.WVTools.copyToClipboard0

//import im.delight.android.webview.AdvancedWebView;
class WVActivity : AppCompatActivity(), ChromeView, UWVlayoutCallback {
    private var var0: WPresenter? = null
    private var doubleBackToExitPressedOnce = false
    private var url: String? = "" // or other values;
    private var isInject = false


    val presenter: WPresenter
        get() = var0!!

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var0!!.onActivityResult(requestCode, resultCode, data)
        // binding.webview.onActivityResult(requestCode, resultCode, intent);
    }

    override fun closeApplication() {
        finish()
    }


    //@@@@@@@
    override fun onPageStarted(url: String?) {
        hideSwipeRefreshing()
        if (this.isProgressEnabled) {
            binding!!.progressBar.visibility = View.VISIBLE
            binding!!.progressBar.isIndeterminate = true
        }
    }

    private val isProgressEnabled: Boolean
        get() = true

    override fun onPageFinished( /*WebView view, */
                                 url: String?
    ) {
        if (!isInject) {
            injectCSS()
            isInject = true
        }

        //getSupportActionBar().setSubtitle("@@"+url);
        hideSwipeRefreshing()
        //        if (getSupportActionBar() != null) {
//            String m = view.getTitle();
//            if(TextUtils.isEmpty(m)){
//                getSupportActionBar().setSubtitle(m);
//            }
//        }
        if (this.isProgressEnabled) {
            hideProgressBar()
        }
    }

    private fun injectCSS() {
        try {
            val inputStream = assets.open("style.css")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
            binding!!.uwlayout.webView.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +  // Tell the browser to BASE64-decode the string into your script !!!
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style)" +
                        "})()"
            )
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun webClientError(failure: ReceivedError) {
    }

    private fun hideSwipeRefreshing() {
    }


    override fun removeErrorPage() {
        switchViews(false)
    }

    override fun setErrorPage(receivedError: ReceivedError) {
        switchViews(receivedError)
    }

    private fun switchViews(receivedError: ReceivedError) {
        binding!!.contentFake.setVisibility(View.GONE)
        binding!!.errorCode.setText(receivedError.description.toString())
        switchViews(true)
    }

    private fun switchViews(b: Boolean) {
        if (b) {
            binding!!.contentFake.setVisibility(View.VISIBLE)
            binding!!.uwlayout.setVisibility(View.GONE)
            //getSupportActionBar().setTitle("...");
        } else {
            binding!!.contentFake.setVisibility(View.GONE)
            binding!!.uwlayout.setVisibility(View.VISIBLE)
            //getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    override fun openBrowser(url: String) {
        openBrowser(this, url)
    }


    //    @Override
    //    public void openOauth2(Activity context, String url) {
    //
    //    }
    private var binding: ActivityMyBinding? = null
    private var title: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val handler = Handler(Looper.getMainLooper())
        var0 = WPresenter(handler, this)

        binding = ActivityMyBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())
        binding!!.uwlayout.collapseMenu().setVisibility(View.GONE)

        setSupportActionBar(binding!!.toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        val bundle = getIntent().getExtras()


        //        binding.webview.setListener(this, this);
//        binding.webview.setMixedContentAllowed(false);
        if (bundle != null) {
            url = bundle.getString(CustomTabsService.KEY_URL)
            title = bundle.getString(BrowserActionsIntent.KEY_TITLE)
            this.presenter.a123(this, binding!!.uwlayout.webView)
            switchViews(false)
            loadUrlRequest(url)
        }

        //        if (BuildConfig.DEBUG) {
//            mBinding.adView.setVisibility(View.GONE);
//        } else {
//            binding.adView.loadAd(new AdRequest.Builder().build());
//        }
    }

    fun loadUrlRequest(url: String?) {
//        if (config.isProgressEnabled()) {
//            binding.progressBar.setVisibility(View.VISIBLE);
//            binding.progressBar.setIndeterminate(true);
//        }
        onPageStarted(url)
        this.presenter.loadUrlRequest(binding!!.uwlayout.webView, url)
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        binding!!.toolbar.setTitle(getString(R.string.webview_title, title))
        //binding.toolbar.setSubtitle("" + url);
        //@        binding.webview.onResume();
//        if (binding.adView != null) {
//            binding.adView.resume();
//        }
    }

    @SuppressLint("NewApi")
    override fun onPause() {
        //@       binding.webview.onPause();
//        if (binding.adView != null) {
//            binding.adView.pause();
//        }
        super.onPause()
    }

    override fun onDestroy() {
//        binding.webview.onDestroy();
//        if (binding.adView != null) {
//            binding.adView.destroy();
//        }
        super.onDestroy()
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
    fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        d(description + errorCode)
        hideProgressBar()
    }

    //    @Override
    //    public void onDownloadRequested(String url, String suggestedFilename, String mimeType,
    //                                    long contentLength, String contentDisposition, String userAgent) {
    //    }
    //
    //    @Override
    //    public void onExternalPageRequest(String url) {
    //    }
    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    fun showProgressBar() {
        binding!!.progressBar.setIndeterminate(true)
    }


    fun hideProgressBar() {
        binding!!.progressBar.setVisibility(View.GONE)
        binding!!.progressBar.setIndeterminate(false)
    }

    override fun onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }


        if (USE_HISTORY() && (binding!!.uwlayout.webView.canGoBack())) {
            //Toast.makeText(this, "##" + (mWebBackForwardList.getCurrentIndex()), Toast.LENGTH_SHORT).show();
            //historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
            binding!!.uwlayout.webView.goBack()
            return
        } else {
            super.onBackPressed()
        }

        this.doubleBackToExitPressedOnce = true
        //        backPressedToast();
//        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1500);
    }

    private fun USE_HISTORY(): Boolean {
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.wv_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.getItemId()
        if (itemId == R.id.action_home) {
            val homeUrl = binding!!.uwlayout.webView.getOriginalUrl()
            if (homeUrl != null) {
                binding!!.uwlayout.webView.loadUrl(homeUrl)
            }
            return true
        } else if (itemId == R.id.action_exit) {
            this.finish()
            return true
        } else if (itemId == R.id.action_url_copy) {
            val url = binding!!.uwlayout.webView.url
            url?.let { copyToClipboard0(this, it) }
            return true
        } else if (itemId == R.id.action_wv_refresh) {
            binding!!.uwlayout.webView.reload()
            Toast.makeText(this, R.string.wwPageUpdated, Toast.LENGTH_SHORT).show()
            return true
        } else if (itemId == R.id.action_wv_clear_cookies) {
            clearCookies()
            return true
        } else {
            return false
        }
    }

    private fun clearCookies() {
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
        } else {
            CookieSyncManager.createInstance(this)
            val cookieManager0 = CookieManager.getInstance()
            cookieManager0.removeAllCookie()
        }
    }

    companion object {
        fun newInstance(context: Context?, url: String?, title: String?): Intent {
            val intent = Intent(context, WVActivity::class.java)
            val b = Bundle()
            b.putString(CustomTabsService.KEY_URL, url)
            b.putString(BrowserActionsIntent.KEY_TITLE, title)
            intent.putExtras(b)
            return intent
        }
    }
}
