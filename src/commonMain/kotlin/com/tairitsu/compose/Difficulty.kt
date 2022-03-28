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


    @Transient
    val content: Chart = Chart()

    @Transient
    internal val context: ArrayDeque<TimingGroup> = ArrayDeque()

    @Transient
    internal val currentTimingGroup: TimingGroup
        get() {
            return if (context.isEmpty()) {
                content.mainTiming
            } else {
                context.last()
            }
        }
}

object DifficultyRatingClassSerializer : KSerializer<Difficulty.RatingClass> {
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

fun Difficulty.timing(offset: Int, bpm: Double, beats: Double) = timing(offset.toLong(), bpm, beats)
fun Difficulty.timing(offset: Int, bpm: Int, beats: Double) = timing(offset.toLong(), bpm.toDouble(), beats)
fun Difficulty.timing(offset: Int, bpm: Double, beats: Int) = timing(offset.toLong(), bpm, beats.toDouble())
fun Difficulty.timing(offset: Int, bpm: Int, beats: Int) = timing(offset.toLong(), bpm.toDouble(), beats.toDouble())
fun Difficulty.timing(offset: Long, bpm: Double, beats: Int) = timing(offset, bpm, beats.toDouble())
fun Difficulty.timing(offset: Long, bpm: Int, beats: Double) = timing(offset, bpm.toDouble(), beats)
fun Difficulty.timing(offset: Long, bpm: Int, beats: Int) = timing(offset, bpm.toDouble(), beats.toDouble())
fun Difficulty.timing(offset: Long, bpm: Double, beats: Double): TimingGroup.Timing {
    val ctx = this.currentTimingGroup
    val ret = TimingGroup.Timing(offset, bpm, beats)
    ctx.timing.add(ret)
    return ret
}

fun Difficulty.timingGroup(name: String = uuid4().toString(), closure: TimingGroup.() -> Unit): TimingGroup {
    val newTimingGroup = TimingGroup(name)
    content.subTiming[name] = newTimingGroup
    context.addLast(newTimingGroup)
    closure(newTimingGroup)
    context.removeLast()
    return newTimingGroup
}

fun Difficulty.normalNote(time: Int, column: Int) = normalNote(time.toLong(), column)
fun Difficulty.normalNote(time: Long, column: Int): NormalNote {
    val ctx = this.currentTimingGroup
    val ret = NormalNote(time, column)
    ctx.notes.add(ret)
    return ret
}

fun Difficulty.holdNote(time: Int, end: Int, column: Int) = holdNote(time.toLong(), end.toLong(), column)
fun Difficulty.holdNote(time: Long, end: Int, column: Int) = holdNote(time, end.toLong(), column)
fun Difficulty.holdNote(time: Int, end: Long, column: Int) = holdNote(time.toLong(), end, column)
fun Difficulty.holdNote(time: Long, end: Long, column: Int): HoldNote {
    val ctx = this.currentTimingGroup
    val ret = HoldNote(time, end, column)
    ctx.notes.add(ret)
    return ret
}

val Difficulty.s: ArcNote.Type
    get() = ArcNote.Type.S
val Difficulty.si: ArcNote.Type
    get() = ArcNote.Type.SI
val Difficulty.so: ArcNote.Type
    get() = ArcNote.Type.SO
val Difficulty.siso: ArcNote.Type
    get() = ArcNote.Type.SISO
val Difficulty.sosi: ArcNote.Type
    get() = ArcNote.Type.SOSI
val Difficulty.sisi: ArcNote.Type
    get() = ArcNote.Type.SISI
val Difficulty.soso: ArcNote.Type
    get() = ArcNote.Type.SOSO

fun Difficulty.arcNote(
    time: Int,
    endTime: Int,
    startPosition: Position,
    curveType: ArcNote.Type,
    endPosition: Position,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
) = arcNote(time.toLong(), endTime.toLong(), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)

fun Difficulty.arcNote(
    time: Long,
    endTime: Int,
    startPosition: Position,
    curveType: ArcNote.Type,
    endPosition: Position,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
) = arcNote(time, endTime.toLong(), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)

fun Difficulty.arcNote(
    time: Int,
    endTime: Long,
    startPosition: Position,
    curveType: ArcNote.Type,
    endPosition: Position,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
) = arcNote(time.toLong(), endTime, startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)

fun Difficulty.arcNote(
    time: Long,
    endTime: Long,
    startPosition: Position,
    curveType: ArcNote.Type,
    endPosition: Position,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
): ArcNote {
    val ctx = this.currentTimingGroup
    val ret = ArcNote(time, endTime, startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    ctx.notes.add(ret)
    return ret
}

fun Difficulty.arcNote(
    time: Int,
    endTime: Int,
    startPosition: Pair<Double, Double>,
    curveType: ArcNote.Type,
    endPosition: Pair<Double, Double>,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
) = arcNote(time.toLong(), endTime.toLong(), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)

fun Difficulty.arcNote(
    time: Long,
    endTime: Int,
    startPosition: Pair<Double, Double>,
    curveType: ArcNote.Type,
    endPosition: Pair<Double, Double>,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
) = arcNote(time, endTime.toLong(), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)

fun Difficulty.arcNote(
    time: Int,
    endTime: Long,
    startPosition: Pair<Double, Double>,
    curveType: ArcNote.Type,
    endPosition: Pair<Double, Double>,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
) = arcNote(time.toLong(), endTime, startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)

fun Difficulty.arcNote(
    time: Long,
    endTime: Long,
    startPosition: Pair<Double, Double>,
    curveType: ArcNote.Type,
    endPosition: Pair<Double, Double>,
    color: ArcNote.Color = ArcNote.Color.BLUE,
    isGuidingLine: Boolean = true,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
): ArcNote {
    val ctx = this.currentTimingGroup
    val ret = ArcNote(time, endTime, startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    ctx.notes.add(ret)
    return ret
}
