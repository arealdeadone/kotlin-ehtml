package com.arvindrachuri.ehtml.utils

object HtmlTagAttributes {

    const val ID = "id"
    const val CLASS = "class"

    enum class Img(val value: String) {
        Src("src"),
        Alt("alt"),
        Height("height"),
        Width("width"),
    }

    enum class A(val value: String) {
        Href("href"),
        Id(HtmlTagAttributes.ID),
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
