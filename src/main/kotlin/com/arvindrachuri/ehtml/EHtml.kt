package com.arvindrachuri.ehtml

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.compiler.DocumentShellPass
import com.arvindrachuri.ehtml.compiler.HtmlEmitter
import com.arvindrachuri.ehtml.compiler.LayoutLoweringPass

fun main() {
    val tree =
        ContainerNode(
            children =
                listOf(
                    RowNode(
                        children =
                            listOf(
                                ColumnNode(widthPercent = 50, children = listOf(TextNode("Left"))),
                                ColumnNode(widthPercent = 50, children = listOf(TextNode("Right"))),
                            )
                    )
                )
        )

    val lowered = LayoutLoweringPass.run(tree)
    val document =
        DocumentShellPass.run(
            lowered,
            title = "Welcome Email",
            lang = "th",
            backgroundColor = "#e6e6e6",
        )
    val html = HtmlEmitter.emit(document)
    println(html)
}
