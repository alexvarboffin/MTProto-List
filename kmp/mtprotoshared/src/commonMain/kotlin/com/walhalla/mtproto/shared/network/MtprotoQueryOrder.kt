package com.walhalla.mtproto.shared.network

import com.walhalla.mtproto.shared.list.UpdateAtChild
import com.walhalla.mtproto.shared.list.orderByChildUpdateAt
import com.walhalla.mtprotolist.entity.MtprotoProxy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * `orderByChild("update_at")` order, before the legacy reverse.
 * Missing `update_at` stays null and sorts before 0.
 */
fun decodeMtprotoInQueryOrder(body: String, json: Json): List<MtprotoProxy?> {
    if (body == "null" || body.isBlank()) return emptyList()
    val element = json.parseToJsonElement(body)
    if (element !is JsonObject) return emptyList()
    val children = element.map { (key, value) ->
        UpdateAtChild(
            key = key,
            updateAt = updateAtOrNull(value),
            value = runCatching {
                json.decodeFromJsonElement(MtprotoProxy.serializer(), value)
            }.getOrNull(),
        )
    }
    return orderByChildUpdateAt(children).map { it.value }
}

fun updateAtOrNull(value: JsonElement): Long? {
    val obj = value as? JsonObject ?: return null
    if (!obj.containsKey("update_at")) return null
    val raw = obj["update_at"] ?: return null
    if (raw is JsonNull || raw is JsonArray || raw is JsonObject) return null
    val primitive = raw as? JsonPrimitive ?: return null
    if (primitive.isString) return null
    return primitive.content.toLongOrNull() ?: primitive.content.toDoubleOrNull()?.toLong()
}
