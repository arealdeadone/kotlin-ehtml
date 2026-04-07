package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class HtmlEmitterTest {

    @Test
    fun `emits plain text`() {
        val node = TextNode("This is plain text")
        assertEquals("This is plain text", HtmlEmitter.emit(node))
    }

    @Test
    fun `escapes html tags in text nodes`() {
        val node =
            TextNode(
                """
                <script>
                    alert(1);
                </script>
                """
                    .trimIndent()
            )
        val result = HtmlEmitter.emit(node)
        assert("<script>" !in result && "<" !in result && ">" !in result)
        assert("&" in result)
    }

    @Test
    fun `emits empty element with open and close tags when no children are passed`() {
        val node = ElementNode(tag = "div")
        assertEquals("<div></div>", HtmlEmitter.emit(node))
    }

    @Test
    fun `void tags are self closing`() {
        val node =
            ElementNode(
                tag = "img",
                mapOf("src" to "https://placehold.co/600x400", "alt" to "placeholder"),
            )
        val result = HtmlEmitter.emit(node)
        assert(result.endsWith("/>"))
        assert("</img>" !in result)
    }

    @Test
    fun `url attributes like href and src are encoded valid urls and query parameters`() {
        val aNode =
            ElementNode(tag = "a", attributes = mapOf("href" to "https://example.com?a=1&b=2"))
        val imgNode =
            ElementNode(tag = "img", attributes = mapOf("src" to "https://example.com?a=1&b=2"))
        val resultANode = HtmlEmitter.emit(aNode)
        val resultImgNode = HtmlEmitter.emit(imgNode)
        assertEquals("""<a href="https://example.com?a=1&amp;b=2"></a>""", resultANode)
        assertEquals("""<img src="https://example.com?a=1&amp;b=2" />""", resultImgNode)
    }

    @Test
    fun `serializes styles into style attribute`() {
        val node = ElementNode(tag = "td", styles = mapOf("color" to "blue", "margin" to "0 auto"))
        val result = HtmlEmitter.emit(node)
        assert("style=\"" in result)
        assert("color: blue;" in result)
        assert("margin: 0 auto" in result)
    }

    @Test
    fun `children are rendered in order`() {
        val node =
            ElementNode(
                tag = "td",
                children = listOf(TextNode("first value "), TextNode("second value")),
            )
        val result = HtmlEmitter.emit(node)
        assertEquals("""<td>first value second value</td>""", result)
    }

    @Test
    fun `raw html is emitted without escaping`() {
        val node = RawHtmlNode("<br />")
        assertEquals("<br />", HtmlEmitter.emit(node))
    }

    @Test
    fun `Container node must be lowered and throws exception when not lowered`() {
        assertFailsWith<IllegalStateException> { HtmlEmitter.emit(ContainerNode()) }
    }

    @Test
    fun `Row node must be lowered and throws exception when not lowered`() {
        assertFailsWith<IllegalStateException> { HtmlEmitter.emit(RowNode()) }
    }

    @Test
    fun `Column node must be lowered and throws exception when not lowered`() {
        assertFailsWith<IllegalStateException> { HtmlEmitter.emit(ColumnNode()) }
    }

    @Test
    fun `mso conditional emits separate style block`() {
        val node =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(CssMsoConditional(listOf(CssRule("table", mapOf("width" to "600px"))))),
            )
        val html = HtmlEmitter.emit(node)
        assert("<!--[if mso]>" in html)
        assert("<![endif]-->" in html)
        assert("width: 600px" in html)
    }

    @Test
    fun `mso conditional style block is separate from main style block`() {
        val node =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(
                        CssRule("body", mapOf("margin" to "0")),
                        CssMsoConditional(listOf(CssRule("table", mapOf("width" to "600px")))),
                    ),
            )
        val html = HtmlEmitter.emit(node)
        val mainStyleStart = html.indexOf("""<style type="text/css">""")
        val mainStyleEnd = html.indexOf("</style>", mainStyleStart)
        val mainBlock = html.substring(mainStyleStart, mainStyleEnd)
        assert("margin: 0" in mainBlock)
        val msoStart = html.indexOf("<!--[if mso]>", mainStyleEnd)
        assert(msoStart > mainStyleEnd)
    }

    @Test
    fun `mso conditional rules do not appear in main style block`() {
        val node =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(
                        CssRule("body", mapOf("margin" to "0")),
                        CssMsoConditional(listOf(CssRule(".mso-only", mapOf("padding" to "10px")))),
                    ),
            )
        val html = HtmlEmitter.emit(node)
        val mainStyleStart = html.indexOf("""<style type="text/css">""")
        val mainStyleEnd = html.indexOf("</style>")
        val mainBlock = html.substring(mainStyleStart, mainStyleEnd)
        assert(".mso-only" !in mainBlock)
        assert("margin: 0" in mainBlock)
    }

    @Test
    fun `mso conditional with multiple rules`() {
        val node =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(
                        CssMsoConditional(
                            listOf(
                                CssRule("table", mapOf("width" to "600px")),
                                CssRule(".btn", mapOf("padding" to "10px 20px")),
                            )
                        )
                    ),
            )
        val html = HtmlEmitter.emit(node)
        assert("width: 600px" in html)
        assert("padding: 10px 20px" in html)
    }

    @Test
    fun `multiple mso conditional blocks emitted separately`() {
        val node =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(
                        CssMsoConditional(listOf(CssRule("table", mapOf("width" to "600px")))),
                        CssMsoConditional(listOf(CssRule(".col", mapOf("display" to "block")))),
                    ),
            )
        val html = HtmlEmitter.emit(node)
        assert("width: 600px" in html)
        assert("display: block" in html)
        val msoStyleCount = """<style type="text/css">""".toRegex().findAll(html).count()
        assertEquals(2, msoStyleCount)
    }

    @Test
    fun `no main style block emitted when only mso styles present`() {
        val node =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(CssMsoConditional(listOf(CssRule("table", mapOf("width" to "600px"))))),
            )
        val html = HtmlEmitter.emit(node)
        val styleCount = """<style type="text/css">""".toRegex().findAll(html).count()
        assertEquals(1, styleCount)
        assert("<!--[if mso]>" in html)
    }

    @Test
    fun `mso conditional with media query inside`() {
        val node =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(
                        CssMsoConditional(
                            listOf(
                                CssMediaQuery(
                                    "max-width: 600px",
                                    listOf(CssRule(".btn", mapOf("width" to "100%"))),
                                )
                            )
                        )
                    ),
            )
        val html = HtmlEmitter.emit(node)
        assert("<!--[if mso]>" in html)
        assert("@media (max-width: 600px)" in html)
        assert("width: 100%" in html)
    }
}
