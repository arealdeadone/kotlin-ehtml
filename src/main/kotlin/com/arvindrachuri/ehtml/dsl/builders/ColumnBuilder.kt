package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl

@EmailDsl
class ColumnBuilder {
    var widthPercent: Int? = null

    private val children = mutableListOf<EmailNode>()
    private var styles = emptyMap<String, String>()

    operator fun String.unaryPlus() {
        children.add(TextNode(this))
    }

    fun rawHtml(value: String) {
        children.add(RawHtmlNode(value))
    }

    fun style(block: StyleBuilder.() -> Unit) {
        val result = StyleBuilder().apply(block).build()
        styles = result.styles
    }

    fun build(): ColumnNode =
        ColumnNode(widthPercent = widthPercent, children = children, styles = styles)
}
