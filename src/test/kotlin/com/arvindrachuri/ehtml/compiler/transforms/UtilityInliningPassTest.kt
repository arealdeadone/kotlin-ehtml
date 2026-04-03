package com.arvindrachuri.ehtml.compiler.transforms

import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_ALIGN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UtilityInliningPassTest {

    @Test
    fun `inlines utility class into element styles`() {
        val node = ElementNode(tag = "div", attributes = mapOf("class" to "d-block"))
        val inlineStyles = mapOf("d-block" to mapOf(DISPLAY to "block"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("block", result.styles[DISPLAY])
    }

    @Test
    fun `removes inlined class from class attribute`() {
        val node = ElementNode(tag = "div", attributes = mapOf("class" to "d-block"))
        val inlineStyles = mapOf("d-block" to mapOf(DISPLAY to "block"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertNull(result.attributes["class"])
    }

    @Test
    fun `removes class attribute entirely when all classes inlined`() {
        val node = ElementNode(tag = "div", attributes = mapOf("class" to "d-block p-4"))
        val inlineStyles =
            mapOf("d-block" to mapOf(DISPLAY to "block"), "p-4" to mapOf(PADDING to "16px"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertTrue("class" !in result.attributes)
    }

    @Test
    fun `keeps non-utility classes in class attribute`() {
        val node =
            ElementNode(tag = "div", attributes = mapOf("class" to "d-block sm-d-none custom"))
        val inlineStyles = mapOf("d-block" to mapOf(DISPLAY to "block"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("sm-d-none custom", result.attributes["class"])
    }

    @Test
    fun `user inline styles win over utility styles`() {
        val node =
            ElementNode(
                tag = "div",
                attributes = mapOf("class" to "p-4"),
                styles = mapOf(PADDING to "30px"),
            )
        val inlineStyles = mapOf("p-4" to mapOf(PADDING to "16px"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("30px", result.styles[PADDING])
    }

    @Test
    fun `utility styles merge with non-conflicting user styles`() {
        val node =
            ElementNode(
                tag = "div",
                attributes = mapOf("class" to "d-block"),
                styles = mapOf(PADDING to "10px"),
            )
        val inlineStyles = mapOf("d-block" to mapOf(DISPLAY to "block"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("block", result.styles[DISPLAY])
        assertEquals("10px", result.styles[PADDING])
    }

    @Test
    fun `multiple utility classes merge correctly`() {
        val node =
            ElementNode(tag = "div", attributes = mapOf("class" to "d-block p-4 text-center"))
        val inlineStyles =
            mapOf(
                "d-block" to mapOf(DISPLAY to "block"),
                "p-4" to mapOf(PADDING to "16px"),
                "text-center" to mapOf(TEXT_ALIGN to "center"),
            )
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("block", result.styles[DISPLAY])
        assertEquals("16px", result.styles[PADDING])
        assertEquals("center", result.styles[TEXT_ALIGN])
    }

    @Test
    fun `first utility class wins when multiple set same property`() {
        val node = ElementNode(tag = "div", attributes = mapOf("class" to "p-4 p-8"))
        val inlineStyles =
            mapOf("p-4" to mapOf(PADDING to "16px"), "p-8" to mapOf(PADDING to "32px"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("16px", result.styles[PADDING])
    }

    @Test
    fun `recurses into child elements`() {
        val node =
            ElementNode(
                tag = "div",
                children =
                    listOf(ElementNode(tag = "p", attributes = mapOf("class" to "text-center"))),
            )
        val inlineStyles = mapOf("text-center" to mapOf(TEXT_ALIGN to "center"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        val child = result.children[0] as ElementNode
        assertEquals("center", child.styles[TEXT_ALIGN])
    }

    @Test
    fun `recurses into deeply nested elements`() {
        val node =
            ElementNode(
                tag = "table",
                children =
                    listOf(
                        ElementNode(
                            tag = "tr",
                            children =
                                listOf(
                                    ElementNode(
                                        tag = "td",
                                        attributes = mapOf("class" to "p-4"),
                                        children =
                                            listOf(
                                                ElementNode(
                                                    tag = "p",
                                                    attributes = mapOf("class" to "text-center"),
                                                )
                                            ),
                                    )
                                ),
                        )
                    ),
            )
        val inlineStyles =
            mapOf("p-4" to mapOf(PADDING to "16px"), "text-center" to mapOf(TEXT_ALIGN to "center"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        val td = (result.children[0] as ElementNode).children[0] as ElementNode
        assertEquals("16px", td.styles[PADDING])
        val p = td.children[0] as ElementNode
        assertEquals("center", p.styles[TEXT_ALIGN])
    }

    @Test
    fun `preserves other attributes besides class`() {
        val node =
            ElementNode(
                tag = "a",
                attributes =
                    mapOf("class" to "d-block", "href" to "https://example.com", "id" to "link"),
            )
        val inlineStyles = mapOf("d-block" to mapOf(DISPLAY to "block"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("https://example.com", result.attributes["href"])
        assertEquals("link", result.attributes["id"])
        assertTrue("class" !in result.attributes)
    }

    @Test
    fun `passes through TextNode unchanged`() {
        val node = TextNode("hello")
        val result = UtilityInliningPass.run(node, mapOf("d-block" to mapOf(DISPLAY to "block")))
        assertEquals(node, result)
    }

    @Test
    fun `handles element with no class attribute`() {
        val node = ElementNode(tag = "div", styles = mapOf(PADDING to "10px"))
        val inlineStyles = mapOf("p-4" to mapOf(PADDING to "16px"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals("10px", result.styles[PADDING])
        assertTrue("class" !in result.attributes)
    }

    @Test
    fun `handles empty inline styles map`() {
        val node = ElementNode(tag = "div", attributes = mapOf("class" to "custom-class"))
        val result = UtilityInliningPass.run(node, emptyMap()) as ElementNode
        assertEquals("custom-class", result.attributes["class"])
    }

    @Test
    fun `preserves children order`() {
        val node =
            ElementNode(
                tag = "div",
                attributes = mapOf("class" to "d-block"),
                children = listOf(TextNode("first"), TextNode("second"), TextNode("third")),
            )
        val inlineStyles = mapOf("d-block" to mapOf(DISPLAY to "block"))
        val result = UtilityInliningPass.run(node, inlineStyles) as ElementNode
        assertEquals(3, result.children.size)
        assertEquals("first", (result.children[0] as TextNode).value)
        assertEquals("third", (result.children[2] as TextNode).value)
    }
}
