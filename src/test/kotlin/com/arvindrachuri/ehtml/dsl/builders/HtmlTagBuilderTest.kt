package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.dsl.builders.html.ColumnBuilder
import com.arvindrachuri.ehtml.dsl.email
import com.arvindrachuri.ehtml.utils.css.values.DisplayType
import com.arvindrachuri.ehtml.utils.css.values.OverflowType
import com.arvindrachuri.ehtml.utils.css.values.TextDecorationType
import com.arvindrachuri.ehtml.utils.css.values.TextTransformType
import kotlin.test.Test

class HtmlTagBuilderTest {

    private fun columnHtml(block: ColumnBuilder.() -> Unit): String {
        return email {
            head { title = "Test" }
            container { row { column(block) } }
        }
    }

    @Test
    fun `div renders inside column`() {
        val html = columnHtml { div { +"content" } }
        assert("<div>content</div>" in html)
    }

    @Test
    fun `p renders inside column`() {
        val html = columnHtml { p { +"paragraph" } }
        assert("<p>paragraph</p>" in html)
    }

    @Test
    fun `h1 renders inside column`() {
        val html = columnHtml { h1 { +"heading" } }
        assert("<h1>heading</h1>" in html)
    }

    @Test
    fun `h2 renders inside column`() {
        val html = columnHtml { h2 { +"heading" } }
        assert("<h2>heading</h2>" in html)
    }

    @Test
    fun `h3 renders inside column`() {
        val html = columnHtml { h3 { +"heading" } }
        assert("<h3>heading</h3>" in html)
    }

    @Test
    fun `h4 renders inside column`() {
        val html = columnHtml { h4 { +"heading" } }
        assert("<h4>heading</h4>" in html)
    }

    @Test
    fun `h5 renders inside column`() {
        val html = columnHtml { h5 { +"heading" } }
        assert("<h5>heading</h5>" in html)
    }

    @Test
    fun `h6 renders inside column`() {
        val html = columnHtml { h6 { +"heading" } }
        assert("<h6>heading</h6>" in html)
    }

    @Test
    fun `span renders inside column`() {
        val html = columnHtml { span { +"inline" } }
        assert("<span>inline</span>" in html)
    }

    @Test
    fun `strong renders inside column`() {
        val html = columnHtml { strong { +"bold" } }
        assert("<strong>bold</strong>" in html)
    }

    @Test
    fun `b renders inside column`() {
        val html = columnHtml { b { +"bold" } }
        assert("<b>bold</b>" in html)
    }

    @Test
    fun `em renders inside column`() {
        val html = columnHtml { em { +"italic" } }
        assert("<em>italic</em>" in html)
    }

    @Test
    fun `a renders with required href`() {
        val html = columnHtml { a(href = "https://example.com") { +"click" } }
        assert("""href="https://example.com"""" in html)
        assert("click" in html)
    }

    @Test
    fun `a with additional attributes`() {
        val html = columnHtml {
            a(href = "https://example.com") {
                attr("target", "_blank")
                +"click"
            }
        }
        assert("""href="https://example.com"""" in html)
        assert("""target="_blank"""" in html)
    }

    @Test
    fun `img renders with required src and alt`() {
        val html = columnHtml { img(src = "logo.png", alt = "Logo") }
        assert("""src="logo.png"""" in html)
        assert("""alt="Logo"""" in html)
    }

    @Test
    fun `img has default display block and border 0 styles`() {
        val html = columnHtml { img(src = "photo.jpg", alt = "Photo") }
        assert("display: block" in html)
        assert("border: 0" in html)
    }

    @Test
    fun `img user styles override defaults`() {
        val html = columnHtml {
            img(src = "photo.jpg", alt = "Photo") { style { border = "1px solid red" } }
        }
        assert("border: 1px solid red" in html)
        assert("border: 0" !in html)
        assert("display: block" in html)
    }

    @Test
    fun `img renders as self-closing void tag`() {
        val html = columnHtml { img(src = "logo.png", alt = "Logo") }
        assert("""<img """ in html)
        assert(""" />""" in html)
        assert("</img>" !in html)
    }

    @Test
    fun `hr renders as self-closing void tag`() {
        val html = columnHtml { hr() }
        assert("<hr />" in html)
        assert("</hr>" !in html)
    }

    @Test
    fun `hr with styles`() {
        val html = columnHtml { hr { style { border = "1px solid #ccc" } } }
        assert("border: 1px solid #ccc" in html)
    }

    @Test
    fun `br renders as self-closing void tag`() {
        val html = columnHtml { br() }
        assert("<br />" in html)
        assert("</br>" !in html)
    }

    @Test
    fun `nested elements inside column`() {
        val html = columnHtml { div { p { strong { +"bold text" } } } }
        assert("<div><p><strong>bold text</strong></p></div>" in html)
    }

    @Test
    fun `multiple elements inside column preserve order`() {
        val html = columnHtml {
            h1 { +"Title" }
            p { +"Body" }
            hr()
        }
        val h1Pos = html.indexOf("<h1>")
        val pPos = html.indexOf("<p>")
        val hrPos = html.indexOf("<hr")
        assert(h1Pos < pPos)
        assert(pPos < hrPos)
    }

    @Test
    fun `elements with styles render inline css`() {
        val html = columnHtml {
            p {
                style {
                    color = "#333"
                    fontSize = "16px"
                }
                +"styled"
            }
        }
        assert("color: #333" in html)
        assert("font-size: 16px" in html)
    }

    @Test
    fun `element nesting inside other elements`() {
        val html = columnHtml { div { span { a(href = "https://example.com") { +"link" } } } }
        assert("""<div><span><a href="https://example.com">link</a></span></div>""" in html)
    }

    @Test
    fun `spacer renders div with height styles`() {
        val html = columnHtml { spacer(30) }
        assert("height: 30px" in html)
        assert("font-size: 30px" in html)
        assert("line-height: 30px" in html)
    }

    @Test
    fun `spacer with different height`() {
        val html = columnHtml { spacer(10) }
        assert("height: 10px" in html)
        assert("font-size: 10px" in html)
        assert("line-height: 10px" in html)
    }

    @Test
    fun `spacer between elements preserves order`() {
        val html = columnHtml {
            p { +"before" }
            spacer(20)
            p { +"after" }
        }
        val beforePos = html.indexOf("before")
        val spacerPos = html.indexOf("height: 20px")
        val afterPos = html.indexOf("after")
        assert(beforePos < spacerPos)
        assert(spacerPos < afterPos)
    }

    @Test
    fun `element with enum-typed styles renders correctly`() {
        val html = columnHtml {
            div {
                style {
                    display = DisplayType.Block
                    textDecoration = TextDecorationType.None
                    textTransform = TextTransformType.UpperCase
                    overflow = OverflowType.Hidden
                }
                +"styled"
            }
        }
        assert("display: block" in html)
        assert("text-decoration: none" in html)
        assert("text-transform: uppercase" in html)
        assert("overflow: hidden" in html)
    }

    @Test
    fun `element with fontFamily renders correctly`() {
        val html = columnHtml {
            p {
                style { fontFamily = "'Comfortaa', Helvetica, sans-serif" }
                +"text"
            }
        }
        assert("font-family" in html)
        assert("Comfortaa" in html)
    }

    @Test
    fun `element with borderRadius renders correctly`() {
        val html = columnHtml {
            div {
                style { borderRadius = "12px" }
                +"card"
            }
        }
        assert("border-radius: 12px" in html)
    }
}
