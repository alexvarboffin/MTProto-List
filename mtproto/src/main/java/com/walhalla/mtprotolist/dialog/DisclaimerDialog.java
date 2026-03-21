package com.walhalla.mtprotolist.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.walhalla.mtprotolist.R;

public class DisclaimerDialog extends DialogFragment {

    public static final String KEY_AGREE_VALUE = DisclaimerDialog.class.getSimpleName();
    public static final String REQUEST_KEY = "requestKey";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.disclaimer_title))
                .setMessage(getString(R.string.disclaimer_message))
                .setPositiveButton(getString(R.string.button_agree), (dialog, id) -> {
                    agreement(true);
                    dismiss();
                })
                .setNegativeButton(getString(android.R.string.cancel), (dialog, id) -> {
                    agreement(false);
                    dismiss();
                });
        return builder.create();
    }

    private void agreement(boolean isAgree) {
        Bundle resultBundle = new Bundle();
        //resultBundle.putInt(KEY_INPUT_OUTPUT_DATA_FONT, position);
        resultBundle.putBoolean(KEY_AGREE_VALUE, isAgree);
        FragmentManager fm = getParentFragmentManager();
        fm.setFragmentResult(DisclaimerDialog.REQUEST_KEY, resultBundle);
    }
}
