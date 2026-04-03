package com.arvindrachuri.ehtml.compiler

import com.arvindrachuri.ehtml.ast.TextNode
import com.arvindrachuri.ehtml.compiler.transforms.DocumentShellPass
import kotlin.test.Test

class DocumentShellPassTest {

    @Test
    fun `wraps email content in default email html scaffolding`() {
        val msoSpecificXml =
            """<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->"""
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body))
        val html = HtmlEmitter.emit(document)
        assert(html.startsWith("<!DOCTYPE html>"))
        assert("<html" in html && "</html>" in html)
        assert("</html>" in html)
        assert(msoSpecificXml in html)
        assert("<body" in html && "</body>" in html)
        assert("<head>" in html && "</head>" in html)
        assert("Hello" in html)
    }

    @Test
    fun `sets custom title when passed`() {
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body), title = "Welcome Email")
        val html = HtmlEmitter.emit(document)
        assert("""<title>Welcome Email</title>""" in html)
    }

    @Test
    fun `title is not present when not passed`() {
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body))
        val html = HtmlEmitter.emit(document)
        assert("""<title>""" !in html && """</title>""" !in html)
    }

    @Test
    fun `supports custom language`() {
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body), lang = "th")
        val html = HtmlEmitter.emit(document)
        assert("""<html lang="th"""" in html)
    }

    @Test
    fun `supports custom background colors in body`() {
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body), backgroundColor = "#e6e6e6")
        val html = HtmlEmitter.emit(document)
        assert("background-color:#e6e6e6" in html)
    }

    @Test
    fun `email content always appears within body tags`() {
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body), lang = "th", backgroundColor = "#e6e6e6")
        val html = HtmlEmitter.emit(document)
        val bodyStart = html.indexOf("<body")
        val bodyEnd = html.indexOf("</body>")
        val contentPos = html.indexOf("Hello")
        assert(contentPos in (bodyStart + 1)..<bodyEnd)
    }

    @Test
    fun `include required namespaces in html tag`() {
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body))
        val html = HtmlEmitter.emit(document)
        assert("""xmlns="http://www.w3.org/1999/xhtml"""" in html)
        assert("""xmlns:v="urn:schemas-microsoft-com:vml"""" in html)
        assert("""xmlns:o="urn:schemas-microsoft-com:office:office"""" in html)
    }

    @Test
    fun `contain the set meta tags`() {
        val body = TextNode("Hello")
        val document = DocumentShellPass.run(listOf(body))
        val html = HtmlEmitter.emit(document)
        assert("""<meta charset="utf-8" />""" in html)
        assert("""<meta name="viewport" content="width=device-width, initial-scale=1" />""" in html)
        assert("""<meta http-equiv="X-UA-Compatible" content="IE=edge" />""" in html)
    }
}
