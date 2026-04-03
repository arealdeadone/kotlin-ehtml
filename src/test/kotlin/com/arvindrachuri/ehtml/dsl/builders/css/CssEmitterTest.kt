package com.arvindrachuri.ehtml.dsl.builders.css

import com.arvindrachuri.ehtml.dsl.email
import com.arvindrachuri.ehtml.dsl.emailDocument
import com.arvindrachuri.ehtml.utils.css.constants.HtmlTagSelector
import com.arvindrachuri.ehtml.utils.css.values.DisplayType
import com.arvindrachuri.ehtml.utils.css.values.OverflowType
import kotlin.test.Test
import kotlin.test.assertEquals

class CssEmitterTest {

    @Test
    fun `head style block emits style tag in head`() {
        val html = email {
            head {
                title = "Test"
                style { classSelector("w-100") { width = "100%" } }
            }
        }
        assert("""<style type="text/css">""" in html)
        assert("</style>" in html)
    }

    @Test
    fun `style tag appears before closing head`() {
        val html = email {
            head {
                title = "Test"
                style { classSelector("w-100") { width = "100%" } }
            }
        }
        val stylePos = html.indexOf("<style")
        val headClosePos = html.indexOf("</head>")
        assert(stylePos < headClosePos)
    }

    @Test
    fun `style tag not emitted when no styles defined`() {
        val html = email { head { title = "Test" } }
        assert("<style" !in html)
    }

    @Test
    fun `classSelector emits dot prefix`() {
        val html = email {
            head {
                title = "Test"
                style { classSelector("w-100") { width = "100%" } }
            }
        }
        assert(".w-100 {" in html)
    }

    @Test
    fun `idSelector emits hash prefix`() {
        val html = email {
            head {
                title = "Test"
                style { idSelector("outlook") { padding = "0" } }
            }
        }
        assert("#outlook {" in html)
    }

    @Test
    fun `tagSelector emits tag name`() {
        val html = email {
            head {
                title = "Test"
                style { tagSelector(HtmlTagSelector.Img) { display = DisplayType.Block } }
            }
        }
        assert("img {" in html)
    }

    @Test
    fun `tagSelector with vararg emits comma-separated selectors`() {
        val html = email {
            head {
                title = "Test"
                style {
                    tagSelector(HtmlTagSelector.Table, HtmlTagSelector.Td, HtmlTagSelector.P) {
                        margin = "0"
                    }
                }
            }
        }
        assert("table, td, p {" in html)
    }

    @Test
    fun `rule emits raw selector`() {
        val html = email {
            head {
                title = "Test"
                style { rule("body, table, td, p, a") { margin = "0" } }
            }
        }
        assert("body, table, td, p, a {" in html)
    }

    @Test
    fun `css rule emits property values`() {
        val html = email {
            head {
                title = "Test"
                style {
                    classSelector("card") {
                        borderRadius = "12px"
                        overflow = OverflowType.Hidden
                    }
                }
            }
        }
        assert("border-radius: 12px" in html)
        assert("overflow: hidden" in html)
    }

    @Test
    fun `media query emits at-media wrapper`() {
        val html = email {
            head {
                title = "Test"
                style { media("max-width: 630px") { classSelector("w-100") { width = "100%" } } }
            }
        }
        assert("@media (max-width: 630px)" in html)
    }

    @Test
    fun `media query contains nested rules`() {
        val html = email {
            head {
                title = "Test"
                style { media("max-width: 630px") { classSelector("w-100") { width = "100%" } } }
            }
        }
        assert(".w-100 {" in html)
        assert("width: 100%" in html)
    }

    @Test
    fun `important block appends important to values`() {
        val html = email {
            head {
                title = "Test"
                style {
                    media("max-width: 630px") {
                        classSelector("w-100") {
                            important {
                                width = "100%"
                                display = DisplayType.Block
                            }
                        }
                    }
                }
            }
        }
        assert("width: 100% !important" in html)
        assert("display: block !important" in html)
    }

    @Test
    fun `important block does not affect properties outside block`() {
        val html = email {
            head {
                title = "Test"
                style {
                    classSelector("card") {
                        borderRadius = "12px"
                        important { width = "100%" }
                    }
                }
            }
        }
        assert("border-radius: 12px;" in html)
        assert("border-radius: 12px !important" !in html)
        assert("width: 100% !important" in html)
    }

    @Test
    fun `string important extension works per property`() {
        val html = email {
            head {
                title = "Test"
                style {
                    classSelector("override") {
                        padding = "16px".important()
                        margin = "0"
                    }
                }
            }
        }
        assert("padding: 16px !important" in html)
        assert("margin: 0;" in html)
        assert("margin: 0 !important" !in html)
    }

    @Test
    fun `multiple rules in same style block`() {
        val html = email {
            head {
                title = "Test"
                style {
                    classSelector("a") { color = "#333" }
                    classSelector("b") { color = "#666" }
                }
            }
        }
        assert(".a {" in html)
        assert(".b {" in html)
        assert("color: #333" in html)
        assert("color: #666" in html)
    }

    @Test
    fun `dark mode media query with important`() {
        val html = email {
            head {
                title = "Test"
                style {
                    media("prefers-color-scheme: dark") {
                        classSelector("darkmode-bg") { important { backgroundColor = "#272623" } }
                    }
                }
            }
        }
        assert("@media (prefers-color-scheme: dark)" in html)
        assert("background-color: #272623 !important" in html)
    }

    @Test
    fun `layout selectors resolve to correct tags`() {
        val html = email {
            head {
                title = "Test"
                style {
                    tagSelector(HtmlTagSelector.Container) { width = "100%" }
                    tagSelector(HtmlTagSelector.Row) { width = "100%" }
                    tagSelector(HtmlTagSelector.Column) { padding = "0" }
                }
            }
        }
        assert("table {" in html)
        assert("tr {" in html)
        assert("td {" in html)
    }

    @Test
    fun `emailDocument preserves headStyles on node`() {
        val doc = emailDocument {
            head {
                title = "Test"
                style { classSelector("w-100") { width = "100%" } }
            }
        }
        assertEquals(1, doc.headStyles.size)
    }

    @Test
    fun `full email with styles and content`() {
        val html = email {
            head {
                title = "Full Test"
                style {
                    rule("body, table, td") {
                        margin = "0"
                        padding = "0"
                    }
                    media("max-width: 630px") {
                        classSelector("w-100") { important { width = "100%" } }
                    }
                }
            }
            container { row { column { +"Hello" } } }
        }
        assert("<title>Full Test</title>" in html)
        assert("""<style type="text/css">""" in html)
        assert("body, table, td {" in html)
        assert("@media (max-width: 630px)" in html)
        assert("Hello" in html)
    }
}
