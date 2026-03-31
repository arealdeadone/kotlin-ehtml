package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.*
import com.arvindrachuri.ehtml.utils.Constants
import com.arvindrachuri.ehtml.utils.HtmlHeaderTag.BODY
import com.arvindrachuri.ehtml.utils.HtmlHeaderTag.HEAD
import com.arvindrachuri.ehtml.utils.HtmlHeaderTag.HTML
import com.arvindrachuri.ehtml.utils.HtmlHeaderTag.META
import com.arvindrachuri.ehtml.utils.css.CssAttribute.BACKGROUND_COLOR
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MARGIN
import com.arvindrachuri.ehtml.utils.css.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.CssAttribute.WORD_SPACING
import org.owasp.encoder.Encode

object HtmlEmitter {
    fun emit(node: EmailNode): String = buildString { appendNode(node) }

    private fun StringBuilder.appendNode(node: EmailNode) {
        when (node) {
            is EmailDocumentNode -> appendDocument(node)
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

    private fun StringBuilder.appendDocument(node: EmailDocumentNode) {
        append("<!DOCTYPE $HTML>")
        append(
            """<$HTML lang="${escapeAttribute(node.lang)}" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">"""
        )
        append("<$HEAD>")
        append("""<$META charset="utf-8" />""")
        append("""<$META name="viewport" content="width=device-width, initial-scale=1" />""")
        append("""<$META http-equiv="X-UA-Compatible" content="IE=edge" />""")
        append(
            """<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->"""
        )
        if (node.title.isNotEmpty()) {
            append("""<title>${escapeTextContent(node.title)}</title>""")
        }
        append("</$HEAD>")
        append(
            """<$BODY style="$MARGIN:0; $PADDING:0; $WORD_SPACING:normal; $BACKGROUND_COLOR:${escapeAttribute(node.backgroundColor)}">"""
        )
        node.children.forEach { child -> appendNode(child) }
        append("""</$BODY></$HTML>""")
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

        if (node.children.isEmpty() && node.tag in Constants.VOID_TAGS) {
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
