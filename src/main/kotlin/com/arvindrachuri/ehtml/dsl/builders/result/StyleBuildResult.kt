package com.arvindrachuri.ehtml.dsl.builders.result

data class StyleBuildResult(
    val styles: Map<String, String>,
    val warnings: List<String>
)
