package com.arvindrachuri.ehtml.utils

import com.arvindrachuri.ehtml.utils.css.constants.HtmlTagSelector
import kotlin.test.Test

class HtmlTagSelectorSyncTest {

    @Test
    fun `HtmlTagSelector covers all HtmlElementTag constants`() {
        val elementTags =
            HtmlElementTag::class
                .java
                .declaredFields
                .filter { it.type == String::class.java }
                .map { it.get(null) as String }
                .toSet()

        val containerTags =
            HtmlContainerTag::class
                .java
                .declaredFields
                .filter { it.type == String::class.java }
                .map { it.get(null) as String }
                .toSet()

        val allTags = elementTags + containerTags

        val selectorTags = HtmlTagSelector.entries.map { it.selector }.toSet()

        val missing = allTags - selectorTags
        assert(missing.isEmpty()) { "HtmlTagSelector is missing entries for: $missing" }
    }
}
