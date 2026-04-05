package com.arvindrachuri.ehtml.dsl.builders.html

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.css.StyleBuilder
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes
import com.arvindrachuri.ehtml.utils.TagUtils

@EmailDsl
class ColumnBuilder : HtmlTagBuilder {
    var widthPercent: Int? = null

    private val children = mutableListOf<EmailNode>()
    private var styles = emptyMap<String, String>()
    private val warnings = mutableListOf<String>()
    private val attributes = mutableMapOf<String, String>()

    var className: String? = null
    var id: String? = null

    override fun addChild(node: EmailNode) {
        children.add(node)
    }

    fun attr(name: String, value: String) {
        attributes[name] = value
    }

    fun attrs(vararg pairs: Pair<String, String>) {
        attributes.putAll(pairs)
    }

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

    fun build(): ColumnNode =
        ColumnNode(
            widthPercent = widthPercent,
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
