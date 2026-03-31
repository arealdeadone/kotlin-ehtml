package com.arvindrachuri.ehtml.utils

object HtmlTagAttributes {
    enum class Img(val value: String) {
        Align("src"),
        Alt("alt"),
    }

    enum class A(val value: String) {
        Href("href")
    }

    enum class Table(val value: String) {
        Align("align"),
        Border("border"),
        Cellpadding("cellpadding"),
        Cellspacing("cellspacing"),
        Role("role"),
        Width("width"),
    }
}
