package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.builders.css.StyleBuilder
import com.arvindrachuri.ehtml.dsl.builders.html.ColumnBuilder
import com.arvindrachuri.ehtml.dsl.builders.html.ContainerBuilder
import com.arvindrachuri.ehtml.dsl.builders.html.EmailBuilder
import com.arvindrachuri.ehtml.dsl.builders.html.RowBuilder
import com.arvindrachuri.ehtml.utils.css.values.DirectionType
import com.arvindrachuri.ehtml.utils.css.values.DisplayType
import com.arvindrachuri.ehtml.utils.css.values.FloatType
import com.arvindrachuri.ehtml.utils.css.values.FontStyleType
import com.arvindrachuri.ehtml.utils.css.values.OverflowType
import com.arvindrachuri.ehtml.utils.css.values.TextDecorationType
import com.arvindrachuri.ehtml.utils.css.values.TextTransformType
import com.arvindrachuri.ehtml.utils.css.values.VerticalAlignType
import com.arvindrachuri.ehtml.utils.css.values.WhiteSpaceType
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class BuilderTest {

    @Test
    fun `EmailBuilder unaryPlus adds TextNode`() {
        val builder = EmailBuilder().apply { +"hello" }
        val result = builder.build()
        assertEquals(1, result.children.size)
        assertEquals(TextNode("hello"), result.children[0])
    }

    @Test
    fun `EmailBuilder rawHtml adds RawHtmlNode`() {
        val builder = EmailBuilder().apply { rawHtml("<br>") }
        val result = builder.build()
        assertEquals(1, result.children.size)
        assertEquals(RawHtmlNode("<br>"), result.children[0])
    }

    @Test
    fun `EmailBuilder container adds ContainerNode`() {
        val builder = EmailBuilder().apply { container { +"content" } }
        val result = builder.build()
        assertEquals(1, result.children.size)
        assertTrue(result.children[0] is ContainerNode)
    }

    @Test
    fun `EmailBuilder preserves children order`() {
        val builder =
            EmailBuilder().apply {
                +"first"
                rawHtml("<hr>")
                container { +"third" }
            }
        val children = builder.build().children
        assertEquals(3, children.size)
        assertTrue(children[0] is TextNode)
        assertTrue(children[1] is RawHtmlNode)
        assertTrue(children[2] is ContainerNode)
    }

    @Test
    fun `EmailBuilder default properties`() {
        val builder = EmailBuilder()
        assertEquals("en", builder.lang)
    }

    @Test
    fun `ContainerBuilder default width is 600`() {
        val builder = ContainerBuilder().apply { +"content" }
        val node = builder.build()
        assertEquals(600, node.width)
    }

    @Test
    fun `ContainerBuilder custom width`() {
        val builder =
            ContainerBuilder().apply {
                width = 800
                +"content"
            }
        val node = builder.build()
        assertEquals(800, node.width)
    }

    @Test
    fun `ContainerBuilder style block sets styles`() {
        val builder =
            ContainerBuilder().apply {
                style { padding = "16px" }
                +"content"
            }
        val node = builder.build()
        assertEquals("16px", node.styles["padding"])
    }

    @Test
    fun `ContainerBuilder row adds RowNode child`() {
        val builder = ContainerBuilder().apply { row { +"row content" } }
        val node = builder.build()
        assertEquals(1, node.children.size)
        assertTrue(node.children[0] is RowNode)
    }

    @Test
    fun `RowBuilder column adds ColumnNode child`() {
        val builder = RowBuilder().apply { column { +"col content" } }
        val node = builder.build()
        assertEquals(1, node.children.size)
        assertTrue(node.children[0] is ColumnNode)
    }

    @Test
    fun `RowBuilder style block sets styles`() {
        val builder = RowBuilder().apply { style { backgroundColor = "#eeeeee" } }
        val node = builder.build()
        assertEquals("#eeeeee", node.styles["background-color"])
    }

    @Test
    fun `RowBuilder unaryPlus adds TextNode`() {
        val builder = RowBuilder().apply { +"text" }
        val node = builder.build()
        assertEquals(1, node.children.size)
        assertEquals(TextNode("text"), node.children[0])
    }

    @Test
    fun `RowBuilder rawHtml adds RawHtmlNode`() {
        val builder = RowBuilder().apply { rawHtml("<br>") }
        val node = builder.build()
        assertEquals(RawHtmlNode("<br>"), node.children[0])
    }

    @Test
    fun `ColumnBuilder default widthPercent is null`() {
        val builder = ColumnBuilder().apply { +"content" }
        val node = builder.build()
        assertEquals(null, node.widthPercent)
    }

    @Test
    fun `ColumnBuilder custom widthPercent`() {
        val builder =
            ColumnBuilder().apply {
                widthPercent = 50
                +"content"
            }
        val node = builder.build()
        assertEquals(50, node.widthPercent)
    }

    @Test
    fun `ColumnBuilder style block sets styles`() {
        val builder = ColumnBuilder().apply { style { color = "#333333" } }
        val node = builder.build()
        assertEquals("#333333", node.styles["color"])
    }

    @Test
    fun `ColumnBuilder unaryPlus adds TextNode`() {
        val builder = ColumnBuilder().apply { +"text" }
        val node = builder.build()
        assertEquals(TextNode("text"), node.children[0])
    }

    @Test
    fun `ColumnBuilder rawHtml adds RawHtmlNode`() {
        val builder = ColumnBuilder().apply { rawHtml("<img>") }
        val node = builder.build()
        assertEquals(RawHtmlNode("<img>"), node.children[0])
    }

    @Test
    fun `StyleBuilder maps all typed properties`() {
        val result =
            StyleBuilder()
                .apply {
                    padding = "10px"
                    margin = "0"
                    fontSize = "16px"
                    fontWeight = "bold"
                    lineHeight = "1.5"
                    color = "#000"
                    backgroundColor = "#fff"
                    textAlign = "center"
                    width = "100%"
                    border = "1px solid #ccc"
                }
                .build()

        assertEquals("10px", result.styles["padding"])
        assertEquals("0", result.styles["margin"])
        assertEquals("16px", result.styles["font-size"])
        assertEquals("bold", result.styles["font-weight"])
        assertEquals("1.5", result.styles["line-height"])
        assertEquals("#000", result.styles["color"])
        assertEquals("#fff", result.styles["background-color"])
        assertEquals("center", result.styles["text-align"])
        assertEquals("100%", result.styles["width"])
        assertEquals("1px solid #ccc", result.styles["border"])
    }

    @Test
    fun `StyleBuilder unset properties are not in map`() {
        val result = StyleBuilder().apply { padding = "10px" }.build()
        assertEquals(1, result.styles.size)
        assertTrue("margin" !in result.styles)
    }

    @Test
    fun `StyleBuilder css escape hatch adds to styles`() {
        val result = StyleBuilder().apply { css("text-decoration", "underline") }.build()
        assertEquals("underline", result.styles["text-decoration"])
    }

    @Test
    fun `StyleBuilder css escape hatch collects warnings`() {
        val result =
            StyleBuilder()
                .apply {
                    css("text-decoration", "underline")
                    css("letter-spacing", "1px")
                }
                .build()
        assertEquals(2, result.warnings.size)
        assertTrue(result.warnings[0].contains("text-decoration"))
        assertTrue(result.warnings[1].contains("letter-spacing"))
    }

    @Test
    fun `StyleBuilder css and typed properties coexist`() {
        val result =
            StyleBuilder()
                .apply {
                    padding = "10px"
                    css("text-decoration", "underline")
                }
                .build()
        assertEquals("10px", result.styles["padding"])
        assertEquals("underline", result.styles["text-decoration"])
    }

    @Test
    fun `StyleBuilder enum-typed display property`() {
        val result = StyleBuilder().apply { display = DisplayType.Block }.build()
        assertEquals("block", result.styles["display"])
    }

    @Test
    fun `StyleBuilder enum-typed textDecoration property`() {
        val result = StyleBuilder().apply { textDecoration = TextDecorationType.None }.build()
        assertEquals("none", result.styles["text-decoration"])
    }

    @Test
    fun `StyleBuilder enum-typed textTransform property`() {
        val result = StyleBuilder().apply { textTransform = TextTransformType.UpperCase }.build()
        assertEquals("uppercase", result.styles["text-transform"])
    }

    @Test
    fun `StyleBuilder enum-typed overflow property`() {
        val result = StyleBuilder().apply { overflow = OverflowType.Hidden }.build()
        assertEquals("hidden", result.styles["overflow"])
    }

    @Test
    fun `StyleBuilder enum-typed verticalAlign property`() {
        val result = StyleBuilder().apply { verticalAlign = VerticalAlignType.Top }.build()
        assertEquals("top", result.styles["vertical-align"])
    }

    @Test
    fun `StyleBuilder enum-typed direction property`() {
        val result = StyleBuilder().apply { direction = DirectionType.Ltr }.build()
        assertEquals("ltr", result.styles["direction"])
    }

    @Test
    fun `StyleBuilder enum-typed float property`() {
        val result = StyleBuilder().apply { float = FloatType.None }.build()
        assertEquals("none", result.styles["float"])
    }

    @Test
    fun `StyleBuilder enum-typed whiteSpace property`() {
        val result = StyleBuilder().apply { whiteSpace = WhiteSpaceType.Nowrap }.build()
        assertEquals("nowrap", result.styles["white-space"])
    }

    @Test
    fun `StyleBuilder enum-typed fontStyle property`() {
        val result = StyleBuilder().apply { fontStyle = FontStyleType.Italic }.build()
        assertEquals("italic", result.styles["font-style"])
    }

    @Test
    fun `StyleBuilder new string properties map correctly`() {
        val result =
            StyleBuilder()
                .apply {
                    fontFamily = "'Comfortaa', Helvetica, sans-serif"
                    borderRadius = "12px"
                    height = "288px"
                    maxHeight = "288px"
                    maxWidth = "0px"
                    minWidth = "160px"
                    minHeight = "50px"
                    opacity = "0"
                    letterSpacing = "1px"
                }
                .build()
        assertEquals("'Comfortaa', Helvetica, sans-serif", result.styles["font-family"])
        assertEquals("12px", result.styles["border-radius"])
        assertEquals("288px", result.styles["height"])
        assertEquals("288px", result.styles["max-height"])
        assertEquals("0px", result.styles["max-width"])
        assertEquals("160px", result.styles["min-width"])
        assertEquals("50px", result.styles["min-height"])
        assertEquals("0", result.styles["opacity"])
        assertEquals("1px", result.styles["letter-spacing"])
    }

    @Test
    fun `StyleBuilder overflowX and overflowY map correctly`() {
        val result =
            StyleBuilder()
                .apply {
                    overflowX = OverflowType.Hidden
                    overflowY = OverflowType.Auto
                }
                .build()
        assertEquals("hidden", result.styles["overflow-x"])
        assertEquals("auto", result.styles["overflow-y"])
    }

    @Test
    fun `nested builder composition produces correct IR`() {
        val builder =
            EmailBuilder().apply {
                container {
                    width = 600
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
        val children = builder.build().children
        val container = children[0] as ContainerNode
        assertEquals(600, container.width)

        val row = container.children[0] as RowNode
        assertEquals(2, row.children.size)

        val left = row.children[0] as ColumnNode
        assertEquals(50, left.widthPercent)
        assertEquals(TextNode("Left"), left.children[0])

        val right = row.children[1] as ColumnNode
        assertEquals(50, right.widthPercent)
        assertEquals(TextNode("Right"), right.children[0])
    }
}
