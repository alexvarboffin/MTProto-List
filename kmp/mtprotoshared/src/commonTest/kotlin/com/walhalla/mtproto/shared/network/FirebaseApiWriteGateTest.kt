package com.walhalla.mtproto.shared.network

import com.walhalla.mtproto.shared.repository.ProxyRepository
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FirebaseApiWriteGateTest {
    @Test
    fun disabledWritesDoNotTouchTheNetwork() = runBlocking {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    error("unexpected RTDB request")
                }
            }
        }
        val api = FirebaseApi(client, writesEnabled = false)
        val mtproto = api.saveMtprotoProxy(MtprotoProxy("h", "1", "s", "US", true))
        val web = ProxyInfo()
        web.proxyUrl = "https://example.com/proxy"
        val webResult = api.saveWebProxy(web)
        assertTrue(mtproto.isFailure)
        assertTrue(webResult.isFailure)
        assertEquals("Firebase RTDB writes are disabled", mtproto.exceptionOrNull()?.message)

        val repository = ProxyRepository(api, GeoApi(client))
        val toggled = repository.toggleMtprotoEnabled(MtprotoProxy("h", "1", "s", "US", true))
        val geo = repository.refreshWebProxyGeo(web)
        assertTrue(toggled.isFailure)
        assertTrue(geo.isFailure)
        assertEquals(true, toggled.exceptionOrNull()?.message?.contains("disabled"))
        client.close()
    }

    @Test
    fun createLeavesWritesDisabled() = runBlocking {
        val repository = ProxyRepository.create()
        val web = ProxyInfo()
        web.proxyUrl = "https://example.com/proxy"
        val result = repository.toggleWebProxyEnabled(web)
        assertTrue(result.isFailure)
        assertEquals("Firebase RTDB writes are disabled", result.exceptionOrNull()?.message)
        assertEquals(false, web.enabled)
    }
}
