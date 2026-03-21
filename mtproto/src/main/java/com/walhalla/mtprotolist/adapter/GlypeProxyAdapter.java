package com.walhalla.mtprotolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.mtprotolist.ProtoAdapterCallback;
import com.walhalla.mtprotolist.databinding.DefaultListItemBinding;
import com.walhalla.mtprotolist.databinding.ItemGlypeBinding;
import com.walhalla.mtprotolist.databinding.ProxyItemBinding;

import com.walhalla.mtprotolist.viewholder.GlypeViewHolder;
import com.walhalla.mtprotolist.webproxy.ProxyInfo;

import java.util.List;

public class GlypeProxyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> data;
    private final Context context;

    private static final int TYPE_PROTO = 1110;
    private final ProtoAdapterCallback<ProxyInfo> callback;
    private final boolean admin;


    @Override
    public int getItemViewType(int position) {
        Object item = data.get(position);
        if (item instanceof ProxyInfo) {
            return TYPE_PROTO;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public GlypeProxyAdapter(Context context, ProtoAdapterCallback<ProxyInfo> callback, List<Object> objects, boolean admin) {
        this.context = context;
        this.data = objects;
        this.callback = callback;
        this.admin = admin;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (TYPE_PROTO == viewType) {
            @NonNull ItemGlypeBinding binding = ItemGlypeBinding.inflate(inflater, parent, false);
            return new GlypeViewHolder(binding, callback);
        }
        //TYPE_DEFAULT_HOLDER
        @NonNull DefaultListItemBinding view0 = DefaultListItemBinding.inflate(inflater, parent, false);
        return new ErrorViewHolder(view0);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(holder.getAdapterPosition());// getItemViewType(position)
        final Object obj = data.get(holder.getAdapterPosition());

        if (TYPE_PROTO == viewType) {
//                MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
//                MenuItem menuItem = (MenuItem) mRecyclerViewItems.get(position);
//
//                // Get the menu item image resource ID.
//                String imageName = menuItem.getImageName();
//                int imageResID = mContext.getResources().getIdentifier(imageName, "drawable",
//                        mContext.getPackageName());
//
//                // Add the menu item details to the menu item view.
//                menuItemHolder.menuItemImage.setImageResource(imageResID);
//                menuItemHolder.menuItemName.setText(menuItem.getName());
//                menuItemHolder.menuItemPrice.setText(menuItem.getPrice());
//                menuItemHolder.menuItemCategory.setText(menuItem.getCategory());
//                menuItemHolder.menuItemDescription.setText(menuItem.getDescription());
            final ProxyInfo info = (ProxyInfo) obj;
            ((GlypeViewHolder) holder).bind(info, admin);
        } else {
            //((ErrorViewHolder) holder).bind(proxy);
        }
    }

    public void swap(List<Object> message) {
        this.data.clear();
        this.data.addAll(message);
        this.notifyDataSetChanged();
    }

    public void swap0(List<ProxyInfo> proxies) {
        this.data.clear();
        this.data.addAll(proxies);
        this.notifyDataSetChanged();
    }

    private static class ErrorViewHolder extends RecyclerView.ViewHolder {
        private final DefaultListItemBinding binding;

        public ErrorViewHolder(DefaultListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
