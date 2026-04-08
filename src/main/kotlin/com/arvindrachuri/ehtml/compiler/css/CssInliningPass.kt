package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode

object CssInliningPass {
    fun run(document: EmailDocumentNode): EmailDocumentNode {
        val (inlinable, remaining) = document.headStyles.partition { it is CssRule }
        val rules = inlinable.filterIsInstance<CssRule>()

        if (rules.isEmpty()) return document

        val inlinedChildren = document.children.map { inlineNode(it, rules, emptyList()) }
        return document.copy(children = inlinedChildren, headStyles = remaining)
    }

    private fun inlineNode(
        node: EmailNode,
        rules: List<CssRule>,
        ancestors: List<ElementNode>,
    ): EmailNode =
        when (node) {
            is ElementNode -> inlineElement(node, rules, ancestors)
            else -> node
        }

    private fun inlineElement(
        node: ElementNode,
        rules: List<CssRule>,
        ancestors: List<ElementNode>,
    ): ElementNode {
        val matchedStyles = mutableMapOf<String, String>()
        for (rule in rules) {
            if (matchesElement(rule.selector, node, ancestors)) {
                matchedStyles.putAll(rule.styles)
            }
        }

        val mergedStyles = matchedStyles + node.styles

        val inlineableClasses =
            rules
                .filter { isSimpleClassSelector(it.selector) || isMultiClassSelector(it.selector) }
                .flatMap { extractClassNames(it.selector) }
                .toSet()

        val currentClasses =
            node.attributes["class"]?.split(" ")?.filter(String::isNotBlank) ?: emptyList()
        val remainingClasses = currentClasses.filter { it !in inlineableClasses }

        val newAttributes =
            node.attributes.toMutableMap().apply {
                if (remainingClasses.isEmpty()) remove("class")
                else put("class", remainingClasses.joinToString(" "))
            }

        return node.copy(
            attributes = newAttributes,
            styles = mergedStyles,
            children = node.children.map { inlineNode(it, rules, ancestors + node) },
        )
    }

    private fun matchesElement(
        selector: String,
        node: ElementNode,
        ancestors: List<ElementNode>,
    ): Boolean =
        selector.split(",").map(String::trim).any { s ->
            val parts = s.split(" ").filter(String::isNotBlank)
            if (parts.size == 1) matchesSingle(parts[0], node)
            else matchesSingle(parts.last(), node) && matchesAncestors(parts.dropLast(1), ancestors)
        }

    private fun matchesAncestors(selectors: List<String>, ancestors: List<ElementNode>): Boolean {
        var idx = selectors.lastIndex
        for (ancestor in ancestors.reversed()) {
            if (idx < 0) return true
            if (matchesSingle(selectors[idx], ancestor)) idx--
        }
        return idx < 0
    }

    private fun matchesSingle(selector: String, node: ElementNode): Boolean {
        val classes =
            node.attributes["class"]?.split(" ")?.filter(String::isNotBlank)?.toSet() ?: emptySet()
        return when {
            selector.startsWith(".") -> selector.removePrefix(".") in classes
            selector.startsWith("#") -> selector.removePrefix("#") == node.attributes["id"]
            else -> selector == node.tag
        }
    }

    private fun isSimpleClassSelector(selector: String): Boolean =
        selector.startsWith(".") && !selector.contains(" ") && !selector.contains(",")

    private fun isMultiClassSelector(selector: String): Boolean =
        selector.contains(",") &&
            selector.split(",").all { it.trim().startsWith(".") && !it.trim().contains(" ") }

    private fun extractClassNames(selector: String): List<String> =
        selector
            .split(",")
            .map(String::trim)
            .filter { it.startsWith(".") && !it.contains(" ") }
            .map { it.removePrefix(".") }
}
