package com.arvindrachuri.ehtml.utils

object TagUtils {
    private val tagNameRegex = "<\\s*/?\\s*([A-Za-z][A-Za-z0-9-]*)".toRegex()

    fun extractHtmlTagName(value: String): String? = tagNameRegex.find(value)?.groupValues?.get(1)
}
