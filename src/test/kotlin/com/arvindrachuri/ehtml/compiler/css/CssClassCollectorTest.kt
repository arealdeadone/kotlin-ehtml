package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CssClassCollectorTest {

    @Test
    fun `collects single class from element`() {
        val nodes =
            listOf(ElementNode(tag = "div", attributes = mapOf(HtmlTagAttributes.CLASS to "w-100")))
        assertEquals(setOf("w-100"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `collects multiple classes from single element`() {
        val nodes =
            listOf(
                ElementNode(
                    tag = "div",
                    attributes = mapOf(HtmlTagAttributes.CLASS to "w-100 d-block p-4"),
                )
            )
        assertEquals(setOf("w-100", "d-block", "p-4"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `collects classes from multiple sibling elements`() {
        val nodes =
            listOf(
                ElementNode(tag = "div", attributes = mapOf(HtmlTagAttributes.CLASS to "w-100")),
                ElementNode(tag = "p", attributes = mapOf(HtmlTagAttributes.CLASS to "text-center")),
            )
        assertEquals(setOf("w-100", "text-center"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `collects classes from nested elements`() {
        val nodes =
            listOf(
                ElementNode(
                    tag = "div",
                    attributes = mapOf(HtmlTagAttributes.CLASS to "w-100"),
                    children =
                        listOf(
                            ElementNode(
                                tag = "p",
                                attributes = mapOf(HtmlTagAttributes.CLASS to "text-center"),
                            )
                        ),
                )
            )
        assertEquals(setOf("w-100", "text-center"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `collects classes from deeply nested elements`() {
        val nodes =
            listOf(
                ElementNode(
                    tag = "table",
                    attributes = mapOf(HtmlTagAttributes.CLASS to "outer"),
                    children =
                        listOf(
                            ElementNode(
                                tag = "tr",
                                children =
                                    listOf(
                                        ElementNode(
                                            tag = "td",
                                            attributes = mapOf(HtmlTagAttributes.CLASS to "inner"),
                                            children =
                                                listOf(
                                                    ElementNode(
                                                        tag = "p",
                                                        attributes =
                                                            mapOf(HtmlTagAttributes.CLASS to "deep"),
                                                    )
                                                ),
                                        )
                                    ),
                            )
                        ),
                )
            )
        assertEquals(setOf("outer", "inner", "deep"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `deduplicates classes across elements`() {
        val nodes =
            listOf(
                ElementNode(tag = "div", attributes = mapOf(HtmlTagAttributes.CLASS to "w-100")),
                ElementNode(tag = "p", attributes = mapOf(HtmlTagAttributes.CLASS to "w-100")),
            )
        assertEquals(setOf("w-100"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `ignores elements without class attribute`() {
        val nodes =
            listOf(
                ElementNode(tag = "div", attributes = mapOf(HtmlTagAttributes.ID to "hero")),
                ElementNode(tag = "p", attributes = mapOf(HtmlTagAttributes.CLASS to "text-center")),
            )
        assertEquals(setOf("text-center"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `ignores blank class names from extra spaces`() {
        val nodes =
            listOf(
                ElementNode(
                    tag = "div",
                    attributes = mapOf(HtmlTagAttributes.CLASS to "  w-100   d-block  "),
                )
            )
        assertEquals(setOf("w-100", "d-block"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `returns empty set for no elements`() {
        assertTrue(CssClassCollector.collect(emptyList()).isEmpty())
    }

    @Test
    fun `returns empty set for elements without classes`() {
        val nodes = listOf(ElementNode(tag = "div"), TextNode("hello"), RawHtmlNode("<hr/>"))
        assertTrue(CssClassCollector.collect(nodes).isEmpty())
    }

    @Test
    fun `skips TextNode and RawHtmlNode`() {
        val nodes =
            listOf(
                TextNode("hello"),
                RawHtmlNode("<hr/>"),
                ElementNode(tag = "div", attributes = mapOf(HtmlTagAttributes.CLASS to "found")),
            )
        assertEquals(setOf("found"), CssClassCollector.collect(nodes))
    }

    @Test
    fun `handles empty class attribute`() {
        val nodes =
            listOf(ElementNode(tag = "div", attributes = mapOf(HtmlTagAttributes.CLASS to "")))
        assertTrue(CssClassCollector.collect(nodes).isEmpty())
    }
}
