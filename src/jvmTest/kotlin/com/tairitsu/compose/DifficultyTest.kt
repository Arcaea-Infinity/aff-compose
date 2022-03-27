package com.tairitsu.compose

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

fun assertDifficultyEquals(a: Difficulty?, b: Difficulty?) {
    org.junit.jupiter.api.Assertions.assertNotNull(a)
    org.junit.jupiter.api.Assertions.assertNotNull(b)

    a!!
    b!!

    org.junit.jupiter.api.Assertions.assertEquals(a.chartDesigner, b.chartDesigner)
    org.junit.jupiter.api.Assertions.assertEquals(a.jacketDesigner, b.jacketDesigner)
    org.junit.jupiter.api.Assertions.assertEquals(a.rating, b.rating)
    org.junit.jupiter.api.Assertions.assertEquals(a.ratingClass, b.ratingClass)
}