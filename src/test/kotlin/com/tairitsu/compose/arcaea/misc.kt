package com.tairitsu.compose.arcaea

import com.tairitsu.compose.arcaea.Difficulty

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
