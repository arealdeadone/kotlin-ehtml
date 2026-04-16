package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.compiler.transforms.LayoutLoweringPass
import com.arvindrachuri.ehtml.utils.Constants
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class LayoutLoweringPassTest {

    @Test
    fun `ContainerNode lowers to centered presentation table with default width`() {
        val node = ContainerNode(children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td>content</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `ContainerNode respects custom width`() {
        val node = ContainerNode(width = 800, children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 800px" width="800"><tbody><tr><td>content</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `RowNode lowers to full-width presentation table`() {
        val node = RowNode(children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr><td style="padding: 0;vertical-align: top">content</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `ColumnNode lowers to td with padding and vertical-align styles`() {
        val node = ColumnNode(children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals("""<td style="padding: 0;vertical-align: top">content</td>""", html)
    }

    @Test
    fun `ColumnNode with widthPercent sets width attribute and style`() {
        val node = ColumnNode(widthPercent = 50, children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<td style="padding: 0;vertical-align: top;width: 50%" width="50%">content</td>""",
            html,
        )
    }

    @Test
    fun `ColumnNode without widthPercent has no width attribute or style`() {
        val node = ColumnNode(widthPercent = null, children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals("""<td style="padding: 0;vertical-align: top">content</td>""", html)
    }

    @Test
    fun `non-column children inside RowNode get auto-wrapped in td`() {
        val node = RowNode(children = listOf(TextNode("text content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr><td style="padding: 0;vertical-align: top">text content</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `RowNode with multiple non-column children wraps each in td`() {
        val node = RowNode(children = listOf(TextNode("first"), TextNode("second")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr><td style="padding: 0;vertical-align: top">first</td><td style="padding: 0;vertical-align: top">second</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `RowNode with ColumnNode children does not double-wrap`() {
        val node =
            RowNode(
                children =
                    listOf(
                        ColumnNode(widthPercent = 50, children = listOf(TextNode("left"))),
                        ColumnNode(widthPercent = 50, children = listOf(TextNode("right"))),
                    )
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr><td style="padding: 0;vertical-align: top;width: 50%" width="50%">left</td><td style="padding: 0;vertical-align: top;width: 50%" width="50%">right</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `RowNode with mixed ColumnNode and non-ColumnNode children`() {
        val node =
            RowNode(
                children =
                    listOf(
                        ColumnNode(widthPercent = 50, children = listOf(TextNode("column"))),
                        TextNode("text"),
                    )
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr><td style="padding: 0;vertical-align: top;width: 50%" width="50%">column</td><td style="padding: 0;vertical-align: top">text</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `nested ContainerNode to RowNode to ColumnNode hierarchy`() {
        val node =
            ContainerNode(
                width = 600,
                children =
                    listOf(
                        RowNode(
                            children =
                                listOf(
                                    ColumnNode(
                                        widthPercent = 100,
                                        children = listOf(TextNode("nested content")),
                                    )
                                )
                        )
                    ),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td><table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr><td style="padding: 0;vertical-align: top;width: 100%" width="100%">nested content</td></tr></tbody></table></td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `TextNode passes through unchanged`() {
        val node = TextNode("plain text")
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals("plain text", html)
    }

    @Test
    fun `RawHtmlNode passes through unchanged`() {
        val node = RawHtmlNode("<br />")
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals("<br />", html)
    }

    @Test
    fun `ElementNode recurses into children`() {
        val node =
            ElementNode(
                tag = "div",
                children = listOf(ContainerNode(width = 600, children = listOf(TextNode("inner")))),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<div><table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td>inner</td></tr></tbody></table></div>""",
            html,
        )
    }

    @Test
    fun `ElementNode with multiple children recurses all`() {
        val node =
            ElementNode(
                tag = "div",
                children =
                    listOf(
                        TextNode("before"),
                        ContainerNode(width = 600, children = listOf(TextNode("container"))),
                        TextNode("after"),
                    ),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<div>before<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td>container</td></tr></tbody></table>after</div>""",
            html,
        )
    }

    @Test
    fun `EmailDocumentNode recurses into children`() {
        val node =
            EmailDocumentNode(
                title = "Test Email",
                children =
                    listOf(ContainerNode(width = 600, children = listOf(TextNode("email content")))),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td>email content</td></tr></tbody></table>""" in
                html
        )
        assert("<title>Test Email</title>" in html)
    }

    @Test
    fun `EmailDocumentNode with multiple children recurses all`() {
        val node =
            EmailDocumentNode(
                title = "Multi Content",
                children =
                    listOf(
                        ContainerNode(width = 600, children = listOf(TextNode("first"))),
                        ContainerNode(width = 600, children = listOf(TextNode("second"))),
                    ),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td>first</td></tr></tbody></table>""" in
                html
        )
        assert(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td>second</td></tr></tbody></table>""" in
                html
        )
    }

    @Test
    fun `ContainerNode with multiple children wraps all in single td`() {
        val node =
            ContainerNode(
                width = 600,
                children = listOf(TextNode("first"), TextNode("second"), TextNode("third")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td>firstsecondthird</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `ColumnNode with multiple children`() {
        val node =
            ColumnNode(widthPercent = 50, children = listOf(TextNode("first"), TextNode("second")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<td style="padding: 0;vertical-align: top;width: 50%" width="50%">firstsecond</td>""",
            html,
        )
    }

    @Test
    fun `deeply nested layout structure`() {
        val node =
            ContainerNode(
                width = 600,
                children =
                    listOf(
                        RowNode(
                            children =
                                listOf(
                                    ColumnNode(
                                        widthPercent = 50,
                                        children =
                                            listOf(
                                                RowNode(
                                                    children =
                                                        listOf(
                                                            ColumnNode(
                                                                widthPercent = 100,
                                                                children = listOf(TextNode("deep")),
                                                            )
                                                        )
                                                )
                                            ),
                                    )
                                )
                        )
                    ),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("<table" in html)
        assert("deep" in html)
        assert("role=\"presentation\"" in html)
    }

    @Test
    fun `ColumnNode with ElementNode children`() {
        val node =
            ColumnNode(
                widthPercent = 50,
                children = listOf(ElementNode(tag = "p", children = listOf(TextNode("paragraph")))),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<td style="padding: 0;vertical-align: top;width: 50%" width="50%"><p>paragraph</p></td>""",
            html,
        )
    }

    @Test
    fun `RawHtmlNode inside ContainerNode passes through`() {
        val node = ContainerNode(width = 600, children = listOf(RawHtmlNode("<br />")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td><br /></td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `RowNode with RawHtmlNode child`() {
        val node = RowNode(children = listOf(RawHtmlNode("<span>raw</span>")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr><td style="padding: 0;vertical-align: top"><span>raw</span></td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `ContainerNode with width 400`() {
        val node = ContainerNode(width = 400, children = listOf(TextNode("narrow")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 400px" width="400"><tbody><tr><td>narrow</td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `ColumnNode with widthPercent 25`() {
        val node = ColumnNode(widthPercent = 25, children = listOf(TextNode("quarter")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<td style="padding: 0;vertical-align: top;width: 25%" width="25%">quarter</td>""",
            html,
        )
    }

    @Test
    fun `ColumnNode with widthPercent 100`() {
        val node = ColumnNode(widthPercent = 100, children = listOf(TextNode("full")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<td style="padding: 0;vertical-align: top;width: 100%" width="100%">full</td>""",
            html,
        )
    }

    @Test
    fun `RowNode with three equal-width columns`() {
        val node =
            RowNode(
                children =
                    listOf(
                        ColumnNode(widthPercent = 33, children = listOf(TextNode("col1"))),
                        ColumnNode(widthPercent = 33, children = listOf(TextNode("col2"))),
                        ColumnNode(widthPercent = 34, children = listOf(TextNode("col3"))),
                    )
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("width: 33%" in html)
        assert("width: 34%" in html)
        assert("col1" in html && "col2" in html && "col3" in html)
    }

    @Test
    fun `ElementNode with attributes and styles recurses children`() {
        val node =
            ElementNode(
                tag = "section",
                attributes = mapOf(HtmlTagAttributes.ID to "main"),
                styles = mapOf("background" to "white"),
                children =
                    listOf(
                        ContainerNode(width = 600, children = listOf(TextNode("section content")))
                    ),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("id=\"main\"" in html)
        assert("background: white" in html)
        assert("section content" in html)
    }

    @Test
    fun `empty ContainerNode`() {
        val node = ContainerNode(width = 600, children = emptyList())
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table align="center" border="0" cellpadding="0" cellspacing="0" ${Constants.MSO_PASS_MARKER}="${Constants.MSO_PASS_MARKED_CONTAINER}" role="presentation" style="margin: 0 auto;width: 600px" width="600"><tbody><tr><td></td></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `empty RowNode`() {
        val node = RowNode(children = emptyList())
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width: 100%" width="100%"><tbody><tr></tr></tbody></table>""",
            html,
        )
    }

    @Test
    fun `empty ColumnNode`() {
        val node = ColumnNode(widthPercent = 50, children = emptyList())
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assertEquals(
            """<td style="padding: 0;vertical-align: top;width: 50%" width="50%"></td>""",
            html,
        )
    }

    @Test
    fun `ContainerNode merges user styles into lowered table`() {
        val node =
            ContainerNode(
                width = 600,
                styles = mapOf("background-color" to "#f5f5f5"),
                children = listOf(TextNode("styled")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("background-color: #f5f5f5" in html)
        assert("margin: 0 auto" in html)
        assert("width: 600px" in html)
    }

    @Test
    fun `ContainerNode user styles override structural styles`() {
        val node =
            ContainerNode(
                width = 600,
                styles = mapOf("margin" to "10px"),
                children = listOf(TextNode("override")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("margin: 10px" in html)
        assert("margin: 0 auto" !in html)
    }

    @Test
    fun `RowNode merges user styles into lowered table`() {
        val node =
            RowNode(
                styles = mapOf("background-color" to "#eeeeee"),
                children = listOf(TextNode("styled row")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("background-color: #eeeeee" in html)
        assert("width: 100%" in html)
    }

    @Test
    fun `RowNode user styles override structural styles`() {
        val node =
            RowNode(styles = mapOf("width" to "50%"), children = listOf(TextNode("override")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("style=\"" in html)
        val styleStart = html.indexOf("style=\"") + 7
        val styleEnd = html.indexOf("\"", styleStart)
        val styleValue = html.substring(styleStart, styleEnd)
        assertEquals(1, styleValue.split("width: ").size - 1)
        assert("width: 50%" in styleValue)
    }

    @Test
    fun `ColumnNode merges user styles into lowered td`() {
        val node =
            ColumnNode(
                widthPercent = 50,
                styles = mapOf("background-color" to "#ffffff", "font-size" to "14px"),
                children = listOf(TextNode("styled column")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("background-color: #ffffff" in html)
        assert("font-size: 14px" in html)
        assert("padding: 0" in html)
        assert("vertical-align: top" in html)
        assert("width: 50%" in html)
    }

    @Test
    fun `ColumnNode user styles override structural styles`() {
        val node =
            ColumnNode(
                widthPercent = 50,
                styles = mapOf("padding" to "16px"),
                children = listOf(TextNode("override")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("padding: 16px" in html)
        assert("padding: 0" !in html)
    }

    @Test
    fun `ContainerNode attributes land on lowered table`() {
        val node =
            ContainerNode(
                width = 600,
                attributes =
                    mapOf(HtmlTagAttributes.CLASS to "w-100", HtmlTagAttributes.ID to "hero"),
                children = listOf(TextNode("content")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("""class="w-100"""" in html)
        assert("""id="hero"""" in html)
    }

    @Test
    fun `RowNode attributes land on lowered table`() {
        val node =
            RowNode(
                attributes =
                    mapOf(HtmlTagAttributes.CLASS to "d-sm-block", HtmlTagAttributes.ID to "row-1"),
                children = listOf(TextNode("content")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("""class="d-sm-block"""" in html)
        assert("""id="row-1"""" in html)
    }

    @Test
    fun `ColumnNode attributes land on lowered td`() {
        val node =
            ColumnNode(
                widthPercent = 50,
                attributes =
                    mapOf(
                        HtmlTagAttributes.CLASS to "text-sm-center",
                        HtmlTagAttributes.ID to "col-1",
                    ),
                children = listOf(TextNode("content")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("""class="text-sm-center"""" in html)
        assert("""id="col-1"""" in html)
    }

    @Test
    fun `container className survives full pipeline to emitted HTML`() {
        val html =
            com.arvindrachuri.ehtml.dsl.email {
                head { title = "Test" }
                container {
                    className = "darkmode-bg"
                    row {
                        className = "custom-row"
                        column {
                            className = "custom-col"
                            +"content"
                        }
                    }
                }
            }
        assert("""class="darkmode-bg"""" in html)
        assert("""class="custom-row"""" in html)
        assert("""class="custom-col"""" in html)
    }

    @Test
    fun `column default padding can be overridden by user style`() {
        val node =
            ColumnNode(styles = mapOf("padding" to "24px"), children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("padding: 24px" in html)
        assert("padding: 0" !in html)
    }

    @Test
    fun `column default vertical-align can be overridden by user style`() {
        val node =
            ColumnNode(
                styles = mapOf("vertical-align" to "bottom"),
                children = listOf(TextNode("content")),
            )
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("vertical-align: bottom" in html)
        assert("vertical-align: top" !in html)
    }

    @Test
    fun `column preserves default padding when no override`() {
        val node = ColumnNode(children = listOf(TextNode("content")))
        val lowered = LayoutLoweringPass.run(node)
        val html = HtmlEmitter.emit(lowered)
        assert("padding: 0" in html)
        assert("vertical-align: top" in html)
    }

    @Test
    fun `container padding flows to inner wrapping td`() {
        val node =
            ContainerNode(
                styles = mapOf("padding" to "16px"),
                children = listOf(TextNode("content")),
            )
        val lowered = LayoutLoweringPass.run(node) as ElementNode
        val html = HtmlEmitter.emit(lowered)
        val tds = Regex("<td[^>]*>").findAll(html).toList()
        val innerTd = tds.first().value
        assert("padding: 16px" in innerTd)
    }

    @Test
    fun `container padding does not go on outer table`() {
        val node =
            ContainerNode(
                styles = mapOf("padding" to "16px", "background-color" to "#fff"),
                children = listOf(TextNode("content")),
            )
        val lowered = LayoutLoweringPass.run(node) as ElementNode
        assert("padding" !in HtmlEmitter.emit(ElementNode(tag = "x", styles = lowered.styles)))
        assert(
            "background-color" in HtmlEmitter.emit(ElementNode(tag = "x", styles = lowered.styles))
        )
    }

    @Test
    fun `column p-16 utility class overrides default padding in full pipeline`() {
        val html =
            com.arvindrachuri.ehtml.dsl.email {
                head { title = "Test" }
                container {
                    row {
                        column {
                            className = "p-16"
                            +"content"
                        }
                    }
                }
            }
        assert("padding: 16px" in html)
    }
}
