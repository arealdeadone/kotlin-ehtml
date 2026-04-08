package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.TextNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CssInliningPassTest {

    private fun document(
        headStyles: List<com.arvindrachuri.ehtml.ast.CssNode>,
        children: List<com.arvindrachuri.ehtml.ast.EmailNode>,
    ) = EmailDocumentNode(title = "Test", headStyles = headStyles, children = children)

    @Test
    fun `inlines simple class selector into matching element`() {
        val doc = document(
            headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("class" to "btn"))),
        )
        val result = CssInliningPass.run(doc)
        val el = result.children[0] as ElementNode
        assertEquals("10px", el.styles["padding"])
    }

    @Test
    fun `removes inlined class rule from headStyles`() {
        val doc = document(
            headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("class" to "btn"))),
        )
        val result = CssInliningPass.run(doc)
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `strips inlined class name from element`() {
        val doc = document(
            headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("class" to "btn"))),
        )
        val result = CssInliningPass.run(doc)
        val el = result.children[0] as ElementNode
        assertTrue("class" !in el.attributes)
    }

    @Test
    fun `keeps non-inlined classes on element`() {
        val doc = document(
            headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("class" to "btn custom"))),
        )
        val result = CssInliningPass.run(doc)
        val el = result.children[0] as ElementNode
        assertEquals("custom", el.attributes["class"])
    }

    @Test
    fun `inlines multi-class selector into all matching elements`() {
        val doc = document(
            headStyles = listOf(CssRule(".a, .b", mapOf("margin" to "0"))),
            children = listOf(
                ElementNode(tag = "div", attributes = mapOf("class" to "a")),
                ElementNode(tag = "div", attributes = mapOf("class" to "b")),
            ),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("0", (result.children[0] as ElementNode).styles["margin"])
        assertEquals("0", (result.children[1] as ElementNode).styles["margin"])
    }

    @Test
    fun `inlines id selector into matching element`() {
        val doc = document(
            headStyles = listOf(CssRule("#hero", mapOf("color" to "red"))),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("id" to "hero"))),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("red", (result.children[0] as ElementNode).styles["color"])
    }

    @Test
    fun `inlines tag selector into matching elements`() {
        val doc = document(
            headStyles = listOf(CssRule("p", mapOf("margin" to "0"))),
            children = listOf(
                ElementNode(tag = "p"),
                ElementNode(tag = "div"),
                ElementNode(tag = "p"),
            ),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("0", (result.children[0] as ElementNode).styles["margin"])
        assertTrue((result.children[1] as ElementNode).styles.isEmpty())
        assertEquals("0", (result.children[2] as ElementNode).styles["margin"])
    }

    @Test
    fun `inlines compound selector into descendant`() {
        val doc = document(
            headStyles = listOf(CssRule(".card p", mapOf("font-size" to "12px"))),
            children = listOf(
                ElementNode(
                    tag = "div",
                    attributes = mapOf("class" to "card"),
                    children = listOf(ElementNode(tag = "p")),
                ),
            ),
        )
        val result = CssInliningPass.run(doc)
        val card = result.children[0] as ElementNode
        val p = card.children[0] as ElementNode
        assertEquals("12px", p.styles["font-size"])
    }

    @Test
    fun `compound selector does not match non-descendant`() {
        val doc = document(
            headStyles = listOf(CssRule(".card p", mapOf("font-size" to "12px"))),
            children = listOf(
                ElementNode(tag = "p"),
                ElementNode(
                    tag = "div",
                    attributes = mapOf("class" to "card"),
                    children = listOf(ElementNode(tag = "span")),
                ),
            ),
        )
        val result = CssInliningPass.run(doc)
        assertTrue((result.children[0] as ElementNode).styles.isEmpty())
    }

    @Test
    fun `deeply nested compound selector matches`() {
        val doc = document(
            headStyles = listOf(CssRule(".wrapper .card p", mapOf("color" to "blue"))),
            children = listOf(
                ElementNode(
                    tag = "div",
                    attributes = mapOf("class" to "wrapper"),
                    children = listOf(
                        ElementNode(
                            tag = "div",
                            attributes = mapOf("class" to "card"),
                            children = listOf(ElementNode(tag = "p")),
                        ),
                    ),
                ),
            ),
        )
        val result = CssInliningPass.run(doc)
        val wrapper = result.children[0] as ElementNode
        val card = wrapper.children[0] as ElementNode
        val p = card.children[0] as ElementNode
        assertEquals("blue", p.styles["color"])
    }

    @Test
    fun `element inline styles win over inlined css`() {
        val doc = document(
            headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            children = listOf(
                ElementNode(tag = "div", attributes = mapOf("class" to "btn"), styles = mapOf("padding" to "20px")),
            ),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("20px", (result.children[0] as ElementNode).styles["padding"])
    }

    @Test
    fun `later css rule overrides earlier for same property`() {
        val doc = document(
            headStyles = listOf(
                CssRule(".btn", mapOf("color" to "red")),
                CssRule(".btn", mapOf("color" to "blue")),
            ),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("class" to "btn"))),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("blue", (result.children[0] as ElementNode).styles["color"])
    }

    @Test
    fun `media query rules stay in headStyles`() {
        val doc = document(
            headStyles = listOf(
                CssMediaQuery("max-width: 600px", listOf(CssRule(".btn", mapOf("width" to "100%")))),
            ),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("class" to "btn"))),
        )
        val result = CssInliningPass.run(doc)
        assertEquals(1, result.headStyles.size)
        assertTrue(result.headStyles[0] is CssMediaQuery)
    }

    @Test
    fun `mso conditional rules stay in headStyles`() {
        val doc = document(
            headStyles = listOf(
                CssMsoConditional(listOf(CssRule("table", mapOf("width" to "600px")))),
            ),
            children = listOf(ElementNode(tag = "table")),
        )
        val result = CssInliningPass.run(doc)
        assertEquals(1, result.headStyles.size)
        assertTrue(result.headStyles[0] is CssMsoConditional)
    }

    @Test
    fun `mixed inlineable and non-inlineable rules`() {
        val doc = document(
            headStyles = listOf(
                CssRule(".btn", mapOf("padding" to "10px")),
                CssMediaQuery("max-width: 600px", listOf(CssRule(".btn", mapOf("width" to "100%")))),
                CssRule("p", mapOf("margin" to "0")),
            ),
            children = listOf(
                ElementNode(tag = "div", attributes = mapOf("class" to "btn")),
                ElementNode(tag = "p"),
            ),
        )
        val result = CssInliningPass.run(doc)
        assertEquals(1, result.headStyles.size)
        assertTrue(result.headStyles[0] is CssMediaQuery)
        assertEquals("10px", (result.children[0] as ElementNode).styles["padding"])
        assertEquals("0", (result.children[1] as ElementNode).styles["margin"])
    }

    @Test
    fun `does not strip class from compound selector target`() {
        val doc = document(
            headStyles = listOf(CssRule(".card p", mapOf("font-size" to "12px"))),
            children = listOf(
                ElementNode(
                    tag = "div",
                    attributes = mapOf("class" to "card"),
                    children = listOf(ElementNode(tag = "p")),
                ),
            ),
        )
        val result = CssInliningPass.run(doc)
        val card = result.children[0] as ElementNode
        assertEquals("card", card.attributes["class"])
    }

    @Test
    fun `empty headStyles returns document unchanged`() {
        val doc = document(
            headStyles = emptyList(),
            children = listOf(ElementNode(tag = "div")),
        )
        val result = CssInliningPass.run(doc)
        assertEquals(doc, result)
    }

    @Test
    fun `no matching elements leaves styles in headStyles`() {
        val doc = document(
            headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            children = listOf(ElementNode(tag = "div")),
        )
        val result = CssInliningPass.run(doc)
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `multi-tag selector inlines into all matching tags`() {
        val doc = document(
            headStyles = listOf(CssRule("h1, h2, h3", mapOf("margin" to "0"))),
            children = listOf(
                ElementNode(tag = "h1"),
                ElementNode(tag = "h2"),
                ElementNode(tag = "h3"),
                ElementNode(tag = "p"),
            ),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("0", (result.children[0] as ElementNode).styles["margin"])
        assertEquals("0", (result.children[1] as ElementNode).styles["margin"])
        assertEquals("0", (result.children[2] as ElementNode).styles["margin"])
        assertTrue((result.children[3] as ElementNode).styles.isEmpty())
    }

    @Test
    fun `preserves other attributes besides class`() {
        val doc = document(
            headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            children = listOf(
                ElementNode(tag = "a", attributes = mapOf("class" to "btn", "href" to "https://example.com", "id" to "cta")),
            ),
        )
        val result = CssInliningPass.run(doc)
        val el = result.children[0] as ElementNode
        assertEquals("https://example.com", el.attributes["href"])
        assertEquals("cta", el.attributes["id"])
    }

    @Test
    fun `does not strip id after inlining id selector`() {
        val doc = document(
            headStyles = listOf(CssRule("#hero", mapOf("color" to "red"))),
            children = listOf(ElementNode(tag = "div", attributes = mapOf("id" to "hero"))),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("hero", (result.children[0] as ElementNode).attributes["id"])
    }

    @Test
    fun `passes through TextNode unchanged`() {
        val doc = document(
            headStyles = listOf(CssRule("p", mapOf("margin" to "0"))),
            children = listOf(TextNode("hello")),
        )
        val result = CssInliningPass.run(doc)
        assertEquals("hello", (result.children[0] as TextNode).value)
    }
}
