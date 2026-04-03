package com.arvindrachuri.ehtml.utils.css.models

sealed interface ValueScale {
    fun resolve(modifier: String): String?
}

data class KeywordScale(val keywords: Set<String>) : ValueScale {
    override fun resolve(modifier: String): String? = if (modifier in keywords) modifier else null
}

data class NamedScale(val map: Map<String, String>) : ValueScale {
    override fun resolve(modifier: String): String? = map[modifier]
}

data class SpacingScale(val valuesSet: Set<Int>) : ValueScale {
    override fun resolve(modifier: String): String? =
        when {
            modifier.equals("auto", ignoreCase = true) -> "auto"
            modifier.toIntOrNull() in valuesSet -> "${modifier}px"
            else -> null
        }
}

data class PercentageScale(val percentageSet: Set<Int>) : ValueScale {
    override fun resolve(modifier: String): String? =
        when {
            modifier.equals("auto", ignoreCase = true) -> "auto"
            modifier.toIntOrNull() in percentageSet -> "${modifier}%"
            else -> null
        }
}

data class LiteralScale(val value: String) : ValueScale {
    override fun resolve(modifier: String): String? = if (modifier.isEmpty()) value else null
}
