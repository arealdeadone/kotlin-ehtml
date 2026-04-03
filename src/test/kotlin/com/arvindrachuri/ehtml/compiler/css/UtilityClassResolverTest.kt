package com.arvindrachuri.ehtml.compiler.css

import com.arvindrachuri.ehtml.ast.CssMediaQuery
import com.arvindrachuri.ehtml.ast.CssRule
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.BACKGROUND_COLOR
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.COLOR
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.DISPLAY
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.FONT_SIZE
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.FONT_WEIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_BOTTOM
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_LEFT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_RIGHT
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.PADDING_TOP
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_ALIGN
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.TEXT_TRANSFORM
import com.arvindrachuri.ehtml.utils.css.constants.CssAttribute.WIDTH
import com.arvindrachuri.ehtml.utils.css.constants.Sizes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UtilityClassResolverTest {

    private val rules =
        listOf(
            rule("d", DISPLAY, keyword("block", "inline-block", "none")),
            rule("w", WIDTH, percentage(50, 100)),
            rule("p", PADDING, spacing(0, 4, 8, 12, 16, 20)),
            rule("px", listOf(PADDING_LEFT, PADDING_RIGHT), spacing(0, 4, 8, 12, 16)),
            rule("py", listOf(PADDING_TOP, PADDING_BOTTOM), spacing(0, 4, 8, 12, 16)),
            rule("text", TEXT_ALIGN, keyword("center", "left", "right")),
            rule(
                "text",
                FONT_SIZE,
                named("sm" to "12px", "base" to "14px", "lg" to "16px", "xl" to "18px"),
            ),
            rule("font", FONT_WEIGHT, keyword("bold", "normal")),
            rule("uppercase", TEXT_TRANSFORM, literal("uppercase")),
            rule("bg", BACKGROUND_COLOR, named("primary" to "#1B7C96", "surface" to "#fdfdfd")),
            rule("text", COLOR, named("foreground" to "#1D2129", "muted" to "#999999")),
        )

    private val resolver = UtilityClassResolver(rules)

    @Test
    fun `resolves keyword class to inline style`() {
        val result = resolver.resolve(setOf("d-block"))
        assertEquals(mapOf(DISPLAY to "block"), result.inlineStyles["d-block"])
    }

    @Test
    fun `resolves spacing class to px value`() {
        val result = resolver.resolve(setOf("p-16"))
        assertEquals(mapOf(PADDING to "16px"), result.inlineStyles["p-16"])
    }

    @Test
    fun `resolves percentage class`() {
        val result = resolver.resolve(setOf("w-100"))
        assertEquals(mapOf(WIDTH to "100%"), result.inlineStyles["w-100"])
    }

    @Test
    fun `resolves named class`() {
        val result = resolver.resolve(setOf("text-sm"))
        assertEquals(mapOf(FONT_SIZE to "12px"), result.inlineStyles["text-sm"])
    }

    @Test
    fun `resolves literal class with no modifier`() {
        val result = resolver.resolve(setOf("uppercase"))
        assertEquals(mapOf(TEXT_TRANSFORM to "uppercase"), result.inlineStyles["uppercase"])
    }

    @Test
    fun `resolves theme color class`() {
        val result = resolver.resolve(setOf("bg-primary"))
        assertEquals(mapOf(BACKGROUND_COLOR to "#1B7C96"), result.inlineStyles["bg-primary"])
    }

    @Test
    fun `resolves multi-property rule to all css properties`() {
        val result = resolver.resolve(setOf("px-8"))
        assertEquals(
            mapOf(PADDING_LEFT to "8px", PADDING_RIGHT to "8px"),
            result.inlineStyles["px-8"],
        )
    }

    @Test
    fun `resolves py to top and bottom padding`() {
        val result = resolver.resolve(setOf("py-12"))
        assertEquals(
            mapOf(PADDING_TOP to "12px", PADDING_BOTTOM to "12px"),
            result.inlineStyles["py-12"],
        )
    }

    @Test
    fun `text-center resolves to text-align not font-size`() {
        val result = resolver.resolve(setOf("text-center"))
        assertEquals(mapOf(TEXT_ALIGN to "center"), result.inlineStyles["text-center"])
    }

    @Test
    fun `text-sm resolves to font-size not text-align`() {
        val result = resolver.resolve(setOf("text-sm"))
        assertEquals(mapOf(FONT_SIZE to "12px"), result.inlineStyles["text-sm"])
    }

    @Test
    fun `text-foreground resolves to color`() {
        val result = resolver.resolve(setOf("text-foreground"))
        assertEquals(mapOf(COLOR to "#1D2129"), result.inlineStyles["text-foreground"])
    }

    @Test
    fun `text prefix resolves all three scale types correctly`() {
        val result = resolver.resolve(setOf("text-center", "text-lg", "text-muted"))
        assertEquals(mapOf(TEXT_ALIGN to "center"), result.inlineStyles["text-center"])
        assertEquals(mapOf(FONT_SIZE to "16px"), result.inlineStyles["text-lg"])
        assertEquals(mapOf(COLOR to "#999999"), result.inlineStyles["text-muted"])
    }

    @Test
    fun `resolves multiple classes in a single call`() {
        val result = resolver.resolve(setOf("d-block", "p-8", "text-center"))
        assertEquals(mapOf(DISPLAY to "block"), result.inlineStyles["d-block"])
        assertEquals(mapOf(PADDING to "8px"), result.inlineStyles["p-8"])
        assertEquals(mapOf(TEXT_ALIGN to "center"), result.inlineStyles["text-center"])
    }

    @Test
    fun `resolves mix of different scale types`() {
        val result =
            resolver.resolve(
                setOf("d-none", "w-50", "p-4", "text-xl", "font-bold", "uppercase", "bg-surface")
            )
        assertEquals(7, result.inlineStyles.size)
    }

    @Test
    fun `unknown class name is silently skipped`() {
        val result = resolver.resolve(setOf("my-custom-class"))
        assertTrue(result.inlineStyles.isEmpty())
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `invalid modifier for known prefix is skipped`() {
        val result = resolver.resolve(setOf("d-flex"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `spacing out of range is skipped`() {
        val result = resolver.resolve(setOf("p-99"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `non-numeric spacing modifier is skipped`() {
        val result = resolver.resolve(setOf("p-foo"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `percentage out of range is skipped`() {
        val result = resolver.resolve(setOf("w-75"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `literal with modifier is skipped`() {
        val result = resolver.resolve(setOf("uppercase-bold"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `mix of valid and invalid classes resolves only valid ones`() {
        val result = resolver.resolve(setOf("d-block", "unknown", "p-99", "w-100"))
        assertEquals(2, result.inlineStyles.size)
        assertTrue("d-block" in result.inlineStyles)
        assertTrue("w-100" in result.inlineStyles)
    }

    @Test
    fun `empty class set returns empty result`() {
        val result = resolver.resolve(emptySet())
        assertTrue(result.inlineStyles.isEmpty())
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `sm prefix resolves to media query`() {
        val result = resolver.resolve(setOf("sm-d-block"))
        assertTrue(result.inlineStyles.isEmpty())
        assertEquals(1, result.headStyles.size)
        val mediaQuery = result.headStyles[0] as CssMediaQuery
        assertEquals("max-width: 600px", mediaQuery.condition)
    }

    @Test
    fun `sm prefix uses original class name as selector`() {
        val result = resolver.resolve(setOf("sm-d-block"))
        val mediaQuery = result.headStyles[0] as CssMediaQuery
        val rule = mediaQuery.rules[0] as CssRule
        assertEquals(".sm-d-block", rule.selector)
    }

    @Test
    fun `sm prefix adds important to values`() {
        val result = resolver.resolve(setOf("sm-d-block"))
        val mediaQuery = result.headStyles[0] as CssMediaQuery
        val rule = mediaQuery.rules[0] as CssRule
        assertEquals("block !important", rule.styles[DISPLAY])
    }

    @Test
    fun `multiple sm classes grouped in single media query`() {
        val result = resolver.resolve(setOf("sm-d-block", "sm-text-center", "sm-w-100"))
        assertEquals(1, result.headStyles.size)
        val mediaQuery = result.headStyles[0] as CssMediaQuery
        assertEquals(3, mediaQuery.rules.size)
    }

    @Test
    fun `sm prefix with spacing rule`() {
        val result = resolver.resolve(setOf("sm-p-8"))
        val mediaQuery = result.headStyles[0] as CssMediaQuery
        val rule = mediaQuery.rules[0] as CssRule
        assertEquals(".sm-p-8", rule.selector)
        assertEquals("8px !important", rule.styles[PADDING])
    }

    @Test
    fun `sm prefix with multi-property rule`() {
        val result = resolver.resolve(setOf("sm-px-4"))
        val mediaQuery = result.headStyles[0] as CssMediaQuery
        val rule = mediaQuery.rules[0] as CssRule
        assertEquals("4px !important", rule.styles[PADDING_LEFT])
        assertEquals("4px !important", rule.styles[PADDING_RIGHT])
    }

    @Test
    fun `sm prefix with invalid modifier is skipped`() {
        val result = resolver.resolve(setOf("sm-d-flex"))
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `sm prefix with unknown rule is skipped`() {
        val result = resolver.resolve(setOf("sm-unknown"))
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `base and responsive classes split correctly`() {
        val result = resolver.resolve(setOf("d-block", "sm-d-none", "p-4", "sm-text-center"))
        assertEquals(2, result.inlineStyles.size)
        assertTrue("d-block" in result.inlineStyles)
        assertTrue("p-4" in result.inlineStyles)
        assertEquals(1, result.headStyles.size)
        val mediaQuery = result.headStyles[0] as CssMediaQuery
        assertEquals(2, mediaQuery.rules.size)
    }

    @Test
    fun `base classes do not get important`() {
        val result = resolver.resolve(setOf("d-block"))
        val styles = result.inlineStyles["d-block"]!!
        assertEquals("block", styles[DISPLAY])
    }

    @Test
    fun `multiple breakpoints produce separate media queries`() {
        val multiBreakpointResolver =
            UtilityClassResolver(
                rules,
                breakpoints =
                    mapOf(Sizes.Small to "max-width: 600px", Sizes.ExtraSmall to "max-width: 430px"),
            )
        val result = multiBreakpointResolver.resolve(setOf("sm-d-block", "xs-d-none"))
        assertEquals(2, result.headStyles.size)
        val conditions = result.headStyles.map { (it as CssMediaQuery).condition }.toSet()
        assertTrue("max-width: 600px" in conditions)
        assertTrue("max-width: 430px" in conditions)
    }

    @Test
    fun `multiple classes in same breakpoint grouped together`() {
        val multiBreakpointResolver =
            UtilityClassResolver(
                rules,
                breakpoints =
                    mapOf(Sizes.Small to "max-width: 600px", Sizes.ExtraSmall to "max-width: 430px"),
            )
        val result = multiBreakpointResolver.resolve(setOf("sm-d-block", "sm-w-100", "xs-d-none"))
        val smQuery =
            result.headStyles
                .map { it as CssMediaQuery }
                .first { it.condition == "max-width: 600px" }
        val xsQuery =
            result.headStyles
                .map { it as CssMediaQuery }
                .first { it.condition == "max-width: 430px" }
        assertEquals(2, smQuery.rules.size)
        assertEquals(1, xsQuery.rules.size)
    }

    @Test
    fun `duplicate class names in set produce single entry`() {
        val result = resolver.resolve(setOf("d-block", "d-block"))
        assertEquals(1, result.inlineStyles.size)
    }

    @Test
    fun `class name that is just a prefix with no modifier`() {
        val result = resolver.resolve(setOf("d"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `class name that is prefix with trailing dash`() {
        val result = resolver.resolve(setOf("d-"))
        assertTrue(result.inlineStyles.isEmpty())
    }

    @Test
    fun `prefix that is substring of another prefix does not cross-match`() {
        val result = resolver.resolve(setOf("p-4"))
        assertEquals(mapOf(PADDING to "4px"), result.inlineStyles["p-4"])
        assertTrue("px" !in (result.inlineStyles["p-4"]?.keys ?: emptySet()))
    }

    @Test
    fun `first matching rule wins for same prefix`() {
        val result = resolver.resolve(setOf("text-center"))
        val styles = result.inlineStyles["text-center"]!!
        assertTrue(TEXT_ALIGN in styles)
        assertTrue(FONT_SIZE !in styles)
        assertTrue(COLOR !in styles)
    }

    @Test
    fun `w-auto resolves via percentage scale`() {
        val result = resolver.resolve(setOf("w-auto"))
        assertTrue(
            result.inlineStyles.isEmpty() || result.inlineStyles["w-auto"]?.get(WIDTH) == "auto"
        )
    }

    @Test
    fun `resolver with no rules returns empty for all classes`() {
        val emptyResolver = UtilityClassResolver(emptyList())
        val result = emptyResolver.resolve(setOf("d-block", "p-4"))
        assertTrue(result.inlineStyles.isEmpty())
        assertTrue(result.headStyles.isEmpty())
    }

    @Test
    fun `sm prefix does not match class name that starts with sm but has no dash`() {
        val result = resolver.resolve(setOf("small"))
        assertTrue(result.inlineStyles.isEmpty())
        assertTrue(result.headStyles.isEmpty())
    }
}
