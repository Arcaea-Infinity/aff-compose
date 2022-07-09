package com.tairitsu.compose.arcaea

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class DifficultyTest {
    @Test
    fun `test for serialization` () {
        val a = Difficulty()
        a.chartDesigner = "Alice"
        a.jacketDesigner = "Bob"
        a.rating = 10
        a.ratingClass = Difficulty.RatingClass.BEYOND

        val json = Json.encodeToString(a)

        val b = Json.decodeFromString<Difficulty>(json)

        assertDifficultyEquals(a, b)
    }
}

