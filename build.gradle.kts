/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2025 iseki zero and all contributors
 * Licensed under the MIT License. See LICENSE file for details.
 */

import java.net.URI
import java.util.*

plugins {
    kotlin("multiplatform") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    `maven-publish`
    signing
}

allprojects {
    group = "space.iseki.urikt"
    if (version == "unspecified") version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

kotlin {
    jvmToolchain(17)
    jvm {}
//    js {
//        browser()
//    }

    sourceSets {
        jvmMain.configure {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.12.0")
            }
        }
    }
}

dependencies {
    commonTestImplementation(kotlin("test"))
    commonMainCompileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.0")
    commonMainCompileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

tasks.named("jvmTest") {
    this as Test
    useJUnitPlatform()
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

publishing {
    repositories {
        maven {
            name = "Central"
            afterEvaluate {
                url = if (version.toString().endsWith("SNAPSHOT")) {
                    // uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                    uri("https://oss.sonatype.org/content/repositories/snapshots")
                } else {
                    // uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                }
            }
            credentials {
                username = properties["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
                password = properties["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
            }
        }
        if (!System.getenv("GITHUB_TOKEN").isNullOrBlank()) {
            maven {
                name = "GitHubPackages"
                url = URI.create("https://maven.pkg.github.com/iseki0/urikt")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")!!
                    password = System.getenv("GITHUB_TOKEN")!!
                }
            }
        }
    }
    publications {
        withType<MavenPublication> {
            val pubName = name.replaceFirstChar { it.titlecase(Locale.getDefault()) }
            val emptyJavadocJar by tasks.register<Jar>("emptyJavadocJar$pubName") {
                archiveClassifier = "javadoc"
                archiveBaseName = artifactId
            }
            artifact(emptyJavadocJar)
            pom {
                name = "PurlKt-${project.name}"
                val projectUrl = "https://github.com/iseki0/urikt"
                description = "A library for URI parsing and building, in Kotlin multiplatform"
                url = projectUrl
                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }
                developers {
                    developer {
                        id = "iseki0"
                        name = "iseki zero"
                        email = "iseki@iseki.space"
                    }
                }
                inceptionYear = "2025"
                scm {
                    connection = "scm:git:$projectUrl.git"
                    developerConnection = "scm:git:$projectUrl.git"
                    url = projectUrl
                }
                issueManagement {
                    system = "GitHub"
                    url = "$projectUrl/issues"
                }
                ciManagement {
                    system = "GitHub"
                    url = "$projectUrl/actions"
                }
            }
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
//        includes.from(rootProject.layout.projectDirectory.file("module.md"))
        sourceLink {
            localDirectory = project.layout.projectDirectory.dir("src").asFile
            val p =
                project.layout.projectDirectory.dir("src").asFile.relativeTo(rootProject.layout.projectDirectory.asFile)
                    .toString()
                    .replace('\\', '/')
            remoteUrl = URI.create("https://github.com/iseki0/urikt/tree/master/$p")
            remoteLineSuffix = "#L"
        }
        externalDocumentationLinks.create("") {
            url = URI.create("https://kotlinlang.org/api/kotlinx.serialization/")
        }
    }
}

tasks.withType<Jar> {
    if ("emptyJavadocJar" !in name) {
        into("/") {
            from(rootProject.projectDir.resolve("LICENSE"))
        }
    }
}

