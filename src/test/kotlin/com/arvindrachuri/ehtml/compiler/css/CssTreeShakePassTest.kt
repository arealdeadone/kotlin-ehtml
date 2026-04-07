package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.TextNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CssTreeShakePassTest {

    private fun document(
        classes: List<String> = emptyList(),
        headStyles: List<com.arvindrachuri.ehtml.ast.CssNode> = emptyList(),
    ): EmailDocumentNode {
        val children = classes.map { cls ->
            ElementNode(
                tag = "div",
                attributes = mapOf("class" to cls),
                children = listOf(TextNode("content")),
            )
        }
        return EmailDocumentNode(title = "Test", headStyles = headStyles, children = children)
    }

    @Test
    fun `keeps used class selector`() {
        val doc =
            document(
                classes = listOf("btn"),
                headStyles = listOf(CssRule(".btn", mapOf("padding" to "10px"))),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `removes unused class selector`() {
        val doc =
            document(
                classes = listOf("btn"),
                headStyles =
                    listOf(
                        CssRule(".btn", mapOf("padding" to "10px")),
                        CssRule(".unused", mapOf("color" to "red")),
                    ),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
        assertEquals(".btn", (result.headStyles[0] as CssRule).selector)
    }

    @Test
    fun `keeps tag selector`() {
        val doc = document(headStyles = listOf(CssRule("table", mapOf("border" to "0"))))
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `keeps multi-tag selector`() {
        val doc = document(headStyles = listOf(CssRule("h1, h2, h3, p", mapOf("margin" to "0"))))
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `keeps id selector`() {
        val doc = document(headStyles = listOf(CssRule("#outlook", mapOf("padding" to "0"))))
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `keeps compound selector`() {
        val doc = document(headStyles = listOf(CssRule(".ExternalClass p", mapOf("margin" to "0"))))
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `keeps rule if any sub-selector matches used class`() {
        val doc =
            document(
                classes = listOf("active"),
                headStyles = listOf(CssRule(".unused, .active", mapOf("color" to "blue"))),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `keeps rule if compound sub-selector present in multi-selector`() {
        val doc =
            document(
                headStyles = listOf(CssRule(".ExternalClass p, .unused", mapOf("margin" to "0")))
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `removes multi-class selector when none are used`() {
        val doc =
            document(
                classes = listOf("btn"),
                headStyles = listOf(CssRule(".foo, .bar", mapOf("color" to "red"))),
            )
        val result = CssTreeShakePass.run(doc)
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `prunes unused rules inside media query`() {
        val doc =
            document(
                classes = listOf("btn"),
                headStyles =
                    listOf(
                        CssMediaQuery(
                            "max-width: 600px",
                            listOf(
                                CssRule(".btn", mapOf("width" to "100%")),
                                CssRule(".unused", mapOf("display" to "none")),
                            ),
                        )
                    ),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
        val media = result.headStyles[0] as CssMediaQuery
        assertEquals(1, media.rules.size)
        assertEquals(".btn", (media.rules[0] as CssRule).selector)
    }

    @Test
    fun `removes media query entirely when all rules pruned`() {
        val doc =
            document(
                classes = listOf("btn"),
                headStyles =
                    listOf(
                        CssMediaQuery(
                            "max-width: 600px",
                            listOf(
                                CssRule(".unused-a", mapOf("width" to "100%")),
                                CssRule(".unused-b", mapOf("display" to "none")),
                            ),
                        )
                    ),
            )
        val result = CssTreeShakePass.run(doc)
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `keeps tag selectors inside media query`() {
        val doc =
            document(
                headStyles =
                    listOf(
                        CssMediaQuery(
                            "max-width: 600px",
                            listOf(CssRule("body", mapOf("width" to "100%"))),
                        )
                    )
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
    }

    @Test
    fun `prunes unused rules inside mso conditional`() {
        val doc =
            document(
                classes = listOf("mso-btn"),
                headStyles =
                    listOf(
                        CssMsoConditional(
                            listOf(
                                CssRule(".mso-btn", mapOf("padding" to "10px")),
                                CssRule(".mso-unused", mapOf("width" to "600px")),
                            )
                        )
                    ),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
        val mso = result.headStyles[0] as CssMsoConditional
        assertEquals(1, mso.rules.size)
    }

    @Test
    fun `removes mso conditional entirely when all rules pruned`() {
        val doc =
            document(
                headStyles =
                    listOf(
                        CssMsoConditional(listOf(CssRule(".mso-unused", mapOf("width" to "600px"))))
                    )
            )
        val result = CssTreeShakePass.run(doc)
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `handles nested media query inside mso conditional`() {
        val doc =
            document(
                classes = listOf("btn"),
                headStyles =
                    listOf(
                        CssMsoConditional(
                            listOf(
                                CssMediaQuery(
                                    "max-width: 600px",
                                    listOf(
                                        CssRule(".btn", mapOf("width" to "100%")),
                                        CssRule(".unused", mapOf("display" to "none")),
                                    ),
                                )
                            )
                        )
                    ),
            )
        val result = CssTreeShakePass.run(doc)
        val mso = result.headStyles[0] as CssMsoConditional
        val media = mso.rules[0] as CssMediaQuery
        assertEquals(1, media.rules.size)
    }

    @Test
    fun `collects classes from nested elements`() {
        val doc =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(
                        CssRule(".outer", mapOf("padding" to "10px")),
                        CssRule(".inner", mapOf("color" to "red")),
                        CssRule(".unused", mapOf("margin" to "0")),
                    ),
                children =
                    listOf(
                        ElementNode(
                            tag = "div",
                            attributes = mapOf("class" to "outer"),
                            children =
                                listOf(
                                    ElementNode(tag = "p", attributes = mapOf("class" to "inner"))
                                ),
                        )
                    ),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(2, result.headStyles.size)
    }

    @Test
    fun `preserves ExternalClass and ReadMsgBody selectors`() {
        val doc =
            document(
                headStyles =
                    listOf(
                        CssRule(".ExternalClass, .ReadMsgBody", mapOf("width" to "100%")),
                        CssRule(".ExternalClass p", mapOf("margin" to "0")),
                    )
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(2, result.headStyles.size)
    }

    @Test
    fun `empty headStyles returns empty`() {
        val doc = document(classes = listOf("btn"), headStyles = emptyList())
        val result = CssTreeShakePass.run(doc)
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `no elements with classes removes all class selectors`() {
        val doc =
            EmailDocumentNode(
                title = "Test",
                headStyles =
                    listOf(
                        CssRule(".a", mapOf("color" to "red")),
                        CssRule(".b", mapOf("color" to "blue")),
                        CssRule("body", mapOf("margin" to "0")),
                    ),
                children = listOf(TextNode("no classes here")),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(1, result.headStyles.size)
        assertEquals("body", (result.headStyles[0] as CssRule).selector)
    }

    @Test
    fun `multiple classes on single element keeps all`() {
        val doc =
            document(
                classes = listOf("a b c"),
                headStyles =
                    listOf(
                        CssRule(".a", mapOf("color" to "red")),
                        CssRule(".b", mapOf("color" to "blue")),
                        CssRule(".c", mapOf("color" to "green")),
                        CssRule(".d", mapOf("color" to "yellow")),
                    ),
            )
        val result = CssTreeShakePass.run(doc)
        assertEquals(3, result.headStyles.size)
    }
}
