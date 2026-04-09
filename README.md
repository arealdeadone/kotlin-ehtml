# kotlin-ehtml

A Kotlin DSL for composing email HTML with a compiler pipeline that handles the hard parts for you.

[![Maven Central](https://img.shields.io/maven-central/v/com.arvindrachuri/kotlin-ehtml)](https://central.sonatype.com/artifact/com.arvindrachuri/kotlin-ehtml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)](https://kotlinlang.org)

## What is kotlin-ehtml?

`kotlin-ehtml` is an email compiler disguised as a DSL. You write semantic, composable Kotlin code. The compiler lowers it into bulletproof HTML compatible with Gmail, Outlook, Apple Mail, and Yahoo -- handling table layouts, inline styles, Outlook conditional comments, and CSS optimization automatically.

No templates. No string concatenation. Just Kotlin with full IDE support, type checking, and refactoring.

## Quick Start

```kotlin
import com.arvindrachuri.ehtml.dsl.email

val html = email {
    head {
        title = "Welcome"
        style {
            tagSelector(Table, Td) { margin = "0"; padding = "0" }
        }
    }
    single {
        width = 600
        className = "text-center"
        h1 {
            className = "text-2xl font-bold text-primary"
            +"Welcome aboard!"
        }
        spacer(20)
        p {
            className = "text-base text-foreground"
            +"We're excited to have you."
        }
        spacer(20)
        button("Get Started", "https://example.com", "cta-1") {
            className = "bg-primary text-surface font-bold uppercase rounded-md"
        }
    }
}
```

`html` is a `String` containing a complete, ready-to-send HTML email document.

## Installation

Available on Maven Central. No special repository configuration needed.

```kotlin
dependencies {
    implementation("com.arvindrachuri:kotlin-ehtml:1.0.0-beta-1")
}
```

## Features

### Layout System

Table-based email layout primitives that the compiler lowers to nested `<table>/<tr>/<td>` structures with all the structural defaults email clients expect.

```kotlin
container {
    width = 650
    row {
        column { width = "50%"; p { +"Left" } }
        column { width = "50%"; p { +"Right" } }
    }
}
```

- `container {}` / `row {}` / `column {}` -- Table-based email grid
- `single {}` -- Shorthand for `container { row { column { ... } } }`
- `spacer(height)` -- Vertical spacing primitive

### HTML Elements

All email-compatible tags are available as typed DSL functions:

`div`, `p`, `h1`--`h6`, `span`, `strong`, `b`, `em`, `a`, `img`, `hr`, `br`, `ul`, `ol`, `li`, `blockquote`, `i`, `u`, `s`, `sup`, `sub`, `pre`

Specialized primitives:

- `button(text, href, id) {}` -- Email-safe CTA as a styled `<a>` with sensible defaults
- `img(src, alt, width?, height?)` -- Typed width/height with dual HTML attribute + CSS style output
- `preheader(text)` -- Hidden preview text with proper hiding styles

### Type-Safe CSS

Inline styles, `<style>` blocks, and selectors are all typed:

```kotlin
head {
    title = "My Email"
    style {
        tagSelector(Table, Td) { margin = "0"; padding = "0" }
        classSelector("card") { borderRadius = "12px"; overflow = "hidden" }
        idSelector("hero") { backgroundColor = "#1B7C96" }
        media("(max-width: 600px)") {
            classSelector("sm-stack") { display = "block !important" }
        }
        mso {
            classSelector("fallback") { width = "600px" }
        }
    }
}
```

- `style {}` on any element for inline CSS with typed properties
- `tagSelector()`, `classSelector()`, `idSelector()` -- Type-safe CSS selectors
- `media(condition) {}` -- Responsive `@media` blocks
- `mso {}` -- MSO conditional CSS (`<!--[if mso]><style>`)
- `important {}` / `"value".important()` -- Scoped `!important`

### Utility Class System

Tailwind-inspired utility classes that resolve on demand. Only used classes generate CSS.

```kotlin
h1 {
    className = "text-2xl font-bold text-center p-16 sm-text-left"
    +"Hello"
}
```

| Category | Examples |
|---|---|
| Display | `d-block`, `d-none`, `d-inline-block` |
| Width | `w-50`, `w-100`, `w-auto` |
| Padding | `p-0` -- `p-40`, `px-*`, `py-*`, `pt-*`, `pr-*`, `pb-*`, `pl-*` |
| Margin | `m-0` -- `m-40`, `mx-*`, `my-*`, `mt-*`, `mr-*`, `mb-*`, `ml-*` |
| Typography | `text-center`, `text-left`, `text-right` |
| Font size | `text-xs`, `text-sm`, `text-base`, `text-lg`, `text-xl`, `text-2xl`, `text-3xl`, `text-4xl` |
| Font weight | `font-bold`, `font-normal` |
| Line height | `leading-none`, `leading-tight`, `leading-normal`, `leading-relaxed` |
| Text decoration | `underline`, `no-underline` |
| Text transform | `uppercase`, `lowercase`, `capitalize` |
| Border radius | `rounded`, `rounded-sm`, `rounded-md`, `rounded-lg`, `rounded-full`, `rounded-none` |
| Overflow | `overflow-hidden`, `overflow-visible`, `overflow-auto` |
| Vertical align | `align-top`, `align-middle`, `align-bottom`, `align-baseline` |
| Responsive | Prefix any utility with `sm-` for `@media (max-width: 600px)` |

### Theme System

Define semantic color tokens once, use them as utility classes everywhere.

```kotlin
val html = email {
    theme = EmailTheme(
        primary = ColorToken("#1B7C96", "#1B7C96"),
        secondary = ColorToken("#0D3B66", "#0D3B66"),
        background = ColorToken("#F3F7F9", "#F3F7F9"),
        surface = ColorToken("#FFF", "#272623"),
        surfaceAlt = ColorToken("#EAF2F5", "#1A1A1A"),
        foreground = ColorToken("#1D2129", "#f5f5f5"),
        mutedForeground = ColorToken("#999", "#f5f5f5"),
        border = "#D9E2EC",
    )

    single {
        h1 { className = "text-primary"; +"Themed heading" }
        p { className = "text-foreground bg-surface p-16"; +"Themed content" }
    }
}
```

Each `ColorToken(light, dark)` generates `bg-*` and `text-*` utility classes with automatic dark mode support via `@media (prefers-color-scheme: dark)`.

### Attributes

All builders support typed attribute access:

- `className` / `id` -- Available on Element, Container, Row, Column, and Single builders
- `attr(name, value)` / `attrs(vararg pairs)` -- Raw HTML attribute access

## Compiler Pipeline

The DSL produces an IR that passes through a series of compiler transforms before emitting HTML:

```
DSL
 |  build
 v
IR (EmailDocumentNode)
 |  UtilityInliningPass
 |  LayoutLoweringPass
 |  MsoConditionalPass
 |  DocumentShellPass
 |  CssTreeShakePass
 |  CssInliningPass
 |  CssTreeShakePass (second pass)
 |  CssOptimizationPass
 v
HTML string
```

| Pass | What it does |
|---|---|
| **UtilityInlining** | Resolves utility classes into inline styles; user-defined styles always win |
| **LayoutLowering** | Converts `container`/`row`/`column` to `table`/`tr`/`td` with structural defaults |
| **MsoConditional** | Wraps container tables in Outlook ghost table conditional comments |
| **DocumentShell** | Wraps content in `<!DOCTYPE>`, `<html>`, `<head>`, `<body>` with email meta tags |
| **CssTreeShake** | Removes unused class selectors from `<style>`, preserves safelist (`ExternalClass`, `ReadMsgBody`) |
| **CssInlining** | Inlines `<style>` rules into matching elements (class, id, tag, compound selectors); element inline styles always win; media queries and MSO conditionals stay in `<style>` |
| **CssOptimization** | Merges duplicate selectors, deduplicates identical style blocks, collapses shorthand properties (`padding-*` / `margin-*`), minifies hex colors (`#ffffff` -> `#fff`), trims zero units (`0px` -> `0`) |

The tree-shake pass runs twice -- once before inlining to prune early, once after to catch classes that were fully inlined and stripped.

## Full Example

Emails are composable Kotlin. Extract components as functions and build data-driven templates:

```kotlin
import com.arvindrachuri.ehtml.dsl.email
import com.arvindrachuri.ehtml.utils.css.models.ColorToken
import com.arvindrachuri.ehtml.utils.css.models.EmailTheme

fun productUpdateEmail(title: String, body: String, ctaUrl: String): String = email {
    lang = "en"
    backgroundColor = "#F3F7F9"
    theme = EmailTheme(
        primary = ColorToken("#1B7C96", "#1B7C96"),
        surface = ColorToken("#FFF", "#272623"),
        foreground = ColorToken("#1D2129", "#f5f5f5"),
        mutedForeground = ColorToken("#999", "#f5f5f5"),
    )

    head {
        title = "Product Update"
        style {
            tagSelector(Table, Td) { margin = "0"; padding = "0" }
            media("(prefers-color-scheme: dark)") {
                classSelector("darkmode-bg") {
                    backgroundColor = "#13202A"
                }
            }
        }
    }

    preheader("New features just launched")

    container {
        width = 650
        className = "sm-w-100"
        row {
            column {
                className = "p-24"
                h1 {
                    className = "text-2xl font-bold text-primary"
                    +title
                }
                spacer(16)
                p {
                    className = "text-base text-foreground"
                    +body
                }
                spacer(20)
                button("Learn More", ctaUrl, "cta") {
                    className = "bg-primary text-surface font-bold uppercase rounded-md"
                }
            }
        }
    }
}
```

The output is a complete HTML document with inlined styles, Outlook conditionals, responsive breakpoints, and optimized CSS -- ready to send.

## Requirements

- Kotlin 1.9.20+
- JVM 17+

## License

MIT
