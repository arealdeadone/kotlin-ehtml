package com.arvindrachuri.ehtml.dsl.builders.html

import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.utils.HtmlElementTag.A
import com.arvindrachuri.ehtml.utils.HtmlElementTag.B
import com.arvindrachuri.ehtml.utils.HtmlElementTag.BLOCKQUOTE
import com.arvindrachuri.ehtml.utils.HtmlElementTag.BR
import com.arvindrachuri.ehtml.utils.HtmlElementTag.DIV
import com.arvindrachuri.ehtml.utils.HtmlElementTag.EM
import com.arvindrachuri.ehtml.utils.HtmlElementTag.H1
import com.arvindrachuri.ehtml.utils.HtmlElementTag.H2
import com.arvindrachuri.ehtml.utils.HtmlElementTag.H3
import com.arvindrachuri.ehtml.utils.HtmlElementTag.H4
import com.arvindrachuri.ehtml.utils.HtmlElementTag.H5
import com.arvindrachuri.ehtml.utils.HtmlElementTag.H6
import com.arvindrachuri.ehtml.utils.HtmlElementTag.HR
import com.arvindrachuri.ehtml.utils.HtmlElementTag.I
import com.arvindrachuri.ehtml.utils.HtmlElementTag.IMG
import com.arvindrachuri.ehtml.utils.HtmlElementTag.LI
import com.arvindrachuri.ehtml.utils.HtmlElementTag.OL
import com.arvindrachuri.ehtml.utils.HtmlElementTag.P
import com.arvindrachuri.ehtml.utils.HtmlElementTag.PRE
import com.arvindrachuri.ehtml.utils.HtmlElementTag.S
import com.arvindrachuri.ehtml.utils.HtmlElementTag.SPAN
import com.arvindrachuri.ehtml.utils.HtmlElementTag.STRONG
import com.arvindrachuri.ehtml.utils.HtmlElementTag.SUB
import com.arvindrachuri.ehtml.utils.HtmlElementTag.SUP
import com.arvindrachuri.ehtml.utils.HtmlElementTag.U
import com.arvindrachuri.ehtml.utils.HtmlElementTag.UL
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.BORDER
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.HEIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.LINE_HEIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_ALIGN
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_DECORATION
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.WIDTH
import com.arvindrachuri.ehtml.utils.css.values.DisplayType
import com.arvindrachuri.ehtml.utils.css.values.TextAlignType
import com.arvindrachuri.ehtml.utils.css.values.TextDecorationType

interface HtmlTagBuilder {
    fun addChild(node: EmailNode)

    fun div(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(DIV).apply(block).build())
    }

    fun h1(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(H1).apply(block).build())
    }

    fun h2(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(H2).apply(block).build())
    }

    fun h3(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(H3).apply(block).build())
    }

    fun h4(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(H4).apply(block).build())
    }

    fun h5(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(H5).apply(block).build())
    }

    fun h6(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(H6).apply(block).build())
    }

    fun p(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(P).apply(block).build())
    }

    fun span(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(SPAN).apply(block).build())
    }

    fun strong(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(STRONG).apply(block).build())
    }

    fun b(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(B).apply(block).build())
    }

    fun em(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(EM).apply(block).build())
    }

    fun a(href: String, block: ElementBuilder.() -> Unit) {
        addChild(
            ElementBuilder(A, mapOf(HtmlTagAttributes.A.Href.value to href)).apply(block).build()
        )
    }

    fun button(text: String, href: String, id: String, block: ElementBuilder.() -> Unit) {
        val builder = ElementBuilder(A, mapOf(HtmlTagAttributes.A.Href.value to href)).apply(block)
        builder.attr(HtmlTagAttributes.A.Id.value, id)
        builder.applyDefaultStyles(
            mapOf(
                DISPLAY to DisplayType.Block.value,
                TEXT_ALIGN to TextAlignType.Center.value,
                TEXT_DECORATION to TextDecorationType.None.value,
            )
        )
        builder.addChild(TextNode(text))
        addChild(builder.build())
    }

    fun img(
        src: String,
        alt: String,
        height: Int? = null,
        width: Int? = null,
        block: ElementBuilder.() -> Unit = {},
    ) {
        val builder =
            ElementBuilder(
                    IMG,
                    mapOf(
                        HtmlTagAttributes.Img.Src.value to src,
                        HtmlTagAttributes.Img.Alt.value to alt,
                    ),
                )
                .apply(block)
        builder.applyDefaultStyles(mapOf(DISPLAY to DisplayType.Block.value, BORDER to "0"))
        height?.let {
            builder.attr(HtmlTagAttributes.Img.Height.value, it.toString())
            builder.applyDefaultStyles(mapOf(HEIGHT to "${it}px"))
        }
        width?.let {
            builder.attr(HtmlTagAttributes.Img.Width.value, it.toString())
            builder.applyDefaultStyles(mapOf(WIDTH to "${it}px"))
        }
        addChild(builder.build())
    }

    fun hr(block: ElementBuilder.() -> Unit = {}) {
        addChild(ElementBuilder(HR).apply(block).build())
    }

    fun br(block: ElementBuilder.() -> Unit = {}) {
        addChild(ElementBuilder(BR).apply(block).build())
    }

    fun ul(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(UL).apply(block).build())
    }

    fun ol(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(OL).apply(block).build())
    }

    fun li(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(LI).apply(block).build())
    }

    fun blockquote(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(BLOCKQUOTE).apply(block).build())
    }

    fun i(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(I).apply(block).build())
    }

    fun u(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(U).apply(block).build())
    }

    fun s(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(S).apply(block).build())
    }

    fun sup(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(SUP).apply(block).build())
    }

    fun sub(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(SUB).apply(block).build())
    }

    fun pre(block: ElementBuilder.() -> Unit) {
        addChild(ElementBuilder(PRE).apply(block).build())
    }

    fun spacer(height: Int) {
        addChild(
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
}
