import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.ncorti.ktfmt.gradle") version "0.26.0"
    jacoco
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            groupId = "com.arvindrachuri"
            artifactId = "kotlin-ehtml"
            version = findProperty("publishVersion")?.toString() ?: project.version.toString()
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/arealdeadone/kotlin-ehtml")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
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
