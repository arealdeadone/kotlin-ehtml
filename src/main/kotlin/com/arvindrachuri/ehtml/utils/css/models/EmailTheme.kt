package com.arvindrachuri.ehtml.utils.css.models

data class EmailTheme(
    val primary: ColorToken,
    val secondary: ColorToken,
    val background: ColorToken,
    val surface: ColorToken,
    val surfaceAlt: ColorToken,
    val foreground: ColorToken,
    val mutedForeground: ColorToken,
    val border: String,
    val success: ColorToken? = null,
    val warning: ColorToken? = null,
    val error: ColorToken? = null,
)
