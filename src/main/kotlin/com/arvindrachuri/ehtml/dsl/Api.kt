package com.arvindrachuri.ehtml.dsl

import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.compiler.HtmlEmitter
import com.arvindrachuri.ehtml.compiler.css.CssClassCollector
import com.arvindrachuri.ehtml.compiler.css.CssInliningPass
import com.arvindrachuri.ehtml.compiler.css.CssOptimizationPass
import com.arvindrachuri.ehtml.compiler.css.CssTreeShakePass
import com.arvindrachuri.ehtml.compiler.css.UtilityClassResolver
import com.arvindrachuri.ehtml.compiler.css.emailUtilityRules
import com.arvindrachuri.ehtml.compiler.css.themeUtilityRules
import com.arvindrachuri.ehtml.compiler.transforms.DocumentShellPass
import com.arvindrachuri.ehtml.compiler.transforms.LayoutLoweringPass
import com.arvindrachuri.ehtml.compiler.transforms.MsoConditionalPass
import com.arvindrachuri.ehtml.compiler.transforms.UtilityInliningPass
import com.arvindrachuri.ehtml.dsl.builders.html.EmailBuilder

fun email(block: EmailBuilder.() -> Unit): String {
    return HtmlEmitter.emit(emailDocument(block))
}

fun emailDocument(block: EmailBuilder.() -> Unit): EmailDocumentNode {
    val builder = EmailBuilder().apply(block)
    val rules = emailUtilityRules() + (builder.theme?.let { themeUtilityRules(it) } ?: emptyList())
    val resolver = UtilityClassResolver(rules)

    val result = builder.build()
    val usedClasses = CssClassCollector.collect(result.children)
    val resolved = resolver.resolve(usedClasses)
    val inlinedChildren = result.children.map { UtilityInliningPass.run(it, resolved.inlineStyles) }
    val document =
        DocumentShellPass.run(
            body = inlinedChildren.map(LayoutLoweringPass::run).flatMap(MsoConditionalPass::run),
            title = result.title,
            lang = builder.lang,
            headStyles = resolved.headStyles + result.styles,
            backgroundColor = builder.backgroundColor,
        )
    val treeShaken = CssTreeShakePass.run(document)
    val inlined = CssInliningPass.run(treeShaken)
    val treeShakeSecondPass = CssTreeShakePass.run(inlined)

    return treeShakeSecondPass.copy(
        headStyles = CssOptimizationPass.run(treeShakeSecondPass.headStyles)
    )
}
