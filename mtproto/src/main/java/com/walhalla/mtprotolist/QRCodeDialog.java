package com.walhalla.mtprotolist;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.walhalla.mtprotolist.databinding.QrLayoutBinding;

public class QRCodeDialog extends DialogFragment {


    private static final String ARG_CODE = "arg_qr_code_text";

    private String url;
    private Bitmap bitmap;
    private QrLayoutBinding binding;

    public QRCodeDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.url = getArguments().getString(ARG_CODE);
        }
    }

    public static QRCodeDialog newInstance(String code) {
        QRCodeDialog fragment = new QRCodeDialog();
        Bundle arg = new Bundle();
        arg.putString(ARG_CODE, code);
        fragment.setArguments(arg);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrLayoutBinding.inflate(inflater, container, false);
        bitmap = net.glxn.qrgen.android.QRCode.from(url)
                //.withColor(0xFFFF0000, 0xFFFFFFAA)
                .bitmap();
        binding.imageView.setImageBitmap(bitmap);
        binding.imageView.setOnClickListener(v -> this.dismiss());
        return binding.getRoot();
    }


//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
//    }
//     .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())

}
