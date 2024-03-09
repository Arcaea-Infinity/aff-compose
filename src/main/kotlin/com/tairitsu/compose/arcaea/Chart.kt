package com.tairitsu.compose.arcaea

import kotlin.math.roundToInt

class Chart {

    var audioOffset: Long = 0
    val mainTiming: TimingGroup = TimingGroup("main")
    val subTiming: MutableMap<String, TimingGroup> = mutableMapOf()

    fun serialize(): String {
        val sb = StringBuilder()

        sb.append("AudioOffset:${audioOffset}\r\n")
        sb.append("-\r\n")

        sb.append(mainTiming.serialize(0))
        for (timing in subTiming.values) {
            sb.append("timinggroup(${timing.specialEffects.serialize()}){\r\n")
            sb.append(timing.serialize(padding = 4))
            sb.append("};\r\n")
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

            if (a is Timing && b is Timing) {
                return 0
            }
            if (a is Note && b is Note) {
                return Note.Comparator.compare(a, b)
            }
            if (a is Timing) {
                return -1
            }
            if (b is Timing) {
                return 1
            }
            return 0
        }
    }
}

internal val Double.affFormat: String
    get() {
        val ret = ((this * 100.00).roundToInt() / 100.00).toString()
        val len = ret.length
        if (ret[len - 2] == '.') {
            return ret + "0"
        }
        return ret
    }

class Timing(val offset: Long, val bpm: Double, val beats: Double) : TimedObject {
    override val time: Long
        get() = offset

    override fun serialize(): String {
        return "timing($offset,${bpm.affFormat},${beats.affFormat});"
    }
}

enum class TimingGroupSpecialEffect(val codeName: String) {
    NO_INPUT("noinput"),
    FADING_HOLDS("fadingholds"),
    ANGLEX("anglex"),
    ANGLEY("angley"),
}

class TimingGroupSpecialEffects {

    private val effects = mutableListOf<String>()

    fun add(effect: TimingGroupSpecialEffect, extraParam: Int?) {
        if (extraParam == null) {
            effects.add(effect.codeName)
        } else {
            effects.add("${effect.codeName}${extraParam}")
        }
    }

    fun serialize(): String {
        return effects.joinToString(separator = "_")
    }
}

class TimingGroup(val name: String) {

    val specialEffects: TimingGroupSpecialEffects = TimingGroupSpecialEffects()

    internal val timing: MutableList<Timing> = mutableListOf()

    private val noteFilters: ArrayDeque<NoteFilter> = ArrayDeque()

    private val notes = mutableListOf<Note>()

    /**
     * get a copy of all [Note]
     */
    fun getNotes(): List<Note> {
        return notes.toList()
    }

    private fun applyFilterImpl(note: Note): Note {
        var ret = note

        val filterSize = noteFilters.size
        for (idx in (0 until filterSize).reversed()) {
            ret = noteFilters[idx](ret)
        }

        return ret
    }

    private fun applyFilterImpl(note: Collection<Note>): Collection<Note> {
        return note.map { applyFilterImpl(it) }
    }

    private fun Note.applyFilter(): Note {
        return applyFilterImpl(this)
    }

    private fun Collection<Note>.applyFilter(): Collection<Note> {
        return this.map { applyFilterImpl(it) }
    }

    /**
     * Add a [NoteFilter]
     */
    fun addNoteFilter(filter: NoteFilter) {
        noteFilters.addLast(filter)
    }

    /**
     * Remove the last [NoteFilter]
     */
    fun popNoteFilter() {
        noteFilters.removeLast()
    }

    /**
     * Add a [NormalNote]
     */
    fun addNormalNote(note: NormalNote): Note {
        val commitNote = note.applyFilter()
        notes.add(commitNote)
        return commitNote
    }

    /**
     * Add a [HoldNote]
     */
    fun addHoldNote(note: HoldNote): Note {
        val commitNote = note.applyFilter()
        notes.add(commitNote)
        return commitNote
    }

    /**
     * Add a [ArcNote]
     */
    fun addArcNote(note: ArcNote): Note {
        val commitNote = note.applyFilter()
        notes.add(commitNote)
        return commitNote
    }

    fun addSpecialEffect(effect: TimingGroupSpecialEffect, extraParam: Int?) {
        specialEffects.add(effect, extraParam)
    }

    fun addSpecialEffect(effect: TimingGroupSpecialEffect) {
        if (effect == TimingGroupSpecialEffect.ANGLEX || effect == TimingGroupSpecialEffect.ANGLEY) {
            throw IllegalArgumentException("Effect `${effect.codeName}` needs a parameter")
        }
        specialEffects.add(effect, null)
    }

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
        get() = isGuidingLineField || tapTimestampList.isNotEmpty()

    private val tapTimestampList: MutableList<Int> = mutableListOf()

    val tapList: ArcTapList
        get() = ArcTapList(tapTimestampList)

    init {
        arcTapClosure(tapList)
    }

    override fun serialize(): String {
        val sb = StringBuilder()
        sb.append("arc(${time},${endTime},${startPosition.x.affFormat},${endPosition.x.affFormat},${curveType.value},${startPosition.y.affFormat},${endPosition.y.affFormat},${color.value},$padding,$isGuidingLine)")
        if (tapTimestampList.isNotEmpty()) {
            tapTimestampList.sort()
            sb.append("[")
            for (idx in tapTimestampList.indices) {
                val tap = tapTimestampList[idx]
                sb.append("arctap(${tap})")
                if (idx < tapTimestampList.size - 1) {
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
        fun tap(vararg tap: Int) {
            tap.forEach { tapList.add(it) }
        }

        fun arctap(vararg tap: Int) {
            tap(*tap)
        }

        fun arcTap(vararg tap: Int) {
            tap(*tap)
        }

        val data: MutableList<Int>
            get() = tapList

    }
}

class Position(
    val x: Double = 0.0,
    val y: Double = 0.0,
)