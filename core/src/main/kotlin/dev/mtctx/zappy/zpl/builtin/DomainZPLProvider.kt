/*
 * Zappy (Zappy.core.main): DomainZPLProvider.kt
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

object DomainZPLProvider : ZPLProvider() {
    override val id: String = "domain"
    override val characterList = emptyList<Char>()
    private val tldList = listOf("com", "net", "org", "io")

    override fun generate(minLength: Int, maxLength: Int): String {
        require(minLength <= maxLength) { "minLength must be less than maxLength." }

        var name = NameZPLProvider.generate(maxLength = 16)
        while (name.startsWith("-") || name.endsWith("-")) name = NameZPLProvider.generate(maxLength = 16)
        while (name.contains("_")) name = NameZPLProvider.generate(maxLength = 16)

        return buildString {
            append(name)
            append(".")
            append(tldList.random())
        }
    }
}