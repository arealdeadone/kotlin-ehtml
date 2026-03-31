package com.arvindrachuri.ehtml.dsl.builders.result

import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.ast.EmailNode

data class EmailBuildResult(
    val title: String,
    val styles: List<CssNode>,
    val children: List<EmailNode>,
)
