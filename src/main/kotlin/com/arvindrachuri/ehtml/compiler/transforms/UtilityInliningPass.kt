package com.arvindrachuri.ehtml.compiler.transforms

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes

object UtilityInliningPass {
    fun run(node: EmailNode, inlineStyles: Map<String, Map<String, String>>): EmailNode =
        when (node) {
            is EmailDocumentNode ->
                node.copy(children = node.children.map { run(it, inlineStyles) })
            is ElementNode ->
                inlineLayoutNode(node.attributes, node.styles, node.children, inlineStyles) {
                    attrs,
                    styles,
                    children ->
                    node.copy(attributes = attrs, styles = styles, children = children)
                }
            is ContainerNode ->
                inlineLayoutNode(node.attributes, node.styles, node.children, inlineStyles) {
                    attrs,
                    styles,
                    children ->
                    node.copy(attributes = attrs, styles = styles, children = children)
                }
            is RowNode ->
                inlineLayoutNode(node.attributes, node.styles, node.children, inlineStyles) {
                    attrs,
                    styles,
                    children ->
                    node.copy(attributes = attrs, styles = styles, children = children)
                }
            is ColumnNode ->
                inlineLayoutNode(node.attributes, node.styles, node.children, inlineStyles) {
                    attrs,
                    styles,
                    children ->
                    node.copy(attributes = attrs, styles = styles, children = children)
                }
            else -> node
        }

    private fun <T : EmailNode> inlineLayoutNode(
        attributes: Map<String, String>,
        styles: Map<String, String>,
        children: List<EmailNode>,
        inlineStyles: Map<String, Map<String, String>>,
        rebuild: (Map<String, String>, Map<String, String>, List<EmailNode>) -> T,
    ): T {
        val classNames =
            attributes[HtmlTagAttributes.CLASS]?.split(" ")?.filter { it.isNotBlank() }
                ?: emptyList()

        val utilityStyles = mutableMapOf<String, String>()
        val remainingClasses = mutableListOf<String>()

        for (className in classNames) {
            val resolved = inlineStyles[className]
            if (resolved != null) {
                resolved.forEach { (prop, value) ->
                    if (prop !in styles && prop !in utilityStyles) {
                        utilityStyles[prop] = value
                    }
                }
            } else remainingClasses.add(className)
        }

        val newAttributes =
            if (remainingClasses.isEmpty()) attributes - HtmlTagAttributes.CLASS
            else attributes + (HtmlTagAttributes.CLASS to remainingClasses.joinToString(" "))
        val mergedStyles = utilityStyles + styles
        val processedChildren = children.map { run(it, inlineStyles) }

        return rebuild(newAttributes, mergedStyles, processedChildren)
    }
}
