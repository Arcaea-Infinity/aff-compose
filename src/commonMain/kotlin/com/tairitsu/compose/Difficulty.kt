package com.tairitsu.compose

import com.benasher44.uuid.uuid4
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A difficulty of the song
 */
@Serializable
class Difficulty {
    /**
     * The rating class of the difficulty, 0 for past, 1 for present, 2 for future, and 3 for beyond
     */
    var ratingClass: RatingClass = RatingClass.PAST
        internal set

    /**
     * The rating of the difficult, 0 for `?`, other number for corresponding number value.
     * before 3.0.0 it is 0 for `?`, 10 for `9`, 11 for `10` and 1~9 for corresponding number value.
     */
    var rating: Int = 0

    @Serializable(with = DifficultyRatingClassSerializer::class)
    enum class RatingClass(val rating: Int) {
        PAST(0),
        PRESENT(1),
        FUTURE(2),
        BEYOND(3),
    }

    /**
     * The designer of the chart of the difficulty
     */
    var chartDesigner: String = "Kotlin"

    /**
     * The designer of the jacket of the difficulty
     */
    var jacketDesigner: String = "Kotlin"

    /**
     * The chart content of the difficulty
     */
    @Transient
    val chart: Chart = Chart()

    /**
     * The context while mapping in code.
     */
    @Transient
    internal val context: ArrayDeque<TimingGroup> = ArrayDeque()

    /**
     * Get the current context.
     */
    internal val currentTimingGroup: TimingGroup
        get() {
            return if (context.isEmpty()) {
                chart.mainTiming
            } else {
                context.last()
            }
        }

    /**
     * Serializer for [RatingClass]
     */
    object DifficultyRatingClassSerializer : KSerializer<RatingClass> {
        override fun deserialize(decoder: Decoder): Difficulty.RatingClass = when (decoder.decodeInt()) {
            0 -> Difficulty.RatingClass.PAST
            1 -> Difficulty.RatingClass.PRESENT
            2 -> Difficulty.RatingClass.FUTURE
            3 -> Difficulty.RatingClass.BEYOND
            else -> throw IllegalArgumentException("Invalid rating class")
        }

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = SerialDescriptor("Difficulty.RatingClass", Int.serializer().descriptor)

        override fun serialize(encoder: Encoder, value: Difficulty.RatingClass) {
            encoder.encodeInt(value.rating)
        }
    }
}
