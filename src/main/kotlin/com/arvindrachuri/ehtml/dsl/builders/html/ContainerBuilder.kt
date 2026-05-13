package com.arvindrachuri.ehtml.dsl.builders.html

import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.css.StyleBuilder
import com.arvindrachuri.ehtml.utils.HtmlElementTag.DIV
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes
import com.arvindrachuri.ehtml.utils.TagUtils
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.HEIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.LINE_HEIGHT

@EmailDsl
class ContainerBuilder {
    var width: Int = 600

    private val children = mutableListOf<EmailNode>()
    private var styles = emptyMap<String, String>()
    private val warnings = mutableListOf<String>()
    private val attributes = mutableMapOf<String, String>()

    var className: String? = null
    var id: String? = null

    operator fun String.unaryPlus() {
        children.add(TextNode(this))
    }

    fun attr(name: String, value: String) {
        attributes[name] = value
    }

    fun attrs(vararg pairs: Pair<String, String>) {
        attributes.putAll(pairs)
    }

    fun style(block: StyleBuilder.() -> Unit) {
        val result = StyleBuilder().apply(block).build()
        styles = result.styles
    }

    fun rawHtml(value: String) {
        val openingTag = TagUtils.extractHtmlTagName(value) ?: "Unknown Element"
        warnings.add("Raw html tag $openingTag used - behavior may not be email-safe")
        children.add(RawHtmlNode(value))
    }

    fun row(block: RowBuilder.() -> Unit) {
        children.add(RowBuilder().apply(block).build())
    }

    fun spacer(height: Int) {
        children.add(
            ElementNode(
                tag = DIV,
                styles =
                    mapOf(
                        HEIGHT to "${height}px",
                        FONT_SIZE to "${height}px",
                        LINE_HEIGHT to "${height}px",
                    ),
            )
        )
    }

    fun single(block: ColumnBuilder.() -> Unit) {
        val columnBuilder = ColumnBuilder().apply(block)
        val column = columnBuilder.build()
        val row = RowNode(children = listOf(column))
        children.add(row)
    }

    fun build(): ContainerNode =
        ContainerNode(
            width = width,
            attributes =
                buildMap {
                    putAll(attributes)
                    className?.let { put(HtmlTagAttributes.CLASS, it) }
                    id?.let { put(HtmlTagAttributes.ID, it) }
                },
            children = children,
            styles = styles,
        )
}
