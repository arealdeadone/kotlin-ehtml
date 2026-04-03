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
import kotlin.collections.minus

object MsoConditionalPass {
    fun run(node: EmailNode): List<EmailNode> =
        when (node) {
            is ElementNode -> processElement(node)
            is EmailDocumentNode -> listOf(node.copy(children = node.children.flatMap(::run)))
            is TextNode,
            is RawHtmlNode,
            is RowNode,
            is ColumnNode,
            is ContainerNode -> listOf(node)
        }

    private fun processElement(node: ElementNode): List<EmailNode> {
        val processed = node.copy(children = node.children.flatMap(::run))

        if (
            processed.attributes[Constants.MSO_PASS_MARKER] != Constants.MSO_PASS_MARKED_CONTAINER
        ) {
            return listOf(processed)
        }

        val width = processed.attributes["width"] ?: "600"
        val stripped = processed.copy(attributes = processed.attributes - Constants.MSO_PASS_MARKER)

        return listOf(
            RawHtmlNode(
                """<!--[if mso]><table role="presentation" cellpadding="0" cellspacing="0" border="0" width="$width" align="center"><tr><td><![endif]-->"""
            ),
            stripped,
            RawHtmlNode("""<!--[if mso]></td></tr></table><![endif]-->"""),
        )
    }
}
