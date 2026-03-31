package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.*
import com.arvindrachuri.ehtml.utils.Constants
import com.arvindrachuri.ehtml.utils.HtmlContainerTag.TABLE
import com.arvindrachuri.ehtml.utils.HtmlContainerTag.TBODY
import com.arvindrachuri.ehtml.utils.HtmlContainerTag.TD
import com.arvindrachuri.ehtml.utils.HtmlContainerTag.TR
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes.Table
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MARGIN
import com.arvindrachuri.ehtml.utils.css.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.CssAttribute.VERTICAL_ALIGN
import com.arvindrachuri.ehtml.utils.css.CssAttribute.WIDTH
import com.arvindrachuri.ehtml.utils.css.values.VerticalAlignType

object LayoutLoweringPass {
    fun run(node: EmailNode): EmailNode =
        when (node) {
            is ContainerNode -> lowerContainer(node)
            is RowNode -> lowerRow(node)
            is ColumnNode -> lowerColumn(node)
            is ElementNode -> node.copy(children = node.children.map(::run))
            is EmailDocumentNode -> node.copy(children = node.children.map(::run))
            is TextNode -> node
            is RawHtmlNode -> node
        }

    private fun lowerContainer(node: ContainerNode): EmailNode {
        val loweredChildren = node.children.map(::run)

        return table(
            attributes =
                mapOf(
                    Table.Align.value to "center",
                    Table.Border.value to "0",
                    Table.Cellpadding.value to "0",
                    Table.Cellspacing.value to "0",
                    Constants.MSO_PASS_MARKER to Constants.MSO_PASS_MARKED_CONTAINER,
                    Table.Role.value to "presentation",
                    Table.Width.value to node.width.toString(),
                ),
            styles = mapOf(MARGIN to "0 auto", WIDTH to "${node.width}px") + node.styles,
            children =
                listOf(
                    tbody(children = listOf(tr(children = listOf(td(children = loweredChildren)))))
                ),
        )
    }

    private fun lowerRow(node: RowNode): EmailNode {
        val loweredCells =
            node.children.map { child ->
                when (child) {
                    is ColumnNode -> lowerColumn(child)
                    else ->
                        td(
                            styles =
                                mapOf(
                                    PADDING to "0",
                                    VERTICAL_ALIGN to VerticalAlignType.Top.value,
                                ),
                            children = listOf(run(child)),
                        )
                }
            }
        return table(
            attributes =
                mapOf(
                    Table.Border.value to "0",
                    Table.Cellpadding.value to "0",
                    Table.Cellspacing.value to "0",
                    Table.Role.value to "presentation",
                    Table.Width.value to "100%",
                ),
            styles = mapOf(WIDTH to "100%") + node.styles,
            children = listOf(tbody(children = listOf(tr(children = loweredCells)))),
        )
    }

    private fun lowerColumn(node: ColumnNode): EmailNode =
        td(
            attributes = buildMap { node.widthPercent?.let { put("width", "$it%") } },
            styles =
                buildMap {
                    put(PADDING, "0")
                    put(VERTICAL_ALIGN, VerticalAlignType.Top.value)
                    node.widthPercent?.let { put(WIDTH, "$it%") }
                    putAll(node.styles)
                },
            children = node.children.map(::run),
        )

    private fun table(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) = ElementNode(tag = TABLE, attributes = attributes, styles = styles, children = children)

    private fun tbody(children: List<EmailNode> = emptyList()) =
        ElementNode(tag = TBODY, children = children)

    private fun tr(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) = ElementNode(tag = TR, attributes = attributes, styles = styles, children = children)

    private fun td(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) = ElementNode(tag = TD, attributes = attributes, styles = styles, children = children)
}
