package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode

object LayoutLoweringPass {
    fun run(node: EmailNode): EmailNode =
        when (node) {
            is ContainerNode -> lowerContainer(node)
            is RowNode -> lowerRow(node)
            is ColumnNode -> lowerColumn(node)
            is ElementNode -> node.copy(children = node.children.map(::run))
            is TextNode -> node
            is RawHtmlNode -> node
        }

    private fun lowerContainer(node: ContainerNode): EmailNode {
        val loweredChildren = node.children.map(::run)

        return table(
            attributes =
                mapOf(
                    "align" to "center",
                    "border" to "0",
                    "cellpadding" to "0",
                    "cellspacing" to "0",
                    "role" to "presentation",
                    "width" to node.width.toString(),
                ),
            styles = mapOf("margin" to "0 auto", "width" to "${node.width}px"),
            children = listOf(tr(children = listOf(td(children = loweredChildren)))),
        )
    }

    private fun lowerRow(node: RowNode): EmailNode {
        val loweredCells =
            node.children.map { child ->
                when (child) {
                    is ColumnNode -> lowerColumn(child)
                    else ->
                        td(
                            styles = mapOf("padding" to "0", "vertical-align" to "top"),
                            children = listOf(run(child)),
                        )
                }
            }
        return table(
            attributes =
                mapOf(
                    "border" to "0",
                    "cellpadding" to "0",
                    "cellspacing" to "0",
                    "role" to "presentation",
                    "width" to "100%",
                ),
            styles = mapOf("width" to "100%"),
            children = listOf(tr(children = loweredCells)),
        )
    }

    private fun lowerColumn(node: ColumnNode): EmailNode =
        td(
            attributes = buildMap { node.widthPercent?.let { put("width", "$it%") } },
            styles =
                buildMap {
                    put("padding", "0")
                    put("vertical-align", "top")
                    node.widthPercent?.let { put("width", "$it%") }
                },
            children = node.children.map(::run),
        )

    private fun table(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) = ElementNode(tag = "table", attributes = attributes, styles = styles, children = children)

    private fun tr(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) = ElementNode(tag = "tr", attributes = attributes, styles = styles, children = children)

    private fun td(
        attributes: Map<String, String> = emptyMap(),
        styles: Map<String, String> = emptyMap(),
        children: List<EmailNode> = emptyList(),
    ) = ElementNode(tag = "td", attributes = attributes, styles = styles, children = children)
}
