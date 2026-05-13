package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.dsl.email
import com.arvindrachuri.ehtml.utils.css.constants.HtmlTagSelector
import com.arvindrachuri.ehtml.utils.css.models.ColorToken
import com.arvindrachuri.ehtml.utils.css.models.EmailTheme
import kotlin.test.Test
import kotlin.test.assertTrue

class UtilityPipelineIntegrationTest {

    private val testTheme =
        EmailTheme(
            primary = ColorToken("#1B7C96", "#1B7C96"),
            secondary = ColorToken("#488BF8", "#488BF8"),
            background = ColorToken("#f5f5f5", "#f5f5f5"),
            surface = ColorToken("#fdfdfd", "#272623"),
            surfaceAlt = ColorToken("#EAEAEA", "#272623"),
            foreground = ColorToken("#1D2129", "#f5f5f5"),
            mutedForeground = ColorToken("#999999", "#f5f5f5"),
            border = "#dddddd",
        )

    @Test
    fun `utility class inlines into element style attribute`() {
        val html = email {
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "p-16"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("padding: 16px" in html)
    }

    @Test
    fun `user inline style wins over utility class`() {
        val html = email {
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "p-16"
                            style { padding = "30px" }
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("padding: 30px" in html)
        assertTrue("padding: 16px" !in html)
    }

    @Test
    fun `responsive utility class emitted in head style block`() {
        val html = email {
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "sm-d-block"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("@media (max-width: 600px)" in html)
        assertTrue(".sm-d-block" in html)
        assertTrue("display: block !important" in html)
    }

    @Test
    fun `responsive class preserved in element class attribute`() {
        val html = email {
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "sm-d-block"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("""class="sm-d-block"""" in html)
    }

    @Test
    fun `base utility class removed from element class attribute`() {
        val html = email {
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "p-16"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("""class="p-16"""" !in html)
    }

    @Test
    fun `mixed base and responsive classes handled correctly`() {
        val html = email {
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "d-block sm-d-none p-8"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("display: block" in html)
        assertTrue("padding: 8px" in html)
        assertTrue(".sm-d-none" in html)
        assertTrue("display: none !important" in html)
        assertTrue("""class="sm-d-none"""" in html)
    }

    @Test
    fun `theme color utility inlines into element`() {
        val html = email {
            theme = testTheme
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "bg-primary"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("background-color: #1B7C96" in html)
    }

    @Test
    fun `theme text color utility inlines into element`() {
        val html = email {
            theme = testTheme
            head { title = "Test" }
            container {
                row {
                    column {
                        p {
                            className = "text-foreground"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("color: #1D2129" in html)
    }

    @Test
    fun `combined base and theme utilities in single element`() {
        val html = email {
            theme = testTheme
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "bg-surface p-16 text-center"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("background-color: #fdfdfd" in html)
        assertTrue("padding: 16px" in html)
        assertTrue("text-align: center" in html)
    }

    @Test
    fun `user css in head inlines into elements alongside utility css`() {
        val html = email {
            head {
                title = "Test"
                style { classSelector("custom") { fontSize = "20px" } }
            }
            container {
                row {
                    column {
                        div {
                            className = "sm-d-block custom"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("font-size: 20px" in html)
        assertTrue(".sm-d-block" in html)
    }

    @Test
    fun `no theme produces no theme utility classes`() {
        val html = email {
            head { title = "Test" }
            container {
                row {
                    column {
                        div {
                            className = "bg-primary"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("background-color: #1B7C96" !in html)
    }

    @Test
    fun `user class selector inlines into element`() {
        val html = email {
            head {
                title = "Test"
                style { classSelector("my-custom") { padding = "10px" } }
            }
            container {
                row {
                    column {
                        div {
                            className = "my-custom"
                            +"content"
                        }
                    }
                }
            }
        }
        assertTrue("padding: 10px" in html)
    }

    @Test
    fun `mso conditional css emits in separate block via dsl`() {
        val html = email {
            head {
                title = "Test"
                style {
                    classSelector("btn") { padding = "10px" }
                    mso { tagSelector(HtmlTagSelector.Table) { width = "600px" } }
                }
            }
            container {
                single {
                    div {
                        className = "btn"
                        +"content"
                    }
                }
            }
        }
        assertTrue("<!--[if mso]>" in html)
        assertTrue("width: 600px" in html)
        assertTrue("padding: 10px" in html)
    }

    @Test
    fun `mso conditional css stays in conditional block after inlining`() {
        val html = email {
            head {
                title = "Test"
                style {
                    classSelector("main") { color = "red" }
                    mso { classSelector("mso-only") { color = "blue" } }
                }
            }
            container {
                single {
                    div {
                        className = "main"
                        +"content"
                    }
                    div {
                        className = "mso-only"
                        +"mso content"
                    }
                }
            }
        }
        assertTrue("color: red" in html)
        assertTrue("<!--[if mso]>" in html)
        assertTrue("color: blue" in html)
    }
}
