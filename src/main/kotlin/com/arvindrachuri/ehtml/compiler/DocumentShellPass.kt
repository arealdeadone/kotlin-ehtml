package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.utils.Colors

object DocumentShellPass {
    fun run(
        body: EmailNode,
        title: String = "",
        lang: String = "en",
        backgroundColor: String = Colors.WHITE.value,
    ): EmailDocumentNode =
        EmailDocumentNode(
            title = title,
            lang = lang,
            backgroundColor = backgroundColor,
            children = listOf(body),
        )
}
