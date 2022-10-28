package com.tairitsu.compose.arcaea

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MapSetTest {
    @Test
    fun `test context`() {
        val a = mapSet {
            difficulties.future {
                timing(
                    offset = 0,
                    bpm = 120,
                    beats = 4,
                )
                (0..4).forEach { i ->
                    normalNote(i * 1000 * 3, 1)
                }
                normalNote(14 * 1000 * 3, 1).also {
                    Assertions.assertEquals("(42000,1);", it.serialize())
                }
                holdNote(2000, 3000, 2).also {
                    Assertions.assertEquals("hold(2000,3000,2);", it.serialize())
                }
                arcNote(10000, 20000, 0.0 to 1.0, s, 0.0 to 1.0) {
                    tap(12000)
                }.also {
                    Assertions.assertEquals("arc(10000,20000,0.00,0.00,s,1.00,1.00,0,none,true)[arctap(12000)];", it.serialize())
                }
                arcNote(10000, 20000, 1.0 to 1.0, s, 0.0 to 1.0, ArcNote.Color.RED, false).also {
                    Assertions.assertEquals("arc(10000,20000,1.00,0.00,s,1.00,1.00,1,none,false);", it.serialize())
                }


                timingGroup {
                    timing(
                        offset = 0,
                        bpm = 120 * 4,
                        beats = 4,
                    )
                    (0..4).forEach { i ->
                        normalNote(i * 1000 * 3, 1)
                    }
                    normalNote(14 * 1000 * 3, 1)
                    holdNote(2000, 3000, 2)
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

        Assertions.assertEquals(9, diff.chart.mainTiming.getNotes().size)
        Assertions.assertEquals(7, diff.chart.subTiming.values.first().getNotes().size)


        println(diff.chart.serialize())
    }
}