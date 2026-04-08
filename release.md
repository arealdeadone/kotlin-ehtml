# kotlin-ehtml v0.1.0-alpha

A Kotlin DSL for composing email-safe HTML. Write type-safe, component-based email templates that compile to bulletproof HTML compatible with Gmail, Outlook, Apple Mail, and Yahoo.

## Highlights

- **Kotlin DSL** — No templates, no string concatenation. Emails are plain Kotlin code with full IDE support, type checking, and refactoring.
- **Tailwind-like utility classes** — `className = "p-16 text-center font-bold sm-d-block"` with automatic inlining and responsive `@media` generation.
- **Theme system** — Define semantic color tokens once, use `bg-primary`, `text-foreground`, `text-muted` everywhere.
- **Compiler pipeline** — DSL → IR → UtilityInlining → LayoutLowering → MsoConditional → DocumentShell → TreeShake → Optimize → Emit.

## DSL Features

### Layout
- `container {}` / `row {}` / `column {}` — Table-based layout grid
- `single {}` — Shorthand for `container { row { column { ... } } }`
- `spacer(height)` — Vertical spacing primitive

### HTML Elements
`div`, `p`, `h1`–`h6`, `span`, `strong`, `b`, `em`, `a`, `img`, `hr`, `br`, `ul`, `ol`, `li`, `blockquote`, `i`, `u`, `s`, `sup`, `sub`, `pre`

### Specialized Primitives
- `button(text, href, id)` — Email-safe button as styled `<a>` with sensible defaults
- `img(src, alt, width?, height?)` — Typed width/height with dual HTML attribute + CSS style output
- `preheader(text)` — Hidden preview text with email-safe hiding styles

### Attributes
- `className` / `id` — Typed properties on all builders (Element, Container, Row, Column, Single)
- `attr(name, value)` / `attrs(vararg pairs)` — Raw attribute access
- `style {}` — Type-safe inline CSS with enum values for fixed properties

### Head & CSS
- `head { title = "..."; style { ... } }` — Structured head section
- `tagSelector()`, `classSelector()`, `idSelector()` — Type-safe CSS selectors
- `media(condition) {}` — Responsive `@media` blocks
- `mso {}` — MSO conditional CSS (`<!--[if mso]><style>`)
- `important {}` / `"value".important()` — Scoped `!important` in CSS rules

## Utility Class System

Tailwind-inspired utility classes that resolve on demand — only used classes generate CSS.

| Category | Examples |
|---|---|
| Display | `d-block`, `d-none`, `d-inline-block` |
| Width | `w-50`, `w-100`, `w-auto` |
| Padding | `p-0` – `p-40`, `px-*`, `py-*`, `pt-*`, `pr-*`, `pb-*`, `pl-*` |
| Margin | `m-0` – `m-40`, `mx-*`, `my-*`, `mt-*`, etc. |
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

### Theme Colors

```kotlin
theme = EmailTheme(
    primary = ColorToken("#1B7C96", "#1B7C96"),
    surface = ColorToken("#fdfdfd", "#272623"),
    foreground = ColorToken("#1D2129", "#f5f5f5"),
    mutedForeground = ColorToken("#999999", "#f5f5f5"),
    // ... secondary, background, surfaceAlt, border, success?, warning?, error?
)
```

Generates `bg-primary`, `text-primary`, `bg-surface`, `text-foreground`, `text-muted`, etc.

## Compiler Passes

| Pass | What it does |
|---|---|
| **UtilityInlining** | Resolves utility classes into inline styles, user styles always win |
| **LayoutLowering** | Converts `container`/`row`/`column` to `table`/`tr`/`td` with structural defaults |
| **MsoConditional** | Wraps container tables in Outlook ghost table conditionals |
| **DocumentShell** | Wraps content in `<!DOCTYPE>`, `<html>`, `<head>`, `<body>` with email meta tags |
| **CssTreeShake** | Removes unused class selectors from `<style>` block, preserves safelist |
| **CssOptimization** | Merges duplicate selectors, deduplicates identical style blocks, collapses shorthand properties |

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

## Installation

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/arealdeadone/kotlin-ehtml")
        credentials {
            username = "GITHUB_USERNAME"
            password = "GITHUB_TOKEN" // needs read:packages scope
        }
    }
}

dependencies {
    implementation("com.arvindrachuri:kotlin-ehtml:0.1.0-alpha")
}
```

## Requirements

- Kotlin 1.9.0+
- JVM 17+

## What's Next

- CSS inlining pass for user-defined styles
- DSL nesting enforcement
- CSS optimization pass (-O3 level)
