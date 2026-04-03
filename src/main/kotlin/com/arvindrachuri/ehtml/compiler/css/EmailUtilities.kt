package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.BACKGROUND_COLOR
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.BORDER
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.BORDER_RADIUS
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.COLOR
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.FONT_WEIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.HEIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.LINE_HEIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_BOTTOM
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_LEFT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_RIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MARGIN_TOP
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.OVERFLOW
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.OVERFLOW_X
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.OVERFLOW_Y
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_BOTTOM
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_LEFT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_RIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_TOP
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_ALIGN
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_DECORATION
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_TRANSFORM
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.VERTICAL_ALIGN
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.WIDTH
import com.arvindrachuri.ehtml.utils.css.models.ColorToken
import com.arvindrachuri.ehtml.utils.css.models.EmailTheme
import com.arvindrachuri.ehtml.utils.css.models.UtilityRule

fun emailUtilityRules(): List<UtilityRule> =
    listOf(
        rule(
            "d",
            DISPLAY,
            keyword("block", "inline-block", "inline", "none", "table", "table-cell"),
        ),
        rule("w", WIDTH, percentage(25, 50, 75, 100)),
        rule("h", HEIGHT, percentage(25, 50, 75, 100)),
        rule("p", PADDING, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule(
            "px",
            listOf(PADDING_LEFT, PADDING_RIGHT),
            spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40),
        ),
        rule(
            "py",
            listOf(PADDING_TOP, PADDING_BOTTOM),
            spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40),
        ),
        rule("pt", PADDING_TOP, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("pr", PADDING_RIGHT, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("pb", PADDING_BOTTOM, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("pl", PADDING_LEFT, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("m", MARGIN, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule(
            "mx",
            listOf(MARGIN_LEFT, MARGIN_RIGHT),
            spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40),
        ),
        rule(
            "my",
            listOf(MARGIN_TOP, MARGIN_BOTTOM),
            spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40),
        ),
        rule("mt", MARGIN_TOP, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("mr", MARGIN_RIGHT, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("mb", MARGIN_BOTTOM, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("ml", MARGIN_LEFT, spacing(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40)),
        rule("text", TEXT_ALIGN, keyword("center", "left", "right")),
        rule(
            "text",
            FONT_SIZE,
            named(
                "xs" to "10px",
                "sm" to "12px",
                "base" to "14px",
                "lg" to "16px",
                "xl" to "18px",
                "2xl" to "20px",
                "3xl" to "24px",
                "4xl" to "30px",
            ),
        ),
        rule("font", FONT_WEIGHT, keyword("bold", "normal")),
        rule(
            "leading",
            LINE_HEIGHT,
            named("none" to "1", "tight" to "1.25", "normal" to "1.5", "relaxed" to "1.75"),
        ),
        rule("underline", TEXT_DECORATION, literal("underline")),
        rule("no-underline", TEXT_DECORATION, literal("none")),
        rule("uppercase", TEXT_TRANSFORM, literal("uppercase")),
        rule("lowercase", TEXT_TRANSFORM, literal("lowercase")),
        rule("capitalize", TEXT_TRANSFORM, literal("capitalize")),
        rule(
            "rounded",
            BORDER_RADIUS,
            named(
                "" to "4px",
                "sm" to "2px",
                "md" to "6px",
                "lg" to "12px",
                "full" to "9999px",
                "none" to "0",
            ),
        ),
        rule("overflow", OVERFLOW, keyword("hidden", "visible", "auto")),
        rule("overflow-x", OVERFLOW_X, keyword("hidden", "visible", "auto")),
        rule("overflow-y", OVERFLOW_Y, keyword("hidden", "visible", "auto")),
        rule("border", BORDER, named("0" to "0", "" to "1px solid")),
        rule("align", VERTICAL_ALIGN, keyword("top", "middle", "bottom", "baseline")),
    )

fun themeUtilityRules(theme: EmailTheme): List<UtilityRule> = buildList {
    fun addColorToken(name: String, token: ColorToken) {
        add(rule("bg-$name", BACKGROUND_COLOR, literal(token.light)))
        add(rule("text-$name", COLOR, literal(token.light)))
    }
    addColorToken("primary", theme.primary)
    addColorToken("secondary", theme.secondary)
    addColorToken("background", theme.background)
    addColorToken("surface", theme.surface)
    addColorToken("surface-alt", theme.surfaceAlt)
    addColorToken("foreground", theme.foreground)
    addColorToken("muted", theme.mutedForeground)
    theme.success?.let { addColorToken("success", it) }
    theme.warning?.let { addColorToken("warning", it) }
    theme.error?.let { addColorToken("error", it) }
    add(rule("border-default", "border-color", literal(theme.border)))
}
