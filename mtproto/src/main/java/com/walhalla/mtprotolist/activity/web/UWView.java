package com.walhalla.mtprotolist.activity.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

public class UWView extends android.webkit.WebView {

    public UWView(Context context) {
        super(context);
    }

    public UWView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UWView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public UWView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public UWView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }
}
