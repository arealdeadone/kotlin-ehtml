package com.arvindrachuri.ehtml.utils

import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

class TagUtilsTest {

    @Test
    fun `extracts tag name from simple opening tag`() {
        assertEquals("div", TagUtils.extractHtmlTagName("<div>"))
    }

    @Test
    fun `extracts tag name from tag with attributes`() {
        assertEquals("div", TagUtils.extractHtmlTagName("""<div class="foo">hello</div>"""))
    }

    @Test
    fun `extracts tag name from self-closing tag`() {
        assertEquals("br", TagUtils.extractHtmlTagName("<br />"))
    }

    @Test
    fun `extracts tag name from closing tag`() {
        assertEquals("span", TagUtils.extractHtmlTagName("</span>"))
    }

    @Test
    fun `extracts tag name with spaces after bracket`() {
        assertEquals("div", TagUtils.extractHtmlTagName("< div>"))
    }

    @Test
    fun `extracts first tag name from multiple tags`() {
        assertEquals("div", TagUtils.extractHtmlTagName("<div><span>text</span></div>"))
    }

    @Test
    fun `extracts tag name with hyphen`() {
        assertEquals("custom-tag", TagUtils.extractHtmlTagName("<custom-tag>hello</custom-tag>"))
    }

    @Test
    fun `extracts tag name with numbers`() {
        assertEquals("h1", TagUtils.extractHtmlTagName("<h1>heading</h1>"))
    }

    @Test
    fun `returns null for plain text`() {
        assertNull(TagUtils.extractHtmlTagName("no tags here"))
    }

    @Test
    fun `returns null for empty string`() {
        assertNull(TagUtils.extractHtmlTagName(""))
    }

    @Test
    fun `extracts tag from img with attributes`() {
        assertEquals("img", TagUtils.extractHtmlTagName("""<img src="photo.jpg" alt="photo" />"""))
    }

    @Test
    fun `extracts tag from closing tag with spaces`() {
        assertEquals("div", TagUtils.extractHtmlTagName("< / div>"))
    }
}
