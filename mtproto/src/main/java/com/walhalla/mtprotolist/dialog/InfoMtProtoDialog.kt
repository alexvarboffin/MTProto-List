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
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.manager.VideoRepository
import com.walhalla.mtprotolist.util.ProxyDialogArgs
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException

class InfoMtProtoDialog : DialogFragment() {
    private lateinit var binding: InfoDialogBinding
    private val videoRepository = VideoRepository()

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
        val server = ProxyDialogArgs.decodeMtproto(arguments?.getString(KEY_ARG_DATA)) ?: return

        try {
            bindServerInfo(server)
            if (Config.BuildConfigDEBUG) {
                setupDisableButton(server)
                setupUpdateGeoButton(server)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun bindServerInfo(server: MtprotoProxy) {
        binding.countryTextView.text = getString(R.string.country, server.country)
        binding.countryCodeTextView.text = getString(R.string.country_code, server.code)
        binding.regionTextView.text =
            getString(R.string.region, server.region.noneNullPlaceholder())
        binding.regionNameTextView.text =
            getString(R.string.region_name, server.regionName.noneNullPlaceholder())
        binding.cityTextView.text =
            getString(R.string.city, server.city.noneNullPlaceholder())
        binding.zipTextView.text =
            getString(R.string.zip, server.zip.noneNullPlaceholder())

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
        binding.timezoneTextView.text = getString(R.string.timezone, server.timezone.noneNullPlaceholder())
        binding.ispTextView.text = getString(R.string.isp, server.isp.noneNullPlaceholder())
        binding.orgTextView.text = getString(R.string.org, server.org.noneNullPlaceholder())
        binding.asTextView.text = getString(R.string.as_info, server.`as`.noneNullPlaceholder())
    }

    private fun setupDisableButton(server: MtprotoProxy) {
        binding.disable.visibility = View.VISIBLE
        updateDisableButtonLabel(server)
        binding.disable.setOnClickListener {
            toggleEnabledInFirebase(server)
        }
    }

    private fun updateDisableButtonLabel(server: MtprotoProxy) {
        binding.disable.text = if (server.enabled != false) "DISABLE" else "ENABLE"
    }

    private fun setupUpdateGeoButton(server: MtprotoProxy) {
        binding.updateGeo.visibility = View.VISIBLE
        binding.updateGeo.setOnClickListener {
            updateGeoInFirebase(server)
        }
    }

    private fun updateGeoInFirebase(server: MtprotoProxy) {
        binding.updateGeo.isEnabled = false
        videoRepository.updateMtprotoGeo(
            server,
            onSuccess = { updated ->
                if (!isAdded) return@updateMtprotoGeo
                bindServerInfo(updated)
                binding.updateGeo.isEnabled = true
                Toast.makeText(requireContext(), "Geo updated in Firebase", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                if (!isAdded) return@updateMtprotoGeo
                binding.updateGeo.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    error ?: "Geo update failed",
                    Toast.LENGTH_LONG,
                ).show()
            },
        )
    }

    private fun toggleEnabledInFirebase(server: MtprotoProxy) {
        val host = server.host?.trim().orEmpty()
        val port = server.port?.trim().orEmpty()
        val secret = server.secret?.trim().orEmpty()
        if (host.isEmpty() || port.isEmpty() || secret.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid proxy data", Toast.LENGTH_SHORT).show()
            return
        }

        server.enabled = server.enabled == false
        binding.disable.isEnabled = false

        val url = String.format(Config.PROXY_HANDLER, host, port, secret)
        val key = HashUtils.md5(url)
        FirebaseDatabase.getInstance()
            .getReference(Config.REF_KEY_MTPROTO)
            .child(key)
            .setValue(server)
            .addOnSuccessListener {
                binding.disable.isEnabled = true
                updateDisableButtonLabel(server)
                val state = if (server.enabled == true) "enabled" else "disabled"
                Toast.makeText(requireContext(), "Firebase updated: $state", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                binding.disable.isEnabled = true
                server.enabled = server.enabled == false
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

        fun newInstance(data: MtprotoProxy?): InfoMtProtoDialog {
            val dialog = InfoMtProtoDialog()
            val args = Bundle()
            args.putString(KEY_ARG_DATA, ProxyDialogArgs.encodeMtproto(data))
            dialog.arguments = args
            return dialog
        }
    }
}

private fun String?.noneNullPlaceholder(): String {
    return if (isNullOrEmpty() || this == "null") "-" else this
}
