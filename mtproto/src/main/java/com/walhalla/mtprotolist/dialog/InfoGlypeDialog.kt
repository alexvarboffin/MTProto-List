package com.walhalla.mtprotolist.dialog

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.FirebaseDatabase
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.HashUtils
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.databinding.InfoDialogBinding
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException

class InfoGlypeDialog : DialogFragment() {
    private lateinit var binding: InfoDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = InfoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val server = arguments?.getSerializable(KEY_ARG_DATA) as? ProxyInfo ?: return

        try {
            bindServerInfo(server)
            if (Config.BuildConfigDEBUG) {
                setupDisableButton(server)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun bindServerInfo(server: ProxyInfo) {
//            if (Config.BuildConfigDEBUG
//
//                && (TextUtils.isEmpty(server!!.region)
//                        || (server.ip!!.length < 6))
//            ) {
//                val mm: VideoRepository = VideoRepository({ result -> })
//                mm.bbbbbProxyInfo(server)
//            }

        binding.countryTextView.text = getString(R.string.country, server.country)
        binding.countryCodeTextView.text = getString(R.string.country_code, server.code)
        binding.regionTextView.text = getString(R.string.region, server.region)
        binding.regionNameTextView.text = getString(R.string.region_name, server.regionName)
        binding.cityTextView.text = getString(R.string.city, server.city)
        binding.zipTextView.text = getString(R.string.zip, server.zip)

        binding.latLonTextView.text = getString(R.string.lat_lon, server.lat, server.lon)
        binding.latLonTextView.paintFlags = binding.latLonTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.latLonTextView.setOnClickListener {
            val gmmIntentUri = ("geo:${server.lat},${server.lon}?z=15").toUri()
            d("@@@@$gmmIntentUri")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(requireContext(), "Google Maps not installed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timezoneTextView.text = getString(R.string.timezone, server.timezone)
        binding.ispTextView.text = getString(R.string.isp, server.isp)
        binding.orgTextView.text = getString(R.string.org, server.org)
        binding.asTextView.text = getString(R.string.as_info, server.`as`)
    }

    private fun setupDisableButton(server: ProxyInfo) {
        binding.disable.visibility = View.VISIBLE
        updateDisableButtonLabel(server)
        binding.disable.setOnClickListener {
            toggleEnabledInFirebase(server)
        }
    }

    private fun updateDisableButtonLabel(server: ProxyInfo) {
        binding.disable.text = if (server.enabled) "DISABLE" else "ENABLE"
    }

    private fun toggleEnabledInFirebase(server: ProxyInfo) {
        val proxyUrl = server.proxyUrl?.trim().orEmpty()
        if (proxyUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid proxy data", Toast.LENGTH_SHORT).show()
            return
        }

        server.enabled = !server.enabled
        binding.disable.isEnabled = false

        val key = HashUtils.md5(proxyUrl)
        FirebaseDatabase.getInstance()
            .getReference(Config.REF_KEY_GLYPE)
            .child(key)
            .setValue(server)
            .addOnSuccessListener {
                binding.disable.isEnabled = true
                updateDisableButtonLabel(server)
                val state = if (server.enabled) "enabled" else "disabled"
                Toast.makeText(requireContext(), "Firebase updated: $state", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                binding.disable.isEnabled = true
                server.enabled = !server.enabled
                updateDisableButtonLabel(server)
                Toast.makeText(
                    requireContext(),
                    error.message ?: "Firebase update failed",
                    Toast.LENGTH_LONG,
                ).show()
            }
    }

    companion object {
        private const val KEY_ARG_DATA = "data"

        @JvmStatic
        fun newInstance(data: ProxyInfo?): InfoGlypeDialog {
            val dialog = InfoGlypeDialog()
            val args = Bundle()
            args.putSerializable(KEY_ARG_DATA, data)
            dialog.arguments = args
            return dialog
        }
    }
}
