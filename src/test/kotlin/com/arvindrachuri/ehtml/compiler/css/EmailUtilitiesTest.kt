package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.BACKGROUND_COLOR
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.COLOR
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_ALIGN
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.WIDTH
import com.arvindrachuri.ehtml.utils.css.models.ColorToken
import com.arvindrachuri.ehtml.utils.css.models.EmailTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmailUtilitiesTest {

    @Test
    fun `emailUtilityRules returns non-empty list`() {
        assertTrue(emailUtilityRules().isNotEmpty())
    }

    @Test
    fun `d-block resolves via utility rules`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("d-block"))
        assertEquals(mapOf(DISPLAY to "block"), result.inlineStyles["d-block"])
    }

    @Test
    fun `w-100 resolves via utility rules`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("w-100"))
        assertEquals(mapOf(WIDTH to "100%"), result.inlineStyles["w-100"])
    }

    @Test
    fun `p-16 resolves via utility rules`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("p-16"))
        assertEquals(mapOf(PADDING to "16px"), result.inlineStyles["p-16"])
    }

    @Test
    fun `text-center resolves via utility rules`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("text-center"))
        assertEquals(mapOf(TEXT_ALIGN to "center"), result.inlineStyles["text-center"])
    }

    @Test
    fun `text-sm resolves to font-size via utility rules`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("text-sm"))
        assertEquals(mapOf(FONT_SIZE to "12px"), result.inlineStyles["text-sm"])
    }

    @Test
    fun `uppercase resolves via utility rules`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("uppercase"))
        assertEquals("uppercase", result.inlineStyles["uppercase"]?.values?.first())
    }

    @Test
    fun `rounded resolves to default border-radius`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("rounded"))
        assertEquals("4px", result.inlineStyles["rounded"]?.values?.first())
    }

    @Test
    fun `rounded-lg resolves to 12px`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("rounded-lg"))
        assertEquals("12px", result.inlineStyles["rounded-lg"]?.values?.first())
    }

    @Test
    fun `sm-d-block resolves to media query`() {
        val resolver = UtilityClassResolver(emailUtilityRules())
        val result = resolver.resolve(setOf("sm-d-block"))
        assertTrue(result.inlineStyles.isEmpty())
        assertEquals(1, result.headStyles.size)
    }

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
    fun `themeUtilityRules generates rules for all required tokens`() {
        val rules = themeUtilityRules(testTheme)
        assertTrue(rules.isNotEmpty())
    }

    @Test
    fun `bg-primary resolves via theme rules`() {
        val resolver = UtilityClassResolver(themeUtilityRules(testTheme))
        val result = resolver.resolve(setOf("bg-primary"))
        assertEquals(mapOf(BACKGROUND_COLOR to "#1B7C96"), result.inlineStyles["bg-primary"])
    }

    @Test
    fun `text-foreground resolves via theme rules`() {
        val resolver = UtilityClassResolver(themeUtilityRules(testTheme))
        val result = resolver.resolve(setOf("text-foreground"))
        assertEquals(mapOf(COLOR to "#1D2129"), result.inlineStyles["text-foreground"])
    }

    @Test
    fun `bg-surface resolves via theme rules`() {
        val resolver = UtilityClassResolver(themeUtilityRules(testTheme))
        val result = resolver.resolve(setOf("bg-surface"))
        assertEquals(mapOf(BACKGROUND_COLOR to "#fdfdfd"), result.inlineStyles["bg-surface"])
    }

    @Test
    fun `text-muted resolves via theme rules`() {
        val resolver = UtilityClassResolver(themeUtilityRules(testTheme))
        val result = resolver.resolve(setOf("text-muted"))
        assertEquals(mapOf(COLOR to "#999999"), result.inlineStyles["text-muted"])
    }

    @Test
    fun `border-default resolves via theme rules`() {
        val resolver = UtilityClassResolver(themeUtilityRules(testTheme))
        val result = resolver.resolve(setOf("border-default"))
        assertEquals(mapOf("border-color" to "#dddddd"), result.inlineStyles["border-default"])
    }

    @Test
    fun `optional success token generates rules when present`() {
        val theme = testTheme.copy(success = ColorToken("#10B981", "#10B981"))
        val resolver = UtilityClassResolver(themeUtilityRules(theme))
        val result = resolver.resolve(setOf("bg-success"))
        assertEquals(mapOf(BACKGROUND_COLOR to "#10B981"), result.inlineStyles["bg-success"])
    }

    @Test
    fun `optional success token absent does not generate rules`() {
        val resolver = UtilityClassResolver(themeUtilityRules(testTheme))
        val result = resolver.resolve(setOf("bg-success"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `combined base and theme rules resolve correctly`() {
        val rules = emailUtilityRules() + themeUtilityRules(testTheme)
        val resolver = UtilityClassResolver(rules)
        val result =
            resolver.resolve(setOf("d-block", "bg-primary", "text-center", "text-foreground"))
        assertEquals(4, result.inlineStyles.size)
        assertEquals(mapOf(DISPLAY to "block"), result.inlineStyles["d-block"])
        assertEquals(mapOf(BACKGROUND_COLOR to "#1B7C96"), result.inlineStyles["bg-primary"])
        assertEquals(mapOf(TEXT_ALIGN to "center"), result.inlineStyles["text-center"])
        assertEquals(mapOf(COLOR to "#1D2129"), result.inlineStyles["text-foreground"])
    }
}
