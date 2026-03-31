package com.arvindrachuri.ehtml.dsl

import com.arvindrachuri.ehtml.utils.Colors
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ApiTest {

    @Test
    fun `email produces valid HTML document string`() {
        val html = email {
            title = "Test"
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
            title = "MSO Test"
            container { +"Content" }
        }
        assertTrue("<!--[if mso]>" in html)
        assertTrue("<![endif]-->" in html)
    }

    @Test
    fun `emailDocument returns correct title`() {
        val doc = emailDocument {
            title = "Test"
            container { +"Hello" }
        }
        assertEquals("Test", doc.title)
    }

    @Test
    fun `emailDocument uses default lang`() {
        val doc = emailDocument { title = "Test" }
        assertEquals("en", doc.lang)
    }

    @Test
    fun `emailDocument uses custom lang`() {
        val doc = emailDocument {
            title = "Test"
            lang = "fr"
        }
        assertEquals("fr", doc.lang)
    }

    @Test
    fun `emailDocument uses default background color`() {
        val doc = emailDocument { title = "Test" }
        assertEquals(Colors.WHITE.value, doc.backgroundColor)
    }

    @Test
    fun `emailDocument uses custom background color`() {
        val doc = emailDocument {
            title = "Test"
            backgroundColor = "#000000"
        }
        assertEquals("#000000", doc.backgroundColor)
    }

    @Test
    fun `email with empty title omits title tag`() {
        val html = email {
            title = ""
            container { +"Hello" }
        }
        assertTrue("<title>" !in html)
    }

    @Test
    fun `email renders nested container row column structure`() {
        val html = email {
            title = "Full"
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
            title = "Styled"
            container {
                style { backgroundColor = "#f0f0f0" }
                +"Styled content"
            }
        }
        assertTrue("background-color:#f0f0f0" in html)
    }

    @Test
    fun `email renders rawHtml at top level`() {
        val html = email {
            title = "Raw"
            rawHtml("<custom-tag>hello</custom-tag>")
        }
        assertTrue("<custom-tag>hello</custom-tag>" in html)
    }

    @Test
    fun `email renders text at top level`() {
        val html = email {
            title = "Text"
            +"Top level text"
        }
        assertTrue("Top level text" in html)
    }

    @Test
    fun `email with multiple containers wraps each in MSO conditionals`() {
        val html = email {
            title = "Multi"
            container { +"First" }
            container { +"Second" }
        }
        val msoCount = "<!--\\[if mso\\]>".toRegex().findAll(html).count()
        assertTrue(msoCount >= 4)
    }
}
