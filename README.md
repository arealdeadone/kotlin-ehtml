# kotlin-ehtml

`kotlin-ehtml` is a Kotlin-first project for authoring composable email templates and compiling them into a single email-safe HTML document.

This repository is intentionally being built as an **email compiler** rather than a generic HTML DSL. The user-facing DSL should stay pleasant and semantic, while the compiler handles the unpleasant parts of email HTML: table layout, inline styles, Outlook quirks, and compatibility rules.

## Current status

- Kotlin JVM project initialized
- foundational compiler research completed
- implementation is expected to start from AST/IR + lowering passes, not from a full DSL
