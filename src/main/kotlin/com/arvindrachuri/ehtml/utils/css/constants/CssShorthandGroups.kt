package com.arvindrachuri.ehtml.utils.css.constants

import com.arvindrachuri.ehtml.utils.css.models.ShorthandGroup

object CssShorthandGroups {
    val SHORTHAND_GROUPS =
        listOf(
            ShorthandGroup(
                shorthand = CssAttribute.PADDING,
                top = CssAttribute.PADDING_TOP,
                right = CssAttribute.PADDING_RIGHT,
                bottom = CssAttribute.PADDING_BOTTOM,
                left = CssAttribute.PADDING_LEFT,
            ),
            ShorthandGroup(
                shorthand = CssAttribute.MARGIN,
                top = CssAttribute.MARGIN_TOP,
                right = CssAttribute.MARGIN_RIGHT,
                bottom = CssAttribute.MARGIN_BOTTOM,
                left = CssAttribute.MARGIN_LEFT,
            ),
        )
    val HEX_COLLAPSE_REGEX = Regex("#([0-9a-fA-F])\\1([0-9a-fA-F])\\2([0-9a-fA-F])\\3")
    val ZERO_PX_REGEX = Regex("\\b0px\\b")
}
