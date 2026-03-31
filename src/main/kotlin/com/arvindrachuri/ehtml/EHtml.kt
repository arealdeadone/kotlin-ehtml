package com.arvindrachuri.ehtml

import com.arvindrachuri.ehtml.dsl.email

fun main() {
    val comprehensiveExample = email {
        title = "Three column layout"
        backgroundColor = "#f7f7f7"
        container {
            row {
                style { border = "1px solid black" }
                column {
                    widthPercent = 33
                    style { border = "1px solid black" }
                    div {
                        p {
                            +"This is some text with inline styled"
                            span {
                                style {
                                    color = "lavender"
                                    fontSize = "16px"
                                }
                                a(href = "https://example.com") { +" span with links" }
                            }
                            +". This can be modelled like this"
                        }
                    }
                }
                column {
                    widthPercent = 33
                    style {
                        color = "red"
                        fontSize = "32px"
                        border = "1px solid black"
                    }
                    +"This is the second column"
                }
                column {
                    widthPercent = 33
                    style {
                        color = "grey"
                        fontSize = "48px"
                        border = "1px solid black"
                    }
                    +"This is the third column. I also support hr"
                    hr()
                    +"This is the text below HR"
                }
            }
        }
    }
    println(comprehensiveExample)
}
