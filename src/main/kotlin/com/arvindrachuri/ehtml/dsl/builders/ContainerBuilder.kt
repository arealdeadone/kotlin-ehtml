package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl

@EmailDsl
class ContainerBuilder {
    var width: Int = 600

    private val children = mutableListOf<EmailNode>()
    private var styles = emptyMap<String, String>()

    operator fun String.unaryPlus() {
        children.add(TextNode(this))
    }

    fun style(block: StyleBuilder.() -> Unit) {
        val result = StyleBuilder().apply(block).build()
        styles = result.styles
    }

    fun rawHtml(value: String) {
        children.add(RawHtmlNode(value))
    }

    fun row(block: RowBuilder.() -> Unit) {
        children.add(RowBuilder().apply(block).build())
    }

    fun build(): ContainerNode = ContainerNode(width = width, children = children, styles = styles)

}