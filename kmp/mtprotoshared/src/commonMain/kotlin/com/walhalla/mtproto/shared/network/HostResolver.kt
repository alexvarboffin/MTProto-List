package com.walhalla.mtproto.shared.network

expect fun resolveHostToIp(host: String): String?

expect fun resolveWebProxyHost(proxyUrl: String): String?
