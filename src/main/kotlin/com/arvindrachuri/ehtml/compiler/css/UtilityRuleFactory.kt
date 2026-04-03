package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.utils.css.models.KeywordScale
import com.arvindrachuri.ehtml.utils.css.models.LiteralScale
import com.arvindrachuri.ehtml.utils.css.models.NamedScale
import com.arvindrachuri.ehtml.utils.css.models.PercentageScale
import com.arvindrachuri.ehtml.utils.css.models.SpacingScale
import com.arvindrachuri.ehtml.utils.css.models.UtilityRule
import com.arvindrachuri.ehtml.utils.css.models.ValueScale

fun keyword(vararg values: String) = KeywordScale(values.toSet())

fun named(vararg pairs: Pair<String, String>) = NamedScale(pairs.toMap())

fun spacing(vararg values: Int) = SpacingScale(values.toSet())

fun percentage(vararg values: Int) = PercentageScale(values.toSet())

fun literal(value: String) = LiteralScale(value)

fun rule(prefix: String, cssProperty: String, scale: ValueScale) =
    UtilityRule(prefix, listOf(cssProperty), scale)

fun rule(prefix: String, cssProperties: List<String>, scale: ValueScale) =
    UtilityRule(prefix, cssProperties, scale)
