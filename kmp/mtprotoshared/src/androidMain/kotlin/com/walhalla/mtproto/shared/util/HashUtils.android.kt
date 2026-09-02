package com.walhalla.mtproto.shared.util

import java.security.MessageDigest

internal actual fun md5Digest(bytes: ByteArray): ByteArray {
    return MessageDigest.getInstance("MD5").digest(bytes)
}
