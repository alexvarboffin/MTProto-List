package com.walhalla.mtprotolist.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotolist.MyTextUtils;
import com.walhalla.mtprotolist.QRCodeDialog;
import com.walhalla.mtprotolist.R;

import com.walhalla.mtprotolist.ProtoAdapterCallback;
import com.walhalla.mtprotolist.adapter.GlypeProxyAdapter;
import com.walhalla.mtprotolist.databinding.ProxylistrssBinding;
import com.walhalla.mtprotolist.dialog.InfoGlypeDialog;

import com.walhalla.mtprotolist.webproxy.ProxyInfo;
import com.walhalla.ui.DLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GlypeProxy extends CompatFragment {


    private QRCodeDialog qrCodeDialog;

    private GlypeProxyAdapter adapter;


    //private NativeHelper nativeHelper;
    private final List<Object> mRecyclerViewItems = new ArrayList<>();

    public static final int NUMBER_OF_ADS = 5;

    // List of native ads that have been successfully loaded.
    //private List<NativeAd> mNativeAds = new ArrayList<>();
    //private AdLoader adLoader;

    private ProxylistrssBinding binding;
    private final ProtoAdapterCallback<ProxyInfo> callback = new ProtoAdapterCallback<ProxyInfo>() {
        @Override
        public void categorySelected(int position, ProxyInfo proxy) {
            //mainCallback.onSetMachine(position, proxy);
        }

        @Override
        public void viewProxy(int adapterPosition, ProxyInfo data) {
            String content = data.proxyUrl;
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.dialog_webproxy_title, data.ip))
                    .setMessage(content)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())
                    .show();
        }

        @Override
        public void copyClipboard(String text) {
            if (!TextUtils.isEmpty(text) && getActivity() != null) {
                ClipboardManager clipboard = (ClipboardManager) getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copy", text);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(),
                            getString(R.string.copied_to_clipboard) + " " + text, Toast.LENGTH_SHORT).show();
                }
            }
        }

        //https://t.me/proxy?server=morning-lab-0770.likesky.blue&port=4443&secret=ddf0e9d0a888731a670692a6fec9ed2477

        @Override
        public void handleProxyIntent(int adapterPosition, ProxyInfo data) {
            mainCallback.handleProxy0(data);
        }


        @Override
        public void shareProxy(int adapterPosition, ProxyInfo data) {
            if (getContext() != null) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String content = data.proxyUrl;
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                        //String.format(Module_U.GOOGLE_PLAY_URL, getContext().getPackageName())
                        getString(R.string.app_name)
                );
                sharingIntent.putExtra(Intent.EXTRA_TEXT, content);
                startActivity(Intent.createChooser(sharingIntent,
                        getString(R.string.share_proxy_data) + " - " + content));
            }
        }

        @Override
        public void qrProxyCode(int adapterPosition, ProxyInfo data) {
            String qr = data.proxyUrl;
            qrCodeDialog = QRCodeDialog.newInstance(qr);
            if (getChildFragmentManager() != null) {
                qrCodeDialog.show(getChildFragmentManager(), GlypeProxy.class.getSimpleName());
            }
        }

        @Override
        public void delete(ProxyInfo data) {

        }

        @Override
        public void proxyInfo(ProxyInfo data) {
            DialogFragment dialog = InfoGlypeDialog.newInstance(data);
            dialog.show(getChildFragmentManager(), "info_dialog");
        }

        @Override
        public void copyPort(ProxyInfo data) {
            copyClipboard(data.type);
            //Toast.makeText(getContext(), ""+data.type, Toast.LENGTH_SHORT).show();
//            if(Config.BuildConfigDEBUG && (TextUtils.isEmpty(data.type) ||(data.type.equals("@@")))
//            ){
//                VideoRepository repository = new VideoRepository(new VideoRepository.Callback() {
//                    @Override
//                    public void successResult(String result) {
//
//                    }
//                });
//                repository.detectType(getContext(), data);
//            }
        }

    };

    public static GlypeProxy newInstance(String s, String s1) {
        return new GlypeProxy();
    }


//    private void loadNativeAds() {
//
//        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.ad_unit_id));
//
//        adLoader = builder.forUnifiedNativeAd(
//                unifiedNativeAd -> {
//                    // A native ad loaded successfully, check if the ad loader has finished loading
//                    // and if so, insert the ads into the list.
//                    mNativeAds.add(unifiedNativeAd);
//                    if (!adLoader.isLoading()) {
//                        insertAdsInMenuItems();
//                    }
//                }).withAdListener(
//                new AdListener() {
//                    @Override
//                    public void onAdFailedToLoad(int errorCode) {
//                        // A native ad failed to load, check if the ad loader has finished loading
//                        // and if so, insert the ads into the list.
//                        Log.e(TAG, "The previous native ad failed to load. Attempting to"
//                                + " load another.");
//                        if (!adLoader.isLoading()) {
//                            insertAdsInMenuItems();
//                        }
//                    }
//                }).build();
//
//        // Load the Native ads.
//        adLoader.loadAds(new AdRequest.Builder()
//                .addTestDevice("955E006200AF8225680E0C4911819CF6")
//                .build(), NUMBER_OF_ADS);
//    }

//    private void insertAdsInMenuItems() {
//        if (mNativeAds.size() <= 0) {
//            return;
//        }
//        int offset = (mRecyclerViewItems.size() / mNativeAds.size()) + 1;
//        int index = 0;
//        for (UnifiedNativeAd ad : mNativeAds) {
//            mRecyclerViewItems.add(index, ad);
//            index = index + offset;
//        }
//        loadMenu();
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            binding.swipe.setRefreshing(true);
            this.loadCategory();
            binding.swipe.setRefreshing(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getActivity() != null) {
            ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (toolbar != null) {
                toolbar.setSubtitle(DLog.getAppVersion(getActivity()));
            }
        }

        binding = ProxylistrssBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyTextUtils.changeString(getActivity(), binding.emptyNotesView1);

        binding.emptyNotesView2.setMovementMethod(LinkMovementMethod.getInstance());
        //recyclerView.setHasFixedSize(true);


//        btnStartTest.setOnClickListener(v -> mainCallback.onSetMachine(State.START_NEW_GAME));
//        buttonPrivacyPolicy.setOnClickListener(v->{
//            Module_U.openBrowser(getContext(), Config.url_privacy_policy);
//        });

//        if (BuildConfig.DEBUG) {
//            mainCallback.onSetMachine();
//        }

        if (adapter == null) {
            adapter = new GlypeProxyAdapter(getActivity(), callback, new ArrayList<>(), false);
        }
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);

        //add ItemDecoration
        //recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        //or
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        //or
//        recyclerView.addItemDecoration(
//                new DividerItemDecoration(
//                //        getActivity(), manager.getOrientation()
//                        getActivity(), R.drawable.divider
//                ));

//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));

        setDecorator(getActivity());
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(adapter);
        binding.swipe.setOnRefreshListener(this::loadCategory);


//        this.nativeHelper = new NativeHelperBuilder()
//                .setNumberOfAds(Config.BANNER_COUNTS)
//                .setMaxFetchAttempt(8)
//                .setUnitId(getString(R.string.ad_unit_id))
//                .create();
//        this.nativeHelper.addListener(new NativeHelper.Callback() {
//
//            @Override
//            public void onAdCountChanged(List<NativeAd> mNativeAds) {
//                //insertAdsInMenuItems(mNativeAds);
//            }
//
//            @Override
//            public void onAdCountChanged(NativeAd mNativeAd) {
//                insertAdsInMenuItems(mNativeAd);
//            }
//        });


        if (savedInstanceState == null) {
            //if (getContext() != null && AppStatus.getInstance(getContext()).isOnline()) {
            loadCategory();
//        } else {
//            this.mainCallback.showMessage("Cannot connect to the server");
//        }
        }
    }

    private void setDecorator(Context context) {
        DividerItemDecoration itemDecorator = new DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(
                context, R.drawable.ic_item_divider)));
//        DividerItemDecoration itemDecorator = new DividerItemDecoration(
//                getContext(), DividerItemDecoration.VERTICAL);

        binding.recyclerView.addItemDecoration(itemDecorator);
    }

    //One by one injection
    int index = 0;

//    private void insertAdsInMenuItems(NativeAd o) {
//        if (Config.BANNER_COUNTS <= 0 || mRecyclerViewItems.isEmpty() || index > mRecyclerViewItems.size()) {
//            return;
//        }
//        int offset = (mRecyclerViewItems.size() / Config.BANNER_COUNTS) + 1;
//        this.adapter.add(index, o);
//        index = index + offset;
//    }

    private void onMessageRetrieved(List<Object> message) {
        if (message != null && !message.isEmpty()) {
            mRecyclerViewItems.clear();
            mRecyclerViewItems.addAll(message);

            if (getContext() != null && isAdded()) {
                //loadNativeAds();
                //M.d( "loadMenu: " + mRecyclerViewItems.toString());

                if (mRecyclerViewItems.isEmpty()) {
                    binding.emptyNotesView1.setVisibility(View.VISIBLE);
                    binding.emptyNotesView2.setVisibility(View.VISIBLE);

                    //recyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyNotesView1.setVisibility(View.GONE);
                    binding.emptyNotesView2.setVisibility(View.GONE);

                    //recyclerView.setVisibility(View.VISIBLE);

                    adapter.swap(mRecyclerViewItems);
                    if (mainCallback != null) {
                        mainCallback.showMessage(getString(R.string.data_successfully_updated));
                    }
                }
            }
        }
    }

    private void onRetrievalFailed(String error) {
        //M.d( "onRetrievalFailed: " + error);
        if (mainCallback != null) {
            mainCallback.hideLoader();
            mainCallback.showMessage(error);
        }
    }

    public void showLoader() {
        if (mainCallback != null) {
            mainCallback.showLoader();
        }
    }

    private void loadCategory() {
        index = 0;
        binding.swipe.setRefreshing(false);

        try {
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.REF_KEY_GLYPE);
//            reference.onDisconnect().removeValue((error, reference1) -> {
//                if (error != null) {
//                    Log.d(TAG, "could not establish onDisconnect event:" + error.getMessage());
//                }
//            });
//            reference.onDisconnect().cancel((databaseError, databaseReference) -> {
//                M.d( "onComplete: 00000");
//            });
            try {
                reference//.child("/")
                        .orderByChild("rate")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //showLoader();
                                if (mainCallback != null) {
                                    mainCallback.hideLoader();
                                }
                                List<Object> tmp = new ArrayList<>();
                                for (DataSnapshot obj : snapshot.getChildren()) {
                                    try {

                                        ProxyInfo category = obj.getValue(ProxyInfo.class);
                                        if (category.enabled) {
                                            tmp.add(category);
                                        }
                                        //DLog.d("@@@@" + category.proxyUrl);
                                    } catch (Exception e) {
                                        DLog.handleException(e);
                                        onRetrievalFailed("Failed to getUrl value." + e.getLocalizedMessage());
                                    }
                                }

                                if (!tmp.isEmpty()) {
                                    Collections.reverse(tmp);
                                    onMessageRetrieved(tmp);
                                } else {
                                    onRetrievalFailed("Database is empty, reinstall the Application");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onRetrievalFailed(error.getMessage());
                            }
                        });

                //reference.addChildEventListener(childEventListener);
            } catch (Exception e) {
                onRetrievalFailed(e.getLocalizedMessage());
            }
        } catch (Exception e) {
            DLog.handleException(e);
            onRetrievalFailed("loadCategory: " + e.getLocalizedMessage());
        }

//        if (Config.ENABLE_NATIVE_ADS) {
//            this.nativeHelper.prefetchAds(getActivity());
//        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }


//    public static class DividerItemDecoration3 extends RecyclerView.ItemDecoration {
//
//        private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
//
//        private final Drawable divider;
//
//        /**
//         * Default divider will be used
//         */
//        public DividerItemDecoration3(Context context) {
//            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
//            divider = styledAttributes.getDrawable(0);
//            styledAttributes.recycle();
//        }
//
//        /**
//         * Custom divider will be used
//         */
//        public DividerItemDecoration3(Context context, int resId) {
//            divider = ContextCompat.getDrawable(context, resId);
//        }
//
//        @Override
//        public void onDraw(@NonNull Canvas c, RecyclerView parent, @NonNull RecyclerView.State state) {
//            int left = parent.getPaddingLeft();
//            int right = parent.getWidth() - parent.getPaddingRight();
//
//            int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View child = parent.getChildAt(i);
//
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//
//                int top = child.getBottom() + params.bottomMargin;
//                int bottom = top + divider.getIntrinsicHeight();
//
//                divider.setBounds(left, top, right, bottom);
//                divider.draw(c);
//            }
//        }
//    }
}
