package com.arvindrachuri.ehtml.dsl.builders.result

import com.arvindrachuri.ehtml.ast.CssNode

data class HeadBuildResult(val title: String, val styles: List<CssNode>)
