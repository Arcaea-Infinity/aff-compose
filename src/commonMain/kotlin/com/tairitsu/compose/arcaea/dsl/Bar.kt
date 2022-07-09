package com.tairitsu.compose.arcaea.dsl

import com.tairitsu.compose.*
import com.tairitsu.compose.arcaea.*
import kotlin.math.roundToLong
import com.tairitsu.compose.arcaea.ArcNote as MappingArcNote

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
    private fun calculateTimestamp(beat: Number): Long = (barStartTime + beat.toDouble() * beatDuration).roundToLong()

    /**
     * Calculate the milliseconds for a beat
     */
    fun Number.toTimestamp(): Long = calculateTimestamp(this.toDouble())

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
    fun <TTime : Number> normalNote(time: TTime, column: Int) = diff.normalNote(calculateTimestamp(time), column)

    // Hold Note
    fun <TTime : Number, TEndTime : Number> holdNote(time: TTime, endTime: TEndTime, column: Int) =
        diff.holdNote(calculateTimestamp(time), calculateTimestamp(endTime), column)

    // Arc Note
    fun <TTime : Number, TEndTime : Number, TStartPositionX : Number, TStartPositionY : Number, TEndPositionX : Number, TEndPositionY : Number> arcNote(
        time: TTime,
        endTime: TEndTime,
        startPosition: Pair<TStartPositionX, TStartPositionY>,
        curveType: MappingArcNote.Type,
        endPosition: Pair<TEndPositionX, TEndPositionY>,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure)
    )


    fun <TTime : Number, TEndTime : Number> arcNote(
        time: TTime,
        endTime: TEndTime,
        startPosition: Position,
        curveType: MappingArcNote.Type,
        endPosition: Position,
        color: MappingArcNote.Color? = null,
        isGuidingLine: Boolean = color == null,
        arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
    ) = diff.arcNote(calculateTimestamp(time),
        calculateTimestamp(endTime),
        startPosition,
        curveType,
        endPosition,
        color,
        isGuidingLine,
        ArcNote.ArcTapList.createProxy(this, arcTapClosure)
    )

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
            fun tap(tap: Number) {
                addTap(bar.calculateTimestamp(tap).toInt())
            }

            fun arctap(tap: Number) {
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
