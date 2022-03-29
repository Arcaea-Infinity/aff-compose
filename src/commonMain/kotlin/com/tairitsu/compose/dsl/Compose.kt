package com.tairitsu.compose.dsl

import com.tairitsu.compose.Difficulty

fun Difficulty.bar(id: Int, closure: Bar.() -> Unit): Bar {
    val bar = Bar(this, id)
    bar.closure()
    return bar
}
