/*
 * Zappy (Zappy.processor): build.gradle.kts
 * Copyright (C) 2025 mtctx
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the **GNU General Public License** as published
 * by the Free Software Foundation, either **version 3** of the License, or
 * (at your option) any later version.
 *
 * This program is distributed WITHOUT ANY WARRANTY; see the
 * GNU General Public License for more details, which you should have
 * received with this program.
 *
 * SPDX-FileCopyrightText: 2025 mtctx
 * SPDX-License-Identifier: GPL-3.0-only
 */

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

/*
 * Zappy (Zappy.processor): build.gradle.kts
 * Copyright (C) 2025 mtctx
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the **GNU General Public License** as published
 * by the Free Software Foundation, either **version 3** of the License, or
 * (at your option) any later version.
 *
 * This program is distributed WITHOUT ANY WARRANTY; see the
 * GNU General Public License for more details, which you should have
 * received with this program.
 *
 * SPDX-FileCopyrightText: 2025 mtctx
 * SPDX-License-Identifier: GPL-3.0-only
 */

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    signing
}

group = "dev.mtctx.library"
version = "1.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
    implementation(libs.kspSymbolProcessingApi)
}

mavenPublishing {
    coordinates(group.toString(), "zappy-processor", version.toString())

    pom {
        name.set("Zappy-Processor")
        description.set("Zappy KSP Processor – Generates type-safe mock<T>() and mock_X_Y_Z_Class() functions from @Mock-annotated data classes.")
        inceptionYear.set("2025")
        url.set("https://github.com/mtctx/Zappy/tree/main/processor")

        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                distribution.set("repo")
            }
        }

        scm {
            url.set("https://github.com/mtctx/Zappy/tree/main/processor")
            connection.set("scm:git:git@github.com:mtctx/Zappy.git")
            developerConnection.set("scm:git:ssh://git@github.com:mtctx/Zappy.git")
        }

        developers {
            developer {
                id.set("mtctx")
                name.set("mtctx")
                email.set("me@mtctx.dev")
            }
        }

    }

    configure(KotlinJvm(JavadocJar.Dokka("dokkaGenerateJavadoc"), sourcesJar = true))

    signAllPublications()
    publishToMavenCentral(automaticRelease = true)
}

signing {
    useGpgCmd()
}

kotlin {
    jvmToolchain(21)
}