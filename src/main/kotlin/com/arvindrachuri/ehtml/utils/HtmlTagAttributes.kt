package com.arvindrachuri.ehtml.utils

object HtmlTagAttributes {
    enum class Img(val value: String) {
        SRC("src"),
        ALT("alt"),
    }

    enum class A(val value: String) {
        HREF("href")
    }
}
