package com.arvindrachuri.ehtml

import com.arvindrachuri.ehtml.ast.ColumnNode
import com.arvindrachuri.ehtml.ast.ContainerNode
import com.arvindrachuri.ehtml.ast.RowNode
import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.compiler.DocumentShellPass
import com.arvindrachuri.ehtml.compiler.HtmlEmitter
import com.arvindrachuri.ehtml.compiler.LayoutLoweringPass
import com.arvindrachuri.ehtml.dsl.email
import com.arvindrachuri.ehtml.dsl.emailDocument

fun main() {
    println("==== USING AST ===")
    val document = emailDocument {
        title = "Using AST"
        container {
            row {
                column {
                    widthPercent = 50
                    +"Left"
                }
                column {
                    widthPercent = 50
                    +"Right"
                }
            }
        }
    }

    val html = HtmlEmitter.emit(document)
    println(html)
    println()
    println("==== USING DSL ===")
    println(email {
        title = "Using DSL"
        container {
            row {
                column {
                    widthPercent = 50
                    +"Left"
                }
                column {
                    widthPercent = 50
                    +"Right"
                }
            }
        }
    })
}
