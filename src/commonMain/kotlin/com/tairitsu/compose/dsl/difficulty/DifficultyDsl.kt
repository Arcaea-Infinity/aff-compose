package com.tairitsu.compose.dsl.difficulty

import com.tairitsu.compose.Difficulty

fun Difficulty.bar(id: Int, closure: Bar.() -> Unit) {
    val bar = Bar(id)
    bar.closure()
}

infix fun Difficulty.d(`val`: Int): Int = 1




infix fun Bar.n(`val`: Int): NormalNote = NormalNote(`val`)
infix fun NormalNote.c(`val`: Int): NormalNote  {
    this.column = `val`
    return this
}

infix fun Bar.h(`val`: Pair<Int, Int>): Int = 1

val Difficulty.diff: Difficulty
    get() = this

val Bar.bar: Bar
    get() = this


class NormalNote(val `val`: Int) {
    var column: Int = 0
}

class Bar(id: Int) {

}