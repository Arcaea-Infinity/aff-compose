package com.tairitsu.compose.arcaea

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KProperty

/**
 * The difficulties of a song.
 */
@Serializable(with = Difficulties.DifficultiesSerializer::class)
class Difficulties : List<Difficulty> {
    /**
     * The internal storage of the difficulties.
     */
    internal var storage: MutableList<Difficulty> = ArrayList()

    /**
     * Internal constructor.
     */
    internal constructor()

    /**
     * Construct a new difficulties with the given difficulties' information.
     */
    internal constructor(diff: List<Difficulty>) {
        for (difficulty in diff) {
            storage.add(difficulty)
        }
    }

    /**
     * The [past] difficulty of the song.
     */
    var past: Difficulty? by DifficultyGetting

    /**
     * The [present] difficulty of the song.
     */
    var present: Difficulty? by DifficultyGetting

    /**
     * The [future] difficulty of the song.
     */
    var future: Difficulty? by DifficultyGetting

    /**
     * The [past] difficulty of the song.
     */
    var beyond: Difficulty? by DifficultyGetting

    // Query Operations
    override val size: Int = storage.size
    override fun isEmpty(): Boolean = storage.isEmpty()
    override fun contains(element: @UnsafeVariance Difficulty): Boolean = storage.contains(element)
    override fun iterator(): Iterator<Difficulty> = storage.iterator()

    // Bulk Operations
    override fun containsAll(elements: Collection<@UnsafeVariance Difficulty>): Boolean = storage.containsAll(elements)

    // Positional Access Operations
    public override operator fun get(index: Int): Difficulty = storage[index]

    // Search Operations
    public override fun indexOf(element: @UnsafeVariance Difficulty): Int = storage.indexOf(element)
    public override fun lastIndexOf(element: @UnsafeVariance Difficulty): Int = storage.lastIndexOf(element)

    // List Iterators
    public override fun listIterator(): ListIterator<Difficulty> = storage.listIterator()
    public override fun listIterator(index: Int): ListIterator<Difficulty> = storage.listIterator(index)

    // View
    public override fun subList(fromIndex: Int, toIndex: Int): List<Difficulty> = storage.subList(fromIndex, toIndex)

    /**
     * Delegate for difficulty of the song.
     */
    private object DifficultyGetting {
        operator fun getValue(obj: Difficulties, property: KProperty<*>): Difficulty? {
            for (difficulty in obj.storage) {
                if (difficulty.ratingClass == Difficulty.RatingClass.PAST && property.name == "past") {
                    return difficulty
                } else if (difficulty.ratingClass == Difficulty.RatingClass.PRESENT && property.name == "present") {
                    return difficulty
                } else if (difficulty.ratingClass == Difficulty.RatingClass.FUTURE && property.name == "future") {
                    return difficulty
                } else if (difficulty.ratingClass == Difficulty.RatingClass.BEYOND && property.name == "beyond") {
                    return difficulty
                }
            }

            return null
        }

        operator fun setValue(obj: Difficulties, property: KProperty<*>, value: Difficulty?) {
            if (value == null) {
                return
            }

            for (index in obj.storage.indices) {
                val difficulty = obj.storage[index]

                if (difficulty.ratingClass == Difficulty.RatingClass.PAST && property.name == "past") {
                    value.ratingClass = Difficulty.RatingClass.PAST
                    obj.storage[index] = value
                    return
                } else if (difficulty.ratingClass == Difficulty.RatingClass.PRESENT && property.name == "present") {
                    value.ratingClass = Difficulty.RatingClass.PRESENT
                    obj.storage[index] = value
                    return
                } else if (difficulty.ratingClass == Difficulty.RatingClass.FUTURE && property.name == "future") {
                    value.ratingClass = Difficulty.RatingClass.FUTURE
                    obj.storage[index] = value
                    return
                } else if (difficulty.ratingClass == Difficulty.RatingClass.BEYOND && property.name == "beyond") {
                    value.ratingClass = Difficulty.RatingClass.BEYOND
                    obj.storage[index] = value
                    return
                }
            }

            value.ratingClass = when (property.name) {
                "past" -> Difficulty.RatingClass.PAST
                "present" -> Difficulty.RatingClass.PRESENT
                "future" -> Difficulty.RatingClass.FUTURE
                "beyond" -> Difficulty.RatingClass.BEYOND
                else -> throw IllegalArgumentException("Property name must be one of past, present, future, or beyond")
            }
            obj.storage.add(value)
        }
    }

    /**
     * Serializer for [Difficulties].
     */
    object DifficultiesSerializer : KSerializer<Difficulties> {
        private val listSerializer = kotlinx.serialization.builtins.ListSerializer(Difficulty.serializer())

        override fun deserialize(decoder: Decoder): Difficulties {
            return Difficulties(listSerializer.deserialize(decoder))
        }

        override val descriptor: SerialDescriptor
            get() = listSerializer.descriptor

        override fun serialize(encoder: Encoder, value: Difficulties) {
            return listSerializer.serialize(encoder, value.storage)
        }
    }

}
