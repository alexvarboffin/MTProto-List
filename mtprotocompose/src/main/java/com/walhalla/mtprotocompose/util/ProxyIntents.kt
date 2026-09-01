package com.walhalla.mtprotocompose.util

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotocompose.R
import com.walhalla.ui.UConst

fun openTelegramProxy(context: Context, proxy: MtprotoProxy, onMissingClient: () -> Unit = {}) {
    val host = proxy.host?.trim().orEmpty().lowercase()
    val port = proxy.port?.trim().orEmpty()
    val secret = proxy.secret?.trim().orEmpty()
    val uri = String.format(AppConfig.PROXY_HANDLER_TG, host, port, secret)
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, R.string.err_telegram_client_not_unstalled, Toast.LENGTH_LONG).show()
        onMissingClient()
    }
}

fun showTelegramInstallerDialog(context: Context) {
    val apps = linkedMapOf(
        "Telegram" to "org.telegram.messenger",
        "Telegram X" to "org.thunderdog.challegram",
        "BGram" to "org.telegram.BifToGram",
        "Vidogram" to "org.vidogram.messenger",
        "Plus Messenger" to "org.telegram.plus",
        "Graph Messenger" to "ir.ilmili.telegraph",
        "Pentagram" to "top.pentagram",
    )
    val names = apps.keys.toTypedArray()
    val packages = apps.values.toTypedArray()
    AlertDialog.Builder(context)
        .setTitle(R.string.err_telegram_client_not_unstalled)
        .setItems(names) { _, which ->
            val playUri = Uri.parse(UConst.GOOGLE_PLAY_CONSTANT + packages[which])
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, playUri))
            } catch (_: ActivityNotFoundException) {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, playUri).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                )
            }
        }
        .setPositiveButton(android.R.string.cancel, null)
        .show()
}

fun shareText(context: Context, subject: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_proxy_data)))
}

fun copyToClipboard(context: Context, label: String, text: String) {
    if (text.isBlank()) return
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "${context.getString(R.string.copied_to_clipboard)} $text", Toast.LENGTH_SHORT).show()
}

fun openMaps(context: Context, lat: Double, lon: Double) {
    val uri = Uri.parse("geo:$lat,$lon?z=15")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, context.getString(R.string.err_maps_not_installed), Toast.LENGTH_SHORT).show()
    }
}
