package com.tairitsu.compose.arcaea.filter

import com.tairitsu.compose.arcaea.Difficulty

fun Difficulty.mirror(closure: (Difficulty.() -> Unit)) {
    val ctx = this.currentTimingGroup
    ctx.notes.addFilter(MirrorFilter)
    closure()
    ctx.notes.popFilter()
}
