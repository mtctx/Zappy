/*
 * Zappy (Zappy.core.main): PhoneNumberZPLProvider.kt
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

package dev.mtctx.zappy.zpl.builtin

import dev.mtctx.zappy.zpl.ZPLProvider
import mtctx.utilities.Ignore
import mtctx.utilities.IgnoreReason
import kotlin.reflect.KClass

object PhoneNumberZPLProvider : ZPLProvider<String>() {
    override val id: String = "phone-number"
    override val characterList = ('0'..'9').toList()
    override val returnType: KClass<String> = String::class

    override fun generate(
        @Ignore(IgnoreReason.NOT_IMPLEMENTED) minLength: Int,
        @Ignore(IgnoreReason.NOT_IMPLEMENTED) maxLength: Int
    ): String {
        val areaCodeLength = (1..2).random()
        val areaCode =
            if (areaCodeLength == 1) "+$areaCodeLength" else "+" + (1..areaCodeLength).map { characterList.random() }
                .joinToString("")

        return areaCode + (1..15).map { characterList.random() }.joinToString("")
    }
}