//package com.walhalla.mtprotolist.fragment;
//
//
//import static com.walhalla.mtprotolist.fragment.RanFragment.KEY_CURRENT_DIALOG;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.Fragment;
//
//import androidx.fragment.app.FragmentManager;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.walhalla.mtprotolist.R;
//import com.walhalla.mtprotolist.databinding.FragmentMtprotoProxyBinding;
//import com.walhalla.mtprotolist.entity.MtprotoProxy;
//import com.walhalla.mtprotolist.adapter.MtprotoProxyAdapter;
//import com.walhalla.mtprotolist.ProtoAdapterCallback;
//
//import com.walhalla.mtprotolist.presenter.MtprotoProxyView;
//import com.walhalla.mtprotolist.presenter.ProxyListPresenter;
//import com.walhalla.ui.DLog;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class MtprotoProxyFragment extends Fragment implements MtprotoProxyView {
//    private FragmentMtprotoProxyBinding binding;
//    private MtprotoProxyAdapter adapter;
//
//    private ProxyListPresenter presenter;
//
//    private final ProtoAdapterCallback callback = new ProtoAdapterCallback() {
//        @Override
//        public void categorySelected(int position, MtprotoProxy title) {
//
//        }
//
//        @Override
//        public void shareProxy(int adapterPosition, MtprotoProxy data) {
//
//        }
//
//        @Override
//        public void qrProxyCode(int adapterPosition, MtprotoProxy data) {
//
//        }
//
//        @Override
//        public void handleProxyIntent(int adapterPosition, MtprotoProxy data) {
//
//        }
//
//        @Override
//        public void viewProxy(int adapterPosition, MtprotoProxy data) {
//
//        }
//
//        @Override
//        public void copyClipboard(String host) {
//
//        }
//
//        @Override
//        public void delete(MtprotoProxy data) {
//            presenter.delete(data);
//        }
//
//        @Override
//        public void info(MtprotoProxy data) {
//
//        }
//    };
//
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (getActivity() != null) {
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            fragmentManager.setFragmentResultListener(AddProxyFragment.REQUEST_KEY, getViewLifecycleOwner(), (requestKey, result) -> {
//                boolean var0 = result.containsKey(AddProxyFragment.KEY_IMG_POSITION);
//                if (var0) {
//                    int selectedImgPos = result.getInt(AddProxyFragment.KEY_IMG_POSITION);
//                    if (selectedImgPos > -1 /*&& adapterPosition > -1*/) {
//                        //adapter.updateRecyclerViewItemBackground(adapterPosition, selectedImgPos);
//                        DLog.d("@@@");
//                    }
//                }
//            });
//        }
//    }
//
//
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentMtprotoProxyBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        MtprotoProxyListViewModel viewModel = new ViewModelProvider(this).get(MtprotoProxyListViewModel.class);
//        //binding.setViewModel(viewModel);
//        //binding.setLifecycleOwner(this);
//        viewModel.getProxyAddedEvent().observe(getViewLifecycleOwner(), added -> {
//            if (added) {
//                // Прокси успешно добавлен, закройте фрагмент
//                //requireActivity().onBackPressed();
//
////                Bundle resultBundle = new Bundle();
////                resultBundle.putInt(KEY_IMG_POSITION, position);
////                FragmentManager fm = getParentFragmentManager();
////                fm.setFragmentResult(REQUEST_KEY, resultBundle);
////                okRequest();
////                AddProxyFragment.this.dismiss();
//                Toast.makeText(getActivity(), "999", Toast.LENGTH_SHORT).show();
//            }
//
//            if (try_attach_this != null) {
//                try_attach_this.dismiss();
//            }
//        });
//
//
//        adapter = new MtprotoProxyAdapter(getActivity(), callback, new ArrayList<>(), true);
//        setDecorator(getActivity());
//        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        binding.recyclerView.setAdapter(adapter);
//        //AppDatabase m = LocalDatabaseRepo.getDatabase(getContext(), "mtproto");
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        presenter = new ProxyListPresenter(getActivity(), this, handler);
//        binding.addProxy.setOnClickListener(v -> {
//            openGalleryDialog(MtprotoProxyFragment.this);
//        });
//    }
//    private void setDecorator(Context context) {
//        DividerItemDecoration itemDecorator = new DividerItemDecoration(
//                context, DividerItemDecoration.VERTICAL);
//        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(
//                context, R.drawable.item_divider)));
////        DividerItemDecoration itemDecorator = new DividerItemDecoration(
////                getContext(), DividerItemDecoration.VERTICAL);
//
//        binding.recyclerView.addItemDecoration(itemDecorator);
//    }
//    @Override
//    public void showProxies(List<MtprotoProxy> message) {
//        if (getContext() != null && isAdded()) {
//            //loadNativeAds();
//            //M.d( "loadMenu: " + mRecyclerViewItems.toString());
//
//            if (message.isEmpty()) {
//                binding.emptyNotesView1.setVisibility(View.VISIBLE);
//                binding.emptyNotesView2.setVisibility(View.VISIBLE);
//
//                //recyclerView.setVisibility(View.GONE);
//            } else {
//                binding.emptyNotesView1.setVisibility(View.GONE);
//                binding.emptyNotesView2.setVisibility(View.GONE);
//
//                //recyclerView.setVisibility(View.VISIBLE);
//                adapter.swap0(message);
////                    if (mainCallback != null) {
////                        mainCallback.showMessage(getString(R.string.data_successfully_updated));
////                    }
//            }
//        }
//    }
//
//    DialogFragment try_attach_this = null;
//
//    public void openGalleryDialog(Fragment context) {
//        DLog.d("@@@@@@@@@@@@@@");
//        FragmentManager fragmentManager = context.getParentFragmentManager();
//        Fragment oldFragment = null;
//        if (fragmentManager != null) {
//            oldFragment = fragmentManager.findFragmentByTag(KEY_CURRENT_DIALOG);
//        }
//        if (oldFragment != null) {
//            fragmentManager.beginTransaction().remove(oldFragment).commit();
//        }
//
//        //if (rr != null) {
//        //try_attach_this = DialogFactory.getDialog(this, rr);
//        try_attach_this = new AddProxyFragment();
//
////        Bundle bundle = new Bundle();
////        bundle.putString(D_Color.KEY_INPUT_OUTPUT_DATA, PillRequest.INSTANCE.get(MpcField.COLOR));
////        dialog.setArguments(bundle);
//        //D_Color.newInstance(PillRequest.INSTANCE.get(MpcField.COLOR));
//        //try_attach_this.setTargetFragment(rootFragment, REQUEST_GALLERY_OPTION);
//        //}
//
//        if (try_attach_this != null) {
//            Dialog dialog0 = try_attach_this.getDialog();
//            if (dialog0 != null && dialog0.isShowing()) {
//                return;
//            }
//            if (!try_attach_this.isAdded()) {
//                if (fragmentManager != null) {
//                    try_attach_this.show(fragmentManager, KEY_CURRENT_DIALOG);
//                }
//            }
//        }
//    }
//}