package com.arvindrachuri.ehtml.example

import com.arvindrachuri.ehtml.dsl.builders.css.CssStyleBuilder
import com.arvindrachuri.ehtml.utils.css.constants.HtmlTagSelector.*
import com.arvindrachuri.ehtml.utils.css.values.DisplayType
import com.arvindrachuri.ehtml.utils.css.values.FloatType
import com.arvindrachuri.ehtml.utils.css.values.TextAlignType
import com.arvindrachuri.ehtml.utils.css.values.TextDecorationType

fun CssStyleBuilder.emailResetStyles() {
    rule("html, body") {
        width = "100%"
        height = "100%"
        margin = "0"
        padding = "0"
    }
    rule("body, table, td, p, a, li, blockquote") {
        css("-webkit-text-size-adjust", "100%")
        css("-ms-text-size-adjust", "100%")
    }
    tagSelector(Img) {
        height = "auto"
        lineHeight = "100%"
        border = "0"
        css("outline", "none")
        textDecoration = TextDecorationType.None
        display = DisplayType.Inline
        css("-ms-interpolation-mode", "bicubic")
    }
    tagSelector(Table) {
        css("border-collapse", "collapse !important")
        css("mso-table-lspace", "0pt")
        css("mso-table-rspace", "0pt")
        css("border-spacing", "0")
    }
    rule("table td") {
        css("mso-line-height-rule", "exactly")
        css("mso-table-lspace", "0pt")
        css("mso-table-rspace", "0pt")
    }
    tagSelector(H1, H2, H3, H4, H5, H6, P) { margin = "0" }
    tagSelector(Table) { display = DisplayType.Table }
    tagSelector(Column) {
        display = DisplayType.TableCell
        padding = "0px"
    }
    idSelector("outlook") { padding = "0" }
    rule(".ExternalClass, .ReadMsgBody") { width = "100%" }
    rule(".ExternalClass p") {
        margin = "0"
        css("margin-bottom", "0")
    }
    rule(
        ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div"
    ) {
        lineHeight = "100%"
    }
    tagSelector(A) { textDecoration = TextDecorationType.Underline }
}

fun CssStyleBuilder.darkModeStyles() {
    media("prefers-color-scheme: dark") {
        rule("body") { important { backgroundColor = Colors.BACKGROUND } }
        classSelector("darkmode-bg") {
            important {
                backgroundColor = Colors.DARK_SURFACE
                border = "none"
            }
        }
        classSelector("darkmode-text") { important { color = Colors.DARK_TEXT } }
    }
    media("prefers-color-scheme: light") { rule("body") { backgroundColor = Colors.BACKGROUND } }
}

fun CssStyleBuilder.responsiveStyles() {
    media("max-width: 630px") {
        classSelector("text-title-xl-mob") {
            important {
                fontSize = "20px"
                fontWeight = "bold"
            }
        }
        classSelector("text-body-mob") { important { fontSize = "13px" } }
        classSelector("btn-primary") {
            important {
                border = "1px solid ${Colors.PRIMARY}"
                fontSize = "14px"
                lineHeight = "2"
            }
        }
        classSelector("responsive") {
            important {
                width = "100%"
                height = "auto"
            }
        }
        classSelector("center-on-narrow") {
            important {
                width = "90%"
                textAlign = TextAlignType.Center
                display = DisplayType.Block
            }
            css("margin-left", "auto !important")
            css("margin-right", "auto !important")
            float = FloatType.None
        }
    }
}
