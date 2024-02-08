package com.tairitsu.compose.arcaea

import com.tairitsu.compose.arcaea.LocalizedString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class LocalizedStringTest {
    @Test
    fun `test for serialization`() {
        val a = LocalizedString("Test")
        a.ja = "テスト"
        a.ko = "테스트"
        a.zhHans = "测试"
        a.zhHant = "測試"

        val json = Json.encodeToString(a)

        val b = Json.decodeFromString<LocalizedString>(json)

        org.junit.jupiter.api.Assertions.assertEquals(a.en, b.en)
        org.junit.jupiter.api.Assertions.assertEquals(a.ja, b.ja)
        org.junit.jupiter.api.Assertions.assertEquals(a.ko, b.ko)
        org.junit.jupiter.api.Assertions.assertEquals(a.zhHans, b.zhHans)
        org.junit.jupiter.api.Assertions.assertEquals(a.zhHant, b.zhHant)
    }
}
