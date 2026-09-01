package com.walhalla.mtprotolist.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.gms.ads.AdLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.MyTextUtils
import com.walhalla.mtprotolist.ProtoAdapterCallback
import com.walhalla.mtprotolist.QRCodeDialog
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.adapter.MtprotoProxyAdapter
import com.walhalla.mtprotolist.databinding.ProxylistrssBinding
import com.walhalla.mtprotolist.databinding.RewardDialogLayoutBinding
import com.walhalla.mtprotolist.dialog.InfoMtProtoDialog
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.wads.KSUtil
import com.walhalla.ui.DLog
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.utils.AManagerI.RewardManagerCallback
import com.walhalla.utils.RewardManager
import java.util.Collections
import java.util.Objects

class f1 : CompatFragment(),
    RewardManagerCallback //implements MProtoFragmentPresenter.MProtoFragmentView
{
    private var m: KSUtil? = null
    private var rm: RewardManager? = null

    private var qrCodeDialog: QRCodeDialog? = null

    private var adapter: MtprotoProxyAdapter? = null

    //private NativeHelper nativeHelper;
    private val mRecyclerViewItems: MutableList<Any?> = ArrayList<Any?>()

    // List of native ads that have been successfully loaded.
    //private List<NativeAd> mNativeAds = new ArrayList<>();
    private val adLoader: AdLoader? = null

    private var binding: ProxylistrssBinding? = null
    private val callback: ProtoAdapterCallback<MtprotoProxy> =
        object : ProtoAdapterCallback<MtprotoProxy> {
            override fun categorySelected(position: Int, proxy: MtprotoProxy) {
                //mainCallback.onSetMachine(position, proxy);
            }

            override fun viewProxy(adapterPosition: Int, data: MtprotoProxy) {
                val content = String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret)
                AlertDialog.Builder(requireActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("PROXY " + data.host)
                    .setMessage(content)
                    .setPositiveButton(
                        android.R.string.ok,
                        DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> dialog!!.cancel() })
                    .show()
            }

            override fun copyClipboard(text: String) {
                if (!TextUtils.isEmpty(text) && activity != null) {
                    val clipboard = requireActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("Copy", text)
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            getActivity(),
                            getString(R.string.copied_to_clipboard) + " " + text,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun delete(data: MtprotoProxy) {
            }

            override fun info(data: MtprotoProxy) {
                val infoDialog = InfoMtProtoDialog.newInstance(data)
                infoDialog.show(getParentFragmentManager(), "info_dialog")
            }

            override fun copyPort(data: MtprotoProxy) {
                copyClipboard(data.port?:"")
            }


            override fun shareProxy(adapterPosition: Int, data: MtprotoProxy) {
                if (context != null) {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val content =
                        String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret)
                    sharingIntent.putExtra(
                        Intent.EXTRA_SUBJECT,  //String.format(Module_U.GOOGLE_PLAY_URL, getContext().getPackageName())
                        getString(R.string.app_name)
                    )
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, content)
                    startActivity(
                        Intent.createChooser(sharingIntent, getString(R.string.share_proxy_data) + " - " + content)
                    )
                }
            }

            override fun qrProxyCode(adapterPosition: Int, data: MtprotoProxy) {
                val qr = String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret)
                qrCodeDialog = QRCodeDialog.newInstance(qr)
                if (getFragmentManager() != null) {
                    qrCodeDialog!!.show(requireFragmentManager(), "dlg1")
                }
            }

            //https://t.me/proxy?server=morning-lab-0770.likesky.blue&port=4443&secret=ddf0e9d0a888731a670692a6fec9ed2477
            override fun handleProxyIntent(adapterPosition: Int, data: MtprotoProxy) {
                if (m!!.isItemLocked(adapterPosition)) {
                    //data.setLock(LessonState.UNLOCK);
                    showUnlockDialog(requireActivity(), data, adapterPosition)
                } else {
                    handleProxy(data)
                }
            }
        }


    private fun showUnlockDialog(activity: FragmentActivity, data: MtprotoProxy?, position: Int) {
//        new AlertDialog.Builder(getContext())
//                .setTitle("Unlock Item")
//                .setMessage("Watch an ad to unlock this item?")
//                .setPositiveButton("OK", (dialog, which) -> showRewardedAd(getActivity(), data, position))
//                .setNegativeButton("Cancel", null)
//                .show();
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val binding = RewardDialogLayoutBinding.inflate(inflater)
        builder.setView(binding.getRoot())
        val alertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.dPurchase.setOnClickListener(View.OnClickListener { view: View? ->
            alertDialog.dismiss()
            rm!!.showRewardAdBanner(requireActivity(), position, this)
        })
        binding.dCancel.setOnClickListener { view: View? -> alertDialog.dismiss() }
        alertDialog.show()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //m = new MProtoFragmentPresenter(getContext(), this);
        m = KSUtil.getInstance(requireContext())
        rm = RewardManager.instance
        val data: MutableSet<Int> = HashSet()
        data.add(4)
        data.add(6)
        data.add(7)
        data.add(9)
        m!!.initialize(data)


        rm!!.loadRewardAd(requireActivity())
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_refresh) {
            binding!!.swipe.isRefreshing = true
            this.loadCategory()
            binding!!.swipe.isRefreshing = false
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (getActivity() != null) {
            val toolbar = (activity as AppCompatActivity).getSupportActionBar()
            if (toolbar != null) {
                toolbar.setSubtitle(DLog.getAppVersion(requireActivity()))
            }
        }
        binding = ProxylistrssBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyTextUtils.changeString(requireActivity(), binding!!.emptyNotesView1)

        binding!!.emptyNotesView2.movementMethod = LinkMovementMethod.getInstance()


        //recyclerView.setHasFixedSize(true);


//        btnStartTest.setOnClickListener(v -> mainCallback.onSetMachine(State.START_NEW_GAME));
//        buttonPrivacyPolicy.setOnClickListener(v->{
//            Module_U.openBrowser(getContext(), Config.url_privacy_policy);
//        });

//        if (BuildConfig.DEBUG) {
//            mainCallback.onSetMachine();
//        }
        if (adapter == null) {
            adapter = MtprotoProxyAdapter(
                getActivity(),
                m!!.blockedItems,
                callback,
                ArrayList<Any?>(),
                false
            )
        }
        val manager = LinearLayoutManager(getActivity())
        binding!!.recyclerView.setLayoutManager(manager)

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
        setDecorator(requireActivity())
        binding!!.recyclerView.setItemAnimator(DefaultItemAnimator())
        binding!!.recyclerView.setAdapter(adapter)
        binding!!.swipe.setOnRefreshListener(OnRefreshListener { this.loadCategory() })


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
            loadCategory()
            //        } else {
//            this.mainCallback.showMessage("Cannot connect to the server");
//        }
        }
    }

    private fun setDecorator(context: Context) {
        val itemDecorator = DividerItemDecoration(
            context, DividerItemDecoration.VERTICAL
        )
        itemDecorator.setDrawable(
            Objects.requireNonNull<Drawable?>(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_item_divider
                )
            )
        )

        //        DividerItemDecoration itemDecorator = new DividerItemDecoration(
//                getContext(), DividerItemDecoration.VERTICAL);
        binding!!.recyclerView.addItemDecoration(itemDecorator)
    }

    //One by one injection
    var index: Int = 0

    //    private void insertAdsInMenuItems(NativeAd o) {
    //        if (Config.BANNER_COUNTS <= 0 || mRecyclerViewItems.isEmpty() || index > mRecyclerViewItems.size()) {
    //            return;
    //        }
    //        int offset = (mRecyclerViewItems.size() / Config.BANNER_COUNTS) + 1;
    //        this.adapter.add(index, o);
    //        index = index + offset;
    //    }
    private fun onMessageRetrieved(message: MutableList<Any?>?) {
        if (message != null && !message.isEmpty()) {
            mRecyclerViewItems.clear()
            mRecyclerViewItems.addAll(message)

            if (getContext() != null && isAdded()) {
                //loadNativeAds();
                //M.d( "loadMenu: " + mRecyclerViewItems.toString());

                if (mRecyclerViewItems.isEmpty()) {
                    binding!!.emptyNotesView1.setVisibility(View.VISIBLE)
                    binding!!.emptyNotesView2.setVisibility(View.VISIBLE)

                    //recyclerView.setVisibility(View.GONE);
                } else {
                    binding!!.emptyNotesView1.setVisibility(View.GONE)
                    binding!!.emptyNotesView2.setVisibility(View.GONE)

                    //recyclerView.setVisibility(View.VISIBLE);
                    adapter!!.swap(mRecyclerViewItems)
                    if (mainCallback != null) {
                        mainCallback.showMessage(getString(R.string.data_successfully_updated))
                    }
                }
            }
        }
    }

    private fun onRetrievalFailed(error: String?) {
        //M.d( "onRetrievalFailed: " + error);
        if (mainCallback != null) {
            mainCallback.hideLoader()
            mainCallback.showMessage(error)
        }
    }

    fun showLoader() {
        if (mainCallback != null) {
            mainCallback.showLoader()
        }
    }


    //firebase
    private fun loadCategory() {
        index = 0
        binding!!.swipe.setRefreshing(false)

        try {
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            val reference = FirebaseDatabase.getInstance().getReference(Config.REF_KEY_MTPROTO)
            //            reference.onDisconnect().removeValue((error, reference1) -> {
//                if (error != null) {
//                    Log.d(TAG, "could not establish onDisconnect event:" + error.getMessage());
//                }
//            });
//            reference.onDisconnect().cancel((databaseError, databaseReference) -> {
//                M.d( "onComplete: 00000");
//            });
            try {
                reference //.child("/")
                    .orderByChild("update_at")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            //showLoader();
                            if (mainCallback != null) {
                                mainCallback.hideLoader()
                            }
                            val tmp: MutableList<Any?> = ArrayList<Any?>()
                            for (obj in snapshot.getChildren()) {
                                try {
                                    val category =
                                        obj.getValue<MtprotoProxy?>(MtprotoProxy::class.java)
                                    tmp.add(category)
                                } catch (e: Exception) {
                                    handleException(e)
                                    onRetrievalFailed("Failed to getUrl value." + e.getLocalizedMessage())
                                }
                            }

                            if (!tmp.isEmpty()) {
                                Collections.reverse(tmp)
                                onMessageRetrieved(tmp)
                            } else {
                                onRetrievalFailed("Database is empty, reinstall the Application")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            onRetrievalFailed(error.getMessage())
                        }
                    })

                //reference.addChildEventListener(childEventListener);
            } catch (e: Exception) {
                onRetrievalFailed(e.getLocalizedMessage())
            }
        } catch (e: Exception) {
            handleException(e)
            onRetrievalFailed("loadCategory: " + e.getLocalizedMessage())
        }

        //        if (Config.ENABLE_NATIVE_ADS) {
//            this.nativeHelper.prefetchAds(getActivity());
//        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun handleProxy(data: MtprotoProxy?) {
        mainCallback.handleProxy(data)
    }


    override fun successResult7(position: Int) {
        d("@@@@@@")
        // User earned the reward.
        m!!.unlockItem(position)
        if (this@f1.mainCallback != null) {
            this@f1.mainCallback.rewardExplode()
        } else {
            //Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
        adapter!!.notifyItemChanged(position)

        //handleProxy(data);
    }


    override fun errorShowAds(position: Int) {
        Toast.makeText(getContext(), R.string.ad_not_loaded_try_another_time, Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        const val NUMBER_OF_ADS: Int = 5

        //private MProtoFragmentPresenter m;
        @JvmStatic
        fun newInstance(s: String?, s1: String?): f1 {
            return f1()
        }
    }
}
