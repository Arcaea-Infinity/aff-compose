package com.tairitsu.compose.arcaea

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun MapSet.writeToFolder(outputPath: Path) {
    writeToFolder(outputPath.toFile())
}

fun MapSet.writeToFolder(outputPath: File) {
    Files.createDirectories(outputPath.toPath())
    writeToFile(outputPath.path)
}

fun Difficulty.printToConsole() {
    this.chart.serialize().let {
        println(it)
    }
}
