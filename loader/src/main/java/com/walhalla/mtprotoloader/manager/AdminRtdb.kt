package com.walhalla.mtprotoloader.manager

import com.google.api.core.ApiFuture
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import java.io.File
import java.io.FileInputStream

/**
 * RTDB writes for the Android loader via the Firebase Admin SDK.
 * Key path: env GOOGLE_APPLICATION_CREDENTIALS. The JSON key is not shipped in the app.
 */
object AdminRtdb {
    private const val DATABASE_URL = "https://mtprotolist.firebaseio.com"
    private val lock = Any()

    @Volatile
    private var ready = false

    fun enqueue(path: String, key: String, value: Map<String, Any?>): ApiFuture<Void> {
        ensureInitialized()
        return FirebaseDatabase.getInstance()
            .reference
            .child(path)
            .child(key)
            .setValueAsync(value)
    }

    fun mtprotoValue(data: MtprotoProxy): Map<String, Any?> = linkedMapOf(
        "host" to data.host,
        "port" to data.port,
        "secret" to data.secret,
        "code" to data.code,
        "enabled" to data.enabled,
        "update_at" to data.update_at,
        "city" to data.city,
        "regionName" to data.regionName,
        "lat" to data.lat,
        "lon" to data.lon,
        "country" to data.country,
        "region" to data.region,
        "zip" to data.zip,
        "timezone" to data.timezone,
        "isp" to data.isp,
        "org" to data.org,
        "as" to data.`as`,
    ).filterValues { it != null }

    fun webProxyValue(data: ProxyInfo): Map<String, Any?> = linkedMapOf(
        "proxyUrl" to data.proxyUrl,
        "ip" to data.ip,
        "lastChecked" to data.lastChecked,
        "type" to data.type,
        "ssl" to data.ssl,
        "speed" to data.speed,
        "rate" to data.rate,
        "code" to data.code,
        "city" to data.city,
        "regionName" to data.regionName,
        "lat" to data.lat,
        "lon" to data.lon,
        "country" to data.country,
        "region" to data.region,
        "zip" to data.zip,
        "timezone" to data.timezone,
        "isp" to data.isp,
        "org" to data.org,
        "as" to data.`as`,
        "isActive" to data.isActive,
        "enabled" to data.enabled,
        "update_at" to data.update_at,
    ).filterValues { it != null }

    private fun ensureInitialized() {
        if (ready) return
        synchronized(lock) {
            if (ready) return
            val path = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
            if (path.isNullOrBlank()) {
                throw IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS is not set")
            }
            val file = File(path)
            if (!file.isFile) {
                throw IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS is not a file")
            }
            FileInputStream(file).use { input ->
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(input))
                    .setDatabaseUrl(DATABASE_URL)
                    .build()
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options)
                }
            }
            ready = true
        }
    }
}
