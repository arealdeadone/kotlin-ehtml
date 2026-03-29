import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask

plugins {
    kotlin("jvm") version "2.3.20"
    id("com.ncorti.ktfmt.gradle") version "0.26.0"
}

group = "com.arvindrachuri"

version = "0.1.0"

repositories { mavenCentral() }

dependencies {
    implementation("org.owasp.encoder:encoder:1.4.0")
    testImplementation(kotlin("test"))
}

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(21) }

tasks.register<KtfmtFormatTask>("ktfmtPreCommitFormat") {
    source = project.fileTree(rootDir)
    include("**/*.kt")
}

ktfmt { kotlinLangStyle() }
