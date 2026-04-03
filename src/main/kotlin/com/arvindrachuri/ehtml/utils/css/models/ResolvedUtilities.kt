package com.arvindrachuri.ehtml.utils.css.models

import com.arvindrachuri.ehtml.ast.CssNode

data class ResolvedUtilities(
    val inlineStyles: Map<String, Map<String, String>>,
    val headStyles: List<CssNode>,
)
