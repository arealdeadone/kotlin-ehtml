package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.utils.TagUtils
import com.arvindrachuri.ehtml.utils.css.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.CssAttribute.HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.LINE_HEIGHT

@EmailDsl
class ContainerBuilder {
    var width: Int = 600

    private val children = mutableListOf<EmailNode>()
    private var styles = emptyMap<String, String>()
    private val warnings = mutableListOf<String>()

    operator fun String.unaryPlus() {
        children.add(TextNode(this))
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
                tag = "div",
                styles =
                    mapOf(
                        HEIGHT to "${height}px",
                        FONT_SIZE to "${height}px",
                        LINE_HEIGHT to "${height}px",
                    ),
            )
        )
    }

    fun build(): ContainerNode = ContainerNode(width = width, children = children, styles = styles)
}
