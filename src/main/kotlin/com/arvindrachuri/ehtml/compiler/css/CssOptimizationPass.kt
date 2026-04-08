package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.utils.css.constants.CssShorthandGroups
import com.arvindrachuri.ehtml.utils.css.constants.CssShorthandGroups.HEX_COLLAPSE_REGEX
import com.arvindrachuri.ehtml.utils.css.constants.CssShorthandGroups.ZERO_PX_REGEX
import kotlin.collections.iterator

object CssOptimizationPass {

    fun run(document: EmailDocumentNode): EmailDocumentNode {
        val optimizedHeadStyles =
            document.headStyles
                .let(::mergeDuplicateSelectors)
                .let(::dedupIdenticalStyles)
                .let(::collapseShorthands)
                .let(::minifyValues)
        val optimizedChildren = document.children.map(::minifyNode)
        return document.copy(headStyles = optimizedHeadStyles, children = optimizedChildren)
    }

    private fun minifyNode(node: EmailNode): EmailNode =
        when (node) {
            is ElementNode ->
                node.copy(
                    styles = minifyStyles(node.styles),
                    children = node.children.map(::minifyNode),
                )
            else -> node
        }

    private fun minifyStyles(styles: Map<String, String>): Map<String, String> =
        styles.mapValues { (_, v) ->
            collapseHexColors(trimZeros(v))
        }

    private fun mergeDuplicateSelectors(nodes: List<CssNode>): List<CssNode> {
        val seen = mutableMapOf<String, MutableMap<String, String>>()
        val result = mutableListOf<CssNode>()
        val firstIndex = mutableMapOf<String, Int>()

        for (node in nodes) {
            when (node) {
                is CssRule -> {
                    if (node.selector in seen) {
                        seen[node.selector]!!.putAll(node.styles)
                    } else {
                        seen[node.selector] = node.styles.toMutableMap()
                        firstIndex[node.selector] = result.size
                        result.add(node)
                    }
                }
                is CssMediaQuery ->
                    result.add(CssMediaQuery(node.condition, mergeDuplicateSelectors(node.rules)))
                is CssMsoConditional ->
                    result.add(CssMsoConditional(mergeDuplicateSelectors(node.rules)))
            }
        }
        for ((selector, styles) in seen) {
            result[firstIndex[selector]!!] = CssRule(selector, styles)
        }
        return result
    }

    private fun dedupIdenticalStyles(nodes: List<CssNode>): List<CssNode> {
        val result = mutableListOf<CssNode>()
        val styleToSelectors = mutableMapOf<Map<String, String>, MutableList<String>>()
        val firstIndex = mutableMapOf<Map<String, String>, Int>()

        for (node in nodes) {
            when (node) {
                is CssRule -> {
                    if (node.styles in styleToSelectors) {
                        styleToSelectors[node.styles]!!.add(node.selector)
                    } else {
                        styleToSelectors[node.styles] = mutableListOf(node.selector)
                        firstIndex[node.styles] = result.size
                        result.add(node)
                    }
                }
                is CssMediaQuery ->
                    result.add(CssMediaQuery(node.condition, dedupIdenticalStyles(node.rules)))
                is CssMsoConditional ->
                    result.add(CssMsoConditional(dedupIdenticalStyles(node.rules)))
            }
        }
        for ((styles, selectors) in styleToSelectors) {
            result[firstIndex[styles]!!] = CssRule(selectors.joinToString(", "), styles)
        }
        return result
    }

    private fun collapseShorthands(nodes: List<CssNode>): List<CssNode> = nodes.map { node ->
        when (node) {
            is CssRule -> CssRule(node.selector, collapseStyles(node.styles))
            is CssMediaQuery -> CssMediaQuery(node.condition, collapseShorthands(node.rules))
            is CssMsoConditional -> CssMsoConditional(collapseShorthands(node.rules))
        }
    }

    private fun collapseStyles(styles: Map<String, String>): Map<String, String> {
        val result = styles.toMutableMap()
        for (group in CssShorthandGroups.SHORTHAND_GROUPS) {
            val top = result[group.top] ?: continue
            val right = result[group.right] ?: continue
            val bottom = result[group.bottom] ?: continue
            val left = result[group.left] ?: continue

            if (group.shorthand in result) continue

            val shorthand =
                when {
                    top == right && right == bottom && bottom == left -> top
                    top == bottom && right == left -> "$top $right"
                    right == left -> "$top $right $bottom"
                    else -> "$top $right $bottom $left"
                }

            result[group.shorthand] = shorthand
            result.remove(group.top)
            result.remove(group.right)
            result.remove(group.bottom)
            result.remove(group.left)
        }
        return result
    }

    private fun minifyValues(nodes: List<CssNode>): List<CssNode> = nodes.map { node ->
        when (node) {
            is CssRule ->
                CssRule(
                    node.selector,
                    node.styles.mapValues { (_, v) -> collapseHexColors(trimZeros(v)) },
                )
            is CssMediaQuery -> CssMediaQuery(node.condition, minifyValues(node.rules))
            is CssMsoConditional -> CssMsoConditional(minifyValues(node.rules))
        }
    }

    private fun collapseHexColors(value: String): String =
        HEX_COLLAPSE_REGEX.replace(value) { m ->
            "#${m.groupValues[1]}${m.groupValues[2]}${m.groupValues[3]}"
        }

    private fun trimZeros(value: String): String = ZERO_PX_REGEX.replace(value, "0")
}
