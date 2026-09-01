package com.walhalla.mtproto.shared.network

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
