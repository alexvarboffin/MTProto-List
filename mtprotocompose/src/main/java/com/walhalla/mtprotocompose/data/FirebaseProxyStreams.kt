package com.walhalla.mtprotocompose.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtproto.shared.list.IndexedMtproto
import com.walhalla.mtproto.shared.list.UpdateAtChild
import com.walhalla.mtproto.shared.list.orderByChildUpdateAt
import com.walhalla.mtproto.shared.list.withLegacySourceIndex
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirebaseProxyStreams {
    fun mtprotoList(): Flow<Result<List<IndexedMtproto>>> = callbackFlow {
        val reference = FirebaseDatabase.getInstance()
            .getReference(AppConfig.REF_KEY_MTPROTO)
            .orderByChild("update_at")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val inQueryOrder = orderByChildUpdateAt(
                    snapshot.children.map { child ->
                        UpdateAtChild(
                            key = child.key.orEmpty(),
                            updateAt = updateAtOf(child.child("update_at")),
                            value = runCatching {
                                child.getValue(MtprotoProxy::class.java)
                            }.getOrNull(),
                        )
                    }.toList(),
                ).map { it.value }
                val items = inQueryOrder.withLegacySourceIndex()
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

    private fun updateAtOf(snapshot: DataSnapshot): Long? {
        if (!snapshot.exists()) return null
        val raw = snapshot.value ?: return null
        return (raw as? Number)?.toLong()
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
