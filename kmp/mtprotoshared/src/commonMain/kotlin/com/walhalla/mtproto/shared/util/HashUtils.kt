package com.walhalla.mtproto.shared.util

import kotlin.experimental.and

object HashUtils {
    fun md5(input: String): String {
        if (input.isEmpty()) return ""
        val digest = md5Digest(input.encodeToByteArray())
        return digest.joinToString("") { byte ->
            val value = byte and 0xFF.toByte()
            value.toInt().toString(16).padStart(2, '0')
        }
    }
}

internal expect fun md5Digest(bytes: ByteArray): ByteArray
