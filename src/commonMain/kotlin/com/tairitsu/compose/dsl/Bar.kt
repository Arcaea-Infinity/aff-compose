package com.tairitsu.compose.dsl

import com.tairitsu.compose.*
import kotlin.math.roundToLong
import com.tairitsu.compose.ArcNote as MappingArcNote

/**
 * Bar definition.
 *
 * [diff] is the difficulty we are modifying.
 * [count] is the bar number we are modifying.
 */
class Bar(private val diff: Difficulty, private val count: Int) {

    /**
     * Current timing group.
     * [currentTiming] is the timing group we are modifying when we create this [Bar] instance.
     */
    private val currentTimingGroup: TimingGroup = diff.currentTimingGroup

    /**
     * Current timing.
     * [currentTiming] is the timing group we are modifying when we create this [Bar] instance. is the timing group we are modifying when we create this [Bar] instance.
     */
    private val currentTiming: TimingGroup.Timing = currentTimingGroup.timing.last()

    /**
     * Division for a bar
     */
    private var divide: Double = currentTiming.beats

    /**
     * How long a bar is
     */
    private val TimingGroup.Timing.barDuration: Double
        get() = 60_000 * this.beats / this.bpm

    /**
     * How long a beat is
     * This beat is not the beat from [currentTiming], this beat is a [Bar] divided by [divide]
     */
    private val beatDuration: Double
        get() = currentTiming.barDuration / divide

    /**
     * Bar start time
     */
    private val barStartTime = (currentTiming.barDuration * count + currentTiming.offset)

    /**
     * Bar start time
     */
    val startTime = barStartTime.roundToLong()

    /**
     * Calculate the milliseconds for a beat
     */
    private fun calculateTimestamp(beat: Double): Long
        = (barStartTime + beat * beatDuration).roundToLong()


    /**
     * Calculate the milliseconds for a beat
     */
    private fun calculateTimestamp(beat: Int): Long
        = (barStartTime + beat * beatDuration).roundToLong()

    /**
     * Calculate the milliseconds for a beat
     */
    fun Number.toTimestamp(): Long
        = calculateTimestamp(this.toDouble())

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
        startPosition: Pair<Double, Double>,
        curveType: MappingArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    fun arcNote(
        time: Double,
        endTime: Int,
        startPosition: Pair<Double, Double>,
        curveType: MappingArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    fun arcNote(
        time: Int,
        endTime: Double,
        startPosition: Pair<Double, Double>,
        curveType: MappingArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    fun arcNote(
        time: Int,
        endTime: Int,
        startPosition: Pair<Double, Double>,
        curveType: MappingArcNote.Type,
        endPosition: Pair<Double, Double>,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    fun arcNote(
        time: Double,
        endTime: Double,
        startPosition: Position,
        curveType: MappingArcNote.Type,
        endPosition: Position,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    fun arcNote(
        time: Double,
        endTime: Int,
        startPosition: Position,
        curveType: MappingArcNote.Type,
        endPosition: Position,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    fun arcNote(
        time: Int,
        endTime: Double,
        startPosition: Position,
        curveType: MappingArcNote.Type,
        endPosition: Position,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    fun arcNote(
        time: Int,
        endTime: Int,
        startPosition: Position,
        curveType: MappingArcNote.Type,
        endPosition: Position,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (Bar.ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure))

    /**
     * ArcNote Proxy
     */
    class ArcNote {
        /**
         * ArcNote Tap List Proxy
         *
         * Create an ArcNote Tap List with bar based timestamp.
         */
        class ArcTapList(val bar: Bar, private val addTap: (Int) -> Unit) {
            fun tap(tap: Int) {
                addTap(bar.calculateTimestamp(tap).toInt())
            }

            fun arctap(tap: Int) {
                addTap(bar.calculateTimestamp(tap).toInt())
            }

            companion object {
                fun createProxy(
                    bar: Bar,
                    arcTapClosure: (ArcTapList.() -> Unit),
                ): (MappingArcNote.ArcTapList.() -> Unit) {
                    return {
                        val proxy = ArcTapList(
                            bar = bar,
                            addTap = fun(time: Int) {
                                arctap(time)
                            }
                        )
                        arcTapClosure(proxy)
                    }
                }
            }
        }
    }
}
