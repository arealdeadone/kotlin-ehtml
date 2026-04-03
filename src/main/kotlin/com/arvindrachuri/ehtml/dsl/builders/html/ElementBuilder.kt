package com.arvindrachuri.ehtml.dsl.builders.html

import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.css.StyleBuilder

@EmailDsl
class ElementBuilder(
    private val tag: String,
    private val requiredAttributes: Map<String, String> = emptyMap(),
) : HtmlTagBuilder {

    private val children = mutableListOf<EmailNode>()
    private var styles = emptyMap<String, String>()
    private var attributes = mutableMapOf<String, String>()

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

    fun style(block: StyleBuilder.() -> Unit) {
        val result = StyleBuilder().apply(block).build()
        styles = result.styles
    }

    fun applyDefaultStyles(defaults: Map<String, String>) {
        styles = defaults + styles
    }

    fun build(): ElementNode =
        ElementNode(
            tag = tag,
            attributes =
                buildMap {
                    putAll(attributes)
                    className?.let { put("class", it) }
                    id?.let { put("id", it) }
                    putAll(requiredAttributes)
                },
            styles = styles,
            children = children,
        )
}
