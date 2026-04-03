package com.arvindrachuri.ehtml.example

import com.arvindrachuri.ehtml.dsl.email
import com.arvindrachuri.ehtml.utils.css.models.ColorToken
import com.arvindrachuri.ehtml.utils.css.models.EmailTheme

fun exampleSaasPromotionalEmail(
    subscriber: Subscriber,
    config: EmailConfig,
    plans: List<Plan>,
    features: List<Feature>,
): String = email {
    lang = "en"
    backgroundColor = Colors.BACKGROUND
    theme =
        EmailTheme(
            primary = ColorToken(Colors.PRIMARY, Colors.SECONDARY),
            secondary = ColorToken(Colors.SECONDARY, Colors.PRIMARY),
            background = ColorToken(Colors.BACKGROUND, Colors.BACKGROUND),
            surface = ColorToken(Colors.SURFACE, Colors.DARK_SURFACE),
            surfaceAlt = ColorToken(Colors.SURFACE_ALT, Colors.DARK_SURFACE),
            foreground = ColorToken(Colors.FOREGROUND, Colors.DARK_TEXT),
            mutedForeground = ColorToken(Colors.MUTED, Colors.DARK_TEXT),
            border = Colors.DIVIDER,
        )

    head {
        title = "CloudSync SaaS Product Update"
        style {
            emailResetStyles()
            darkModeStyles()
            responsiveStyles()
        }
    }

    preheader("CloudSync just launched new productivity features and plans for growing teams")

    container {
        width = 650
        className = "sm-w-100"
        header(config.logoUrl)
    }

    spacer(24)

    container {
        width = 650
        className = "sm-w-100"
        heroHeading("Plan smarter and deliver faster with CloudSync")
    }

    spacer(24)

    container {
        width = 650
        className = "sm-w-100"
        heroBanner(subscriber, config.primaryCtaUrl, config.heroImageUrl)
    }

    spacer(24)

    container {
        width = 650
        className = "sm-w-100"
        featuresSection(features)
    }

    spacer(20)

    plans.forEach { plan ->
        container {
            width = 650
            className = "sm-w-100"
            planCard(plan)
        }
        spacer(20)
    }

    container {
        width = 650
        className = "sm-w-100"
        bottomCta(config.primaryCtaUrl)
    }

    spacer(24)

    container {
        width = 650
        className = "sm-w-100"
        disclaimer(
            "*Plan limits, storage allocations, and add-on pricing depend on your selected CloudSync tier and current contract terms."
        )
        disclaimerWithIcon(
            "https://placehold.co/37x37/1B7C96/white?text=i",
            "By clicking any call-to-action in this message, you agree to CloudSync's Terms of Service and acknowledge our data processing policy.",
        )
    }

    spacer(24)

    container {
        width = 650
        className = "sm-w-100"
        footer(config.logoUrl, config.unsubscribeUrl)
    }
}
