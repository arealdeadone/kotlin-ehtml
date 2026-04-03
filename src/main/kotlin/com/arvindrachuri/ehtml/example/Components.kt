package com.arvindrachuri.ehtml.example

import com.arvindrachuri.ehtml.dsl.builders.html.ColumnBuilder
import com.arvindrachuri.ehtml.dsl.builders.html.ContainerBuilder
import com.arvindrachuri.ehtml.utils.css.values.WhiteSpaceType

object Fonts {
    const val HEADING = "'Comfortaa', Helvetica, sans-serif"
    const val BODY = "'Nunito', Helvetica, sans-serif"
}

object Colors {
    const val PRIMARY = "#1B7C96"
    const val SECONDARY = "#2F96B4"
    const val BACKGROUND = "#F3F7F9"
    const val SURFACE = "#FFFFFF"
    const val SURFACE_ALT = "#EAF2F5"
    const val FOREGROUND = "#102A43"
    const val MUTED = "#627D98"
    const val DIVIDER = "#D9E2EC"
    const val DARK_SURFACE = "#13202A"
    const val DARK_TEXT = "#F5F7FA"
}

fun ContainerBuilder.header(logoUrl: String) {
    row {
        className = "bg-surface darkmode-bg"
        column {
            className = "text-center p-0"
            style { fontFamily = Fonts.BODY }
            img(src = logoUrl, alt = "CloudSync") { className = "d-inline w-100" }
        }
    }
}

fun ContainerBuilder.heroHeading(text: String) {
    row {
        column {
            className = "p-24"
            p {
                className = "text-3xl text-title-xl-mob font-bold text-primary text-center"
                style {
                    lineHeight = "1.35"
                    fontFamily = Fonts.HEADING
                }
                +text
            }
        }
    }
}

fun ContainerBuilder.heroBanner(
    subscriber: Subscriber,
    primaryCtaUrl: String,
    heroImageUrl: String,
) {
    row {
        className = "bg-surface rounded-lg darkmode-bg"
        column {
            widthPercent = 55
            className = "sm-d-block sm-w-100 p-24"
            style { fontFamily = Fonts.BODY }
            p {
                className = "text-sm text-foreground darkmode-text"
                strong { +"Team: " }
                +subscriber.teamName
                br()
                strong { +"Workspace ID: " }
                +subscriber.workspaceId
                br()
                br()
                +"Hi ${subscriber.name},"
                br()
                br()
                +"CloudSync is rolling out smarter planning and automation workflows so your team can ship faster with less manual work."
                br()
                br()
                +"Choose the plan that matches your growth stage and activate your upgrade in one click."
            }
            spacer(12)
            ctaButton("ACTIVATE YOUR PLAN", primaryCtaUrl, "cta-hero")
        }
        column {
            widthPercent = 45
            className = "sm-d-block sm-w-100 sm-text-center align-bottom p-16"
            img(src = heroImageUrl, alt = "CloudSync features", width = 290, height = 288) {
                className = "responsive"
            }
        }
    }
}

fun ColumnBuilder.ctaButton(text: String, url: String, buttonId: String = "cta") {
    button(text, url, buttonId) {
        className = "bg-primary text-surface font-bold text-base uppercase rounded-lg btn-primary"
        attr("target", "_blank")
        style {
            lineHeight = "14px"
            fontFamily = Fonts.BODY
            padding = "10px 14px"
        }
    }
}

fun ColumnBuilder.ctaButtonInverted(text: String, url: String, buttonId: String = "cta") {
    button(text, url, buttonId) {
        className = "bg-surface text-primary font-bold text-lg uppercase rounded-lg btn-primary"
        attr("target", "_blank")
        style {
            lineHeight = "16px"
            fontFamily = Fonts.BODY
            padding = "10px 14px"
        }
    }
}

fun ContainerBuilder.featuresSection(features: List<Feature>) {
    row {
        className = "bg-surface rounded-lg darkmode-bg"
        column {
            className = "p-24"
            p {
                className = "text-center text-xl font-bold text-primary"
                style { fontFamily = Fonts.HEADING }
                +"Why teams choose CloudSync"
            }
        }
    }
    row {
        className = "bg-surface darkmode-bg"
        style { borderRadius = "0 0 12px 12px" }
        features.forEach { feature ->
            column {
                widthPercent = 100 / features.size
                className = "sm-d-block sm-w-100 center-on-narrow align-top p-16"
                style { fontFamily = Fonts.BODY }
                img(src = feature.iconUrl, alt = feature.title, width = 45, height = 45)
                spacer(8)
                p {
                    className = "text-base font-bold uppercase text-primary"
                    +feature.title
                }
                spacer(8)
                p {
                    className = "text-sm text-foreground darkmode-text"
                    +feature.description
                }
            }
        }
    }
}

fun ContainerBuilder.planCard(plan: Plan) {
    row {
        className = "bg-surface rounded-lg overflow-hidden darkmode-bg"
        column {
            widthPercent = 30
            className = "sm-d-block sm-w-100 sm-text-center p-16 align-top"
            img(src = plan.imageUrl, alt = plan.title, width = 155) { className = "responsive" }
        }
        column {
            widthPercent = 70
            className = "sm-d-block sm-w-100 p-16"
            style {
                fontFamily = Fonts.BODY
                lineHeight = "1.6"
            }
            p {
                className = "text-sm text-foreground darkmode-text"
                span {
                    className = "text-xl font-bold"
                    style { fontFamily = Fonts.HEADING }
                    +plan.title
                }
                br()
                +plan.description
                br()
                strong { +"Includes: " }
                +plan.details
            }
            spacer(8)
            ctaButton(plan.ctaText, plan.ctaUrl)
        }
    }
}

fun ContainerBuilder.bottomCta(url: String) {
    row {
        className = "bg-primary rounded-lg"
        style { fontFamily = Fonts.BODY }
        column {
            className = "p-24"
            p {
                className = "text-lg text-surface text-center darkmode-text"
                +"Upgrade today and get priority onboarding plus advanced automation templates for your team."
            }
            br()
            ctaButtonInverted("START YOUR FREE TRIAL", url, "cta-bottom")
        }
    }
}

fun ContainerBuilder.disclaimer(text: String) {
    row {
        className = "bg-surface-alt rounded-lg darkmode-bg"
        style { fontFamily = Fonts.BODY }
        column {
            className = "p-24"
            p {
                className = "text-sm text-foreground text-left darkmode-text"
                +text
            }
        }
    }
}

fun ContainerBuilder.disclaimerWithIcon(iconUrl: String, text: String) {
    row {
        className = "bg-surface-alt darkmode-bg"
        style { borderRadius = "0 0 12px 12px" }
        column {
            widthPercent = 15
            className = "align-top p-16"
            img(src = iconUrl, alt = "Info", width = 37, height = 37)
        }
        column {
            widthPercent = 85
            className = "p-16"
            style { fontFamily = Fonts.BODY }
            p {
                className = "text-base text-foreground text-left darkmode-text"
                +text
            }
        }
    }
}

fun ContainerBuilder.footer(logoUrl: String, unsubscribeUrl: String) {
    row {
        column {
            hr {
                className = "m-0"
                style {
                    border = "none"
                    css("border-top", "1px solid ${Colors.DIVIDER}")
                }
            }
            spacer(20)
            p {
                className = "text-center text-muted text-sm darkmode-text"
                style {
                    fontFamily = Fonts.BODY
                    lineHeight = "1.6"
                }
                +"Copyright © CloudSync Labs, All rights reserved."
                br()
                +"You are receiving this email because you subscribed to CloudSync updates."
                br()
                +"CloudSync Labs, 240 Market Street, San Francisco, CA 94103"
            }
            spacer(16)
            p {
                className = "text-center text-sm"
                style {
                    fontFamily = Fonts.BODY
                    lineHeight = "2"
                }
                a(href = unsubscribeUrl) {
                    className = "underline text-muted"
                    attr("target", "_blank")
                    style { whiteSpace = WhiteSpaceType.Nowrap }
                    +"Unsubscribe from this email"
                }
            }
            spacer(20)
            p {
                className = "text-center"
                img(src = logoUrl, alt = "CloudSync") { className = "d-inline w-100 h-50" }
            }
            spacer(20)
        }
    }
}
