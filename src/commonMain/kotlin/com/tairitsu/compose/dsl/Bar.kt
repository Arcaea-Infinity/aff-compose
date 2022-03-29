package com.tairitsu.compose.dsl

import com.tairitsu.compose.*
import kotlin.math.roundToLong

/**
 * Bar definition.
 *
 * [diff] is the difficulty we are modifying.
 * [count] is the bar number we are modifying.
 */
class Bar(val diff: Difficulty, val count: Int) {

    /**
     * Current timing group.
     * [currentTiming] is the timing group we are modifying when we create this [Bar] instance.
     */
    private val currentTimingGroup: TimingGroup = diff.currentTimingGroup

    /**
     * Current timing.
     * [currentTiming] is the timing group we are modifying when we create this [Bar] instance.] is the timing group we are modifying when we create this [Bar] instance.
     */
    private val currentTiming: TimingGroup.Timing = currentTimingGroup.timing.last()

    /**
     * Division for a bar
     */
    private var divide: Double = 4.0

    /**
     * How long a beat is
     */
    private val TimingGroup.Timing.beatDuration: Double
        get() = 60000 / this.bpm

    /**
     * Calculate the milliseconds for a beat
     */
    private fun calculateTimestamp(beat: Double): Long {
        return ((beat + count * divide) * currentTiming.beatDuration / divide).roundToLong()
    }

    /**
     * Calculate the milliseconds for a beat
     */
    private fun calculateTimestamp(beat: Int): Long {
        return ((beat + count * divide) * currentTiming.beatDuration / divide).roundToLong()
    }

    /**
     * Divide one bar into [val] parts
     */
    fun d(`val`: Int) {
        divide = `val`.toDouble()
    }

    /**
     * Divide one bar into [val] parts
     */
    fun d(`val`: Double) {
        divide = `val`
    }

    // Normal Note
    
    fun normalNote(time: Double, column: Int) = diff.normalNote(calculateTimestamp(time), column)
    fun normalNote(time: Int, column: Int) = diff.normalNote(calculateTimestamp(time), column)

    // Hold Note
    
    fun holdNote(time: Double, endTime: Double, column: Int) =
        diff.holdNote(calculateTimestamp(time), calculateTimestamp(endTime), column)
    fun holdNote(time: Double, endTime: Int, column: Int) =
        diff.holdNote(calculateTimestamp(time), calculateTimestamp(endTime), column)
    fun holdNote(time: Int, endTime: Double, column: Int) =
        diff.holdNote(calculateTimestamp(time), calculateTimestamp(endTime), column)
    fun holdNote(time: Int, endTime: Int, column: Int) =
        diff.holdNote(calculateTimestamp(time), calculateTimestamp(endTime), column)

    // Arc Note
    
    fun arcNote(
        time: Double,
        endTime: Double,
        startPosition: Position,
        curveType: ArcNote.Type,
        endPosition: Position,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    fun arcNote(
        time: Double,
        endTime: Int,
        startPosition: Position,
        curveType: ArcNote.Type,
        endPosition: Position,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    fun arcNote(
        time: Int,
        endTime: Double,
        startPosition: Position,
        curveType: ArcNote.Type,
        endPosition: Position,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    fun arcNote(
        time: Int,
        endTime: Int,
        startPosition: Position,
        curveType: ArcNote.Type,
        endPosition: Position,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    fun arcNote(
        time: Double,
        endTime: Double,
        startPosition: Pair<Double, Double>,
        curveType: ArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    fun arcNote(
        time: Double,
        endTime: Int,
        startPosition: Pair<Double, Double>,
        curveType: ArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    fun arcNote(
        time: Int,
        endTime: Double,
        startPosition: Pair<Double, Double>,
        curveType: ArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
    fun arcNote(
        time: Int,
        endTime: Int,
        startPosition: Pair<Double, Double>,
        curveType: ArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: ArcNote.Color = ArcNote.Color.BLUE,
        isGuidingLine: Boolean = true,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time), calculateTimestamp(endTime), startPosition, curveType, endPosition, color, isGuidingLine, arcTapClosure)
}