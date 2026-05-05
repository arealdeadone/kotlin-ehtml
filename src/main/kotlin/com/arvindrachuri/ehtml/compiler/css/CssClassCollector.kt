package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.ElementNode
import com.arvindrachuri.ehtml.ast.EmailDocumentNode
import com.arvindrachuri.ehtml.ast.EmailNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.utils.HtmlTagAttributes

object CssClassCollector {

    fun collect(nodes: List<EmailNode>): Set<String> {
        val classes = mutableSetOf<String>()
        nodes.forEach { collectFromNode(it, classes) }
        return classes
    }

    private fun collectFromNode(node: EmailNode, classes: MutableSet<String>) {
        val (attributes, children) =
            when (node) {
                is ElementNode -> node.attributes to node.children
                is EmailDocumentNode -> emptyMap<String, String>() to node.children
                else -> return
            }
        attributes[HtmlTagAttributes.CLASS]
            ?.split(" ")
            ?.filter { it.isNotBlank() }
            ?.let { classes.addAll(it) }
        children.forEach { collectFromNode(it, classes) }
    }
}
