package com.arvindrachuri.ehtml.dsl.builders.html

import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.css.StyleBuilder
import com.arvindrachuri.ehtml.utils.TagUtils

@EmailDsl
class RowBuilder {
    private val children = mutableListOf<EmailNode>()
    private var styles = emptyMap<String, String>()
    private val warnings = mutableListOf<String>()

    operator fun String.unaryPlus() {
        children.add(TextNode(this))
    }

    fun rawHtml(value: String) {
        val openingTag = TagUtils.extractHtmlTagName(value) ?: "Unknown Element"
        warnings.add("Raw html tag $openingTag used - behavior may not be email-safe")
        children.add(RawHtmlNode(value))
    }

    fun style(block: StyleBuilder.() -> Unit) {
        val result = StyleBuilder().apply(block).build()
        styles = result.styles
    }

    fun column(block: ColumnBuilder.() -> Unit) {
        children.add(ColumnBuilder().apply(block).build())
    }

    fun build(): RowNode = RowNode(styles = styles, children = children)
}
