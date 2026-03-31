package com.arvindrachuri.ehtml.dsl.builders.css

import com.arvindrachuri.ehtml.dsl.EmailDsl

@EmailDsl
class CssRuleBuilder : StyleBuilder() {
    private val importantStyles = mutableMapOf<String, String>()

    fun important(block: StyleBuilder.() -> Unit) {
        val builder = StyleBuilder().apply(block)
        importantStyles.putAll(builder.build().styles)
    }

    fun String.important() = "$this !important"

    fun buildCssRule(): Map<String, String> {
        val normalStyles = build().styles
        val markedImportant = importantStyles.mapValues { (_, value) -> "$value !important" }
        return normalStyles + markedImportant
    }
}
