import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.ncorti.ktfmt.gradle") version "0.26.0"
    jacoco
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.arvindrachuri"
            artifactId = "kotlin-ehtml"
            version = "0.1.0-alpha"
            from(components["java"])
        }
    }
}

java { withSourcesJar() }

group = "com.arvindrachuri"

version = "0.1.0"

repositories { mavenCentral() }

dependencies {
    api("org.owasp.encoder:encoder:1.4.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco { toolVersion = "0.8.14" }

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

kotlin { jvmToolchain(17) }

tasks.register<KtfmtFormatTask>("ktfmtPreCommitFormat") {
    source = project.fileTree(rootDir)
    include("**/*.kt")
}

ktfmt { kotlinLangStyle() }
