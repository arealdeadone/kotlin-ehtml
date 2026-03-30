package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.result.StyleBuildResult

@EmailDsl
class StyleBuilder {
    var padding: String? = null
    var margin: String? = null
    var fontSize: String? = null
    var fontWeight: String? = null
    var lineHeight: String? = null
    var color: String? = null
    var backgroundColor: String? = null
    var textAlign: String? = null
    var width: String? = null
    var border: String? = null

    private val custom = mutableMapOf<String, String>()
    private val warnings = mutableListOf<String>()

    fun css(property: String, value: String) {
        custom[property] = value
        warnings.add("Unmodeled CSS property: `$property` used - behavior may not be email-safe")
    }

    fun build(): StyleBuildResult {
        val styles = buildMap {
            padding?.let { put("padding", it) }
            margin?.let { put("margin", it) }
            fontSize?.let { put("font-size", it) }
            fontWeight?.let { put("font-weight", it) }
            lineHeight?.let { put("line-height", it) }
            color?.let { put("color", it) }
            backgroundColor?.let { put("background-color", it) }
            textAlign?.let { put("text-align", it) }
            width?.let { put("width", it) }
            border?.let { put("border", it) }
            putAll(custom)
        }
        return StyleBuildResult(styles, warnings)
    }
}
