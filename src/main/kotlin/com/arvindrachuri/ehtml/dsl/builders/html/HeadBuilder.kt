package com.arvindrachuri.ehtml.dsl.builders.html

import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.css.CssStyleBuilder
import com.arvindrachuri.ehtml.dsl.builders.result.HeadBuildResult

@EmailDsl
class HeadBuilder {
    var title: String = ""

    private var styles: List<CssNode> = emptyList()

    fun style(block: CssStyleBuilder.() -> Unit) {
        styles = CssStyleBuilder().apply(block).build()
    }

    fun build(): HeadBuildResult = HeadBuildResult(title = title, styles = styles)
}
