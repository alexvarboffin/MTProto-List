package com.walhalla.webview_main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {


    private final MyWebChromeClient mCustomWebChromeClient;


    public MyWebChromeClient() {
        this.mCustomWebChromeClient = this;
    }

    // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, null);
    }

    // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooser(uploadMsg, acceptType, null);
    }

    // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileInput(uploadMsg, null, false);
    }

    // file upload callback (Android 5.0 (API level 21) -- current) (public method)
    @SuppressWarnings("all")
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT >= 21) {
            final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;

            openFileInput(null, filePathCallback, allowMultiple);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onProgressChanged(view, newProgress);
        } else {
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onReceivedTitle(view, title);
        } else {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onReceivedIcon(view, icon);
        } else {
            super.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
        } else {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onShowCustomView(view, callback);
        } else {
            super.onShowCustomView(view, callback);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (Build.VERSION.SDK_INT >= 14) {
            if (mCustomWebChromeClient != null) {
                mCustomWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
            } else {
                super.onShowCustomView(view, requestedOrientation, callback);
            }
        }
    }

    @Override
    public void onHideCustomView() {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onHideCustomView();
        } else {
            super.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        } else {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onRequestFocus(view);
        } else {
            super.onRequestFocus(view);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onCloseWindow(window);
        } else {
            super.onCloseWindow(window);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.onJsAlert(view, url, message, result);
        } else {
            return super.onJsAlert(view, url, message, result);
        }
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.onJsConfirm(view, url, message, result);
        } else {
            return super.onJsConfirm(view, url, message, result);
        }
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
            result) {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.onJsPrompt(view, url, message, defaultValue, result);
        } else {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.onJsBeforeUnload(view, url, message, result);
        } else {
            return super.onJsBeforeUnload(view, url, message, result);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback
            callback) {
        if (mGeolocationEnabled) {
            callback.invoke(origin, true, false);
        } else {
            if (mCustomWebChromeClient != null) {
                mCustomWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
            } else {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onGeolocationPermissionsHidePrompt();
        } else {
            super.onGeolocationPermissionsHidePrompt();
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPermissionRequest(PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mCustomWebChromeClient != null) {
                mCustomWebChromeClient.onPermissionRequest(request);
            } else {
                super.onPermissionRequest(request);
            }
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mCustomWebChromeClient != null) {
                mCustomWebChromeClient.onPermissionRequestCanceled(request);
            } else {
                super.onPermissionRequestCanceled(request);
            }
        }
    }

    @Override
    public boolean onJsTimeout() {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.onJsTimeout();
        } else {
            return super.onJsTimeout();
        }
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
        } else {
            super.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.onConsoleMessage(consoleMessage);
        } else {
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.getDefaultVideoPoster();
        } else {
            return super.getDefaultVideoPoster();
        }
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (mCustomWebChromeClient != null) {
            return mCustomWebChromeClient.getVideoLoadingProgressView();
        } else {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.getVisitedHistory(callback);
        } else {
            super.getVisitedHistory(callback);
        }
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (mCustomWebChromeClient != null) {
            mCustomWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        } else {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }


//            @Override
//            public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
//                if (mCustomWebChromeClient != null) {
//                    mCustomWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
//                }
//                else {
//                    super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
//                }
//            }

    }
}