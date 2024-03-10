package com.tairitsu.compose.arcaea

import java.io.Serializable
import java.text.DecimalFormat
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

            if ((a is Timing && b is Timing) || (a is Scenecontrol && b is Scenecontrol)) {
                return 0
            }
            if (a is Note && b is Note) {
                return Note.Comparator.compare(a, b)
            }
            if (a is Timing || a is Scenecontrol) {
                return -1
            }
            if (b is Timing || b is Scenecontrol) {
                return 1
            }
            return 0
        }
    }
}

internal val Double.affFormat: String
    get() {
        return DecimalFormat("#0.00").format((this * 100.00).roundToInt() / 100.00)
    }

class Timing(val offset: Long, val bpm: Double, val beats: Double) : TimedObject {
    override val time: Long
        get() = offset

    override fun serialize(): String {
        return "timing($offset,${bpm.affFormat},${beats.affFormat});"
    }
}

@Suppress("unused")
enum class ScenecontrolType(val id: String, val paramReq1: Boolean, val paramReq2: Boolean) {
    TRACK_HIDE("trackhide", false, false),
    TRACK_SHOW("trackshow", false, false),
    TRACK_DISPLAY("trackdisplay", true, true),
    RED_LINE("redline", true, false),
    ARCAHV_DISTORT("arcahvdistort", true, true),
    ARCAHV_DEBRIS("arcahvdebris", true, true),
    HIDE_GROUP("hidegroup", false, true),
    ENWIDEN_CAMERA("enwidencamera", true, true),
    ENWIDEN_LANES("enwidenlanes", true, true)
}

class Scenecontrol(
    override val time: Long,
    val type: ScenecontrolType,
    val param1: Double?,
    val param2: Int?
) : TimedObject {
    override fun serialize(): String {
        val params = when {
            !type.paramReq1 && !type.paramReq2 -> {
                ""
            }

            !type.paramReq1 && type.paramReq2 -> {
                ",0.00,${param2!!}"
            }

            type.paramReq1 && !type.paramReq2 -> {
                ",${param1!!.affFormat},0"
            }

            else -> {
                ",${param1!!.affFormat},${param2!!}"
            }

        }
        return "scenecontrol(${time.toBigDecimal()},${type.id}${params});"
    }
}

@Suppress("unused")
enum class TimingGroupSpecialEffectType(val codeName: String) {
    NO_INPUT("noinput"),
    FADING_HOLDS("fadingholds"),
    ANGLEX("anglex"),
    ANGLEY("angley"),
}

class TimingGroupSpecialEffects {

    private val effects = mutableListOf<String>()

    fun add(effect: TimingGroupSpecialEffectType, extraParam: Int?) {
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

    private val scenecontrols = mutableListOf<Scenecontrol>()

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
     * Add a [Scenecontrol]
     */
    fun addScenecontrol(sc: Scenecontrol) {
        scenecontrols.add(sc)
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

    fun addSpecialEffect(effect: TimingGroupSpecialEffectType, extraParam: Int?) {
        specialEffects.add(effect, extraParam)
    }

    fun addSpecialEffect(effect: TimingGroupSpecialEffectType) {
        if (effect == TimingGroupSpecialEffectType.ANGLEX || effect == TimingGroupSpecialEffectType.ANGLEY) {
            throw IllegalArgumentException("Effect `${effect.codeName}` needs a parameter")
        }
        specialEffects.add(effect, null)
    }

    fun serialize(padding: Int): String {
        val `object` = mutableListOf<TimedObject>()
        `object`.addAll(notes)
        `object`.addAll(timing)
        `object`.addAll(scenecontrols)
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

data class Position(
    var x: Double,
    var y: Double,
) : Serializable {

    /**
     * Returns string representation of the [Position] including its [x] and [y] values.
     */
    override fun toString(): String = "($x, $y)"

    fun toList(): List<Double> = listOf(x, y)

    fun toPair(): Pair<Double, Double> = x to y
}

infix fun <A : Number, B : Number> A.pos(that: B): Position = Position(this.toDouble(), that.toDouble())

fun Pair<Double, Double>.toPosition(): Position {
    return this.first pos this.second
}