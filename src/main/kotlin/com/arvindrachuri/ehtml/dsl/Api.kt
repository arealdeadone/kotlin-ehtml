package com.arvindrachuri.ehtml.dsl

import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.compiler.DocumentShellPass
import com.arvindrachuri.ehtml.compiler.HtmlEmitter
import com.arvindrachuri.ehtml.compiler.LayoutLoweringPass
import com.arvindrachuri.ehtml.compiler.MsoConditionalPass
import com.arvindrachuri.ehtml.dsl.builders.html.EmailBuilder

fun email(block: EmailBuilder.() -> Unit): String {
    return HtmlEmitter.emit(emailDocument(block))
}

fun emailDocument(block: EmailBuilder.() -> Unit): EmailDocumentNode {
    val builder = EmailBuilder().apply(block)
    val result = builder.build()
    return DocumentShellPass.run(
        body = result.children.map(LayoutLoweringPass::run).flatMap(MsoConditionalPass::run),
        title = result.title,
        lang = builder.lang,
        headStyles = result.styles,
        backgroundColor = builder.backgroundColor,
    )
}
