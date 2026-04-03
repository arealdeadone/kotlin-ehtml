package com.arvindrachuri.ehtml.example

import kotlin.test.Test

class ExampleEmailTest {

    private val subscriber =
        Subscriber(name = "Jordan", teamName = "Northstar Product Team", workspaceId = "WS-48219")

    private val config =
        EmailConfig(
            logoUrl = "https://placehold.co/140x36/1B7C96/white?text=CloudSync",
            heroImageUrl = "https://placehold.co/290x288/1B7C96/white?text=Features",
            primaryCtaUrl = "https://example.com/cloudsync/upgrade",
            unsubscribeUrl = "https://example.com/cloudsync/unsubscribe",
        )

    private val features =
        listOf(
            Feature(
                iconUrl = "https://placehold.co/45x45/1B7C96/white?text=A",
                title = "AUTOMATED WORKFLOWS",
                description = "Trigger recurring tasks and approvals.",
            ),
            Feature(
                iconUrl = "https://placehold.co/45x45/1B7C96/white?text=R",
                title = "REAL-TIME REPORTS",
                description = "Track sprint health from one dashboard.",
            ),
            Feature(
                iconUrl = "https://placehold.co/45x45/1B7C96/white?text=C",
                title = "COLLABORATION HUB",
                description = "Keep docs, updates, and decisions in sync.",
            ),
        )

    private val plans =
        listOf(
            Plan(
                title = "Starter",
                description = "For lean teams launching structured delivery.",
                details = "Up to 10 members and 100GB storage",
                imageUrl = "https://placehold.co/290x288/1B7C96/white?text=Starter",
                ctaText = "CHOOSE STARTER",
                ctaUrl = "https://example.com/cloudsync/plans/starter",
            ),
            Plan(
                title = "Growth",
                description = "For scaling teams with governance needs.",
                details = "Advanced reporting and role controls",
                imageUrl = "https://placehold.co/290x288/1B7C96/white?text=Growth",
                ctaText = "CHOOSE GROWTH",
                ctaUrl = "https://example.com/cloudsync/plans/growth",
            ),
        )

    @Test
    fun `produces valid HTML document`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert(html.startsWith("<!DOCTYPE html>"))
        assert("</html>" in html)
    }

    @Test
    fun `contains preheader with preview text`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("CloudSync just launched new productivity features" in html)
        assert("display: none" in html)
    }

    @Test
    fun `contains title`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("<title>CloudSync SaaS Product Update</title>" in html)
    }

    @Test
    fun `contains embedded style block`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("<style type=\"text/css\">" in html)
        assert("@media" in html)
    }

    @Test
    fun `contains CSS reset styles`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("border-collapse: collapse !important" in html)
        assert("-webkit-text-size-adjust: 100%" in html)
    }

    @Test
    fun `contains responsive media query`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("@media (max-width: 630px)" in html)
        assert("width: 100% !important" in html)
    }

    @Test
    fun `contains dark mode styles`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("@media (prefers-color-scheme: dark)" in html)
        assert("darkmode-text" in html)
    }

    @Test
    fun `contains MSO ghost tables`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("<!--[if mso]>" in html)
        assert("<![endif]-->" in html)
    }

    @Test
    fun `contains feature content`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("Why teams choose CloudSync" in html)
        assert("AUTOMATED WORKFLOWS" in html)
        assert("REAL-TIME REPORTS" in html)
        assert("COLLABORATION HUB" in html)
    }

    @Test
    fun `renders plan cards`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("Starter" in html)
        assert("Growth" in html)
        assert("CHOOSE STARTER" in html)
        assert("CHOOSE GROWTH" in html)
    }

    @Test
    fun `contains bottom CTA`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("START YOUR FREE TRIAL" in html)
        assert("priority onboarding" in html)
    }

    @Test
    fun `contains disclaimer text`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("Plan limits, storage allocations" in html)
        assert("Terms of Service" in html)
    }

    @Test
    fun `contains footer content and unsubscribe`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("CloudSync Labs, All rights reserved" in html)
        assert("Unsubscribe from this email" in html)
        assert("cloudsync/unsubscribe" in html)
    }

    @Test
    fun `data driven rendering produces card per plan`() {
        val singlePlan = listOf(plans[0])
        val htmlSingle = exampleSaasPromotionalEmail(subscriber, config, singlePlan, features)
        val singleCount = "CHOOSE STARTER".toRegex().findAll(htmlSingle).count()
        assert(singleCount == 1)

        val htmlMulti = exampleSaasPromotionalEmail(subscriber, config, plans, features)
        assert("CHOOSE STARTER" in htmlMulti)
        assert("CHOOSE GROWTH" in htmlMulti)
    }

    @Test
    fun `empty plans list produces no plan cards`() {
        val html = exampleSaasPromotionalEmail(subscriber, config, emptyList(), features)
        assert("CHOOSE STARTER" !in html)
        assert("CHOOSE GROWTH" !in html)
        assert("START YOUR FREE TRIAL" in html)
    }
}
