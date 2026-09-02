package com.walhalla.mtprotolist.manager

import com.walhalla.mtproto.shared.repository.ProxyRepository
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VideoRepository {
    private val repository = ProxyRepository.create()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    fun updateMtprotoGeo(
        server: MtprotoProxy,
        onSuccess: (MtprotoProxy) -> Unit,
        onError: (String?) -> Unit,
    ) {
        scope.launch {
            repository.refreshMtprotoGeo(server)
                .onSuccess(onSuccess)
                .onFailure { onError(it.message) }
        }
    }

    fun updateProxyInfoGeo(
        server: ProxyInfo,
        onSuccess: (ProxyInfo) -> Unit,
        onError: (String?) -> Unit,
    ) {
        scope.launch {
            repository.refreshWebProxyGeo(server)
                .onSuccess(onSuccess)
                .onFailure { onError(it.message) }
        }
    }
}
