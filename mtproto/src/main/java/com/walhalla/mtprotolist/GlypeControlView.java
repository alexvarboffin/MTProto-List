package com.walhalla.mtprotolist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.walhalla.mtprotolist.databinding.GlypeControlBinding;

public class GlypeControlView extends FrameLayout {

    private GlypeControlBinding binding;

    public GlypeControlView(Context context) {
        super(context);
        init(context);
    }

    public GlypeControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GlypeControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = GlypeControlBinding.inflate(inflater, this, true);
    }

    public String getUrl() {
        return binding.input.getText().toString();
    }

    public boolean isEncodeUrlChecked() {
        return binding.encodeURL.isChecked();
    }

    public boolean isEncodePageChecked() {
        return binding.encodePage.isChecked();
    }

    public boolean isAllowCookiesChecked() {
        return binding.allowCookies.isChecked();
    }

    public boolean isStripTitleChecked() {
        return binding.stripTitle.isChecked();
    }

    public boolean isStripJSChecked() {
        return binding.stripJS.isChecked();
    }

    public boolean isStripObjectsChecked() {
        return binding.stripObjects.isChecked();
    }
}

