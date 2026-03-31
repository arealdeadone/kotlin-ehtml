package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.utils.Colors
import com.arvindrachuri.ehtml.utils.TagUtils

@EmailDsl
class EmailBuilder {
    var title: String = ""
    var lang: String = "en"
    var backgroundColor: String = Colors.WHITE.value

    private val children = mutableListOf<EmailNode>()
    private val warnings = mutableListOf<String>()

    operator fun String.unaryPlus() {
        children.add(TextNode(this))
    }

    fun rawHtml(value: String) {
        val openingTag = TagUtils.extractHtmlTagName(value) ?: "Unknown Element"
        warnings.add("Raw html tag $openingTag used - behavior may not be email-safe")
        children.add(RawHtmlNode(value))
    }

    fun container(block: ContainerBuilder.() -> Unit) {
        children.add(ContainerBuilder().apply(block).build())
    }

    fun build(): List<EmailNode> = children
}
