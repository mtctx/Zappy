/*
 * Zappy (Zappy.core): build.gradle.kts
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
}

group = "dev.mtctx.library"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.mtctx.library:utilities:1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}