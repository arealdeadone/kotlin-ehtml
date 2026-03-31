package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.result.StyleBuildResult
import com.arvindrachuri.ehtml.utils.CssAttribute.BACKGROUND_COLOR
import com.arvindrachuri.ehtml.utils.CssAttribute.BORDER
import com.arvindrachuri.ehtml.utils.CssAttribute.COLOR
import com.arvindrachuri.ehtml.utils.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.CssAttribute.FONT_WEIGHT
import com.arvindrachuri.ehtml.utils.CssAttribute.LINE_HEIGHT
import com.arvindrachuri.ehtml.utils.CssAttribute.MARGIN
import com.arvindrachuri.ehtml.utils.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.CssAttribute.TEXT_ALIGN
import com.arvindrachuri.ehtml.utils.CssAttribute.WIDTH
import com.arvindrachuri.ehtml.utils.DisplayType

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
    var display: DisplayType? = null
    private val custom = mutableMapOf<String, String>()
    private val warnings = mutableListOf<String>()

    fun css(property: String, value: String) {
        custom[property] = value
        warnings.add("Unmodeled CSS property: `$property` used - behavior may not be email-safe")
    }

    fun build(): StyleBuildResult {
        val styles = buildMap {
            padding?.let { put(PADDING, it) }
            margin?.let { put(MARGIN, it) }
            fontSize?.let { put(FONT_SIZE, it) }
            fontWeight?.let { put(FONT_WEIGHT, it) }
            lineHeight?.let { put(LINE_HEIGHT, it) }
            color?.let { put(COLOR, it) }
            backgroundColor?.let { put(BACKGROUND_COLOR, it) }
            textAlign?.let { put(TEXT_ALIGN, it) }
            width?.let { put(WIDTH, it) }
            border?.let { put(BORDER, it) }
            display?.let { put(DISPLAY, it.value) }
            putAll(custom)
        }
        return StyleBuildResult(styles, warnings)
    }
}
