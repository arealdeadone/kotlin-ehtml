package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import org.owasp.encoder.Encode

object HtmlEmitter {
    private val voidTags = setOf("br", "hr", "img", "input", "link", "meta")

    fun emit(node: EmailNode): String = buildString { appendNode(node) }

    private fun StringBuilder.appendNode(node: EmailNode) {
        when (node) {
            is ElementNode -> appendElement(node)
            is TextNode -> append(escapeTextContent(node.value))
            is RawHtmlNode -> append(node.value)
            is ContainerNode,
            is RowNode,
            is ColumnNode ->
                error(
                    "Layout nodes must be lowered before HTML emission: ${node::class.simpleName}"
                )
        }
    }

    private fun StringBuilder.appendElement(node: ElementNode) {
        append('<').append(node.tag)

        val mergedAttributes = buildMap {
            putAll(node.attributes)

            if (node.styles.isNotEmpty()) {
                put("style", serializeStyles(node.styles))
            }
        }

        mergedAttributes.toSortedMap().forEach { (key, value) ->
            append(' ')
            append(key)
            append("=\"")
            append(escapeAttribute(value))
            append('"')
        }

        if (node.children.isEmpty() && node.tag in voidTags) {
            append(" />")
            return
        }
        append('>')
        node.children.forEach { child -> appendNode(child) }
        append("</").append(node.tag).append('>')
    }

    private fun serializeStyles(styles: Map<String, String>): String =
        styles.toSortedMap().entries.joinToString(";") { (key, value) -> "$key:$value" }

    private fun escapeTextContent(value: String): String = Encode.forHtmlContent(value)

    private fun escapeAttribute(value: String): String = Encode.forHtmlAttribute(value)
}
