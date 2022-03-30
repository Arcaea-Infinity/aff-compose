package com.tairitsu.compose

import kotlin.math.roundToInt

class Chart {
    val audioOffset: Long = 0

    val mainTiming: TimingGroup = TimingGroup("main")

    val subTiming: MutableMap<String, TimingGroup> = mutableMapOf()

    fun serialize(): String {
        val sb = StringBuilder()

        sb.append("AudioOffset:0\r\n")
        sb.append("-\r\n")

        sb.append(mainTiming.serialize(0))
        for (timing in subTiming.values) {
            sb.append("timinggroup(){\r\n")
            sb.append(timing.serialize(padding = 4))
            sb.append("}\r\n")
        }

        return sb.toString().trim { it <= ' ' }
    }
}

interface TimedObject {
    val time: Long
    fun serialize(): String

    object Comparator : kotlin.Comparator<TimedObject> {
        override fun compare(a: TimedObject, b: TimedObject): Int {
            // sort by time
            val timeCmp = a.time.compareTo(b.time)
            if (timeCmp != 0) return timeCmp

            if (a is TimingGroup.Timing && b is TimingGroup.Timing) {
                return 0
            }
            if (a is Note && b is Note) {
                return Note.Comparator.compare(a, b)
            }
            if (a is TimingGroup.Timing) {
                return -1
            }
            if (b is TimingGroup.Timing) {
                return 1
            }
            return 0
        }
    }
}

val Double.affFormat: String
    get() {
        val ret = ((this * 100.00).roundToInt() / 100.00).toString()
        val len = ret.length
        if (ret[len - 2] == '.') {
            return ret + "0"
        }
        return ret
    }

class TimingGroup(val name: String) {
    val timing: MutableList<Timing> = mutableListOf()

    class Timing(val offset: Long, val bpm: Double, val beats: Double) : TimedObject {
        override val time: Long
            get() = offset

        override fun serialize(): String {
            return "timing($offset,${bpm.affFormat},${beats.affFormat});"
        }
    }

    val notes: MutableList<Note> = mutableListOf()

    fun serialize(padding: Int): String {
        val `object` = mutableListOf<TimedObject>()
        `object`.addAll(notes)
        `object`.addAll(timing)
        `object`.sortWith(TimedObject.Comparator)

        val sb = StringBuilder()
        for (n in `object`) {
            if (padding > 0) {
                sb.append(" ".repeat(padding))
            }
            sb.append(n.serialize()).append("\r\n")
        }
        return sb.toString()
    }

    override fun toString(): String {
        return name
    }
}

abstract class Note : TimedObject {
    override fun toString(): String = serialize()

    object Comparator : kotlin.Comparator<Note> {
        override fun compare(a: Note, b: Note): Int {
            // sort by time
            val timeCmp = a.time.compareTo(b.time)
            if (timeCmp != 0) return timeCmp

            // sort by column
            if (a is KeyboardNote && b is KeyboardNote) {
                val keyCmp = a.column.compareTo(b.column)
                if (keyCmp != 0) return keyCmp
            }

            // sort by type
            if (a is KeyboardNote && b !is KeyboardNote) return -1
            if (a !is KeyboardNote && b is KeyboardNote) return 1

            if (a is ArcNote && b is ArcNote) {
                if (a.isGuidingLine && !b.isGuidingLine) return -1
                if (!a.isGuidingLine && b.isGuidingLine) return 1

                if (a.startPosition.x < b.startPosition.x) return -1
                if (a.startPosition.x > b.startPosition.x) return 1

                if (a.startPosition.y < b.startPosition.y) return -1
                if (a.startPosition.y > b.startPosition.y) return 1
            }
            return 0
        }
    }
}

abstract class KeyboardNote : Note() {
    abstract val column: Int
}

class NormalNote(
    override val time: Long,
    override val column: Int,
) : KeyboardNote() {
    override fun serialize(): String = "($time,$column);"
}

class HoldNote(
    override val time: Long,
    val endTime: Long,
    override val column: Int,
) : KeyboardNote() {
    override fun serialize(): String = "hold($time,$endTime,$column);"
}

class ArcNote(
    override val time: Long,
    val endTime: Long,
    val startPosition: Position,
    val curveType: Type,
    val endPosition: Position,
    val color: Color,
    isGuidingLine: Boolean,
    arcTapClosure: (ArcTapList.() -> Unit) = {},
) : Note() {

    constructor(
        time: Long,
        endTime: Long,
        startPosition: Pair<Double, Double>,
        curveType: Type,
        endPosition: Pair<Double, Double>,
        color: Color,
        isGuidingLine: Boolean,
        arcTapClosure: (ArcTapList.() -> Unit) = {},
    ) : this(
        time,
        endTime,
        Position(startPosition.first, startPosition.second),
        curveType,
        Position(endPosition.first, endPosition.second),
        color,
        isGuidingLine,
        arcTapClosure
    )

    val padding = "none"

    private val isGuidingLineField: Boolean = isGuidingLine

    val isGuidingLine: Boolean
        get() = isGuidingLineField || tapList.isNotEmpty()

    private val tapList: MutableList<Int> = mutableListOf()

    init {
        arcTapClosure(ArcTapList(tapList))
    }

    override fun serialize(): String {
        val sb = StringBuilder()
        sb.append("arc(${time},${endTime},${startPosition.x.affFormat},${endPosition.x.affFormat},${curveType.value},${startPosition.y.affFormat},${endPosition.y.affFormat},${color.value},$padding,$isGuidingLine)")
        if (tapList.isNotEmpty()) {
            tapList.sort()
            sb.append("[")
            for (idx in tapList.indices) {
                val tap = tapList[idx]
                sb.append("arctap(${tap})")
                if (idx < tapList.size - 1) {
                    sb.append(",")
                }
            }
            sb.append("]")
        }
        sb.append(";")
        return sb.toString()
    }

    enum class Type(val value: String) {
        S("s"),
        B("b"),
        SI("si"),
        SO("so"),
        SISI("sisi"),
        SOSO("soso"),
        SISO("siso"),
        SOSI("sosi"),
    }

    enum class Color(val value: Int) {
        BLUE(0),
        RED(1),
    }

    class ArcTapList(private val tapList: MutableList<Int>) {
        fun tap(tap: Int) {
            tapList.add(tap)
        }

        fun arctap(tap: Int) {
            tapList.add(tap)
        }
    }
}

class Position(
    val x: Double = 0.0,
    val y: Double = 0.0,
)