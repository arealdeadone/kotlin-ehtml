package com.arvindrachuri.ehtml.utils

import com.arvindrachuri.ehtml.utils.HtmlElementTag.BR
import com.arvindrachuri.ehtml.utils.HtmlElementTag.HR
import com.arvindrachuri.ehtml.utils.HtmlElementTag.IMG
import com.arvindrachuri.ehtml.utils.HtmlElementTag.INPUT
import com.arvindrachuri.ehtml.utils.HtmlElementTag.LINK
import com.arvindrachuri.ehtml.utils.HtmlElementTag.META

object Constants {
    const val MSO_PASS_MARKER = "data-ehtml-origin"
    const val MSO_PASS_MARKED_CONTAINER = "container"
    val VOID_TAGS = setOf(BR, HR, IMG, INPUT, LINK, META)
}
