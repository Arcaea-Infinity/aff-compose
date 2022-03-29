package com.tairitsu.compose.dsl

import com.tairitsu.compose.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class MapSetDslTest {
    val rand = Random()

    @Test
    fun `test context`() {
        val a = mapSet {
            difficulties.future {
                timing(
                    offset = 0,
                    bpm = 120,
                    beats = 4,
                )

                bar(0) {
                    d(16)
                    (0..(16 * 4)).forEach {
                        normalNote(it, rand.nextInt(4) + 1)
                    }
                    // normalNote(0, 1)
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



        println(diff.chart.serialize())
    }
}