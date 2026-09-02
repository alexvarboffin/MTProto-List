package com.google.android.ads.nativetemplates.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.R;
import com.google.android.ads.nativetemplates.viewholder.ErrorViewHolder;
import com.google.android.ads.nativetemplates.viewholder.MyAdViewHolder;
import com.google.android.ads.nativetemplates.viewholder.SimpleViewHolder;
import com.google.android.ads.nativetemplates.viewholder.TemplateMedium;
import com.google.android.ads.nativetemplates.viewholder.TemplateSmall;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.List;


public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_DEFAULT_HOLDER = 1111;


    public static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1112;
    public static final int NATIVE_MEDIUM_TEMPLATE = 1114;
    public static final int NATIVE_SMALL_TEMPLATE = 1115;
    public static final int NATIVE_TYPE_SIMPLE = 1116;
    private final int ADS_BANNER_TYPE;

    protected final List<Object> data;
    private final Context context;

    public void swap(List<Object> message) {
        this.data.clear();
        this.data.addAll(message);
        this.notifyDataSetChanged();
    }

    public void add(int index, NativeAd o) {
        this.data.add(index, o);
        this.notifyDataSetChanged();
    }


    public BaseRecyclerViewAdapter(Context context, List<Object> objects, int ADS_BANNER_TYPE) {
        this.context = context;
        this.data = objects;
        this.ADS_BANNER_TYPE = ADS_BANNER_TYPE;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == UNIFIED_NATIVE_AD_VIEW_TYPE) {

//                View view1 = LayoutInflater.from(
//                        parent.getContext()).inflate(R.layout.ad_unified,
//                        parent, false);
//                return new UnifiedNativeAdViewHolder(view1);

            View view1 =
                    inflater.inflate(
                            R.layout.gnt_ad_unified,
                            parent, false);
            return new MyAdViewHolder(view1);
        }
        if (viewType == NATIVE_MEDIUM_TEMPLATE) {
            view =
                    inflater.inflate(
                            R.layout.gnt_medium_template_view,
                            parent, false);

            return new TemplateMedium(view);
        }
        if (viewType == NATIVE_SMALL_TEMPLATE) {
            View v3 =
                    inflater.inflate(R.layout.gnt_small_template_view, parent, false);
            return new TemplateSmall(v3);
        }
        if (viewType == NATIVE_TYPE_SIMPLE) {
            view =
                    inflater.inflate(
                            R.layout.gnt_simple_ad,
                            parent, false);

            return new SimpleViewHolder(view);
        }

        //TYPE_DEFAULT_HOLDER
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.default_list_item, parent, false);
        return new ErrorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(holder.getAdapterPosition());// getItemViewType(position)
        final Object obj = data.get(holder.getAdapterPosition());

        if (viewType == UNIFIED_NATIVE_AD_VIEW_TYPE) {
            NativeAd nativeAd = (NativeAd) obj;
            //UnifiedNativeAdViewHolder) holder).getAdView()
            MyAdViewHolder holder0 = (MyAdViewHolder) holder;
            holder0.bind(nativeAd);
        } else if (viewType == NATIVE_MEDIUM_TEMPLATE) {
            NativeAd nativeAd2 = (NativeAd) obj;
            TemplateMedium mm = (TemplateMedium) holder;
            mm.bind(nativeAd2);
        } else if (viewType == NATIVE_SMALL_TEMPLATE) {
            NativeAd ad = (NativeAd) obj;
            TemplateSmall viewHolder = (TemplateSmall) holder;
            viewHolder.bind(ad);
        } else if (viewType == NATIVE_TYPE_SIMPLE) {
            NativeAd nativeAd1 = (NativeAd) obj;
            SimpleViewHolder ss = (SimpleViewHolder) holder;
            ss.bind(nativeAd1);
        } else if (TYPE_DEFAULT_HOLDER == viewType) {
            //none
        } else {
            //TYPE_DEFAULT_HOLDER
            //none
        }
    }


    @Override
    public int getItemViewType(int position) {
        Object item = data.get(position);
        if (item instanceof NativeAd) { //UnifiedNativeAd ==> NativeAd
            return ADS_BANNER_TYPE;
        }
        return TYPE_DEFAULT_HOLDER;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}