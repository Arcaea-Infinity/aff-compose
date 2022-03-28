package com.tairitsu.compose

fun mapSet(closure: MapSet.() -> Unit): MapSet {
    val mapSet = MapSet()
    mapSet.closure()
    return mapSet
}

fun MapSet.build(path: String) {

}

fun Difficulties.past(closure: Difficulty.() -> Unit) {
    val difficulty = this.past ?: Difficulty()
    difficulty.closure()
    this.past = difficulty
}

fun Difficulties.present(closure: Difficulty.() -> Unit) {
    val difficulty = this.present ?: Difficulty()
    difficulty.closure()
    this.present = difficulty
}

fun Difficulties.future(closure: Difficulty.() -> Unit) {
    val difficulty = this.future ?: Difficulty()
    difficulty.closure()
    this.future = difficulty
}

fun Difficulties.beyond(closure: Difficulty.() -> Unit) {
    val difficulty = this.beyond ?: Difficulty()
    difficulty.closure()
    this.beyond = difficulty
}
