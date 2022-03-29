package com.tairitsu.compose

import io.ktor.utils.io.streams.*
import java.io.File
import java.nio.file.Files

fun main() {
    mapSet {
        id = "demomap"
        titleLocalized = LocalizedString("Demo Map").apply {
            zhHans = "示例谱面"
            zhHant = "示範譜面"
            ja = "デモマップ"
            ko = "데모맵"
        }

        difficulties.future {
            timing(
                offset = 0,
                bpm = 120,
                beats = 4,
            )

            (0..4).forEach { i ->
                normalNote(i * 1000 * 3, 1)
            }
            normalNote(14 * 1000 * 3, 1)


            timingGroup {
                timing(
                    offset = 0,
                    bpm = 120 * 4,
                    beats = 4,
                )
            }
        }
    }.writeToFolder(File(File(System.getProperty("user.home")), "aff-compose"))
}
