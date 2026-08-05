package com.walhalla.mtprotolist.activity.web

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.PopupMenu
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.databinding.CustomWebviewLayoutBinding
import com.walhalla.webview.WVTools.copyToClipboard0


class UWVlayout : RelativeLayout {


    var callback: UWVlayoutCallback? = null


    interface UWVlayoutCallback {
        fun closeApplication()
    }

    private var binding: CustomWebviewLayoutBinding? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = CustomWebviewLayoutBinding.inflate(inflater, this, true)
        binding!!.buttonMenu.setOnClickListener { v: View? ->
            showPopupMenu(
                context,
                v!!
            )
        }
    }

    private fun showPopupMenu(context: Context, v: View) {
        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.wv_menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            val itemId = item!!.itemId
            if (itemId == R.id.action_home) {
                val homeUrl = this.webView.originalUrl
                this.webView.loadUrl(homeUrl!!)
                return@OnMenuItemClickListener true
            } else if (itemId == R.id.action_exit) {
                if (callback != null) {
                    callback!!.closeApplication()
                }
                return@OnMenuItemClickListener true
            } else if (itemId == R.id.action_url_copy) {
                val url = this.webView.url
                url?.let { copyToClipboard0(context, it) }
                return@OnMenuItemClickListener true
            } else {
                return@OnMenuItemClickListener false
            }
        })
        popupMenu.show()
    }

    val webView: UWView
        get() = binding!!.inner00

    fun collapseMenu(): ImageView {
        return binding!!.buttonMenu
    }
}
