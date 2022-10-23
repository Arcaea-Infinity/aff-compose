package com.tairitsu.compose.arcaea.filter

import com.tairitsu.compose.arcaea.Difficulty
import com.tairitsu.compose.arcaea.NoteFilter

fun Difficulty.noteFilter(noteFilter: NoteFilter, closure: (Difficulty.() -> Unit)) {
    addNoteFilter(noteFilter)
    closure()
    popNoteFilter()
}

fun Difficulty.mirror(closure: (Difficulty.() -> Unit)) = noteFilter(MirrorFilter, closure)
