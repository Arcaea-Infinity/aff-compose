package com.tairitsu.compose

import com.benasher44.uuid.uuid4

/**
 * Creating a new instance of [MapSet]
 */
fun mapSet(closure: MapSet.() -> Unit): MapSet {
    val mapSet = MapSet()
    mapSet.closure()
    return mapSet
}

fun MapSet.build(path: String) {

}

/**
 * Get the existing or creating a new past [Difficulty] of the [MapSet]
 */
fun Difficulties.past(closure: Difficulty.() -> Unit) {
    val difficulty = this.past ?: Difficulty()
    difficulty.closure()
    this.past = difficulty
}

/**
 * Get the existing or creating a new present [Difficulty] of the [MapSet]
 */
fun Difficulties.present(closure: Difficulty.() -> Unit) {
    val difficulty = this.present ?: Difficulty()
    difficulty.closure()
    this.present = difficulty
}

/**
 * Get the existing or creating a new future [Difficulty] of the [MapSet]
 */
fun Difficulties.future(closure: Difficulty.() -> Unit) {
    val difficulty = this.future ?: Difficulty()
    difficulty.closure()
    this.future = difficulty
}

/**
 * Get the existing or creating a new past [Difficulty] of the [MapSet]
 */
fun Difficulties.beyond(closure: Difficulty.() -> Unit) {
    val difficulty = this.beyond ?: Difficulty()
    difficulty.closure()
    this.beyond = difficulty
}

// Timing

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

// Timing group

/**
 * Get the existing or creating a new [TimingGroup] of the [Difficulty]
 */
fun Difficulty.timingGroup(name: String = uuid4().toString(), closure: TimingGroup.() -> Unit): TimingGroup {
    val newTimingGroup = chart.subTiming.getOrPut(name) { TimingGroup(name) }
    context.addLast(newTimingGroup)
    closure(newTimingGroup)
    context.removeLast()
    return newTimingGroup
}

// Normal Note

fun Difficulty.normalNote(time: Int, column: Int) = normalNote(time.toLong(), column)
fun Difficulty.normalNote(time: Long, column: Int): NormalNote {
    val ctx = this.currentTimingGroup
    val ret = NormalNote(time, column)
    ctx.notes.add(ret)
    return ret
}

// Hold Note

fun Difficulty.holdNote(time: Int, endTime: Int, column: Int) = holdNote(time.toLong(), endTime.toLong(), column)
fun Difficulty.holdNote(time: Long, endTime: Int, column: Int) = holdNote(time, endTime.toLong(), column)
fun Difficulty.holdNote(time: Int, endTime: Long, column: Int) = holdNote(time.toLong(), endTime, column)
fun Difficulty.holdNote(time: Long, endTime: Long, column: Int): HoldNote {
    val ctx = this.currentTimingGroup
    val ret = HoldNote(time, endTime, column)
    ctx.notes.add(ret)
    return ret
}

// ArcNote

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
