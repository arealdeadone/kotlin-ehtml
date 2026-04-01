package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
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
}
