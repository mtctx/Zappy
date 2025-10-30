/*
 * Zappy (Zappy.test.main): Main.kt
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

package dev.mtctx.zappy.test

import dev.mtctx.zappy.annotation.*
import dev.mtctx.zappy.mock
import dev.mtctx.zappy.zpl.ZPLProvider

object TestZplProvider : ZPLProvider() {
    override val id: String = "test"
    override val characterList = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '_', '=')
}

object Test2ZplProvider : ZPLProvider() {
    override val id: String = "test2"
    override val characterList = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '_', '=')
}

@ZappyAnnotation
annotation class TestAnno(val zpl: String = "<test>")

/**
 * | ID               | Example Output                         |
 * |------------------|----------------------------------------|
 * | `<name>`     | `k9PxM2vN`                             |
 * | `<email>`        | `k9PxM2vN@aa2fa5.org`                  |
 * | `<domain>`       | `aa2fa5.net`                           |
 * | `<numeric>`      | `42`                                   |
 * | `<password>`     | `p@ssW0rd!`                            |
 * | `<token>`        | `aB9-xY2_zW8=`                         |
 * | `<uuid>`         | `f47ac10b-58cc-4372-a567-0e02b2c3d479` |
 * | `<iso-date>`     | `2025-10-29T14:30:22Z`                 |
 * | `<phone-number>` | `+15551234567`                         |
 * | `<url>`          | `https://example.com`                  |
 */
@Mock
data class Test(
    @TestAnno val test: String,
    @Name val name: String,
    @Email val email: String,
    @Domain val domain: String,
    @Numeric val age: String,
    @Password val password: String,
    @Token val token: String,
    @UUID val uuid: String,
    @PhoneNumber val phoneNumber: String,
    @URL val url: String,
)

val providers = arrayOf(TestZplProvider, Test2ZplProvider)

fun main() {
    println(mock<Test>(*providers))
}
