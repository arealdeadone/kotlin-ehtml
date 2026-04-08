package com.arvindrachuri.ehtml.dsl

import com.arvindrachuri.ehtml.utils.Colors
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ApiTest {

    @Test
    fun `email produces valid HTML document string`() {
        val html = email {
            head { title = "Test" }
            container { +"Hello" }
        }
        assertTrue(html.startsWith("<!DOCTYPE html>"))
        assertTrue("<html" in html)
        assertTrue("</html>" in html)
        assertTrue("<title>Test</title>" in html)
        assertTrue("Hello" in html)
    }

    @Test
    fun `email output contains MSO conditional comments around container`() {
        val html = email {
            head { title = "MSO Test" }
            container { +"Content" }
        }
        assertTrue("<!--[if mso]>" in html)
        assertTrue("<![endif]-->" in html)
    }

    @Test
    fun `emailDocument returns correct title`() {
        val doc = emailDocument {
            head { title = "Test" }
            container { +"Hello" }
        }
        assertEquals("Test", doc.title)
    }

    @Test
    fun `emailDocument uses default lang`() {
        val doc = emailDocument { head { title = "Test" } }
        assertEquals("en", doc.lang)
    }

    @Test
    fun `emailDocument uses custom lang`() {
        val doc = emailDocument {
            head { title = "Test" }
            lang = "fr"
        }
        assertEquals("fr", doc.lang)
    }

    @Test
    fun `emailDocument uses default background color`() {
        val doc = emailDocument { head { title = "Test" } }
        assertEquals(Colors.WHITE.value, doc.backgroundColor)
    }

    @Test
    fun `emailDocument uses custom background color`() {
        val doc = emailDocument {
            head { title = "Test" }
            backgroundColor = "#000000"
        }
        assertEquals("#000000", doc.backgroundColor)
    }

    @Test
    fun `email with empty title omits title tag`() {
        val html = email {
            head { title = "" }
            container { +"Hello" }
        }
        assertTrue("<title>" !in html)
    }

    @Test
    fun `email renders nested container row column structure`() {
        val html = email {
            head { title = "Full" }
            container {
                row {
                    column {
                        widthPercent = 50
                        +"Left"
                    }
                    column {
                        widthPercent = 50
                        +"Right"
                    }
                }
            }
        }
        assertTrue("Left" in html)
        assertTrue("Right" in html)
        assertTrue("width=\"50%\"" in html)
    }

    @Test
    fun `email renders container styles`() {
        val html = email {
            head { title = "Styled" }
            container {
                style { backgroundColor = "#f0f0f0" }
                +"Styled content"
            }
        }
        assertTrue("background-color: #f0f0f0" in html)
    }

    @Test
    fun `email renders rawHtml at top level`() {
        val html = email {
            head { title = "Raw" }
            rawHtml("<custom-tag>hello</custom-tag>")
        }
        assertTrue("<custom-tag>hello</custom-tag>" in html)
    }

    @Test
    fun `email renders text at top level`() {
        val html = email {
            head { title = "Text" }
            +"Top level text"
        }
        assertTrue("Top level text" in html)
    }

    @Test
    fun `email with multiple containers wraps each in MSO conditionals`() {
        val html = email {
            head { title = "Multi" }
            container { +"First" }
            container { +"Second" }
        }
        val msoCount = "<!--\\[if mso\\]>".toRegex().findAll(html).count()
        assertTrue(msoCount >= 4)
    }

    @Test
    fun `preheader renders hidden div with preview text`() {
        val html = email {
            head { title = "Preview" }
            preheader("Check out our deals")
        }
        assert("Check out our deals" in html)
        assert("display: none" in html)
        assert("mso-hide: all" in html)
    }

    @Test
    fun `preheader has zero-visibility styles`() {
        val html = email {
            head { title = "Preview" }
            preheader("Preview text")
        }
        assert("font-size: 1px" in html)
        assert("line-height: 1px" in html)
        assert("max-height: 0" in html)
        assert("max-width: 0" in html)
        assert("opacity: 0" in html)
        assert("overflow: hidden" in html)
    }

    @Test
    fun `preheader appears before container content`() {
        val html = email {
            head { title = "Order" }
            preheader("Preview text")
            container { +"Body content" }
        }
        val preheaderPos = html.indexOf("Preview text")
        val bodyPos = html.indexOf("Body content")
        assert(preheaderPos < bodyPos)
    }

    @Test
    fun `spacer renders at email level`() {
        val html = email {
            head { title = "Spaced" }
            container { +"First" }
            spacer(30)
            container { +"Second" }
        }
        assert("height: 30px" in html)
        assert("font-size: 30px" in html)
        assert("line-height: 30px" in html)
    }
}
