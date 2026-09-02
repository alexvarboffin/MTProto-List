package com.google.android.ads.nativetemplates.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.R;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAd;

public class SimpleViewHolder extends RecyclerView.ViewHolder {

    final TemplateView template;

    public SimpleViewHolder(@NonNull View itemView) {
        super(itemView);
        template = itemView.findViewById(R.id.my_template);
    }

    public void bind(NativeAd unifiedNativeAd){
        template.setNativeAd(unifiedNativeAd);
    }
}
