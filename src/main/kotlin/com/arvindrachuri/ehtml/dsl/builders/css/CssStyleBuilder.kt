package com.arvindrachuri.ehtml.dsl.builders.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssMsoConditional
import com.arvindrachuri.ehtml.ast.CssNode
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.dsl.EmailDsl
import com.arvindrachuri.ehtml.utils.css.constants.HtmlTagSelector

@EmailDsl
class CssStyleBuilder {
    private val rules = mutableListOf<CssNode>()

    fun rule(selector: String, block: CssRuleBuilder.() -> Unit) {
        val styles = CssRuleBuilder().apply(block).buildCssRule()
        rules.add(CssRule(selector = selector, styles = styles))
    }

    fun tagSelector(vararg tags: HtmlTagSelector, block: CssRuleBuilder.() -> Unit) {
        val selector = tags.joinToString(", ") { it.selector }
        rule(selector, block)
    }

    fun classSelector(vararg names: String, block: CssRuleBuilder.() -> Unit) {
        val selector = names.joinToString(", ") { ".$it" }
        rule(selector, block)
    }

    fun idSelector(name: String, block: CssRuleBuilder.() -> Unit) {
        rule("#$name", block)
    }

    fun media(condition: String, block: CssStyleBuilder.() -> Unit) {
        val nested = CssStyleBuilder().apply(block).build()
        rules.add(CssMediaQuery(condition = condition, rules = nested))
    }

    fun mso(block: CssStyleBuilder.() -> Unit) {
        val nested = CssStyleBuilder().apply(block).build()
        rules.add(CssMsoConditional(rules = nested))
    }

    fun build(): List<CssNode> = rules
}
