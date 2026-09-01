package com.walhalla.mtprotolist.entity

import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtproto.shared.util.HashUtils
import kotlinx.serialization.Serializable

@Serializable
class MtprotoProxy {
    var _id: Long? = null

    @JvmField
    var host: String? = ""

    @JvmField
    var port: String? = ""

    @JvmField
    var secret: String? = ""

    @JvmField
    var code: String? = ""

    var enabled: Boolean? = null

    var city: String? = null
    var regionName: String? = null
    var lat: Double = 0.0
    var lon: Double = 0.0
    var country: String? = null
    var region: String? = null
    var zip: String? = null
    var timezone: String? = null
    var isp: String? = null
    var org: String? = null
    var `as`: String? = null
    var update_at: Long = 0L

    constructor()

    constructor(host: String?, port: String?, secret: String?, code: String?, enabled: Boolean?) {
        this.host = host
        this.port = port
        this.secret = secret
        this.code = code
        this.enabled = enabled
    }

    fun shareUrl(): String = String.format(AppConfig.PROXY_HANDLER, host, port, secret)

    fun telegramUrl(): String = String.format(AppConfig.PROXY_HANDLER_TG, host, port, secret)

    fun firebaseKey(): String = HashUtils.md5(shareUrl())
}
