package com.arvindrachuri.ehtml.dsl.builders.html

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.css.StyleBuilder
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes

@EmailDsl
class SingleBuilder : HtmlTagBuilder {
    var width: Int = 600
    var className: String? = null
    var id: String? = null
    var widthPercent: Int? = null

    private val children = mutableListOf<EmailNode>()
    private val attributes = mutableMapOf<String, String>()

    private var styles = emptyMap<String, String>()

    override fun addChild(node: EmailNode) {
        children.add(node)
    }

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

    fun build(): ContainerNode {
        val columnAttributes = buildMap {
            putAll(attributes)
            className?.let { put(HtmlTagAttributes.CLASS, it) }
            id?.let { put(HtmlTagAttributes.ID, it) }
        }
        val column =
            ColumnNode(
                widthPercent = widthPercent,
                attributes = columnAttributes,
                styles = styles.toMap(),
                children = children.toList(),
            )
        val row = RowNode(children = listOf(column))
        return ContainerNode(width = width, children = listOf(row))
    }
}
