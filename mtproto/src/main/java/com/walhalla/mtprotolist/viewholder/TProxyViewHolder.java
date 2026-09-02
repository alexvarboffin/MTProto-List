package com.walhalla.mtprotolist.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotolist.PicassoHelper;
import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.entity.MtprotoProxy;
import com.walhalla.mtprotolist.ProtoAdapterCallback;
import com.walhalla.mtprotolist.databinding.ProxyItemBinding;

import java.util.Set;


public class TProxyViewHolder extends RecyclerView.ViewHolder {
    
    public final ProxyItemBinding binding;
    private final ProtoAdapterCallback<MtprotoProxy> callback;
    private final Set<Integer> unlockedItems;
    

    public TProxyViewHolder(ProxyItemBinding binding, ProtoAdapterCallback<MtprotoProxy> callback, Set<Integer> unlockedItems) {
        super(binding.getRoot());
        this.binding = binding;

        //Control button
        this.callback = callback;
        this.unlockedItems = unlockedItems;
    }

    @SuppressLint("SetTextI18n")
    public void bind(MtprotoProxy data, boolean admin) {
        if (admin) {
            binding.actionDelete.setVisibility(View.VISIBLE);
            binding.actionDelete.setOnClickListener(v -> {
                callback.delete(data);
            });
        } else {
            binding.actionDelete.setVisibility(View.GONE);
        }
        binding.hostName.setText("" + data.host);
        binding.description.setText("" + data.secret);
        binding.port.setText("" + data.port);


        PicassoHelper helper = PicassoHelper.getInstance(itemView.getContext());
        if (data.code == null || data.code.isEmpty()) {
            helper.loadEmptyImage(binding.personPhoto, itemView.getContext());
        } else {
            final String icon = String.format(Config.ASSET_HANDLER, data.code.toLowerCase());
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
            callback.copyClipboard(data.host);
        });
        binding.port.setOnClickListener((v) -> {
            callback.copyClipboard(data.port);
        });
        binding.description.setOnClickListener((v) -> {
            callback.copyClipboard(data.secret);
        });

        binding.info.setOnClickListener(v -> {
            callback.proxyInfo(data);
        });

        if (isItemUnlocked(getAdapterPosition())) {
            binding.ivLock.setImageResource(R.drawable.ic_lock);
            binding.ivLock.setVisibility(View.VISIBLE);
            binding.ivLock.setOnClickListener(v -> callback.handleProxyIntent(getAdapterPosition(), data));
        } else {
            binding.ivLock.setVisibility(View.GONE);
        }
    }

    public boolean isItemUnlocked(int position) {
        return unlockedItems.contains(position);
    }
}