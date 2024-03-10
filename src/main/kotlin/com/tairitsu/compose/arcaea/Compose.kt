package com.tairitsu.compose.arcaea

import com.benasher44.uuid.uuid4
import com.tairitsu.compose.arcaea.Difficulty.Companion.timingGroupStack
import io.ktor.utils.io.core.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Creating a new instance of [MapSet]
 */
fun mapSet(closure: MapSet.() -> Unit): MapSet {
    val mapSet = MapSet()
    mapSet.closure()
    return mapSet
}

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    encodeDefaults = true
    explicitNulls = false
}

fun MapSet.writeToFile(outputDirectory: String) {
    val json = Json { prettyPrint = true }

    val songDataFile = File(outputDirectory, "song_data.json")
    songDataFile.writeText(json.encodeToString(MapSet.serializer(), this))

    this.difficulties.forEach { difficulty ->
        val difficultyFile = File(outputDirectory, "${difficulty.ratingClass.rating}.aff")
        difficultyFile.writeText(difficulty.chart.serialize())
    }

    val lineSeparator = System.lineSeparator()

    val songConfigFile = File(outputDirectory, "songconfig.txt").bufferedWriter()
    songConfigFile.use { writer ->
        writer.write("id=${this.id}$lineSeparator")
        writer.write("title=${this.titleLocalized.en}$lineSeparator")
        writer.write("artist=${this.artist}$lineSeparator")
        writer.write(
            "designer=${
                this.difficulties.map { it.chartDesigner }.toSet().joinToString(separator = ",")
            }$lineSeparator"
        )
        writer.write("bpm_disp=${this.bpm}$lineSeparator")
        writer.write("bpm_base=${this.bpmBase}$lineSeparator")
        writer.write("side=${this.side.id}$lineSeparator")
        writer.write(
            "diff=${
                this.difficulties.past?.rating ?: 0
            }-${
                this.difficulties.present?.rating ?: 0
            }-${
                this.difficulties.future?.rating ?: 0
            }-${
                this.difficulties.beyond?.rating ?: 0
            }$lineSeparator"
        )
    }
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
fun <TOffset : Number, TBpm : Number, TBeat : Number> Difficulty.timing(
    offset: TOffset,
    bpm: TBpm,
    beats: TBeat,
): Timing {
    val ctx = this.currentTimingGroup
    val ret = Timing(offset.toLong(), bpm.toDouble(), beats.toDouble())
    ctx.timing.add(ret)
    return ret
}

// Timing group

/**
 * Get the existing or creating a new [TimingGroup] of the [Difficulty]
 */
fun Difficulty.timingGroup(name: String = uuid4().toString(), closure: TimingGroup.() -> Unit): TimingGroup {
    val newTimingGroup = chart.subTiming.getOrPut(name) { TimingGroup(name) }
    context.timingGroupStack.addLast(newTimingGroup)
    closure(newTimingGroup)
    context.timingGroupStack.removeLast()
    return newTimingGroup
}

// Scenecontrol

fun Difficulty.scenecontrol(
    time: Long,
    type: ScenecontrolType,
    param1: Double,
    param2: Int
) {
    val ctx = this.currentTimingGroup
    val sc = Scenecontrol(time, type, param1, param2)
    return ctx.addScenecontrol(sc)
}

fun Difficulty.scenecontrol(
    time: Long,
    type: ScenecontrolType,
    param2: Int
) {
    val ctx = this.currentTimingGroup
    val sc = Scenecontrol(time, type, null, param2)
    return ctx.addScenecontrol(sc)
}

fun Difficulty.scenecontrol(
    time: Long,
    type: ScenecontrolType,
    param1: Double,
) {
    val ctx = this.currentTimingGroup
    val sc = Scenecontrol(time, type, param1, null)
    return ctx.addScenecontrol(sc)
}

fun Difficulty.scenecontrol(
    time: Long,
    type: ScenecontrolType,
) {
    val ctx = this.currentTimingGroup
    val sc = Scenecontrol(time, type, null, null)
    return ctx.addScenecontrol(sc)
}

// Normal Note

fun <TTime : Number> Difficulty.normalNote(time: TTime, column: Int): Note {
    val ctx = this.currentTimingGroup
    val note = NormalNote(time.toLong(), column)
    return ctx.addNormalNote(note)
}

// Hold Note

fun <TTime : Number, TEndTime : Number> Difficulty.holdNote(time: TTime, endTime: TEndTime, column: Int): Note {
    val ctx = this.currentTimingGroup
    val note = HoldNote(time.toLong(), endTime.toLong(), column)
    return ctx.addHoldNote(note)
}

// ArcNote

val Difficulty.s: ArcNote.Type
    get() = ArcNote.Type.S
val Difficulty.b: ArcNote.Type
    get() = ArcNote.Type.B
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

fun <TTime : Number, TEndTime : Number> Difficulty.arcNote(
    time: TTime,
    endTime: TEndTime,
    startPosition: Position,
    curveType: ArcNote.Type,
    endPosition: Position,
    color: ArcNote.Color? = null,
    isGuidingLine: Boolean = color == null,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
): Note {
    val ctx = this.currentTimingGroup
    val note = ArcNote(
        time.toLong(),
        endTime.toLong(),
        startPosition,
        curveType,
        endPosition,
        color ?: ArcNote.Color.BLUE,
        isGuidingLine,
        arcTapClosure
    )
    return ctx.addArcNote(note)
}

fun <TTime : Number, TEndTime : Number, TStartPositionX : Number, TStartPositionY : Number, TEndPositionX : Number, TEndPositionY : Number> Difficulty.arcNote(
    time: TTime,
    endTime: TEndTime,
    startPosition: Pair<TStartPositionX, TStartPositionY>,
    curveType: ArcNote.Type,
    endPosition: Pair<TEndPositionX, TEndPositionY>,
    color: ArcNote.Color? = null,
    isGuidingLine: Boolean = color == null,
    arcTapClosure: (ArcNote.ArcTapList.() -> Unit) = {},
): Note {
    val ctx = this.currentTimingGroup
    val note = ArcNote(
        time.toLong(),
        endTime.toLong(),
        startPosition.first.toDouble() to startPosition.second.toDouble(),
        curveType,
        endPosition.first.toDouble() to endPosition.second.toDouble(),
        color ?: ArcNote.Color.BLUE,
        isGuidingLine,
        arcTapClosure
    )
    return ctx.addArcNote(note)
}
