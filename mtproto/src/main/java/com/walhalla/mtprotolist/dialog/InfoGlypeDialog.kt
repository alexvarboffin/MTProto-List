package com.walhalla.mtprotolist.dialog

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.databinding.InfoDialogBinding
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import androidx.core.net.toUri

class InfoGlypeDialog : DialogFragment() {
    private var binding: InfoDialogBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InfoDialogBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val server = requireArguments().getSerializable("data") as ProxyInfo?
//            if (Config.BuildConfigDEBUG
//
//                && (TextUtils.isEmpty(server!!.region)
//                        || (server.ip!!.length < 6))
//            ) {
//                val mm: VideoRepository = VideoRepository({ result -> })
//                mm.bbbbbProxyInfo(server)
//            }

            //binding.statusTextView.setText(getString(R.string.status, server.status));
            binding!!.countryTextView.text = getString(R.string.country, server!!.country)
            binding!!.countryCodeTextView.text = getString(
                R.string.country_code,
                server.code
            ) //countryCode
            binding!!.regionTextView.text = getString(R.string.region, server.region)
            binding!!.regionNameTextView.text = getString(
                R.string.region_name,
                server.regionName
            )
            binding!!.cityTextView.text = getString(R.string.city, server.city)
            binding!!.zipTextView.text = getString(R.string.zip, server.zip)

            binding!!.latLonTextView.text = getString(
                R.string.lat_lon,
                server.lat,
                server.lon
            )
            binding!!.latLonTextView.paintFlags = binding!!.latLonTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            binding!!.latLonTextView.setOnClickListener(View.OnClickListener { v: View? ->
                //String latLon = binding.latLonTextView.getText().toString();
                // Extract latitude and longitude from the string
//                String[] latLonArray = latLon.split(",");
//                double latitude = Double.parseDouble(latLonArray[0].trim());
//                double longitude = Double.parseDouble(latLonArray[1].trim());

                // Create a Uri with the coordinates
                val gmmIntentUri = ("geo:" + server.lat + "," + server.lon + "?z=15").toUri()
                //Uri gmmIntentUri = Uri.parse("geo:" + latLon + "?z=15");
                //geo:49.4423,11.0191?z=15
                d("@@@@$gmmIntentUri")

                // Create an intent to open Google Maps
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                //mapIntent.setPackage("com.google.android.apps.maps");

                // Check if there's an app to handle the intent
                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    // Handle the case where Google Maps is not installed
                    Toast.makeText(
                        requireContext(),
                        "Google Maps not installed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            binding!!.timezoneTextView.text = getString(R.string.timezone, server.timezone)
            binding!!.ispTextView.text = getString(R.string.isp, server.isp)
            binding!!.orgTextView.text = getString(R.string.org, server.org)
            binding!!.asTextView.text = getString(R.string.as_info, server.`as`)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(data: ProxyInfo?): InfoGlypeDialog {
            val dialog = InfoGlypeDialog()
            val args = Bundle()
            args.putSerializable("data", data)
            dialog.setArguments(args)
            return dialog
        }
    }
}
