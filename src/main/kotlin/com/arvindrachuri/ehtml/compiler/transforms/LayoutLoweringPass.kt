package com.arvindrachuri.ehtml.compiler.transforms

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.utils.Constants
import com.arvindrachuri.ehtml.utils.HtmlContainerTag
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute
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
                buildMap {
                    putAll(
                        mapOf(
                            HtmlTagAttributes.Table.Align.value to "center",
                            HtmlTagAttributes.Table.Border.value to "0",
                            HtmlTagAttributes.Table.Cellpadding.value to "0",
                            HtmlTagAttributes.Table.Cellspacing.value to "0",
                            Constants.MSO_PASS_MARKER to Constants.MSO_PASS_MARKED_CONTAINER,
                            HtmlTagAttributes.Table.Role.value to "presentation",
                            HtmlTagAttributes.Table.Width.value to node.width.toString(),
                        )
                    )
                    putAll(node.attributes)
                },
            styles =
                mapOf(CssAttribute.MARGIN to "0 auto", CssAttribute.WIDTH to "${node.width}px") +
                    node.styles,
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
                                    CssAttribute.PADDING to "0",
                                    CssAttribute.VERTICAL_ALIGN to VerticalAlignType.Top.value,
                                ),
                            children = listOf(run(child)),
                        )
                }
            }
        return table(
            attributes =
                buildMap {
                    putAll(
                        mapOf(
                            HtmlTagAttributes.Table.Border.value to "0",
                            HtmlTagAttributes.Table.Cellpadding.value to "0",
                            HtmlTagAttributes.Table.Cellspacing.value to "0",
                            HtmlTagAttributes.Table.Role.value to "presentation",
                            HtmlTagAttributes.Table.Width.value to "100%",
                        )
                    )
                    putAll(node.attributes)
                },
            styles = mapOf(CssAttribute.WIDTH to "100%") + node.styles,
            children = listOf(tbody(children = listOf(tr(children = loweredCells)))),
        )
    }

    private fun lowerColumn(node: ColumnNode): EmailNode =
        td(
            attributes =
                buildMap {
                    putAll(node.attributes)
                    node.widthPercent?.let { put("width", "$it%") }
                },
            styles =
                buildMap {
                    put(CssAttribute.PADDING, "0")
                    put(CssAttribute.VERTICAL_ALIGN, VerticalAlignType.Top.value)
                    node.widthPercent?.let { put(CssAttribute.WIDTH, "$it%") }
                    putAll(node.styles)
                },
            children = node.children.map(::run),
        )

    private fun table(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) =
        ElementNode(
            tag = HtmlContainerTag.TABLE,
            attributes = attributes,
            styles = styles,
            children = children,
        )

    private fun tbody(children: List<EmailNode> = emptyList()) =
        ElementNode(tag = HtmlContainerTag.TBODY, children = children)

    private fun tr(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) =
        ElementNode(
            tag = HtmlContainerTag.TR,
            attributes = attributes,
            styles = styles,
            children = children,
        )

    private fun td(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) =
        ElementNode(
            tag = HtmlContainerTag.TD,
            attributes = attributes,
            styles = styles,
            children = children,
        )
}
