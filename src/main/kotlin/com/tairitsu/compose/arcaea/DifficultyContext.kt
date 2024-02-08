package com.tairitsu.compose.arcaea

class DifficultyContext : MutableMap<Any, Any?> {
    private val data = mutableMapOf<Any, Any?>()

    override val entries: MutableSet<MutableMap.MutableEntry<Any, Any?>>
        get() = data.entries
    override val keys: MutableSet<Any>
        get() = data.keys
    override val size: Int
        get() = data.size
    override val values: MutableCollection<Any?>
        get() = data.values

    override fun clear() {
        data.clear()
    }

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun remove(key: Any): Any? {
        return data.remove(key)
    }

    override fun putAll(from: Map<out Any, Any?>) {
        return data.putAll(from)
    }

    override fun put(key: Any, value: Any?): Any? {
        return data.put(key, value)
    }

    override fun get(key: Any): Any? {
        return data.get(key)
    }

    override fun containsValue(value: Any?): Boolean {
        return data.containsValue(value)
    }

    override fun containsKey(key: Any): Boolean {
        return data.containsKey(key)
    }


    companion object {
        inline fun <reified T> DifficultyContext.wrap(key: Any): T {
            return this.get(key) as T
        }
    }
}
