package com.walhalla.mtprotocompose.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirebaseProxyStreams {
    fun mtprotoList(): Flow<Result<List<MtprotoProxy>>> = callbackFlow {
        val reference = FirebaseDatabase.getInstance()
            .getReference(AppConfig.REF_KEY_MTPROTO)
            .orderByChild("update_at")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    runCatching { child.getValue(MtprotoProxy::class.java) }.getOrNull()
                }.asReversed()
                if (items.isEmpty()) {
                    trySend(Result.failure(IllegalStateException("Database is empty, reinstall the Application")))
                } else {
                    trySend(Result.success(items))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(IllegalStateException(error.message)))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun webProxyList(): Flow<Result<List<ProxyInfo>>> = callbackFlow {
        val reference = FirebaseDatabase.getInstance()
            .getReference(AppConfig.REF_KEY_GLYPE)
            .orderByChild("rate")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    runCatching { child.getValue(ProxyInfo::class.java) }.getOrNull()
                }.filter { it.enabled }.asReversed()
                if (items.isEmpty()) {
                    trySend(Result.failure(IllegalStateException("Database is empty, reinstall the Application")))
                } else {
                    trySend(Result.success(items))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(IllegalStateException(error.message)))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}
