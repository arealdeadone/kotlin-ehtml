package com.arvindrachuri.ehtml

import com.arvindrachuri.ehtml.example.EmailConfig
import com.arvindrachuri.ehtml.example.Feature
import com.arvindrachuri.ehtml.example.Plan
import com.arvindrachuri.ehtml.example.Subscriber
import com.arvindrachuri.ehtml.example.exampleSaasPromotionalEmail
import java.io.File

fun main() {
    val subscriber =
        Subscriber(name = "Jordan", teamName = "Northstar Product Team", workspaceId = "WS-48219")

    val config =
        EmailConfig(
            logoUrl = "https://placehold.co/140x36/1B7C96/white?text=CloudSync",
            heroImageUrl = "https://placehold.co/290x288/1B7C96/white?text=Features",
            primaryCtaUrl = "https://example.com/cloudsync/upgrade",
            unsubscribeUrl = "https://example.com/cloudsync/unsubscribe",
        )

    val features =
        listOf(
            Feature(
                iconUrl = "https://placehold.co/45x45/1B7C96/white?text=A",
                title = "AUTOMATED WORKFLOWS",
                description =
                    "Trigger recurring tasks, reminders, and approvals without manual follow-up.",
            ),
            Feature(
                iconUrl = "https://placehold.co/45x45/1B7C96/white?text=R",
                title = "REAL-TIME REPORTS",
                description =
                    "Track deadlines, sprint health, and team capacity from a single dashboard.",
            ),
            Feature(
                iconUrl = "https://placehold.co/45x45/1B7C96/white?text=C",
                title = "COLLABORATION HUB",
                description =
                    "Centralize docs, comments, and decisions so every stakeholder stays aligned.",
            ),
        )

    val plans =
        listOf(
            Plan(
                title = "Starter",
                description = "For lean teams launching structured project delivery.",
                details = "Up to 10 members, 100GB storage, timeline views, basic automation",
                imageUrl = "https://placehold.co/290x288/1B7C96/white?text=Starter",
                ctaText = "CHOOSE STARTER",
                ctaUrl = "https://example.com/cloudsync/plans/starter",
            ),
            Plan(
                title = "Growth",
                description = "For scaling teams that need deeper visibility and governance.",
                details = "Unlimited projects, advanced reporting, SLA support, role controls",
                imageUrl = "https://placehold.co/290x288/1B7C96/white?text=Growth",
                ctaText = "CHOOSE GROWTH",
                ctaUrl = "https://example.com/cloudsync/plans/growth",
            ),
            Plan(
                title = "Enterprise",
                description = "For organizations requiring security, scale, and dedicated success.",
                details = "SSO/SAML, custom retention policies, audit logs, onboarding partner",
                imageUrl = "https://placehold.co/290x288/1B7C96/white?text=Enterprise",
                ctaText = "CONTACT SALES",
                ctaUrl = "https://example.com/cloudsync/plans/enterprise",
            ),
        )

    val html = exampleSaasPromotionalEmail(subscriber, config, plans, features)
    File("example.dsl.html").writeText(html)
    println(html)
}
