package com.tairitsu.compose.arcaea

import com.tairitsu.compose.arcaea.dsl.Bar
import com.tairitsu.compose.arcaea.dsl.bar
import java.io.File
import kotlin.math.floor
import kotlin.test.Test

object DemoMap {

    @Test
    fun main() {
        mapSet {
            id = "composingdream"
            titleLocalized = LocalizedString("Composing Dream")
            idx = 330
            artist = "ZZM"
            bpm = "124"
            bpmBase = 124.0
            set = "dream"
            purchase = "dream"
            audioPreview = 31985L
            audioPreviewEnd = 51340L
            side = MapSet.Side.LIGHT

            difficulties.future {
                rating = 0
                chartDesigner = "Eric_Lian"
                jacketDesigner = "Eric_Lian"


                // The timing setting for your main timing group
                timing(
                    // The first bar starts at 1986ms
                    offset = 1986,
                    // The speed of this song is 124bpm
                    bpm = 124,
                    // 4 beats for every bar
                    beats = 4,
                )

                // Bar number and beat number starts at 0

                // How to create a normal note.
                listOf(3, 1, 4, 2).withIndex().forEach { (index, value) ->
                    // Designing the [index]th bar
                    bar(index) {
                        // Create a normal note at the first beat of the [index]th bar
                        normalNote(0, value)
                    }
                }

                // How to create a hold note.
                listOf(3, 1, 4, 2).withIndex().forEach { (index, value) ->
                    // Designing the [index + 4]th bar
                    bar(index + 4) {
                        // Create a hold note at the first beat of the [index + 4]th bar,
                        // and this hold ends at the third beat of the [index + 4]th bar
                        holdNote(0, 3, value)
                    }
                }

                // How to create an arc note.
                bar(8) {
                    // Create an arc note at the first beat of the 8th bar, and this arc ends at the third beat of the 8th bar
                    arcNote(0, 3, 0.0 to 1.0, s, 0.0 to 1.0, ArcNote.Color.BLUE)

                    // Of course, you can create a note not located at the 8th bar.
                    // Create an arc note at the first beat of the 9th bar, and this arc ends at the third beat of the 9th bar
                    arcNote(4, 7, 1.0 to 1.0, s, 1.0 to 1.0, ArcNote.Color.RED)
                }
                bar(10) {
                    arcNote(0, 3, 0.0 to 1.0, si, 1.0 to 0.0, ArcNote.Color.BLUE)
                    arcNote(4, 7, 1.0 to 1.0, si, 0.0 to 0.0, ArcNote.Color.RED)
                }

                // How to create an arc tap
                bar(12) {
                    // Create a guiding line
                    arcNote(0, 4, 1.0 to 1.0, s, 0.5 to 0.25) {
                        // and create an arc tap
                        arctap(2)
                    }
                    normalNote(0, 1)
                    normalNote(1, 3)
                    normalNote(3, 2)
                }
                bar(13) {
                    arcNote(0, 4, 0.0 to 1.0, s, 0.5 to 0.25) {
                        arctap(2)
                    }
                    normalNote(0, 4)
                    normalNote(1, 2)
                    normalNote(3, 3)
                }

                // That's it
                // You can use your Kotlin skill to create your own map as you like
                // Maybe you can use your geometry knowledge to draw an awesome picture by creating tons of guiding lines
                // Maybe you can analyze the music of this map to create some cool effects
                // No matter how, it is time for you.

                bar(14) {
                    arcNote(0, 4, 0.0 to 1.0, so, 0.75 to 0.0) {
                        arctap(2)
                    }
                    arcNote(0, 4, 1.0 to 1.0, so, 0.25 to 0.0) {
                        arctap(1)
                    }
                    normalNote(0, 1)
                    normalNote(3, 4)
                }
                bar(15) {
                    d(8)
                    var aArc: Bar.ArcNote.ArcTapList? = null
                    var bArc: Bar.ArcNote.ArcTapList? = null
                    arcNote(0, 8, 0.75 to 0.0, sisi, 0.0 to 1.0) {
                        aArc = this
                    }
                    arcNote(0, 8, 0.25 to 0.0, sisi, 1.0 to 1.0) {
                        bArc = this
                    }
                    (0..7).forEach {
                        (if (it % 2 == 0) aArc else bArc)!!.arctap(it)
                    }
                    normalNote(5, 1)
                    normalNote(6, 4)
                    normalNote(7, 1)
                }

                timingGroup("BarLineEffect") {
                    timing(offset = 1986, bpm = 124, beats = 4)
                    addSpecialEffect(TimingGroupSpecialEffect.NO_INPUT)
                }
                addBarLineEffect(bar(16).startTime.toDouble())
                bar(16) {
                    d(16)
                    arcNote(0, 5, 0.0 to 1.0, siso, 1.25 to 0.1, ArcNote.Color.BLUE)
                    arcNote(6, 11, 0.0 to 1.0, siso, 1.05 to 0.17, ArcNote.Color.RED)
                    arcNote(12, 15, 0.0 to 1.0, siso, 0.85 to 0.25, ArcNote.Color.BLUE)
                }
                bar(17) {
                    d(16)
                    arcNote(0, 5, 1.0 to 1.0, siso, -0.25 to 0.1, ArcNote.Color.RED)
                    arcNote(6, 11, 1.0 to 1.0, siso, -0.05 to 0.17, ArcNote.Color.BLUE)
                    arcNote(12, 15, 1.0 to 1.0, siso, 0.15 to 0.25, ArcNote.Color.RED)
                }
                bar(18) {
                    d(8)
                    arcNote(0, 3, 0.0 to 1.0, siso, 1.0 to 1.0, ArcNote.Color.BLUE)

                    normalNote(5, 2)
                    normalNote(6, 3)
                    normalNote(7, 2)
                }
                bar(19) {
                    d(8)
                    holdNote(0, 2, 1)
                    holdNote(3, 5, 2)
                    holdNote(6, 7, 4)
                }
                addBarLineEffect(bar(20).startTime.toDouble())

                bar(20) {
                    d(8)
                    arcNote(0, 3, 0.0 to 1.0, s, 0.7 to 1.0, ArcNote.Color.BLUE)
                    arcNote(3, 6, 0.7 to 1.0, s, 0.0 to 1.0, ArcNote.Color.BLUE)

                    arcNote(3, 6, 1.0 to 1.0, s, 0.3 to 1.0, ArcNote.Color.RED)
                    arcNote(6, 8, 0.3 to 1.0, s, 1.0 to 1.0, ArcNote.Color.RED)
                }
                bar(21) {
                    d(8)
                    arcNote(0, 3, 1.0 to 1.0, s, 0.3 to 1.0, ArcNote.Color.RED)
                    arcNote(3, 6, 0.3 to 1.0, s, 1.0 to 1.0, ArcNote.Color.RED)
                    arcNote(6, 8, 1.0 to 1.0, s, 1.0 to 1.0, ArcNote.Color.RED)

                    arcNote(3, 6, 0.0 to 1.0, s, 0.7 to 1.0, ArcNote.Color.BLUE)
                    arcNote(6, 8, 0.7 to 1.0, s, 0.0 to 1.0, ArcNote.Color.BLUE)
                }
                bar(22) {
                    d(8)
                    arcNote(0, 2, 0.0 to 1.0, si, 0.5 to 0.0, ArcNote.Color.BLUE)
                    arcNote(2, 3, 0.5 to 0.0, s, 0.5 to 0.0, ArcNote.Color.BLUE)
                    arcNote(3, 4, 0.5 to 0.0, s, 0.5 to 0.0, ArcNote.Color.BLUE)

                    arcNote(0, 2, 1.0 to 1.0, si, 0.5 to 0.0, ArcNote.Color.RED)
                    arcNote(2, 3, 0.5 to 0.0, s, 0.5 to 0.0, ArcNote.Color.RED)

                    val leftInput = 0.0 to 1.0
                    val rightInput = 1.0 to 1.0
                    val midInput = 0.5 to 1.0
                    val pa = 0.0 to 0.5
                    val pb = 0.5 to 1.0
                    val pc = 1.0 to 0.5

                    listOf(leftInput, rightInput).forEach { a ->
                        listOf(pa, pb, pc).forEach { b ->
                            arcNote(0, 2, a, si, b)
                        }
                    }

                    arcNote(2, 5, pa, s, pa) { arctap(5) }
                    arcNote(2, 7, pb, s, pb) { arctap(7) }
                    arcNote(2, 6, pc, s, pc) { arctap(6) }

                    listOf(leftInput, midInput, rightInput).forEach { arcNote(5, 8, pa, b, it) }
                    listOf(leftInput, midInput, rightInput).forEach { arcNote(7, 8, pb, b, it) }
                    listOf(leftInput, midInput, rightInput).forEach { arcNote(6, 8, pc, b, it) }
                }
                bar(23) {
                    d(8)

                    val leftInput = 0.0 to 1.0
                    val rightInput = 1.0 to 1.0
                    val midInput = 0.5 to 1.0

                    arcNote(0, 3, midInput, s, midInput)
                    arcNote(0, 6, leftInput, s, leftInput)

                    arcNote(0, 4, rightInput, s, rightInput, ArcNote.Color.RED)
                    arcNote(3, 7, midInput, s, midInput, ArcNote.Color.BLUE)
                    arcNote(6, 8, leftInput, s, leftInput, ArcNote.Color.RED)

                    arcNote(4, 8, rightInput, si, leftInput)
                    arcNote(7, 8, midInput, si, leftInput)
                }
                bar(24) {
                    normalNote(0, 1)
                }
            }
        }.writeToFolder(File(File("."), "aff-compose"))
    }

    private fun Difficulty.addBarLineEffect(startTime: Double) {
        timingGroup("BarLineEffect") {
            val barDuration = 60000.0 / 124 * 4
            val endTime = startTime + barDuration * 2
            var t = startTime
            var iterCount = 0
            while (t < endTime) {
                iterCount++
                t += iterCount * 4
                val c = floor(t).toLong()
                arcNote(c, c + 1, -0.5 to 0.0, s, 1.5 to 0.0)
            }
        }

    }
}