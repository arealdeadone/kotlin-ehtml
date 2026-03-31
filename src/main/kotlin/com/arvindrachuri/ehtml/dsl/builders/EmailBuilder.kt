package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RawHtmlNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.utils.Colors
import com.arvindrachuri.ehtml.utils.TagUtils
import com.arvindrachuri.ehtml.utils.css.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.css.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.CssAttribute.HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.LINE_HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MAX_HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MAX_WIDTH
import com.arvindrachuri.ehtml.utils.css.CssAttribute.OPACITY
import com.arvindrachuri.ehtml.utils.css.CssAttribute.OVERFLOW
import com.arvindrachuri.ehtml.utils.css.values.DisplayType
import com.arvindrachuri.ehtml.utils.css.values.OverflowType

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

    fun preheader(text: String) {
        children.add(
            ElementNode(
                tag = "div",
                styles =
                    mapOf(
                        DISPLAY to DisplayType.None.value,
                        FONT_SIZE to "1px",
                        LINE_HEIGHT to "1px",
                        MAX_HEIGHT to "0px",
                        MAX_WIDTH to "0px",
                        OPACITY to "0",
                        OVERFLOW to OverflowType.Hidden.value,
                        "mso-hide" to "all",
                    ),
                children = listOf(TextNode(text)),
            )
        )
    }

    fun build(): List<EmailNode> = children
}
