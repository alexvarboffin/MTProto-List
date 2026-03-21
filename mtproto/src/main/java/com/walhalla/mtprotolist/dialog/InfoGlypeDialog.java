package com.walhalla.mtprotolist.dialog;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.databinding.InfoDialogBinding;

import com.walhalla.mtprotolist.webproxy.ProxyInfo;
import com.walhalla.ui.DLog;


public class InfoGlypeDialog extends DialogFragment {

    private InfoDialogBinding binding;

    public static InfoGlypeDialog newInstance(ProxyInfo data) {
        InfoGlypeDialog dialog = new InfoGlypeDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", data);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = InfoDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ProxyInfo server = (ProxyInfo) getArguments().getSerializable("data");
//            if(Config.BuildConfigDEBUG
//
//                    && (TextUtils.isEmpty(server.region)
//                   ||(server.ip.length() < 6))
//            ){
//                VideoRepository mm = new VideoRepository(result -> {
//
//                });
//                mm.bbbbbProxyInfo(server);
//
//            }

            //binding.statusTextView.setText(getString(R.string.status, server.status));
            binding.countryTextView.setText(getString(R.string.country, server.country));
            binding.countryCodeTextView.setText(getString(R.string.country_code, server.code));//countryCode
            binding.regionTextView.setText(getString(R.string.region, server.region));
            binding.regionNameTextView.setText(getString(R.string.region_name, server.regionName));
            binding.cityTextView.setText(getString(R.string.city, server.city));
            binding.zipTextView.setText(getString(R.string.zip, server.zip));

            binding.latLonTextView.setText(getString(R.string.lat_lon, server.lat, server.lon));
            binding.latLonTextView.setPaintFlags(binding.latLonTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            binding.latLonTextView.setOnClickListener(v -> {
                //String latLon = binding.latLonTextView.getText().toString();

                // Extract latitude and longitude from the string
//                String[] latLonArray = latLon.split(",");
//                double latitude = Double.parseDouble(latLonArray[0].trim());
//                double longitude = Double.parseDouble(latLonArray[1].trim());

                // Create a Uri with the coordinates
                Uri gmmIntentUri = Uri.parse("geo:" + server.lat + "," + server.lon + "?z=15");
                //Uri gmmIntentUri = Uri.parse("geo:" + latLon + "?z=15");
                //geo:49.4423,11.0191?z=15
                DLog.d("@@@@" + gmmIntentUri);

                // Create an intent to open Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");

                // Check if there's an app to handle the intent
                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Handle the case where Google Maps is not installed
                    Toast.makeText(requireContext(), "Google Maps not installed", Toast.LENGTH_SHORT).show();
                }
            });
            binding.timezoneTextView.setText(getString(R.string.timezone, server.timezone));
            binding.ispTextView.setText(getString(R.string.isp, server.isp));
            binding.orgTextView.setText(getString(R.string.org, server.org));
            binding.asTextView.setText(getString(R.string.as_info, server.as));
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }
}
