package com.arvindrachuri.ehtml.ast

sealed interface CssNode

data class CssRule(val selector: String, val styles: Map<String, String>) : CssNode

data class CssMediaQuery(val condition: String, val rules: List<CssNode>) : CssNode
