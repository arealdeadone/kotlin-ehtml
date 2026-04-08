package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.TextNode
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

    private fun optimize(
        headStyles: List<CssNode>,
        children: List<com.arvindrachuri.ehtml.ast.EmailNode> = emptyList(),
    ): EmailDocumentNode =
        CssOptimizationPass.run(
            EmailDocumentNode(title = "Test", headStyles = headStyles, children = children)
        )

    @Test
    fun `merges duplicate selectors`() {
        val result =
            optimize(
                listOf(
                    CssRule(".btn", mapOf("color" to "red")),
                    CssRule(".btn", mapOf("padding" to "10px")),
                )
            )
        assertEquals(1, result.headStyles.size)
        val rule = result.headStyles[0] as CssRule
        assertEquals(".btn", rule.selector)
        assertEquals("red", rule.styles["color"])
        assertEquals("10px", rule.styles[PADDING])
    }

    @Test
    fun `later duplicate selector wins on property conflict`() {
        val result =
            optimize(
                listOf(
                    CssRule(".btn", mapOf("color" to "red")),
                    CssRule(".btn", mapOf("color" to "blue")),
                )
            )
        assertEquals(1, result.headStyles.size)
        assertEquals("blue", (result.headStyles[0] as CssRule).styles["color"])
    }

    @Test
    fun `preserves order with non-duplicate selectors`() {
        val result =
            optimize(
                listOf(
                    CssRule(".a", mapOf("color" to "red")),
                    CssRule(".b", mapOf("color" to "blue")),
                    CssRule(".c", mapOf("color" to "green")),
                )
            )
        assertEquals(3, result.headStyles.size)
        assertEquals(".a", (result.headStyles[0] as CssRule).selector)
        assertEquals(".b", (result.headStyles[1] as CssRule).selector)
        assertEquals(".c", (result.headStyles[2] as CssRule).selector)
    }

    @Test
    fun `merges duplicates inside media query`() {
        val result =
            optimize(
                listOf(
                    CssMediaQuery(
                        "max-width: 600px",
                        listOf(
                            CssRule(".btn", mapOf("color" to "red")),
                            CssRule(".btn", mapOf("padding" to "10px")),
                        ),
                    )
                )
            )
        val media = result.headStyles[0] as CssMediaQuery
        assertEquals(1, media.rules.size)
        assertEquals(2, (media.rules[0] as CssRule).styles.size)
    }

    @Test
    fun `merges duplicates inside mso conditional`() {
        val result =
            optimize(
                listOf(
                    CssMsoConditional(
                        listOf(
                            CssRule("table", mapOf("width" to "600px")),
                            CssRule("table", mapOf("border" to "0")),
                        )
                    )
                )
            )
        val mso = result.headStyles[0] as CssMsoConditional
        assertEquals(1, mso.rules.size)
        val rule = mso.rules[0] as CssRule
        assertEquals("600px", rule.styles["width"])
        assertEquals("0", rule.styles["border"])
    }

    @Test
    fun `deduplicates identical style blocks`() {
        val result =
            optimize(
                listOf(
                    CssRule("h1", mapOf("margin" to "0")),
                    CssRule("h2", mapOf("margin" to "0")),
                    CssRule("p", mapOf("margin" to "0")),
                )
            )
        assertEquals(1, result.headStyles.size)
        assertEquals("h1, h2, p", (result.headStyles[0] as CssRule).selector)
    }

    @Test
    fun `does not dedup rules with different styles`() {
        val result =
            optimize(
                listOf(
                    CssRule("h1", mapOf("margin" to "0")),
                    CssRule("h2", mapOf("margin" to "10px")),
                )
            )
        assertEquals(2, result.headStyles.size)
    }

    @Test
    fun `dedup preserves first rule position`() {
        val result =
            optimize(
                listOf(
                    CssRule(".a", mapOf("color" to "red")),
                    CssRule("h1", mapOf("margin" to "0")),
                    CssRule("h2", mapOf("margin" to "0")),
                    CssRule(".b", mapOf("color" to "blue")),
                )
            )
        assertEquals(3, result.headStyles.size)
        assertEquals(".a", (result.headStyles[0] as CssRule).selector)
        assertEquals("h1, h2", (result.headStyles[1] as CssRule).selector)
        assertEquals(".b", (result.headStyles[2] as CssRule).selector)
    }

    @Test
    fun `dedup inside media query`() {
        val result =
            optimize(
                listOf(
                    CssMediaQuery(
                        "max-width: 600px",
                        listOf(
                            CssRule(".a", mapOf("width" to "100%")),
                            CssRule(".b", mapOf("width" to "100%")),
                        ),
                    )
                )
            )
        val media = result.headStyles[0] as CssMediaQuery
        assertEquals(1, media.rules.size)
        assertEquals(".a, .b", (media.rules[0] as CssRule).selector)
    }

    @Test
    fun `collapses padding shorthand when all four sides equal`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            PADDING_TOP to "10px",
                            PADDING_RIGHT to "10px",
                            PADDING_BOTTOM to "10px",
                            PADDING_LEFT to "10px",
                        ),
                    )
                )
            )
        val rule = result.headStyles[0] as CssRule
        assertEquals("10px", rule.styles[PADDING])
        assertTrue(PADDING_TOP !in rule.styles)
        assertTrue(PADDING_RIGHT !in rule.styles)
        assertTrue(PADDING_BOTTOM !in rule.styles)
        assertTrue(PADDING_LEFT !in rule.styles)
    }

    @Test
    fun `collapses padding shorthand with vertical and horizontal pairs`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            PADDING_TOP to "10px",
                            PADDING_RIGHT to "20px",
                            PADDING_BOTTOM to "10px",
                            PADDING_LEFT to "20px",
                        ),
                    )
                )
            )
        assertEquals("10px 20px", (result.headStyles[0] as CssRule).styles[PADDING])
    }

    @Test
    fun `collapses padding shorthand with three values`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            PADDING_TOP to "10px",
                            PADDING_RIGHT to "20px",
                            PADDING_BOTTOM to "30px",
                            PADDING_LEFT to "20px",
                        ),
                    )
                )
            )
        assertEquals("10px 20px 30px", (result.headStyles[0] as CssRule).styles[PADDING])
    }

    @Test
    fun `collapses padding shorthand with four different values`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            PADDING_TOP to "10px",
                            PADDING_RIGHT to "20px",
                            PADDING_BOTTOM to "30px",
                            PADDING_LEFT to "40px",
                        ),
                    )
                )
            )
        assertEquals("10px 20px 30px 40px", (result.headStyles[0] as CssRule).styles[PADDING])
    }

    @Test
    fun `collapses margin shorthand`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            MARGIN_TOP to "0",
                            MARGIN_RIGHT to "auto",
                            MARGIN_BOTTOM to "0",
                            MARGIN_LEFT to "auto",
                        ),
                    )
                )
            )
        assertEquals("0 auto", (result.headStyles[0] as CssRule).styles[MARGIN])
    }

    @Test
    fun `does not collapse when only partial sides present`() {
        val result =
            optimize(listOf(CssRule(".box", mapOf(PADDING_TOP to "10px", PADDING_LEFT to "20px"))))
        val rule = result.headStyles[0] as CssRule
        assertEquals("10px", rule.styles[PADDING_TOP])
        assertEquals("20px", rule.styles[PADDING_LEFT])
        assertTrue(PADDING !in rule.styles)
    }

    @Test
    fun `does not collapse when shorthand already set`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            PADDING to "5px",
                            PADDING_TOP to "10px",
                            PADDING_RIGHT to "10px",
                            PADDING_BOTTOM to "10px",
                            PADDING_LEFT to "10px",
                        ),
                    )
                )
            )
        val rule = result.headStyles[0] as CssRule
        assertEquals("5px", rule.styles[PADDING])
        assertEquals("10px", rule.styles[PADDING_TOP])
    }

    @Test
    fun `collapses shorthands inside media query`() {
        val result =
            optimize(
                listOf(
                    CssMediaQuery(
                        "max-width: 600px",
                        listOf(
                            CssRule(
                                ".box",
                                mapOf(
                                    PADDING_TOP to "0",
                                    PADDING_RIGHT to "0",
                                    PADDING_BOTTOM to "0",
                                    PADDING_LEFT to "0",
                                ),
                            )
                        ),
                    )
                )
            )
        val media = result.headStyles[0] as CssMediaQuery
        assertEquals("0", (media.rules[0] as CssRule).styles[PADDING])
    }

    @Test
    fun `preserves non-shorthand properties during collapse`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            PADDING_TOP to "10px",
                            PADDING_RIGHT to "10px",
                            PADDING_BOTTOM to "10px",
                            PADDING_LEFT to "10px",
                            "color" to "red",
                            "font-size" to "14px",
                        ),
                    )
                )
            )
        val rule = result.headStyles[0] as CssRule
        assertEquals("10px", rule.styles[PADDING])
        assertEquals("red", rule.styles["color"])
        assertEquals("14px", rule.styles["font-size"])
        assertEquals(3, rule.styles.size)
    }

    @Test
    fun `all optimizations chain correctly`() {
        val result =
            optimize(
                listOf(
                    CssRule("h1", mapOf("margin" to "0")),
                    CssRule("h2", mapOf("margin" to "0")),
                    CssRule(".btn", mapOf("color" to "red")),
                    CssRule(
                        ".btn",
                        mapOf(
                            PADDING_TOP to "10px",
                            PADDING_RIGHT to "10px",
                            PADDING_BOTTOM to "10px",
                            PADDING_LEFT to "10px",
                        ),
                    ),
                )
            )
        assertEquals(2, result.headStyles.size)
        assertEquals("h1, h2", (result.headStyles[0] as CssRule).selector)
        val btn = result.headStyles[1] as CssRule
        assertEquals(".btn", btn.selector)
        assertEquals("red", btn.styles["color"])
        assertEquals("10px", btn.styles[PADDING])
        assertTrue(PADDING_TOP !in btn.styles)
    }

    @Test
    fun `empty input returns empty`() {
        val result = optimize(emptyList())
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `single rule passes through unchanged`() {
        val result = optimize(listOf(CssRule(".btn", mapOf("color" to "red"))))
        assertEquals(1, result.headStyles.size)
        assertEquals("red", (result.headStyles[0] as CssRule).styles["color"])
    }

    @Test
    fun `mixed media and rules optimized independently`() {
        val result =
            optimize(
                listOf(
                    CssRule("h1", mapOf("margin" to "0")),
                    CssRule("h2", mapOf("margin" to "0")),
                    CssMediaQuery(
                        "max-width: 600px",
                        listOf(
                            CssRule(".a", mapOf("width" to "100%")),
                            CssRule(".b", mapOf("width" to "100%")),
                        ),
                    ),
                    CssRule(".c", mapOf("color" to "green")),
                )
            )
        assertEquals(3, result.headStyles.size)
        assertEquals("h1, h2", (result.headStyles[0] as CssRule).selector)
        val media = result.headStyles[1] as CssMediaQuery
        assertEquals(1, media.rules.size)
        assertEquals(".a, .b", (media.rules[0] as CssRule).selector)
        assertEquals(".c", (result.headStyles[2] as CssRule).selector)
    }

    @Test
    fun `collapses hex color #ffffff to #fff`() {
        val result = optimize(listOf(CssRule(".box", mapOf("color" to "#ffffff"))))
        assertEquals("#fff", (result.headStyles[0] as CssRule).styles["color"])
    }

    @Test
    fun `collapses hex color #aabbcc to #abc`() {
        val result = optimize(listOf(CssRule(".box", mapOf("background-color" to "#aabbcc"))))
        assertEquals("#abc", (result.headStyles[0] as CssRule).styles["background-color"])
    }

    @Test
    fun `collapses uppercase hex #FFFFFF to #FFF`() {
        val result = optimize(listOf(CssRule(".box", mapOf("color" to "#FFFFFF"))))
        assertEquals("#FFF", (result.headStyles[0] as CssRule).styles["color"])
    }

    @Test
    fun `does not collapse non-repeating hex #1B7C96`() {
        val result = optimize(listOf(CssRule(".box", mapOf("color" to "#1B7C96"))))
        assertEquals("#1B7C96", (result.headStyles[0] as CssRule).styles["color"])
    }

    @Test
    fun `does not collapse hex #112234`() {
        val result = optimize(listOf(CssRule(".box", mapOf("color" to "#112234"))))
        assertEquals("#112234", (result.headStyles[0] as CssRule).styles["color"])
    }

    @Test
    fun `trims 0px to 0`() {
        val result = optimize(listOf(CssRule(".box", mapOf("margin" to "0px"))))
        assertEquals("0", (result.headStyles[0] as CssRule).styles["margin"])
    }

    @Test
    fun `trims 0px in compound value`() {
        val result = optimize(listOf(CssRule(".box", mapOf("padding" to "0px 10px"))))
        assertEquals("0 10px", (result.headStyles[0] as CssRule).styles["padding"])
    }

    @Test
    fun `trims multiple 0px in compound value`() {
        val result = optimize(listOf(CssRule(".box", mapOf("padding" to "0px 0px 0px 0px"))))
        assertEquals("0 0 0 0", (result.headStyles[0] as CssRule).styles["padding"])
    }

    @Test
    fun `does not trim 10px to 10`() {
        val result = optimize(listOf(CssRule(".box", mapOf("padding" to "10px"))))
        assertEquals("10px", (result.headStyles[0] as CssRule).styles["padding"])
    }

    @Test
    fun `does not trim 20px in compound value`() {
        val result = optimize(listOf(CssRule(".box", mapOf("padding" to "0px 20px"))))
        assertEquals("0 20px", (result.headStyles[0] as CssRule).styles["padding"])
    }

    @Test
    fun `minify applies inside media query`() {
        val result =
            optimize(
                listOf(
                    CssMediaQuery(
                        "max-width: 600px",
                        listOf(CssRule(".box", mapOf("color" to "#ffffff", "margin" to "0px"))),
                    )
                )
            )
        val rule = (result.headStyles[0] as CssMediaQuery).rules[0] as CssRule
        assertEquals("#fff", rule.styles["color"])
        assertEquals("0", rule.styles["margin"])
    }

    @Test
    fun `minify and shorthand collapse chain correctly`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            PADDING_TOP to "0px",
                            PADDING_RIGHT to "0px",
                            PADDING_BOTTOM to "0px",
                            PADDING_LEFT to "0px",
                            "color" to "#ffffff",
                        ),
                    )
                )
            )
        val rule = result.headStyles[0] as CssRule
        assertEquals("0", rule.styles[PADDING])
        assertEquals("#fff", rule.styles["color"])
    }

    @Test
    fun `hex collapse in border shorthand`() {
        val result = optimize(listOf(CssRule(".box", mapOf("border" to "1px solid #aabbcc"))))
        assertEquals("1px solid #abc", (result.headStyles[0] as CssRule).styles["border"])
    }

    @Test
    fun `preserves non-collapsible values`() {
        val result =
            optimize(
                listOf(
                    CssRule(
                        ".box",
                        mapOf(
                            "font-family" to "'Inter', sans-serif",
                            "line-height" to "1.5",
                            "color" to "red",
                        ),
                    )
                )
            )
        val rule = result.headStyles[0] as CssRule
        assertEquals("'Inter', sans-serif", rule.styles["font-family"])
        assertEquals("1.5", rule.styles["line-height"])
        assertEquals("red", rule.styles["color"])
    }

    @Test
    fun `minifies inline styles on element nodes`() {
        val result =
            optimize(
                headStyles = emptyList(),
                children =
                    listOf(
                        ElementNode(
                            tag = "div",
                            styles = mapOf("padding" to "0px", "color" to "#ffffff"),
                        )
                    ),
            )
        val el = result.children[0] as ElementNode
        assertEquals("0", el.styles["padding"])
        assertEquals("#fff", el.styles["color"])
    }

    @Test
    fun `minifies inline styles on nested elements`() {
        val result =
            optimize(
                headStyles = emptyList(),
                children =
                    listOf(
                        ElementNode(
                            tag = "div",
                            styles = mapOf("margin" to "0px"),
                            children =
                                listOf(ElementNode(tag = "p", styles = mapOf("color" to "#aabbcc"))),
                        )
                    ),
            )
        val div = result.children[0] as ElementNode
        assertEquals("0", div.styles["margin"])
        val p = div.children[0] as ElementNode
        assertEquals("#abc", p.styles["color"])
    }

    @Test
    fun `minifies inline styles and headStyles simultaneously`() {
        val result =
            optimize(
                headStyles = listOf(CssRule(".box", mapOf("border" to "0px solid #ffffff"))),
                children = listOf(ElementNode(tag = "td", styles = mapOf("padding" to "0px"))),
            )
        assertEquals("0 solid #fff", (result.headStyles[0] as CssRule).styles["border"])
        assertEquals("0", (result.children[0] as ElementNode).styles["padding"])
    }

    @Test
    fun `does not trim non-zero px in inline styles`() {
        val result =
            optimize(
                headStyles = emptyList(),
                children = listOf(ElementNode(tag = "div", styles = mapOf("padding" to "10px"))),
            )
        assertEquals("10px", (result.children[0] as ElementNode).styles["padding"])
    }

    @Test
    fun `passes through TextNode in children unchanged`() {
        val result = optimize(headStyles = emptyList(), children = listOf(TextNode("hello")))
        assertEquals("hello", (result.children[0] as TextNode).value)
    }

    @Test
    fun `minifies mso structural styles on elements`() {
        val result =
            optimize(
                headStyles = emptyList(),
                children =
                    listOf(
                        ElementNode(
                            tag = "table",
                            styles =
                                mapOf(
                                    "mso-table-lspace" to "0pt",
                                    "mso-table-rspace" to "0pt",
                                    "padding" to "0px",
                                ),
                        )
                    ),
            )
        val el = result.children[0] as ElementNode
        assertEquals("0pt", el.styles["mso-table-lspace"])
        assertEquals("0", el.styles["padding"])
    }
}
