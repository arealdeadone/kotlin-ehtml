import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.ncorti.ktfmt.gradle") version "0.26.0"
    jacoco
    `maven-publish`
    signing
    `java-library`
}

group = "com.arvindrachuri"

version = findProperty("publishVersion")?.toString() ?: "0.1.0-SNAPSHOT"

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.arvindrachuri"
            artifactId = "kotlin-ehtml"
            from(components["java"])

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
                    developerConnection.set(
                        "scm:git:ssh://github.com/arealdeadone/kotlin-ehtml.git"
                    )
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url =
                if (version.toString().endsWith("-SNAPSHOT"))
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = System.getenv("MAVEN_USERNAME") ?: ""
                password = System.getenv("MAVEN_PASSWORD") ?: ""
            }
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["maven"])
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
