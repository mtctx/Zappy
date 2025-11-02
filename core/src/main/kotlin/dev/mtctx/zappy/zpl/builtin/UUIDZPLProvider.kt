/*
 * Zappy (Zappy.core.main): UUIDZPLProvider.kt
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
import kotlin.reflect.KClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UUIDZPLProvider : ZPLProvider<Uuid>() {
    override val id: String = "uuid"
    override val characterList = emptyList<Char>()
    override val returnType: KClass<Uuid> = Uuid::class

    @OptIn(ExperimentalUuidApi::class)
    override fun generate(minLength: Int, maxLength: Int): Uuid = Uuid.random()

    override fun toType(generated: String): Uuid = Uuid.fromByteArray(generated.encodeToByteArray())
}