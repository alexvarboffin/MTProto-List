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

class InfoMtProtoDialog : DialogFragment() {
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
           arguments?.let {
               val server = it.getSerializable(KEY_ARG_DATA) as MtprotoProxy?
               //            if(Config.BuildConfigDEBUG && TextUtils.isEmpty(server.country)){
//                VideoRepository mm = new VideoRepository(result -> {
//
//                });
//                mm.bbbbb(server);
//            }
               //binding.statusTextView.setText(getString(R.string.status, server.status));
               binding!!.countryTextView.text = getString(R.string.country, server!!.country)
               binding!!.countryCodeTextView.text = getString(
                   R.string.country_code,
                   server.code
               ) //countryCode
               binding!!.regionTextView.text = getString(R.string.region, server.region)
               binding!!.regionNameTextView.text = getString(R.string.region_name, server.regionName)
               binding!!.cityTextView.setText(getString(R.string.city, server.city))
               binding!!.zipTextView.setText(getString(R.string.zip, server.zip))

               binding!!.latLonTextView.setText(
                   getString(
                       R.string.lat_lon,
                       server.lat,
                       server.lon
                   )
               )
               binding!!.latLonTextView.setPaintFlags(binding!!.latLonTextView.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
               binding!!.latLonTextView.setOnClickListener(View.OnClickListener { v: View? ->
                   //String latLon = binding.latLonTextView.getText().toString();
                   // Extract latitude and longitude from the string
//                String[] latLonArray = latLon.split(",");
//                double latitude = Double.parseDouble(latLonArray[0].trim());
//                double longitude = Double.parseDouble(latLonArray[1].trim());

                   // Create a Uri with the coordinates
                   val gmmIntentUri =
                       Uri.parse("geo:" + server.lat + "," + server.lon + "?z=15")
                   //Uri gmmIntentUri = Uri.parse("geo:" + latLon + "?z=15");
                   //geo:49.4423,11.0191?z=15
                   d("@@@@" + gmmIntentUri)

                   // Create an intent to open Google Maps
                   val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                   //mapIntent.setPackage("com.google.android.apps.maps");

                   // Check if there's an app to handle the intent
                   if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
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
               binding!!.timezoneTextView.setText(getString(R.string.timezone, server.timezone))
               binding!!.ispTextView.setText(getString(R.string.isp, server.isp))
               binding!!.orgTextView.setText(getString(R.string.org, server.org))
               binding!!.asTextView.setText(getString(R.string.as_info, server.`as`))
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
