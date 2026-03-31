package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.utils.Constants
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class MsoConditionalPassTest {

    private fun containerTable(
        width: Int = 600,
        children: List<EmailNode> = listOf(TextNode("content")),
    ) =
        ElementNode(
            tag = "table",
            attributes =
                mapOf(
                    "align" to "center",
                    "border" to "0",
                    "cellpadding" to "0",
                    "cellspacing" to "0",
                    Constants.MSO_PASS_MARKER to Constants.MSO_PASS_MARKED_CONTAINER,
                    "role" to "presentation",
                    "width" to width.toString(),
                ),
            styles = mapOf("margin" to "0 auto", "width" to "${width}px"),
            children = children,
        )

    private fun emit(nodes: List<EmailNode>): String =
        nodes.joinToString("") { HtmlEmitter.emit(it) }

    @Test
    fun `container table gets ghost-wrapped with MSO conditionals`() {
        val result = MsoConditionalPass.run(containerTable())
        assertEquals(3, result.size)
        val html = emit(result)
        assertTrue(html.startsWith("<!--[if mso]>"))
        assertTrue(html.endsWith("<!--[if mso]></td></tr></table><![endif]-->"))
    }

    @Test
    fun `ghost table opening uses correct width from container`() {
        val result = MsoConditionalPass.run(containerTable(width = 800))
        val opening = result[0] as RawHtmlNode
        assertEquals(
            """<!--[if mso]><table role="presentation" cellpadding="0" cellspacing="0" border="0" width="800" align="center"><tr><td><![endif]-->""",
            opening.value,
        )
    }

    @Test
    fun `ghost table opening uses default 600 width`() {
        val result = MsoConditionalPass.run(containerTable())
        val opening = result[0] as RawHtmlNode
        assertEquals(
            """<!--[if mso]><table role="presentation" cellpadding="0" cellspacing="0" border="0" width="600" align="center"><tr><td><![endif]-->""",
            opening.value,
        )
    }

    @Test
    fun `ghost table closing comment is correct`() {
        val result = MsoConditionalPass.run(containerTable())
        val closing = result[2] as RawHtmlNode
        assertEquals("<!--[if mso]></td></tr></table><![endif]-->", closing.value)
    }

    @Test
    fun `marker attribute is stripped from output table`() {
        val result = MsoConditionalPass.run(containerTable())
        val table = result[1] as ElementNode
        assertTrue(Constants.MSO_PASS_MARKER !in table.attributes)
    }

    @Test
    fun `non-marker attributes are preserved on stripped table`() {
        val result = MsoConditionalPass.run(containerTable())
        val table = result[1] as ElementNode
        assertEquals("center", table.attributes["align"])
        assertEquals("0", table.attributes["border"])
        assertEquals("presentation", table.attributes["role"])
        assertEquals("600", table.attributes["width"])
    }

    @Test
    fun `non-marked ElementNode passes through unchanged`() {
        val node = ElementNode(tag = "div", children = listOf(TextNode("hello")))
        val result = MsoConditionalPass.run(node)
        assertEquals(1, result.size)
        assertEquals(node, result[0])
    }

    @Test
    fun `element with wrong marker value passes through unchanged`() {
        val node =
            ElementNode(
                tag = "table",
                attributes = mapOf(Constants.MSO_PASS_MARKER to "row"),
                children = listOf(TextNode("not a container")),
            )
        val result = MsoConditionalPass.run(node)
        assertEquals(1, result.size)
        assertEquals(node, result[0])
    }

    @Test
    fun `TextNode passes through unchanged`() {
        val node = TextNode("plain text")
        val result = MsoConditionalPass.run(node)
        assertEquals(listOf(node), result)
    }

    @Test
    fun `RawHtmlNode passes through unchanged`() {
        val node = RawHtmlNode("<br />")
        val result = MsoConditionalPass.run(node)
        assertEquals(listOf(node), result)
    }

    @Test
    fun `nested container inside container both get wrapped`() {
        val inner = containerTable(width = 400, children = listOf(TextNode("inner")))
        val outer = containerTable(width = 600, children = listOf(inner))
        val result = MsoConditionalPass.run(outer)
        assertEquals(3, result.size)

        val outerTable = result[1] as ElementNode
        assertEquals(3, outerTable.children.size)

        val innerOpening = outerTable.children[0] as RawHtmlNode
        assertTrue("width=\"400\"" in innerOpening.value)

        val innerTable = outerTable.children[1] as ElementNode
        assertTrue(Constants.MSO_PASS_MARKER !in innerTable.attributes)

        val innerClosing = outerTable.children[2] as RawHtmlNode
        assertEquals("<!--[if mso]></td></tr></table><![endif]-->", innerClosing.value)
    }

    @Test
    fun `container inside non-container ElementNode gets wrapped`() {
        val container = containerTable()
        val wrapper = ElementNode(tag = "div", children = listOf(container))
        val result = MsoConditionalPass.run(wrapper)
        assertEquals(1, result.size)

        val div = result[0] as ElementNode
        assertEquals(3, div.children.size)
        assertTrue(div.children[0] is RawHtmlNode)
        assertTrue(div.children[1] is ElementNode)
        assertTrue(div.children[2] is RawHtmlNode)
    }

    @Test
    fun `EmailDocumentNode recurses into children`() {
        val doc = EmailDocumentNode(title = "Test", children = listOf(containerTable()))
        val result = MsoConditionalPass.run(doc)
        assertEquals(1, result.size)

        val document = result[0] as EmailDocumentNode
        assertEquals(3, document.children.size)
        assertTrue(document.children[0] is RawHtmlNode)
        assertTrue(document.children[1] is ElementNode)
        assertTrue(document.children[2] is RawHtmlNode)
    }

    @Test
    fun `EmailDocumentNode with multiple containers wraps each`() {
        val doc =
            EmailDocumentNode(
                title = "Test",
                children = listOf(containerTable(width = 600), containerTable(width = 400)),
            )
        val result = MsoConditionalPass.run(doc)
        val document = result[0] as EmailDocumentNode
        assertEquals(6, document.children.size)

        val firstOpening = document.children[0] as RawHtmlNode
        assertTrue("width=\"600\"" in firstOpening.value)

        val secondOpening = document.children[3] as RawHtmlNode
        assertTrue("width=\"400\"" in secondOpening.value)
    }

    @Test
    fun `element with no attributes passes through`() {
        val node = ElementNode(tag = "span")
        val result = MsoConditionalPass.run(node)
        assertEquals(1, result.size)
        assertEquals(node, result[0])
    }

    @Test
    fun `mixed children with containers and plain nodes`() {
        val doc =
            EmailDocumentNode(
                title = "Test",
                children = listOf(TextNode("before"), containerTable(), TextNode("after")),
            )
        val result = MsoConditionalPass.run(doc)
        val document = result[0] as EmailDocumentNode
        assertEquals(5, document.children.size)
        assertEquals(TextNode("before"), document.children[0])
        assertTrue(document.children[1] is RawHtmlNode)
        assertTrue(document.children[2] is ElementNode)
        assertTrue(document.children[3] is RawHtmlNode)
        assertEquals(TextNode("after"), document.children[4])
    }
}
