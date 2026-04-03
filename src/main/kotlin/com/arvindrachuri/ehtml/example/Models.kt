package com.arvindrachuri.ehtml.example

data class Plan(
    val title: String,
    val description: String,
    val details: String,
    val imageUrl: String,
    val ctaText: String,
    val ctaUrl: String,
)

data class Feature(val iconUrl: String, val title: String, val description: String)

data class Subscriber(val name: String, val teamName: String, val workspaceId: String)

data class EmailConfig(
    val logoUrl: String,
    val heroImageUrl: String,
    val primaryCtaUrl: String,
    val unsubscribeUrl: String,
)
