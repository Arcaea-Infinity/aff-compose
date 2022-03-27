package com.tairitsu.compose

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KProperty

@Serializable(with = LocalizedStringSerializer::class)
class LocalizedString : Map<String, String> {
    internal var storage: LinkedHashMap<String, String> = LinkedHashMap()

    constructor(kv: LinkedHashMap<String, String>) {
        storage = kv
    }

    public constructor(enLocale: String) {
        storage = LinkedHashMap()
        storage["en"] = enLocale
    }

    var en by Getting
    var ja by Getting
    var ko by Getting
    var zhHans by Getting
    var zhHant by Getting

    internal operator fun set(key: String, value: String) {
        storage[key] = value
    }

    // From Map

    override val size: Int = storage.size
    override fun isEmpty(): Boolean = storage.isEmpty()
    override fun containsKey(key: String): Boolean = storage.containsKey(key)
    override fun containsValue(value: String): Boolean = storage.containsValue(value)
    override fun get(key: String): String? = storage[key]

    override val keys: MutableSet<String> = storage.keys
    override val values: MutableCollection<String> = storage.values
    override val entries: MutableSet<MutableMap.MutableEntry<String, String>> = storage.entries
}

internal val localeNameMapping = mapOf(
    "en" to "en", "ja" to "ja", "ko" to "ko", "zhHans" to "zh-Hans", "zhHant" to "zh-Hant"
)

private object Getting {
    operator fun getValue(obj: LocalizedString, property: KProperty<*>): String
        = obj[localeNameMapping[property.name]!!] ?: obj.en

    operator fun setValue(obj: LocalizedString, property: KProperty<*>, value: String) {
        obj[localeNameMapping[property.name]!!] = value
    }
}

object LocalizedStringSerializer : KSerializer<LocalizedString> {
    private val mapSerializer = kotlinx.serialization.builtins.MapSerializer(String.serializer(), String.serializer())

    override fun deserialize(decoder: Decoder): LocalizedString {
        val map = LinkedHashMap(mapSerializer.deserialize(decoder))
        return LocalizedString(map)
    }

    override val descriptor: SerialDescriptor
        get() = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: LocalizedString) {
        return mapSerializer.serialize(encoder, value.storage)
    }
}