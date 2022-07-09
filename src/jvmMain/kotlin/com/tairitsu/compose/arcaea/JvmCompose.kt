package com.tairitsu.compose.arcaea

import io.ktor.utils.io.streams.*
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

fun MapSet.writeToFolder(outputPath: Path) {
    writeToFolder(outputPath.toFile())
}

fun MapSet.writeToFolder(outputPath: File) {
    Files.createDirectories(outputPath.toPath())
    writeToOutput { fileName ->
        FileOutputStream(File(outputPath, fileName)).asOutput()
    }
}
