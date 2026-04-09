import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "1.9.20"
    id("com.ncorti.ktfmt.gradle") version "0.26.0"
    id("com.vanniktech.maven.publish") version "0.30.0"
    jacoco
    `java-library`
}

group = "com.arvindrachuri"

version = findProperty("publishVersion")?.toString() ?: "0.1.0-SNAPSHOT"

mavenPublishing {
    configure(KotlinJvm(javadocJar = JavadocJar.Empty()))
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("com.arvindrachuri", "kotlin-ehtml", version.toString())

    pom {
        name.set("kotlin-ehtml")
        description.set("Kotlin DSL for composing email-safe HTML with a compiler pipeline")
        url.set("https://github.com/arealdeadone/kotlin-ehtml")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("arealdeadone")
                name.set("Arvind Rachuri")
                url.set("https://github.com/arealdeadone")
            }
        }

        scm {
            url.set("https://github.com/arealdeadone/kotlin-ehtml")
            connection.set("scm:git:git://github.com/arealdeadone/kotlin-ehtml.git")
            developerConnection.set("scm:git:ssh://github.com/arealdeadone/kotlin-ehtml.git")
        }
    }
}

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
