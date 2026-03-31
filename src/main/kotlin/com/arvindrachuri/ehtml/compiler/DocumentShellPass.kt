package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.utils.Colors

object DocumentShellPass {
    fun run(
        body: List<EmailNode>,
        title: String = "",
        lang: String = "en",
        backgroundColor: String = Colors.WHITE.value,
        headStyles: List<CssNode> = emptyList(),
    ): EmailDocumentNode =
        EmailDocumentNode(
            title = title,
            lang = lang,
            backgroundColor = backgroundColor,
            children = body,
            headStyles = headStyles,
        )
}
