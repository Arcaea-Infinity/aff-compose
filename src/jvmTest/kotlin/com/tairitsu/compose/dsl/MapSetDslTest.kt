package com.tairitsu.compose.dsl

import com.tairitsu.compose.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class MapSetDslTest {
    @Test
    fun `test context`() {
        val a = mapSet {
            difficulties.future {
                timing(
                    offset = 0,
                    bpm = 120,
                    beats = 4,
                )

                // bar start time: (60000 / 120 * 4) * 4 + 0
                // 8000
                bar(4) {
                    d(16)

                    // beat per
                    // (8000,1)
                    normalNote(0, 1).also {
                        Assertions.assertEquals("(8000,1);", it.serialize())
                    }
                    // 8000 + 60000 / 120 * 4 / 16 * 3
                    normalNote(3, 1).also {
                        Assertions.assertEquals("(8375,1);", it.serialize())
                    }
                    normalNote(6, 1)
                    normalNote(9, 1)
                    normalNote(12, 1)
                    normalNote(14, 1)

                    holdNote(16, 32, 1).also {
                        Assertions.assertEquals("hold(10000,12000,1);", it.serialize())
                    }
                }

                // 60000 / 120 * 4 * 16
                // 32000
                bar(16) {
                    d(16)

                    arcNote(0, 16, 0.0 to 1.0, s, 0.0 to 1.0) {
                        tap(0)
                        tap(4)
                        tap(8)
                        tap(12)
                    }.also {
                        Assertions.assertEquals("arc(32000,34000,0.00,1.00,s,0.00,1.00,0,none,true)[arctap(32000),arctap(32500),arctap(33000),arctap(33500)];", it.serialize())
                    }
                }

                // 60000 / 120 * 4 * 20
                // 40000
                bar(20) {
                    d(4)

                    arcNote(0, 16, 1.0 to 1.0, b, 0.0 to 1.0, ArcNote.Color.RED, false).also {
                        Assertions.assertEquals("arc(40000,48000,1.00,1.00,b,0.00,1.00,1,none,false);", it.serialize())
                    }
                }

                timingGroup {
                    timing(
                        offset = 0,
                        bpm = 120 * 4,
                        beats = 4,
                    )

                    // 60000 / 120 / 4 * 4 * 20
                    // 10000
                    bar(20) {
                        d(4)

                        normalNote(0, 1).also {
                            Assertions.assertEquals("(10000,1);", it.serialize())
                        }
                    }

                }
            }
        }

        Assertions.assertNull(a.difficulties.past)
        Assertions.assertNull(a.difficulties.present)
        Assertions.assertNull(a.difficulties.beyond)

        val diff = a.difficulties.future
        Assertions.assertNotNull(diff)
        diff!!
        Assertions.assertEquals(1, diff.chart.mainTiming.timing.size)
        Assertions.assertEquals(1, diff.chart.subTiming.size)
        Assertions.assertEquals(120.00, diff.chart.mainTiming.timing.first().bpm)
        Assertions.assertEquals(1, diff.chart.subTiming.values.first().timing.size)
        Assertions.assertEquals(120.00 * 4, diff.chart.subTiming.values.first().timing.first().bpm)

        Assertions.assertEquals(9, diff.chart.mainTiming.notes.size)
        Assertions.assertEquals(1, diff.chart.subTiming.values.first().notes.size)

        println(diff.chart.serialize())
    }
}