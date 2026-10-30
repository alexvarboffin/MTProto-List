package com.walhalla.mtproto.shared.repository

import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.mtproto.shared.network.GeoApi
import com.walhalla.mtproto.shared.network.FirebaseApi
import com.walhalla.mtproto.shared.network.createHttpClient

class ProxyRepository(
    private val firebaseApi: FirebaseApi,
    private val geoApi: GeoApi,
) {
    suspend fun loadMtprotoList(): Result<List<MtprotoProxy>> = firebaseApi.fetchMtprotoProxies()

    suspend fun loadWebProxyList(): Result<List<ProxyInfo>> = firebaseApi.fetchWebProxies()

    suspend fun toggleMtprotoEnabled(proxy: MtprotoProxy): Result<MtprotoProxy> {
        if (!firebaseApi.writesEnabled) {
            return Result.failure(IllegalStateException("Firebase RTDB writes are disabled"))
        }
        proxy.enabled = proxy.enabled != true
        return firebaseApi.saveMtprotoProxy(proxy).map { proxy }
    }

    suspend fun toggleWebProxyEnabled(proxy: ProxyInfo): Result<ProxyInfo> {
        if (!firebaseApi.writesEnabled) {
            return Result.failure(IllegalStateException("Firebase RTDB writes are disabled"))
        }
        proxy.enabled = !proxy.enabled
        return firebaseApi.saveWebProxy(proxy).map { proxy }
    }

    suspend fun refreshMtprotoGeo(proxy: MtprotoProxy): Result<MtprotoProxy> {
        if (!firebaseApi.writesEnabled) {
            return Result.failure(IllegalStateException("Firebase RTDB writes are disabled"))
        }
        return geoApi.updateMtprotoGeo(proxy).onSuccess { updated ->
            firebaseApi.saveMtprotoProxy(updated).getOrThrow()
        }
    }

    suspend fun refreshWebProxyGeo(proxy: ProxyInfo): Result<ProxyInfo> {
        if (!firebaseApi.writesEnabled) {
            return Result.failure(IllegalStateException("Firebase RTDB writes are disabled"))
        }
        return geoApi.updateWebProxyGeo(proxy).onSuccess { updated ->
            firebaseApi.saveWebProxy(updated).getOrThrow()
        }
    }

    companion object {
        fun create(writesEnabled: Boolean = true): ProxyRepository {
            val client = createHttpClient()
            return ProxyRepository(
                firebaseApi = FirebaseApi(client, writesEnabled = writesEnabled),
                geoApi = GeoApi(client),
            )
        }
    }
}
