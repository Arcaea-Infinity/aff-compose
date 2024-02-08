package com.tairitsu.compose.arcaea

import com.tairitsu.compose.arcaea.Difficulties
import com.tairitsu.compose.arcaea.Difficulty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class DifficultiesTest {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    @Test
    fun `test for serialization`() {
        val a = Difficulties()

        a.past = Difficulty().apply {
            chartDesigner = "A"
            jacketDesigner = "B"
            rating = 1
        }

        a.present = Difficulty().apply {
            chartDesigner = "C"
            jacketDesigner = "D"
            rating = 6
        }

        a.future = Difficulty().apply {
            chartDesigner = "E"
            jacketDesigner = "F"
            rating = 10
        }

        a.beyond = Difficulty().apply {
            chartDesigner = "G"
            jacketDesigner = "H"
            rating = 11
        }

        val json = json.encodeToString(a)

        val b = Json.decodeFromString<Difficulties>(json)

        assertDifficultyEquals(a.past, b.past)
        assertDifficultyEquals(a.present, b.present)
        assertDifficultyEquals(a.future, b.future)
        assertDifficultyEquals(a.beyond, b.beyond)
    }
}
