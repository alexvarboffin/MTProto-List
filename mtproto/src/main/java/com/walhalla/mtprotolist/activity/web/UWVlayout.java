package com.walhalla.mtprotolist.activity.web;

import static com.walhalla.webview.WVTools.copyToClipboard0;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.databinding.CustomWebviewLayoutBinding;
import com.walhalla.webview.WVTools;

public class UWVlayout extends RelativeLayout {

    public void setCallback(UWVlayoutCallback callback) {
        this.callback = callback;
    }

    UWVlayoutCallback callback;




    public interface UWVlayoutCallback {

        void closeApplication();
    }

    private CustomWebviewLayoutBinding binding;

    public UWVlayout(Context context) {
        super(context);
        init(context);
    }

    public UWVlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UWVlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = CustomWebviewLayoutBinding.inflate(inflater, this, true);
        binding.buttonMenu.setOnClickListener(v -> showPopupMenu(context, v));
    }

    private void showPopupMenu(Context context, View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.wv_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_home) {
                String homeUrl = getWebView().getOriginalUrl();
                getWebView().loadUrl(homeUrl);
                return true;
            } else if (itemId == R.id.action_exit) {
                if (callback != null) {
                    callback.closeApplication();
                }
                return true;
            } else if (itemId == R.id.action_url_copy) {
                String url = getWebView().getUrl();
                copyToClipboard0(context, url);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    public UWView getWebView() {
        return binding.inner00;
    }

    public ImageView collapseMenu(){
        return binding.buttonMenu;
    }
}
