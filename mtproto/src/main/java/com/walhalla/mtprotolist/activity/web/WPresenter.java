package com.walhalla.mtprotolist.activity.web;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AlertDialog;

import com.walhalla.ui.BuildConfig;
import com.walhalla.ui.DLog;
import com.walhalla.webview.ChromeView;
import com.walhalla.webview.CustomWebViewClient;
import com.walhalla.webview.MyWebChromeClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class WPresenter implements MyWebChromeClient.Callback {

    private final Activity activity;
    //private final WebActivityInterface webActivity;


    //protected ActivityResultLauncher<Intent> requestSelectFileLauncher0;
    //protected ActivityResultLauncher<Intent> requestFileChooser;
    protected ValueCallback<Uri> mUploadMessage;
    protected ValueCallback<Uri[]> uploadMessage;
    private CustomWebViewClient var0;

    public WPresenter(Handler handler, Activity compatActivity/*, WebActivityInterface webActivity*/) {
        this.activity = compatActivity;
        //this.webActivity = webActivity;

//        requestSelectFileLauncher0 = compatActivity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            final int resultCode = result.getResultCode();
////                    if (resultCode == Activity.RESULT_OK) {
////                        Intent data = result.getData();
////                        defaultMarketRateCallback(data, code);
////                    } else if (code == Activity.RESULT_CANCELED) {
////                        Intent data = result.getData();
////                        defaultMarketRateCallback(data, code);
////                    } else {
////                        Intent data = result.getData();
////                        defaultMarketRateCallback(data, code);
////                    }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (uploadMessage == null) return;
//                if (result.getResultCode() == RESULT_OK) {
//                }// Файлы были успешно выбраны
//                Intent data = result.getData();
//                Uri[] selectedFiles = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
//                alert(result, selectedFiles, activity);
//                uploadMessage.onReceiveValue(selectedFiles);
//                uploadMessage = null;
//            }
//        });


//        requestFileChooser = compatActivity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            final int resultCode = result.getResultCode();
//            if (null == mUploadMessage) return;
//            Intent intent = result.getData();
//            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
//            // Use RESULT_OK only if you're implementing WebView inside an Activity
//            Uri result0 = intent == null || resultCode != RESULT_OK ? null : intent.getData();
//            mUploadMessage.onReceiveValue(result0);
//            mUploadMessage = null;
//        });
    }

    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> mUploadMessages;
    private Uri mCapturedImageURI = null;

    private void makeFileSelector21_x(UWView view) {

//        view.setWebChromeClient(@@new WebChromeClient() {
//            // For 3.0+ Devices (Start)
//            // onActivityResult attached before constructor
//            private void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//                openImageChooser(uploadMsg, acceptType);
//            }
//
//            // For Lollipop 5.0+ Devices
//            @Override
//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                openImageChooser(webView, filePathCallback, fileChooserParams);
//                return true;
//            }
//
//            // openFileChooser for Android < 3.0
//
//            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//                openFileChooser(uploadMsg, "");
//            }
//
//            //openFileChooser for other Android versions
//
//            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                openFileChooser(uploadMsg, acceptType);
//            }
//        });

        view.setWebChromeClient(new MyWebChromeClient(this));
    }

//    public void openImageChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
//        if (uploadMessage != null) {
//            uploadMessage.onReceiveValue(null);
//            uploadMessage = null;
//        }
//        uploadMessage = filePathCallback;
//        Intent intent = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            intent = fileChooserParams.createIntent();
//            if (fileChooserParams.getAcceptTypes() != null) {
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, fileChooserParams.getAcceptTypes());
//            }
//        }
//        try {
//            requestSelectFileLauncher0.launch(intent);
//            // activity.startActivityForResult(intent, REQUEST_SELECT_FILE);
//        } catch (ActivityNotFoundException e) {
//            uploadMessage = null;
//            //return false;
//        }
//    }

//    public void openImageChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//        mUploadMessage = uploadMsg;
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*"); //intent.setType("image/*");//"image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        //activity.startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
//        requestFileChooser.launch(Intent.createChooser(intent, "File Browser"));
//    }

    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    public void a123(ChromeView chromeView, UWView mView) {
        WebSettings a = mView.getSettings();
        a.setSupportZoom(false);
        a.setDefaultTextEncodingName("utf-8");
        a.setLoadWithOverviewMode(true);
        //a.setUseWideViewPort(true);

        //ERR_TOO_MANY_REDIRECTS
        //a.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //a.setAppCacheMaxSize( 100 * 1024 * 1024 ); // 100MB

        a.getLoadsImagesAutomatically();
        a.setGeolocationEnabled(true);
        a.setJavaScriptCanOpenWindowsAutomatically(true);
        a.setDomStorageEnabled(true);
        a.setBuiltInZoomControls(false);//!@@@@@@@@@@
        // Отключаем поддержку WebRTC
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            disableWebRTC(a);
        }
        a.setJavaScriptEnabled(true);
        a.setPluginState(WebSettings.PluginState.ON);
        a.setAllowFileAccess(true);
        a.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            a.setAllowFileAccessFromFileURLs(true);
            a.setAllowUniversalAccessFromFileURLs(true);
        }

        //"Mozilla/5.0 (Linux; Android 5.1.1; Nexus 7 Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.78 Safari/537.36 OPR/30.0.1856.93524"
        //System.getProperty("http.agent")
        setCustomUserAgent(a);
        //a.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/118.0");
        if (BuildConfig.DEBUG) {
            //@@@ mView.setBackgroundColor(Color.parseColor("#80770000"));
        }
        var0 = new CustomWebViewClient(mView, chromeView, activity);
        mView.setWebViewClient(var0);
        makeFileSelector21_x(mView);

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mView, true);
        }

//        __mView.addJavascriptInterface(
//                new MyJavascriptInterface(CompatActivity.this, __mView), "JSInterface");
//@@        mView.addJavascriptInterface(new MyJavascriptInterface(mView.getContext(), mView), "Client");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void disableWebRTC(WebSettings webSettings) {
        webSettings.setMediaPlaybackRequiresUserGesture(true); // Не автоматически воспроизводить медиа
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); // Запрещаем открывать окна
    }

    private void setCustomUserAgent(WebSettings a) {
        String agentString = a.getUserAgentString();
        String agentStringNew = agentString.replace("; wv)", ")");
        //String realFirefox = "Mozilla/5.0 (Android 14; Mobile; rv:122.0) Gecko/122.0 Firefox/122.0";
        a.setUserAgentString(agentStringNew);
    }

    //public static final int REQUEST_SELECT_FILE = 100;


//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (requestCode == REQUEST_SELECT_FILE) {
//                if (uploadMessage == null) return;
//                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
//                uploadMessage = null;
//            }
//        } else if (requestCode == FILECHOOSER_RESULTCODE) {
//            if (null == mUploadMessage) return;
//            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
//            mUploadMessage.onReceiveValue(result);
//            mUploadMessage = null;
//        }
//    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadMessages) {
                return;
            }
            if (null != mUploadMessage) {
                handleUploadMessage(requestCode, resultCode, data);
            } else if (mUploadMessages != null) {
                handleUploadMessages(requestCode, resultCode, data);
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void handleUploadMessage(final int requestCode, final int resultCode, final Intent data) {
        Uri result = null;
        try {
            if (resultCode != RESULT_OK) {
                result = null;
            } else {
                // retrieve from the private variable if the intent is null
                result = data == null ? mCapturedImageURI : data.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;

        // code for all versions except of Lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            result = null;

            try {
                if (resultCode != RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result = data == null ? mCapturedImageURI : data.getData();
                }
            } catch (Exception e) {
                Toast.makeText(activity, "activity :" + e, Toast.LENGTH_LONG).show();
            }
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
            }
            mUploadMessage = null;
        }

    } // end of code for all versions except of Lollipop


    private void handleUploadMessages(final int requestCode, final int resultCode, final Intent data) {
        Uri[] results = null;
        try {
            if (resultCode != RESULT_OK) {
                results = null;
            } else {
                if (data != null) {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                } else {
                    results = new Uri[]{mCapturedImageURI};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUploadMessages.onReceiveValue(results);
        mUploadMessages = null;
    }


    // TYPE OF LOAD URL

    public void loadUrlWithClearHistory(UWView mView, String s) {
        mView.stopLoading();
        mView.clearHistory();
        mView.loadUrl("about:blank");
        //mView.loadUrl("file:///android_asset/infAppPaused.html");
        var0.resetAllErrors();
        var0.setHomeUrl(s);
        mView.loadUrl(s);
    }


    public void loadUrlRequest(UWView mView, String url) {
        var0.setHomeUrl(url);
        String domain = extractDomain(url);
        if (!TextUtils.isEmpty(domain)) {
            var0.setCheckSameDomain();
            //var0.setHomeDomain(domain);
        }
        mView.loadUrl(url);
    }

    public static String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            DLog.handleException(e);
            return null;
        }
    }

    private void alert(ActivityResult result, Uri[] selectedFiles, Context context) {
        Intent intent = result.getData();
        StringBuilder sb = new StringBuilder();
        if (selectedFiles != null) {
            for (Uri uri : selectedFiles) {
                sb.append(uri.toString()).append("\n");
            }
        }
        if (intent != null) {
            sb.append(intent);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("debug" + ((selectedFiles == null) ? selectedFiles : selectedFiles.length))

                .setMessage(sb.toString()).setPositiveButton(context.getString(android.R.string.ok), (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.setCancelable(false).create();
        alert.show();
    }


    @Override
    public void onProgressChanged(int progress) {

    }

    private void openImageChooser() {
        try {
            //Toast.makeText(activity, "@@@", Toast.LENGTH_SHORT).show();
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FolderName");
            if (!imageStorageDir.exists()) {
                boolean b = imageStorageDir.mkdirs();
            }
            File file = new File(imageStorageDir + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            //i.setType("image/*");
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
            activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUploadMessage = uploadMsg;
        openImageChooser();
    }

    @Override
    public void openFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        mUploadMessages = filePathCallback;
        openImageChooser();
    }
}
