package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.MAX_WIDTH
import com.arvindrachuri.ehtml.utils.css.constants.Sizes
import com.arvindrachuri.ehtml.utils.css.models.ResolvedUtilities
import com.arvindrachuri.ehtml.utils.css.models.UtilityRule

class UtilityClassResolver(
    private val rules: List<UtilityRule>,
    private val breakpoints: Map<Sizes, String> = mapOf(Sizes.Small to "$MAX_WIDTH: 600px"),
) {

    fun resolve(classNames: Set<String>): ResolvedUtilities {
        val inlineStyles = mutableMapOf<String, Map<String, String>>()
        val mediaRules = mutableMapOf<String, MutableList<CssNode>>()

        for (className in classNames) {
            val breakpointEntry =
                breakpoints.entries.find { className.startsWith("${it.key.stringValue}-") }

            if (breakpointEntry != null) {
                val unprefixed = className.removePrefix("${breakpointEntry.key.stringValue}-")
                val resolved = resolveClassName(unprefixed) ?: continue
                val importantStyles = resolved.mapValues { "${it.value} !important" }
                mediaRules
                    .getOrPut(breakpointEntry.value) { mutableListOf() }
                    .add(CssRule(".$className", importantStyles))
            } else {
                val resolved = resolveClassName(className) ?: continue
                inlineStyles[className] = resolved
            }
        }

        val headStyles = mediaRules.map { (condition, rules) -> CssMediaQuery(condition, rules) }
        return ResolvedUtilities(inlineStyles = inlineStyles, headStyles = headStyles)
    }

    private fun resolveClassName(name: String): Map<String, String>? =
        rules.firstNotNullOfOrNull { rule ->
            extractModifier(name, prefix = rule.prefix)?.let { modifier ->
                rule.scale.resolve(modifier)?.let { value ->
                    rule.cssProperties.associateWith { value }
                }
            }
        }

    private fun extractModifier(className: String, prefix: String): String? =
        when {
            className == prefix -> ""
            className.startsWith("$prefix-") -> className.removePrefix("$prefix-")
            else -> null
        }
}
