package com.arvindrachuri.ehtml.utils.css

import com.arvindrachuri.ehtml.utils.HtmlContainerTag
import com.arvindrachuri.ehtml.utils.HtmlElementTag

enum class HtmlTagSelector(val selector: String) {
    A(HtmlElementTag.A),
    B(HtmlElementTag.B),
    Br(HtmlElementTag.BR),
    Hr(HtmlElementTag.HR),
    Div(HtmlElementTag.DIV),
    Em(HtmlElementTag.EM),
    H1(HtmlElementTag.H1),
    H2(HtmlElementTag.H2),
    H3(HtmlElementTag.H3),
    H4(HtmlElementTag.H4),
    H5(HtmlElementTag.H5),
    H6(HtmlElementTag.H6),
    Img(HtmlElementTag.IMG),
    P(HtmlElementTag.P),
    Span(HtmlElementTag.SPAN),
    Strong(HtmlElementTag.STRONG),
    Table(HtmlContainerTag.TABLE),
    Tbody(HtmlContainerTag.TBODY),
    Tr(HtmlContainerTag.TR),
    Td(HtmlContainerTag.TD),
    Container(HtmlContainerTag.TABLE),
    Row(HtmlContainerTag.TR),
    Column(HtmlContainerTag.TD),
}
