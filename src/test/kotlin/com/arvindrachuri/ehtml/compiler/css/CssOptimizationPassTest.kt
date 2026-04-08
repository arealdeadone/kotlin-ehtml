package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_BOTTOM
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_LEFT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_RIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_TOP
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_BOTTOM
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_LEFT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_RIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_TOP
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CssOptimizationPassTest {

    @Test
    fun `merges duplicate selectors`() {
        val nodes = listOf(
            CssRule(".btn", mapOf("color" to "red")),
            CssRule(".btn", mapOf("padding" to "10px")),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(1, result.size)
        val rule = result[0] as CssRule
        assertEquals(".btn", rule.selector)
        assertEquals("red", rule.styles["color"])
        assertEquals("10px", rule.styles[PADDING])
    }

    @Test
    fun `later duplicate selector wins on property conflict`() {
        val nodes = listOf(
            CssRule(".btn", mapOf("color" to "red")),
            CssRule(".btn", mapOf("color" to "blue")),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(1, result.size)
        assertEquals("blue", (result[0] as CssRule).styles["color"])
    }

    @Test
    fun `preserves order with non-duplicate selectors`() {
        val nodes = listOf(
            CssRule(".a", mapOf("color" to "red")),
            CssRule(".b", mapOf("color" to "blue")),
            CssRule(".c", mapOf("color" to "green")),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(3, result.size)
        assertEquals(".a", (result[0] as CssRule).selector)
        assertEquals(".b", (result[1] as CssRule).selector)
        assertEquals(".c", (result[2] as CssRule).selector)
    }

    @Test
    fun `merges duplicates inside media query`() {
        val nodes = listOf(
            CssMediaQuery("max-width: 600px", listOf(
                CssRule(".btn", mapOf("color" to "red")),
                CssRule(".btn", mapOf("padding" to "10px")),
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val media = result[0] as CssMediaQuery
        assertEquals(1, media.rules.size)
        val rule = media.rules[0] as CssRule
        assertEquals(2, rule.styles.size)
    }

    @Test
    fun `merges duplicates inside mso conditional`() {
        val nodes = listOf(
            CssMsoConditional(listOf(
                CssRule("table", mapOf("width" to "600px")),
                CssRule("table", mapOf("border" to "0")),
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val mso = result[0] as CssMsoConditional
        assertEquals(1, mso.rules.size)
        val rule = mso.rules[0] as CssRule
        assertEquals("600px", rule.styles["width"])
        assertEquals("0", rule.styles["border"])
    }

    @Test
    fun `deduplicates identical style blocks`() {
        val nodes = listOf(
            CssRule("h1", mapOf("margin" to "0")),
            CssRule("h2", mapOf("margin" to "0")),
            CssRule("p", mapOf("margin" to "0")),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(1, result.size)
        assertEquals("h1, h2, p", (result[0] as CssRule).selector)
    }

    @Test
    fun `does not dedup rules with different styles`() {
        val nodes = listOf(
            CssRule("h1", mapOf("margin" to "0")),
            CssRule("h2", mapOf("margin" to "10px")),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(2, result.size)
    }

    @Test
    fun `dedup preserves first rule position`() {
        val nodes = listOf(
            CssRule(".a", mapOf("color" to "red")),
            CssRule("h1", mapOf("margin" to "0")),
            CssRule("h2", mapOf("margin" to "0")),
            CssRule(".b", mapOf("color" to "blue")),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(3, result.size)
        assertEquals(".a", (result[0] as CssRule).selector)
        assertEquals("h1, h2", (result[1] as CssRule).selector)
        assertEquals(".b", (result[2] as CssRule).selector)
    }

    @Test
    fun `dedup inside media query`() {
        val nodes = listOf(
            CssMediaQuery("max-width: 600px", listOf(
                CssRule(".a", mapOf("width" to "100%")),
                CssRule(".b", mapOf("width" to "100%")),
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val media = result[0] as CssMediaQuery
        assertEquals(1, media.rules.size)
        assertEquals(".a, .b", (media.rules[0] as CssRule).selector)
    }

    @Test
    fun `collapses padding shorthand when all four sides equal`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                PADDING_TOP to "10px",
                PADDING_RIGHT to "10px",
                PADDING_BOTTOM to "10px",
                PADDING_LEFT to "10px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val rule = result[0] as CssRule
        assertEquals("10px", rule.styles[PADDING])
        assertTrue(PADDING_TOP !in rule.styles)
        assertTrue(PADDING_RIGHT !in rule.styles)
        assertTrue(PADDING_BOTTOM !in rule.styles)
        assertTrue(PADDING_LEFT !in rule.styles)
    }

    @Test
    fun `collapses padding shorthand with vertical and horizontal pairs`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                PADDING_TOP to "10px",
                PADDING_RIGHT to "20px",
                PADDING_BOTTOM to "10px",
                PADDING_LEFT to "20px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals("10px 20px", (result[0] as CssRule).styles[PADDING])
    }

    @Test
    fun `collapses padding shorthand with three values`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                PADDING_TOP to "10px",
                PADDING_RIGHT to "20px",
                PADDING_BOTTOM to "30px",
                PADDING_LEFT to "20px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals("10px 20px 30px", (result[0] as CssRule).styles[PADDING])
    }

    @Test
    fun `collapses padding shorthand with four different values`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                PADDING_TOP to "10px",
                PADDING_RIGHT to "20px",
                PADDING_BOTTOM to "30px",
                PADDING_LEFT to "40px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals("10px 20px 30px 40px", (result[0] as CssRule).styles[PADDING])
    }

    @Test
    fun `collapses margin shorthand`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                MARGIN_TOP to "0",
                MARGIN_RIGHT to "auto",
                MARGIN_BOTTOM to "0",
                MARGIN_LEFT to "auto",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals("0 auto", (result[0] as CssRule).styles[MARGIN])
    }

    @Test
    fun `does not collapse when only partial sides present`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                PADDING_TOP to "10px",
                PADDING_LEFT to "20px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val rule = result[0] as CssRule
        assertEquals("10px", rule.styles[PADDING_TOP])
        assertEquals("20px", rule.styles[PADDING_LEFT])
        assertTrue(PADDING !in rule.styles)
    }

    @Test
    fun `does not collapse when shorthand already set`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                PADDING to "5px",
                PADDING_TOP to "10px",
                PADDING_RIGHT to "10px",
                PADDING_BOTTOM to "10px",
                PADDING_LEFT to "10px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val rule = result[0] as CssRule
        assertEquals("5px", rule.styles[PADDING])
        assertEquals("10px", rule.styles[PADDING_TOP])
    }

    @Test
    fun `collapses shorthands inside media query`() {
        val nodes = listOf(
            CssMediaQuery("max-width: 600px", listOf(
                CssRule(".box", mapOf(
                    PADDING_TOP to "0",
                    PADDING_RIGHT to "0",
                    PADDING_BOTTOM to "0",
                    PADDING_LEFT to "0",
                )),
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val media = result[0] as CssMediaQuery
        val rule = media.rules[0] as CssRule
        assertEquals("0", rule.styles[PADDING])
    }

    @Test
    fun `preserves non-shorthand properties during collapse`() {
        val nodes = listOf(
            CssRule(".box", mapOf(
                PADDING_TOP to "10px",
                PADDING_RIGHT to "10px",
                PADDING_BOTTOM to "10px",
                PADDING_LEFT to "10px",
                "color" to "red",
                "font-size" to "14px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        val rule = result[0] as CssRule
        assertEquals("10px", rule.styles[PADDING])
        assertEquals("red", rule.styles["color"])
        assertEquals("14px", rule.styles["font-size"])
        assertEquals(3, rule.styles.size)
    }

    @Test
    fun `all optimizations chain correctly`() {
        val nodes = listOf(
            CssRule("h1", mapOf("margin" to "0")),
            CssRule("h2", mapOf("margin" to "0")),
            CssRule(".btn", mapOf("color" to "red")),
            CssRule(".btn", mapOf(
                PADDING_TOP to "10px",
                PADDING_RIGHT to "10px",
                PADDING_BOTTOM to "10px",
                PADDING_LEFT to "10px",
            )),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(2, result.size)
        assertEquals("h1, h2", (result[0] as CssRule).selector)
        val btn = result[1] as CssRule
        assertEquals(".btn", btn.selector)
        assertEquals("red", btn.styles["color"])
        assertEquals("10px", btn.styles[PADDING])
        assertTrue(PADDING_TOP !in btn.styles)
    }

    @Test
    fun `empty input returns empty`() {
        val result = CssOptimizationPass.run(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `single rule passes through unchanged`() {
        val nodes = listOf(CssRule(".btn", mapOf("color" to "red")))
        val result = CssOptimizationPass.run(nodes)
        assertEquals(1, result.size)
        assertEquals("red", (result[0] as CssRule).styles["color"])
    }

    @Test
    fun `mixed media and rules optimized independently`() {
        val nodes = listOf(
            CssRule("h1", mapOf("margin" to "0")),
            CssRule("h2", mapOf("margin" to "0")),
            CssMediaQuery("max-width: 600px", listOf(
                CssRule(".a", mapOf("width" to "100%")),
                CssRule(".b", mapOf("width" to "100%")),
            )),
            CssRule(".c", mapOf("color" to "green")),
        )
        val result = CssOptimizationPass.run(nodes)
        assertEquals(3, result.size)
        assertEquals("h1, h2", (result[0] as CssRule).selector)
        val media = result[1] as CssMediaQuery
        assertEquals(1, media.rules.size)
        assertEquals(".a, .b", (media.rules[0] as CssRule).selector)
        assertEquals(".c", (result[2] as CssRule).selector)
    }
}
