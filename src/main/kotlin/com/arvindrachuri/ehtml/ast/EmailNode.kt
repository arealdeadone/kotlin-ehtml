package com.arvindrachuri.ehtml.ast

import com.arvindrachuri.ehtml.utils.Colors

sealed interface EmailNode

data class ElementNode(
    val tag: String,
    val attributes: Map<String, String> = emptyMap(),
    val styles: Map<String, String> = emptyMap(),
    val children: List<EmailNode> = emptyList(),
) : EmailNode

data class TextNode(val value: String) : EmailNode

data class RawHtmlNode(val value: String) : EmailNode

data class ContainerNode(
    val width: Int = 600,
    val styles: Map<String, String> = emptyMap(),
    val children: List<EmailNode> = emptyList(),
) : EmailNode

data class RowNode(
    val styles: Map<String, String> = emptyMap(),
    val children: List<EmailNode> = emptyList(),
) : EmailNode

data class ColumnNode(
    val widthPercent: Int? = null,
    val styles: Map<String, String> = emptyMap(),
    val children: List<EmailNode> = emptyList(),
) : EmailNode

data class EmailDocumentNode(
    val title: String,
    val lang: String = "en",
    val headStyles: List<CssNode> = emptyList(),
    val backgroundColor: String = Colors.WHITE.value,
    val children: List<EmailNode> = emptyList(),
) : EmailNode
