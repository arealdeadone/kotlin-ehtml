package com.arvindrachuri.ehtml.utils.css.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValueScaleTest {

    @Test
    fun `KeywordScale resolves valid keyword`() {
        val scale = KeywordScale(setOf("block", "none", "inline-block"))
        assertEquals("block", scale.resolve("block"))
    }

    @Test
    fun `KeywordScale resolves all keywords in set`() {
        val scale = KeywordScale(setOf("block", "none", "inline-block"))
        assertEquals("none", scale.resolve("none"))
        assertEquals("inline-block", scale.resolve("inline-block"))
    }

    @Test
    fun `KeywordScale returns null for unknown keyword`() {
        val scale = KeywordScale(setOf("block", "none"))
        assertNull(scale.resolve("flex"))
    }

    @Test
    fun `KeywordScale returns null for empty modifier`() {
        val scale = KeywordScale(setOf("block", "none"))
        assertNull(scale.resolve(""))
    }

    @Test
    fun `KeywordScale is case sensitive`() {
        val scale = KeywordScale(setOf("block"))
        assertNull(scale.resolve("Block"))
        assertNull(scale.resolve("BLOCK"))
    }

    @Test
    fun `NamedScale resolves mapped modifier`() {
        val scale = NamedScale(mapOf("sm" to "12px", "lg" to "16px"))
        assertEquals("12px", scale.resolve("sm"))
        assertEquals("16px", scale.resolve("lg"))
    }

    @Test
    fun `NamedScale returns null for unmapped modifier`() {
        val scale = NamedScale(mapOf("sm" to "12px"))
        assertNull(scale.resolve("xxl"))
    }

    @Test
    fun `NamedScale resolves empty string key`() {
        val scale = NamedScale(mapOf("" to "4px", "lg" to "12px"))
        assertEquals("4px", scale.resolve(""))
    }

    @Test
    fun `SpacingScale resolves valid value to px`() {
        val scale = SpacingScale(setOf(0, 4, 8, 16))
        assertEquals("4px", scale.resolve("4"))
        assertEquals("16px", scale.resolve("16"))
    }

    @Test
    fun `SpacingScale resolves zero`() {
        val scale = SpacingScale(setOf(0, 4, 8))
        assertEquals("0px", scale.resolve("0"))
    }

    @Test
    fun `SpacingScale returns null for value not in set`() {
        val scale = SpacingScale(setOf(0, 4, 8))
        assertNull(scale.resolve("5"))
        assertNull(scale.resolve("10"))
    }

    @Test
    fun `SpacingScale returns null for non-numeric modifier`() {
        val scale = SpacingScale(setOf(0, 4, 8))
        assertNull(scale.resolve("foo"))
        assertNull(scale.resolve(""))
    }

    @Test
    fun `SpacingScale returns null for negative values not in set`() {
        val scale = SpacingScale(setOf(0, 4, 8))
        assertNull(scale.resolve("-4"))
    }

    @Test
    fun `SpacingScale does not substring match`() {
        val scale = SpacingScale(setOf(12, 16))
        assertNull(scale.resolve("1"))
        assertNull(scale.resolve("6"))
    }

    @Test
    fun `PercentageScale resolves valid value to percent`() {
        val scale = PercentageScale(setOf(25, 50, 100))
        assertEquals("50%", scale.resolve("50"))
        assertEquals("100%", scale.resolve("100"))
    }

    @Test
    fun `PercentageScale resolves auto`() {
        val scale = PercentageScale(setOf(100))
        assertEquals("auto", scale.resolve("auto"))
    }

    @Test
    fun `PercentageScale resolves auto case insensitive`() {
        val scale = PercentageScale(setOf(100))
        assertEquals("auto", scale.resolve("AUTO"))
        assertEquals("auto", scale.resolve("Auto"))
    }

    @Test
    fun `PercentageScale returns null for value not in set`() {
        val scale = PercentageScale(setOf(50, 100))
        assertNull(scale.resolve("75"))
    }

    @Test
    fun `PercentageScale returns null for non-numeric non-auto modifier`() {
        val scale = PercentageScale(setOf(100))
        assertNull(scale.resolve("foo"))
        assertNull(scale.resolve(""))
    }

    @Test
    fun `LiteralScale resolves empty modifier to value`() {
        val scale = LiteralScale("uppercase")
        assertEquals("uppercase", scale.resolve(""))
    }

    @Test
    fun `LiteralScale returns null for non-empty modifier`() {
        val scale = LiteralScale("uppercase")
        assertNull(scale.resolve("foo"))
        assertNull(scale.resolve("uppercase"))
    }
}
