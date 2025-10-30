/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2025 iseki zero and all contributors
 * Licensed under the MIT License. See LICENSE file for details.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    kotlin("multiplatform") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("com.vanniktech.maven.publish") version "0.34.0"
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
    jvmToolchain(21)
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    js {
        browser()
        nodejs()
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class) wasmJs {
        browser()
        nodejs()
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class) wasmWasi {
        nodejs()
    }

    // Tier 1
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64()

    // Tier 2
    linuxX64()
    linuxArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    // Tier 3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()
    mingwX64()
    watchosDeviceArm64()

    applyDefaultHierarchyTemplate()
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
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "urikt", version.toString())

    pom {
        val projectUrl = "https://github.com/iseki0/urikt/blob/master/LICENSE"
        description = "A library for URI parsing and building, in Kotlin multiplatform"
        url = projectUrl
        licenses {
            license {
                name = "MIT License"
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

dokka {
    dokkaSourceSets.configureEach {
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
    into("/") {
        from(rootProject.projectDir.resolve("LICENSE"))
    }
}

tasks.named("jvmJar") {
    check(this is Jar)
    manifest {
        attributes("Automatic-Module-Name" to "space.iseki.urikt")
    }
}
