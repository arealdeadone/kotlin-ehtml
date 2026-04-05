package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.dsl.email
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SingleBuilderTest {

    private fun singleHtml(
        block: com.arvindrachuri.ehtml.dsl.builders.html.SingleBuilder.() -> Unit
    ): String {
        return email {
            head { title = "Test" }
            single(block)
        }
    }

    @Test
    fun `single produces table with tr and td`() {
        val html = singleHtml { +"content" }
        assertTrue("<table" in html)
        assertTrue("<tr>" in html)
        assertTrue("<td" in html)
        assertTrue("content" in html)
    }

    @Test
    fun `single renders text content`() {
        val html = singleHtml { +"hello world" }
        assertTrue("hello world" in html)
    }

    @Test
    fun `single renders child elements`() {
        val html = singleHtml {
            p { +"paragraph" }
            div { +"block" }
        }
        assertTrue("<p>paragraph</p>" in html)
        assertTrue("<div>block</div>" in html)
    }

    @Test
    fun `single applies width to container table`() {
        val html = singleHtml {
            width = 500
            +"content"
        }
        assertTrue("""width="500"""" in html)
    }

    @Test
    fun `single default width is 600`() {
        val html = singleHtml { +"content" }
        assertTrue("""width="600"""" in html)
    }

    @Test
    fun `single applies className to column td`() {
        val html = singleHtml {
            className = "custom-class"
            +"content"
        }
        assertTrue("""class="custom-class"""" in html)
    }

    @Test
    fun `single applies id to column td`() {
        val html = singleHtml {
            id = "section-1"
            +"content"
        }
        assertTrue("""id="section-1"""" in html)
    }

    @Test
    fun `single applies style to column td`() {
        val html = singleHtml {
            style { padding = "20px" }
            +"content"
        }
        assertTrue("padding: 20px" in html)
    }

    @Test
    fun `single className and style coexist`() {
        val html = singleHtml {
            className = "my-section"
            style { backgroundColor = "#fff" }
            +"content"
        }
        assertTrue("""class="my-section"""" in html)
        assertTrue("background-color: #fff" in html)
    }

    @Test
    fun `single with utility class inlines style`() {
        val html = singleHtml {
            className = "text-center font-bold"
            +"content"
        }
        assertTrue("text-align: center" in html)
        assertTrue("font-weight: bold" in html)
    }

    @Test
    fun `single with responsive utility keeps class and emits media query`() {
        val html = singleHtml {
            className = "sm-d-block"
            +"content"
        }
        assertTrue("""class="sm-d-block"""" in html)
        assertTrue("@media (max-width: 600px)" in html)
        assertTrue(".sm-d-block" in html)
    }

    @Test
    fun `single with img renders correctly`() {
        val html = singleHtml { img(src = "logo.png", alt = "Logo", width = 140, height = 36) }
        assertTrue("""src="logo.png"""" in html)
        assertTrue("""width="140"""" in html)
    }

    @Test
    fun `single with button renders correctly`() {
        val html = singleHtml { button("Click", "https://example.com", "btn-1") {} }
        assertTrue("""href="https://example.com"""" in html)
        assertTrue(">Click</a>" in html)
    }

    @Test
    fun `single with spacer renders height div`() {
        val html = singleHtml { spacer(20) }
        assertTrue("height: 20px" in html)
    }

    @Test
    fun `single preserves child order`() {
        val html = singleHtml {
            h1 { +"title" }
            p { +"body" }
            hr()
        }
        val h1Pos = html.indexOf("<h1>")
        val pPos = html.indexOf("<p>")
        val hrPos = html.indexOf("<hr")
        assertTrue(h1Pos < pPos)
        assertTrue(pPos < hrPos)
    }

    @Test
    fun `single with attr adds attribute to column`() {
        val html = singleHtml {
            attr("role", "banner")
            +"content"
        }
        assertTrue("""role="banner"""" in html)
    }

    @Test
    fun `single with widthPercent sets column width`() {
        val html = singleHtml {
            widthPercent = 80
            +"content"
        }
        assertTrue("""width="80%"""" in html)
    }

    @Test
    fun `single inside container produces row without nested table`() {
        val html = email {
            head { title = "Test" }
            container {
                width = 650
                single {
                    className = "inner-section"
                    +"content"
                }
            }
        }
        assertTrue("""class="inner-section"""" in html)
        assertTrue("content" in html)
    }

    @Test
    fun `single inside container alongside regular rows`() {
        val html = email {
            head { title = "Test" }
            container {
                width = 650
                row { column { +"first" } }
                single { +"second" }
                row { column { +"third" } }
            }
        }
        val firstPos = html.indexOf("first")
        val secondPos = html.indexOf("second")
        val thirdPos = html.indexOf("third")
        assertTrue(firstPos < secondPos)
        assertTrue(secondPos < thirdPos)
    }

    @Test
    fun `multiple singles at email level`() {
        val html = email {
            head { title = "Test" }
            single {
                width = 650
                p { +"section one" }
            }
            single {
                width = 650
                p { +"section two" }
            }
        }
        assertTrue("section one" in html)
        assertTrue("section two" in html)
    }

    @Test
    fun `single is equivalent to container-row-column`() {
        val singleVersion = email {
            head { title = "Test" }
            single {
                width = 650
                className = "my-class"
                style { padding = "20px" }
                p { +"content" }
            }
        }
        val expandedVersion = email {
            head { title = "Test" }
            container {
                width = 650
                row {
                    column {
                        className = "my-class"
                        style { padding = "20px" }
                        p { +"content" }
                    }
                }
            }
        }
        assertEquals(singleVersion, expandedVersion)
    }

    @Test
    fun `single with MSO ghost tables`() {
        val html = singleHtml {
            width = 650
            +"content"
        }
        assertTrue("<!--[if mso]>" in html)
        assertTrue("<![endif]-->" in html)
    }

    @Test
    fun `single with nested elements and mixed content`() {
        val html = singleHtml {
            className = "custom"
            style { fontFamily = "'Inter', sans-serif" }
            h2 { +"Heading" }
            spacer(16)
            p {
                +"Some text with "
                strong { +"bold" }
                +" and "
                a(href = "https://example.com") { +"a link" }
            }
        }
        assertTrue("<h2>Heading</h2>" in html)
        assertTrue("<strong>bold</strong>" in html)
        assertTrue("""href="https://example.com"""" in html)
        assertTrue("font-family" in html)
    }
}
