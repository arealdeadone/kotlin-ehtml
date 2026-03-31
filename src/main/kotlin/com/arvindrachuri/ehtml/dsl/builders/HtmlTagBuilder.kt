package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.utils.CssAttribute.BORDER
import com.arvindrachuri.ehtml.utils.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.DisplayType
import com.arvindrachuri.ehtml.utils.HtmlElementTag.A
import com.arvindrachuri.ehtml.utils.HtmlElementTag.B
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
import com.arvindrachuri.ehtml.utils.HtmlElementTag.IMG
import com.arvindrachuri.ehtml.utils.HtmlElementTag.P
import com.arvindrachuri.ehtml.utils.HtmlElementTag.SPAN
import com.arvindrachuri.ehtml.utils.HtmlElementTag.STRONG
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes

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
            ElementBuilder(A, mapOf(HtmlTagAttributes.A.HREF.value to href)).apply(block).build()
        )
    }

    fun img(src: String, alt: String, block: ElementBuilder.() -> Unit = {}) {
        val builder =
            ElementBuilder(
                    IMG,
                    mapOf(
                        HtmlTagAttributes.Img.SRC.value to src,
                        HtmlTagAttributes.Img.ALT.value to alt,
                    ),
                )
                .apply(block)
        builder.applyDefaultStyles(mapOf(DISPLAY to DisplayType.Block.value, BORDER to "0"))
        addChild(builder.build())
    }

    fun hr(block: ElementBuilder.() -> Unit = {}) {
        addChild(ElementBuilder(HR).apply(block).build())
    }

    fun br(block: ElementBuilder.() -> Unit = {}) {
        addChild(ElementBuilder(BR).apply(block).build())
    }
}
