package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.ast.EmailDocumentNode

object CssTreeShakePass {

    private val SAFELIST = setOf("ExternalClass", "ReadMsgBody")

    fun run(document: EmailDocumentNode): EmailDocumentNode {
        val usedClasses = CssClassCollector.collect(document.children)
        val prunedStyles = document.headStyles.mapNotNull { pruneNode(it, usedClasses) }
        return document.copy(headStyles = prunedStyles)
    }

    private fun pruneNode(node: CssNode, used: Set<String>): CssNode? =
        when (node) {
            is CssRule -> if (shouldKeepSelector(node.selector, used)) node else null
            is CssMediaQuery -> {
                val prunedRules = node.rules.mapNotNull { pruneNode(it, used) }
                if (prunedRules.isEmpty()) null else CssMediaQuery(node.condition, prunedRules)
            }
            is CssMsoConditional -> {
                val prunedRules = node.rules.mapNotNull { pruneNode(it, used) }
                if (prunedRules.isEmpty()) null else CssMsoConditional(prunedRules)
            }
        }

    private fun shouldKeepSelector(selector: String, used: Set<String>): Boolean =
        selector.split(",").map(String::trim).any { s ->
            s.contains(' ') ||
                s.startsWith('#') ||
                !s.startsWith('.') ||
                s.removePrefix(".") in used ||
                s.removePrefix(".") in SAFELIST
        }
}
