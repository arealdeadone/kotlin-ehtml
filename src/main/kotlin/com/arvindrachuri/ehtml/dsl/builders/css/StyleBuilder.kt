package com.arvindrachuri.ehtml.dsl.builders.css

import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.result.StyleBuildResult
import com.arvindrachuri.ehtml.utils.css.CssAttribute
import com.arvindrachuri.ehtml.utils.css.values.*

@EmailDsl
open class StyleBuilder {
    var padding: String? = null
    var margin: String? = null
    var fontSize: String? = null
    var fontWeight: String? = null
    var fontStyle: FontStyleType? = null
    var fontFamily: String? = null
    var color: String? = null
    var backgroundColor: String? = null
    var width: String? = null
    var maxWidth: String? = null
    var minWidth: String? = null
    var height: String? = null
    var maxHeight: String? = null
    var minHeight: String? = null
    var border: String? = null
    var borderRadius: String? = null
    var opacity: String? = null
    var display: DisplayType? = null
    var lineHeight: String? = null
    var letterSpacing: String? = null
    var wordSpacing: String? = null
    var textAlign: String? = null
    var textDecoration: TextDecorationType? = null
    var textTransform: TextTransformType? = null
    var overflow: OverflowType? = null
    var overflowX: OverflowType? = null
    var overflowY: OverflowType? = null
    var verticalAlign: VerticalAlignType? = null
    var direction: DirectionType? = null
    var float: FloatType? = null
    var whiteSpace: WhiteSpaceType? = null
    private val custom = mutableMapOf<String, String>()
    private val warnings = mutableListOf<String>()

    fun css(property: String, value: String) {
        custom[property] = value
        warnings.add("Unmodeled CSS property: `$property` used - behavior may not be email-safe")
    }

    fun build(): StyleBuildResult {
        val styles = buildMap {
            padding?.let { put(CssAttribute.PADDING, it) }
            margin?.let { put(CssAttribute.MARGIN, it) }
            fontSize?.let { put(CssAttribute.FONT_SIZE, it) }
            fontWeight?.let { put(CssAttribute.FONT_WEIGHT, it) }
            fontStyle?.let { put(CssAttribute.FONT_STYLE, it.value) }
            fontFamily?.let { put(CssAttribute.FONT_FAMILY, it) }
            color?.let { put(CssAttribute.COLOR, it) }
            backgroundColor?.let { put(CssAttribute.BACKGROUND_COLOR, it) }
            width?.let { put(CssAttribute.WIDTH, it) }
            maxWidth?.let { put(CssAttribute.MAX_WIDTH, it) }
            minWidth?.let { put(CssAttribute.MIN_WIDTH, it) }
            height?.let { put(CssAttribute.HEIGHT, it) }
            minHeight?.let { put(CssAttribute.MIN_HEIGHT, it) }
            maxHeight?.let { put(CssAttribute.MAX_HEIGHT, it) }
            border?.let { put(CssAttribute.BORDER, it) }
            borderRadius?.let { put(CssAttribute.BORDER_RADIUS, it) }
            opacity?.let { put(CssAttribute.OPACITY, it) }
            display?.let { put(CssAttribute.DISPLAY, it.value) }
            lineHeight?.let { put(CssAttribute.LINE_HEIGHT, it) }
            letterSpacing?.let { put(CssAttribute.LETTER_SPACING, it) }
            wordSpacing?.let { put(CssAttribute.WORD_SPACING, it) }
            textAlign?.let { put(CssAttribute.TEXT_ALIGN, it) }
            textDecoration?.let { put(CssAttribute.TEXT_DECORATION, it.value) }
            textTransform?.let { put(CssAttribute.TEXT_TRANSFORM, it.value) }
            overflow?.let { put(CssAttribute.OVERFLOW, it.value) }
            overflowX?.let { put(CssAttribute.OVERFLOW_X, it.value) }
            overflowY?.let { put(CssAttribute.OVERFLOW_Y, it.value) }
            verticalAlign?.let { put(CssAttribute.VERTICAL_ALIGN, it.value) }
            direction?.let { put(CssAttribute.DIRECTION, it.value) }
            float?.let { put(CssAttribute.FLOAT, it.value) }
            whiteSpace?.let { put(CssAttribute.WHITE_SPACE, it.value) }
            putAll(custom)
        }
        return StyleBuildResult(styles, warnings)
    }
}
