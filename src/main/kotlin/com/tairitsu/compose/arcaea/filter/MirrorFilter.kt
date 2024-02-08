package com.tairitsu.compose.arcaea.filter

import com.tairitsu.compose.arcaea.*

object MirrorFilter : NoteFilter() {
    override fun filterKeyboardNote(note: NormalNote): Note {
        return NormalNote(note.time, 5 - note.column)
    }

    override fun filterHoldNote(note: HoldNote): Note {
        return HoldNote(note.time, note.endTime, 5 - note.column)
    }

    override fun filterArcNote(note: ArcNote): Note {
        val color = if (note.color == ArcNote.Color.BLUE) ArcNote.Color.RED else ArcNote.Color.BLUE

        val startPosition = (1.0 - note.startPosition.x) to note.startPosition.y
        val endPosition = (1.0 - note.endPosition.x) to note.endPosition.y

        val ret = ArcNote(
            time = note.time,
            endTime = note.endTime,
            startPosition = startPosition,
            curveType = note.curveType,
            endPosition = endPosition,
            color = color,
            isGuidingLine = note.isGuidingLine,
        )

        ret.tapList.tap(*note.tapList.data.toIntArray())

        return ret
    }
}
