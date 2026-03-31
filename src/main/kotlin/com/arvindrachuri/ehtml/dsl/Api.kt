package com.arvindrachuri.ehtml.dsl

import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.compiler.DocumentShellPass
import com.arvindrachuri.ehtml.compiler.HtmlEmitter
import com.arvindrachuri.ehtml.compiler.LayoutLoweringPass
import com.arvindrachuri.ehtml.compiler.MsoConditionalPass
import com.arvindrachuri.ehtml.dsl.builders.EmailBuilder

fun email(block: EmailBuilder.() -> Unit): String {
    return HtmlEmitter.emit(emailDocument(block))
}

fun emailDocument(block: EmailBuilder.() -> Unit): EmailDocumentNode {
    val builder = EmailBuilder().apply(block)
    return DocumentShellPass.run(
        body = builder.build().map(LayoutLoweringPass::run).flatMap(MsoConditionalPass::run),
        title = builder.title,
        lang = builder.lang,
        backgroundColor = builder.backgroundColor,
    )
}
