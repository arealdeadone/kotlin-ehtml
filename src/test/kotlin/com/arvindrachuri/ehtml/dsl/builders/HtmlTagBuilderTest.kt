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

    @Test
    fun `ul renders with li children`() {
        val html = columnHtml {
            ul {
                li { +"item one" }
                li { +"item two" }
            }
        }
        assert("<ul><li>item one</li><li>item two</li></ul>" in html)
    }

    @Test
    fun `ol renders with li children`() {
        val html = columnHtml {
            ol {
                li { +"first" }
                li { +"second" }
            }
        }
        assert("<ol><li>first</li><li>second</li></ol>" in html)
    }

    @Test
    fun `li renders inside column`() {
        val html = columnHtml { li { +"standalone" } }
        assert("<li>standalone</li>" in html)
    }

    @Test
    fun `blockquote renders inside column`() {
        val html = columnHtml { blockquote { +"quoted text" } }
        assert("<blockquote>quoted text</blockquote>" in html)
    }

    @Test
    fun `blockquote with nested elements`() {
        val html = columnHtml { blockquote { p { +"paragraph inside quote" } } }
        assert("<blockquote><p>paragraph inside quote</p></blockquote>" in html)
    }

    @Test
    fun `i renders inside column`() {
        val html = columnHtml { i { +"italic" } }
        assert("<i>italic</i>" in html)
    }

    @Test
    fun `u renders inside column`() {
        val html = columnHtml { u { +"underlined" } }
        assert("<u>underlined</u>" in html)
    }

    @Test
    fun `s renders inside column`() {
        val html = columnHtml { s { +"struck" } }
        assert("<s>struck</s>" in html)
    }

    @Test
    fun `s with pricing pattern`() {
        val html = columnHtml {
            s { +"$99" }
            span { +" $79" }
        }
        assert("<s>$99</s>" in html)
    }

    @Test
    fun `sup renders inside column`() {
        val html = columnHtml { sup { +"2" } }
        assert("<sup>2</sup>" in html)
    }

    @Test
    fun `sub renders inside column`() {
        val html = columnHtml { sub { +"2" } }
        assert("<sub>2</sub>" in html)
    }

    @Test
    fun `pre renders inside column`() {
        val html = columnHtml { pre { +"code block" } }
        assert("<pre>code block</pre>" in html)
    }

    @Test
    fun `ul with styled li`() {
        val html = columnHtml {
            ul {
                li {
                    style { color = "#333" }
                    +"styled item"
                }
            }
        }
        assert("color: #333" in html)
        assert("styled item" in html)
    }

    @Test
    fun `element with className renders class attribute`() {
        val html = columnHtml {
            div {
                className = "darkmode-bg custom-class"
                +"content"
            }
        }
        assert("""class="darkmode-bg custom-class"""" in html)
    }

    @Test
    fun `element with id renders id attribute`() {
        val html = columnHtml {
            div {
                id = "hero"
                +"content"
            }
        }
        assert("""id="hero"""" in html)
    }

    @Test
    fun `element with className and id renders both`() {
        val html = columnHtml {
            p {
                className = "text-sm-center"
                id = "intro"
                +"text"
            }
        }
        assert("""class="text-sm-center"""" in html)
        assert("""id="intro"""" in html)
    }

    @Test
    fun `className coexists with style`() {
        val html = columnHtml {
            div {
                className = "custom-class"
                style { padding = "16px" }
                +"content"
            }
        }
        assert("""class="custom-class"""" in html)
        assert("padding: 16px" in html)
    }

    @Test
    fun `nested list renders correctly`() {
        val html = columnHtml {
            ul {
                li {
                    +"parent"
                    ul { li { +"child" } }
                }
            }
        }
        assert("<ul><li>parent<ul><li>child</li></ul></li></ul>" in html)
    }

    @Test
    fun `button renders as anchor with text`() {
        val html = columnHtml { button("Click Me", "https://example.com", "btn-1") {} }
        assert("""href="https://example.com"""" in html)
        assert("""id="btn-1"""" in html)
        assert(">Click Me</a>" in html)
    }

    @Test
    fun `button has default display block`() {
        val html = columnHtml { button("Click", "https://example.com", "btn-1") {} }
        assert("display: block" in html)
    }

    @Test
    fun `button has default text-align center`() {
        val html = columnHtml { button("Click", "https://example.com", "btn-1") {} }
        assert("text-align: center" in html)
    }

    @Test
    fun `button has default text-decoration none`() {
        val html = columnHtml { button("Click", "https://example.com", "btn-1") {} }
        assert("text-decoration: none" in html)
    }

    @Test
    fun `button user styles override defaults`() {
        val html = columnHtml {
            button("Click", "https://example.com", "btn-1") {
                style { display = DisplayType.InlineBlock }
            }
        }
        assert("display: inline-block" in html)
        assert("display: block" !in html)
    }

    @Test
    fun `button with className and custom styles`() {
        val html = columnHtml {
            button("Join Now", "https://example.com", "cta-1") {
                className = "btn-primary"
                style {
                    backgroundColor = "#1B7C96"
                    color = "#ffffff"
                    padding = "10px"
                    borderRadius = "5px"
                }
            }
        }
        assert("""class="btn-primary"""" in html)
        assert("background-color: #1B7C96" in html)
        assert("color: #fff" in html)
        assert("padding: 10px" in html)
        assert("border-radius: 5px" in html)
        assert("text-decoration: none" in html)
        assert(">Join Now</a>" in html)
    }

    @Test
    fun `button text appears after block children`() {
        val html = columnHtml {
            button("Click", "https://example.com", "btn-1") { span { +"icon " } }
        }
        assert("<span>icon </span>Click</a>" in html)
    }

    @Test
    fun `img with width and height params renders attributes and styles`() {
        val html = columnHtml { img(src = "photo.jpg", alt = "Photo", width = 290, height = 288) }
        assert("""width="290"""" in html)
        assert("""height="288"""" in html)
        assert("width: 290px" in html)
        assert("height: 288px" in html)
    }

    @Test
    fun `img with only width param`() {
        val html = columnHtml { img(src = "photo.jpg", alt = "Photo", width = 155) }
        assert("""width="155"""" in html)
        assert("width: 155px" in html)
        assert("""height=""" !in html)
    }

    @Test
    fun `img with only height param`() {
        val html = columnHtml { img(src = "photo.jpg", alt = "Photo", height = 45) }
        assert("""height="45"""" in html)
        assert("height: 45px" in html)
    }

    @Test
    fun `img width and height user style overrides default`() {
        val html = columnHtml {
            img(src = "photo.jpg", alt = "Photo", width = 290) { style { width = "100%" } }
        }
        assert("width: 100%" in html)
        assert("width: 290px" !in html)
        assert("""width="290"""" in html)
    }

    @Test
    fun `a with style renders inline css`() {
        val html = columnHtml {
            a(href = "https://example.com") {
                style { color = "#1B7C96" }
                +"link"
            }
        }
        assert("color: #1B7C96" in html)
        assert("""href="https://example.com"""" in html)
    }

    @Test
    fun `a with nested elements`() {
        val html = columnHtml { a(href = "https://example.com") { strong { +"bold link" } } }
        assert("""<a href="https://example.com"><strong>bold link</strong></a>""" in html)
    }

    @Test
    fun `h1 with style renders correctly`() {
        val html = columnHtml {
            h1 {
                style {
                    fontSize = "24px"
                    color = "#333"
                }
                +"heading"
            }
        }
        assert("font-size: 24px" in html)
        assert("color: #333" in html)
        assert("<h1" in html)
    }

    @Test
    fun `em with nested content`() {
        val html = columnHtml { em { strong { +"bold italic" } } }
        assert("<em><strong>bold italic</strong></em>" in html)
    }

    @Test
    fun `b with style`() {
        val html = columnHtml {
            b {
                style { color = "red" }
                +"bold"
            }
        }
        assert("color: red" in html)
        assert("<b" in html)
    }

    @Test
    fun `pre with nested elements`() {
        val html = columnHtml { pre { span { +"code" } } }
        assert("<pre><span>code</span></pre>" in html)
    }

    @Test
    fun `blockquote with style`() {
        val html = columnHtml {
            blockquote {
                style { padding = "10px" }
                +"quote"
            }
        }
        assert("padding: 10px" in html)
        assert("<blockquote" in html)
    }

    @Test
    fun `button with empty block`() {
        val html = columnHtml { button("Click", "https://example.com", "btn-1") {} }
        assert(">Click</a>" in html)
        assert("display: block" in html)
    }

    @Test
    fun `spacer with zero height`() {
        val html = columnHtml { spacer(0) }
        assert("height: 0" in html)
        assert("font-size: 0" in html)
        assert("line-height: 0" in html)
    }

    @Test
    fun `text content is HTML escaped`() {
        val html = columnHtml { p { +"<script>alert('xss')</script>" } }
        assert("<script>" !in html)
        assert("&lt;script&gt;" in html)
    }

    @Test
    fun `multiple text nodes inside element`() {
        val html = columnHtml {
            p {
                +"first "
                +"second"
            }
        }
        assert("first second" in html)
    }

    @Test
    fun `element with only children no text`() {
        val html = columnHtml {
            div {
                p { +"one" }
                p { +"two" }
            }
        }
        assert("<div><p>one</p><p>two</p></div>" in html)
    }

    @Test
    fun `div with custom attribute via attr`() {
        val html = columnHtml {
            div {
                attr("role", "banner")
                +"content"
            }
        }
        assert("""role="banner"""" in html)
    }

    @Test
    fun `img with no optional params renders minimal`() {
        val html = columnHtml { img(src = "logo.png", alt = "Logo") }
        assert("""src="logo.png"""" in html)
        assert("""alt="Logo"""" in html)
        assert("display: block" in html)
        assert("border: 0" in html)
    }

    @Test
    fun `ol with styled items`() {
        val html = columnHtml {
            ol {
                li {
                    style { fontWeight = "bold" }
                    +"item"
                }
            }
        }
        assert("font-weight: bold" in html)
        assert("<ol><li" in html)
    }

    @Test
    fun `mixed inline and block elements in column`() {
        val html = columnHtml {
            p { +"text" }
            div {
                span { +"inline" }
                strong { +"bold" }
            }
        }
        val pPos = html.indexOf("<p>text</p>")
        val divPos = html.indexOf("<div>")
        assert(pPos >= 0)
        assert(divPos >= 0)
        assert(pPos < divPos)
    }

    @Test
    fun `deeply nested structure renders correctly`() {
        val html = columnHtml { div { ul { li { p { span { strong { +"deep" } } } } } } }
        assert("<div><ul><li><p><span><strong>deep</strong></span></p></li></ul></div>" in html)
    }

    @Test
    fun `sup and sub mixed with text`() {
        val html = columnHtml {
            p {
                +"E = mc"
                sup { +"2" }
            }
        }
        assert("E = mc" in html)
        assert("<sup>2</sup>" in html)
    }

    @Test
    fun `s and span pricing pattern with styles`() {
        val html = columnHtml {
            p {
                s {
                    style { color = "#999" }
                    +"$99"
                }
                span {
                    style {
                        color = "#1B7C96"
                        fontWeight = "bold"
                    }
                    +" $79"
                }
            }
        }
        assert("<s" in html)
        assert("$99" in html)
        assert("$79" in html)
        assert("font-weight: bold" in html)
    }

    @Test
    fun `i with className for icon fonts`() {
        val html = columnHtml {
            i {
                className = "icon-star"
                attr("aria-hidden", "true")
            }
        }
        assert("""class="icon-star"""" in html)
        assert("""aria-hidden="true"""" in html)
        assert("<i " in html)
    }
}
