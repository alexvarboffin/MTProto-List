package com.walhalla.mtprotolist.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdLoader;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotolist.MyTextUtils;
import com.walhalla.mtprotolist.QRCodeDialog;
import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.databinding.ProxylistrssBinding;
import com.walhalla.mtprotolist.databinding.RewardDialogLayoutBinding;
import com.walhalla.mtprotolist.dialog.InfoMtProtoDialog;
import com.walhalla.mtprotolist.entity.MtprotoProxy;

import com.walhalla.mtprotolist.adapter.MtprotoProxyAdapter;
import com.walhalla.mtprotolist.ProtoAdapterCallback;
import com.walhalla.mtprotolist.wads.KSUtil;
import com.walhalla.ui.DLog;
import com.walhalla.utils.AManagerI;
import com.walhalla.utils.RewardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class f1 extends CompatFragment implements AManagerI.RewardManagerCallback
        //implements MProtoFragmentPresenter.MProtoFragmentView
{
    private KSUtil m;
    private RewardManager rm;

    private QRCodeDialog qrCodeDialog;

    private MtprotoProxyAdapter adapter;

    //private NativeHelper nativeHelper;
    private final List<Object> mRecyclerViewItems = new ArrayList<>();

    public static final int NUMBER_OF_ADS = 5;

    // List of native ads that have been successfully loaded.
    //private List<NativeAd> mNativeAds = new ArrayList<>();
    private AdLoader adLoader;

    private ProxylistrssBinding binding;
    private final ProtoAdapterCallback<MtprotoProxy> callback = new ProtoAdapterCallback<MtprotoProxy>() {
        @Override
        public void categorySelected(int position, MtprotoProxy proxy) {
            //mainCallback.onSetMachine(position, proxy);
        }

        @Override
        public void viewProxy(int adapterPosition, MtprotoProxy data) {
            String content = String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret);
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("PROXY " + data.host)
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
                    Toast.makeText(getActivity(), getString(R.string.copied_to_clipboard) + " " + text, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void delete(MtprotoProxy data) {

        }

        @Override
        public void info(MtprotoProxy data) {
            InfoMtProtoDialog infoDialog = InfoMtProtoDialog.newInstance(data);
            infoDialog.show(getParentFragmentManager(), "info_dialog");
        }

        @Override
        public void copyPort(MtprotoProxy data) {
            copyClipboard(data.port);
        }


        @Override
        public void shareProxy(int adapterPosition, MtprotoProxy data) {
            if (getContext() != null) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String content = String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret);
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
        public void qrProxyCode(int adapterPosition, MtprotoProxy data) {
            String qr = String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret);
            qrCodeDialog = QRCodeDialog.newInstance(qr);
            if (getFragmentManager() != null) {
                qrCodeDialog.show(getFragmentManager(), "dlg1");
            }
        }

        //https://t.me/proxy?server=morning-lab-0770.likesky.blue&port=4443&secret=ddf0e9d0a888731a670692a6fec9ed2477

        @Override
        public void handleProxyIntent(int adapterPosition, MtprotoProxy data) {
            if (m.isItemLocked(adapterPosition)) {
                //data.setLock(LessonState.UNLOCK);
                showUnlockDialog(getActivity(), data, adapterPosition);
            } else {
                handleProxy(data);
            }
        }

    };




    private void showUnlockDialog(FragmentActivity activity, MtprotoProxy data, int position) {
//        new AlertDialog.Builder(getContext())
//                .setTitle("Unlock Item")
//                .setMessage("Watch an ad to unlock this item?")
//                .setPositiveButton("OK", (dialog, which) -> showRewardedAd(getActivity(), data, position))
//                .setNegativeButton("Cancel", null)
//                .show();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        RewardDialogLayoutBinding binding = RewardDialogLayoutBinding.inflate(inflater);
        builder.setView(binding.getRoot());
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.dPurchase.setOnClickListener(view -> {
            alertDialog.dismiss();
            rm.showRewardAdBanner(getActivity(), position, this);
        });
        binding.dCancel.setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.show();
    }


    //private MProtoFragmentPresenter m;


    public static f1 newInstance(String s, String s1) {
        return new f1();
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
        //m = new MProtoFragmentPresenter(getContext(), this);
        m = KSUtil.getInstance(getActivity());
        rm = RewardManager.getInstance();
        Set<Integer> data = new HashSet<>();
        data.add(4);
        data.add(6);
        data.add(7);
        data.add(9);
        m.initialize(data);


        rm.loadRewardAd(getActivity());
    }


    @Override
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
            adapter = new MtprotoProxyAdapter(getActivity(), m.getBlockedItems(), callback, new ArrayList<>(), false);
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
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.ic_item_divider)));
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


    //firebase
    private void loadCategory() {
        index = 0;
        binding.swipe.setRefreshing(false);

        try {
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.REF_KEY_MTPROTO);
//            reference.onDisconnect().removeValue((error, reference1) -> {
//                if (error != null) {
//                    Log.d(TAG, "could not establish onDisconnect event:" + error.getMessage());
//                }
//            });
//            reference.onDisconnect().cancel((databaseError, databaseReference) -> {
//                M.d( "onComplete: 00000");
//            });
            try {
                reference
                        //.child("/")
                        .orderByChild("update_at")
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
                                        MtprotoProxy category = obj.getValue(MtprotoProxy.class);
                                        tmp.add(category);
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

    public void handleProxy(MtprotoProxy data) {
        mainCallback.handleProxy(data);
    }




    @Override
    public void successResult7(int position) {
        DLog.d("@@@@@@");
        // User earned the reward.
        m.unlockItem(position);
        if (f1.this.mainCallback != null) {
            f1.this.mainCallback.rewardExplode();
        } else {
            //Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyItemChanged(position);
        //handleProxy(data);

    }

    @Override
    public void errorShowAds() {
        Toast.makeText(getContext(), R.string.ad_not_loaded_try_another_time, Toast.LENGTH_SHORT).show();
    }
}
