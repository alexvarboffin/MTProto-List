package com.walhalla.mtprotolist.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotolist.PicassoHelper;
import com.walhalla.mtprotolist.ProtoAdapterCallback;
import com.walhalla.mtprotolist.databinding.ItemGlypeBinding;
import com.walhalla.mtprotolist.webproxy.ProxyInfo;


public class GlypeViewHolder extends RecyclerView.ViewHolder {
    private final ItemGlypeBinding binding;
    private final ProtoAdapterCallback<ProxyInfo> callback;

    public GlypeViewHolder(ItemGlypeBinding binding, ProtoAdapterCallback<ProxyInfo> callback) {
        super(binding.getRoot());
        this.binding = binding;

        //Control button
        this.callback = callback;
    }

    @SuppressLint("SetTextI18n")
    public void bind(ProxyInfo data, boolean admin) {
        if (admin) {
            binding.actionDelete.setVisibility(View.VISIBLE);
            binding.actionDelete.setOnClickListener(v -> {
                callback.delete(data);
            });
        } else {
            binding.actionDelete.setVisibility(View.GONE);
        }
        binding.hostName.setText("" + data.ip);
        binding.description.setText("" + data.proxyUrl);
        binding.port.setText("" + data.type);

        PicassoHelper helper = PicassoHelper.getInstance(itemView.getContext());
        if (data.code == null || data.code.isEmpty()) {
            helper.loadEmptyImage(binding.personPhoto, itemView.getContext());
        } else {
            final String icon = String.format(Config.handler, data.code.toLowerCase());
            helper.rawCountryFlag(icon, binding.personPhoto);
            //DLog.d( "bind: " + icon);
        }
        //personPhoto.setImageResource(data.get(i).icon);

        itemView.setOnClickListener(v ->
                callback.categorySelected(getAdapterPosition(), data));
        binding.actionView.setOnClickListener(v -> {
            callback.viewProxy(getAdapterPosition(), data);
        });
        binding.actionShare.setOnClickListener(v -> {
            callback.shareProxy(getAdapterPosition(), data);
        });
        binding.actionQr.setOnClickListener(v -> {
            callback.qrProxyCode(getAdapterPosition(), data);
        });
        binding.actionConnect.setOnClickListener(v -> {
            callback.handleProxyIntent(getAdapterPosition(), data);
        });
        binding.hostName.setOnClickListener((v) -> {
            callback.copyClipboard(data.ip);
        });
        binding.port.setOnClickListener((v) -> {
            callback.copyPort(data);
        });
        binding.description.setOnClickListener((v) -> {
            callback.copyClipboard(data.proxyUrl);
        });
        binding.info.setOnClickListener(v -> {
            callback.info(data);
        });
    }
}