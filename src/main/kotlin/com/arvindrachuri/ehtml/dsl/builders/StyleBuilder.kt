package com.arvindrachuri.ehtml.dsl.builders

import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.dsl.builders.result.StyleBuildResult
import com.arvindrachuri.ehtml.utils.css.CssAttribute.BACKGROUND_COLOR
import com.arvindrachuri.ehtml.utils.css.CssAttribute.BORDER
import com.arvindrachuri.ehtml.utils.css.CssAttribute.BORDER_RADIUS
import com.arvindrachuri.ehtml.utils.css.CssAttribute.COLOR
import com.arvindrachuri.ehtml.utils.css.CssAttribute.DIRECTION
import com.arvindrachuri.ehtml.utils.css.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.css.CssAttribute.FLOAT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.FONT_FAMILY
import com.arvindrachuri.ehtml.utils.css.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.CssAttribute.FONT_STYLE
import com.arvindrachuri.ehtml.utils.css.CssAttribute.FONT_WEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.LETTER_SPACING
import com.arvindrachuri.ehtml.utils.css.CssAttribute.LINE_HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MARGIN
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MAX_HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MAX_WIDTH
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MIN_HEIGHT
import com.arvindrachuri.ehtml.utils.css.CssAttribute.MIN_WIDTH
import com.arvindrachuri.ehtml.utils.css.CssAttribute.OPACITY
import com.arvindrachuri.ehtml.utils.css.CssAttribute.OVERFLOW
import com.arvindrachuri.ehtml.utils.css.CssAttribute.OVERFLOW_X
import com.arvindrachuri.ehtml.utils.css.CssAttribute.OVERFLOW_Y
import com.arvindrachuri.ehtml.utils.css.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.CssAttribute.TEXT_ALIGN
import com.arvindrachuri.ehtml.utils.css.CssAttribute.TEXT_DECORATION
import com.arvindrachuri.ehtml.utils.css.CssAttribute.TEXT_TRANSFORM
import com.arvindrachuri.ehtml.utils.css.CssAttribute.VERTICAL_ALIGN
import com.arvindrachuri.ehtml.utils.css.CssAttribute.WHITE_SPACE
import com.arvindrachuri.ehtml.utils.css.CssAttribute.WIDTH
import com.arvindrachuri.ehtml.utils.css.CssAttribute.WORD_SPACING
import com.arvindrachuri.ehtml.utils.css.values.DirectionType
import com.arvindrachuri.ehtml.utils.css.values.DisplayType
import com.arvindrachuri.ehtml.utils.css.values.FloatType
import com.arvindrachuri.ehtml.utils.css.values.FontStyleType
import com.arvindrachuri.ehtml.utils.css.values.OverflowType
import com.arvindrachuri.ehtml.utils.css.values.TextDecorationType
import com.arvindrachuri.ehtml.utils.css.values.TextTransformType
import com.arvindrachuri.ehtml.utils.css.values.VerticalAlignType
import com.arvindrachuri.ehtml.utils.css.values.WhiteSpaceType

@EmailDsl
class StyleBuilder {
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
            padding?.let { put(PADDING, it) }
            margin?.let { put(MARGIN, it) }
            fontSize?.let { put(FONT_SIZE, it) }
            fontWeight?.let { put(FONT_WEIGHT, it) }
            fontStyle?.let { put(FONT_STYLE, it.value) }
            fontFamily?.let { put(FONT_FAMILY, it) }
            color?.let { put(COLOR, it) }
            backgroundColor?.let { put(BACKGROUND_COLOR, it) }
            width?.let { put(WIDTH, it) }
            maxWidth?.let { put(MAX_WIDTH, it) }
            minWidth?.let { put(MIN_WIDTH, it) }
            height?.let { put(HEIGHT, it) }
            minHeight?.let { put(MIN_HEIGHT, it) }
            maxHeight?.let { put(MAX_HEIGHT, it) }
            border?.let { put(BORDER, it) }
            borderRadius?.let { put(BORDER_RADIUS, it) }
            opacity?.let { put(OPACITY, it) }
            display?.let { put(DISPLAY, it.value) }
            lineHeight?.let { put(LINE_HEIGHT, it) }
            letterSpacing?.let { put(LETTER_SPACING, it) }
            wordSpacing?.let { put(WORD_SPACING, it) }
            textAlign?.let { put(TEXT_ALIGN, it) }
            textDecoration?.let { put(TEXT_DECORATION, it.value) }
            textTransform?.let { put(TEXT_TRANSFORM, it.value) }
            overflow?.let { put(OVERFLOW, it.value) }
            overflowX?.let { put(OVERFLOW_X, it.value) }
            overflowY?.let { put(OVERFLOW_Y, it.value) }
            verticalAlign?.let { put(VERTICAL_ALIGN, it.value) }
            direction?.let { put(DIRECTION, it.value) }
            float?.let { put(FLOAT, it.value) }
            whiteSpace?.let { put(WHITE_SPACE, it.value) }
            putAll(custom)
        }
        return StyleBuildResult(styles, warnings)
    }
}
