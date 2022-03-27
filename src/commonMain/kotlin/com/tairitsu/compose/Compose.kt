package com.tairitsu.compose

fun mapSet(closure: MapSet.() -> Unit): MapSet {
    val mapSet = MapSet()
    mapSet.closure()
    return mapSet
}

fun MapSet.build(path: String) {

}