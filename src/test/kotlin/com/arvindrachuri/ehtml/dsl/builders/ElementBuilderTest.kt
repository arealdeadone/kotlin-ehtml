package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.compiler.HtmlEmitter
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementBuilderTest {

    @Test
    fun `builds element with tag name`() {
        val node = ElementBuilder("div").build()
        assertEquals("div", node.tag)
    }

    @Test
    fun `unaryPlus adds text child`() {
        val node = ElementBuilder("p").apply { +"hello" }.build()
        val html = HtmlEmitter.emit(node)
        assert("<p>hello</p>" == html)
    }

    @Test
    fun `attr sets attribute on element`() {
        val node = ElementBuilder("div").apply { attr("id", "main") }.build()
        val html = HtmlEmitter.emit(node)
        assert("""id="main"""" in html)
    }

    @Test
    fun `attrs sets multiple attributes`() {
        val node =
            ElementBuilder("div").apply { attrs("id" to "main", "class" to "wrapper") }.build()
        val html = HtmlEmitter.emit(node)
        assert("""id="main"""" in html)
        assert("""class="wrapper"""" in html)
    }

    @Test
    fun `required attributes are preserved in output`() {
        val node = ElementBuilder("a", mapOf("href" to "https://example.com")).build()
        val html = HtmlEmitter.emit(node)
        assert("""href="https://example.com"""" in html)
    }

    @Test
    fun `required attributes override user attrs with same key`() {
        val node =
            ElementBuilder("a", mapOf("href" to "https://required.com"))
                .apply { attr("href", "https://user.com") }
                .build()
        val html = HtmlEmitter.emit(node)
        assert("""href="https://required.com"""" in html)
        assert("https://user.com" !in html)
    }

    @Test
    fun `style block sets inline styles`() {
        val node =
            ElementBuilder("div")
                .apply {
                    style {
                        padding = "16px"
                        color = "#333"
                    }
                }
                .build()
        val html = HtmlEmitter.emit(node)
        assert("padding:16px" in html)
        assert("color:#333" in html)
    }

    @Test
    fun `applyDefaultStyles merges under user styles`() {
        val builder = ElementBuilder("img").apply { style { width = "200px" } }
        builder.applyDefaultStyles(mapOf("display" to "block", "border" to "0"))
        val node = builder.build()
        val html = HtmlEmitter.emit(node)
        assert("display:block" in html)
        assert("border:0" in html)
        assert("width:200px" in html)
    }

    @Test
    fun `user styles override default styles on conflict`() {
        val builder = ElementBuilder("img").apply { style { border = "1px solid red" } }
        builder.applyDefaultStyles(mapOf("border" to "0"))
        val node = builder.build()
        val html = HtmlEmitter.emit(node)
        assert("border:1px solid red" in html)
        assert("border:0" !in html)
    }

    @Test
    fun `nested elements produce correct html`() {
        val node = ElementBuilder("div").apply { p { +"hello" } }.build()
        val html = HtmlEmitter.emit(node)
        assert("<div><p>hello</p></div>" == html)
    }

    @Test
    fun `deeply nested elements`() {
        val node = ElementBuilder("div").apply { span { strong { +"bold" } } }.build()
        val html = HtmlEmitter.emit(node)
        assert("<div><span><strong>bold</strong></span></div>" == html)
    }

    @Test
    fun `mixed text and element children`() {
        val node =
            ElementBuilder("p")
                .apply {
                    +"before "
                    strong { +"bold" }
                    +" after"
                }
                .build()
        val html = HtmlEmitter.emit(node)
        assert("<p>before <strong>bold</strong> after</p>" == html)
    }

    @Test
    fun `empty element produces open and close tags`() {
        val node = ElementBuilder("div").build()
        val html = HtmlEmitter.emit(node)
        assert("<div></div>" == html)
    }
}
