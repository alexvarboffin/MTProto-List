package com.walhalla.mtprotolist.dialog

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.databinding.InfoDialogBinding
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import androidx.core.net.toUri
import androidx.transition.Visibility
import com.walhalla.mtprotolist.Config

class InfoMtProtoDialog : DialogFragment() {
    private lateinit var binding: InfoDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InfoDialogBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(Config.BuildConfigDEBUG){
            binding.disable.visibility = View.VISIBLE
            binding.disable.setOnClickListener {
                Toast.makeText(context, "@@@@", Toast.LENGTH_SHORT).show()
            }
        }
        try {
            arguments?.let {
                val server = it.getSerializable(KEY_ARG_DATA) as MtprotoProxy
                //            if(Config.BuildConfigDEBUG && TextUtils.isEmpty(server.country)){
//                VideoRepository mm = new VideoRepository(result -> {
//
//                });
//                mm.bbbbb(server);
//            }
                //binding.statusTextView.setText(getString(R.string.status, server.status));
                binding.countryTextView.text = getString(R.string.country, server.country)
                binding.countryCodeTextView.text = getString(
                    R.string.country_code,
                    server.code
                ) //countryCode
                binding.regionTextView.text =
                    getString(R.string.region, server.region.noneNullPlaceholder())
                binding.regionNameTextView.text =
                    getString(R.string.region_name, server.regionName.noneNullPlaceholder())
                binding.cityTextView.text =
                    getString(R.string.city, server.city.noneNullPlaceholder())
                binding.zipTextView.text =
                    getString(R.string.zip, server.zip.noneNullPlaceholder())

                binding.latLonTextView.text = getString(
                    R.string.lat_lon,
                    server.lat,
                    server.lon
                )
                binding.latLonTextView.setPaintFlags(binding.latLonTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
                binding.latLonTextView.setOnClickListener { v: View? ->
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
                }
                binding.timezoneTextView.text = getString(R.string.timezone, server.timezone.noneNullPlaceholder())
                binding.ispTextView.text = getString(R.string.isp, server.isp.noneNullPlaceholder())
                binding.orgTextView.text = getString(R.string.org, server.org.noneNullPlaceholder())
                binding.asTextView.text = getString(R.string.as_info, server.`as`.noneNullPlaceholder())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    companion object {
        private const val KEY_ARG_DATA = "data"
        fun newInstance(data: MtprotoProxy?): InfoMtProtoDialog {
            val dialog = InfoMtProtoDialog()
            val args = Bundle()
            args.putSerializable(KEY_ARG_DATA, data)
            dialog.setArguments(args)
            return dialog
        }
    }
}


private fun String?.noneNullPlaceholder(): String {
    //return this ?: "-"
    return if(isNullOrEmpty() || (this == "null")) "-" else this@noneNullPlaceholder
}
