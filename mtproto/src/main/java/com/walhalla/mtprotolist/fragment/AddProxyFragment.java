package com.walhalla.mtprotolist.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.databinding.FragmentAddProxyBinding;
import com.walhalla.mtprotolist.entity.MtprotoProxy;

public class AddProxyFragment extends DialogFragment {

    public static final String KEY_IMG_POSITION = "key_input_data";
    public static final String REQUEST_KEY = "requestKey";
    private FragmentAddProxyBinding binding;
    private MtprotoProxyListViewModel viewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddProxyBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MtprotoProxyListViewModel.class);
        //viewModel.getProxyAddedEvent().observe(getViewLifecycleOwner(), added -> {


        //this - ok
        //getViewLifecycleOwner()-ok
        viewModel.getProxyAddedEvent().observe(getViewLifecycleOwner(), added -> {

            if (added) {
                // Прокси успешно добавлен, закройте фрагмент
                //requireActivity().onBackPressed();

//                Bundle resultBundle = new Bundle();
//                resultBundle.putInt(KEY_IMG_POSITION, position);
//                FragmentManager fm = getParentFragmentManager();
//                fm.setFragmentResult(REQUEST_KEY, resultBundle);
                okRequest();
                //dismiss();
                //dismissAllowingStateLoss();
                AddProxyFragment.this.dismiss();
            }
        });


        binding.setViewModel(viewModel);

        //-->binding.setLifecycleOwner(this);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.button.setOnClickListener(v -> {

            String server = binding.editTextServer.getText().toString().trim();
            String port = binding.editTextPort.getText().toString().trim();
            String secret = binding.editTextSecret.getText().toString().trim();

            if (isValidInput(port, server, secret)) {
                MtprotoProxy proxy = new MtprotoProxy(
                        server, port, secret, "", true
                );
                viewModel.onAddProxyClicked(proxy);
            } else {
                onValidationError(R.string.error_empty_field);
            }
        });
    }

    private void onValidationError(int errorEmptyField) {
        Toast.makeText(getContext(), errorEmptyField, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidInput(String port, String server, String secret) {

        if (TextUtils.isEmpty(server) || TextUtils.isEmpty(port) || TextUtils.isEmpty(secret)) {
            return false;
        } else {
            return true;
        }
    }

    private void okRequest() {
        Bundle resultBundle = new Bundle();
        int position = 1;
        resultBundle.putInt(KEY_IMG_POSITION, position);
        FragmentManager fm = getParentFragmentManager();
        fm.setFragmentResult(REQUEST_KEY, resultBundle);
    }
}
