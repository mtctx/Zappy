/*
 * Zappy (Zappy.core.main): ZPLProvider.kt
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

package dev.mtctx.zappy.zpl

import dev.mtctx.zappy.zpl.builtin.*

/**
 * ZPL stands for Zappy Pattern Language
 */
abstract class ZPLProvider {
    abstract val id: String
    abstract val characterList: Collection<Char>

    open val defaultMinLength: Int = 1
    open val defaultMaxLength: Int = 20

    open fun generate(minLength: Int = defaultMinLength, maxLength: Int = defaultMaxLength): String {
        require(minLength <= maxLength) { "minLength must be less than maxLength." }

        return length(minLength, maxLength)
            .map { characterList.random() }
            .joinToString("")
    }

    protected fun length(minLength: Int, maxLength: Int): IntRange = minLength..((minLength..maxLength).random())

    fun mapEntry(): Pair<String, ZPLProvider> = id to this
}

val defaultZPLProviders = mapOf(
    DomainZPLProvider.mapEntry(),
    EmailZPLProvider.mapEntry(),
    NameZPLProvider.mapEntry(),
    NumericZPLProvider.mapEntry(),
    PasswordZPLProvider.mapEntry(),
    PhoneNumberZPLProvider.mapEntry(),
    TokenZPLProvider.mapEntry(),
    URLZPLProvider.mapEntry(),
    UUIDZPLProvider.mapEntry(),
)

fun String.generateWithZPLWithProviders(providers: Map<String, ZPLProvider> = emptyMap()): String {
    val regex = "<([^:]+)(?::([0-9]+)-([0-9]+)|:([0-9]+)|:-([0-9]+))?>".toRegex()

    return regex.replace(this) { match ->
        val id = match.groups[1]?.value?.lowercase() ?: return@replace match.value
        val provider = providers[id] ?: return@replace match.value

        val rangeMin = match.groups[2]?.value?.toIntOrNull()
        val rangeMax = match.groups[3]?.value?.toIntOrNull()
        val minOnly = match.groups[4]?.value?.toIntOrNull()
        val maxOnly = match.groups[5]?.value?.toIntOrNull()

        when {
            rangeMin != null && rangeMax != null -> provider.generate(rangeMin, rangeMax) // Exact range (:X-Y)
            minOnly != null -> provider.generate(minLength = minOnly) // Min X, Max 20
            maxOnly != null -> provider.generate(maxLength = maxOnly) // Min 1, Max Y
            else -> provider.generate() // Min 1, Max 20 (default, but depends on provider)
        }
    }
}

fun String.generateWithZPL(customProviders: Collection<ZPLProvider> = emptyList()) =
    generateWithZPLWithProviders((defaultZPLProviders + customProviders.associateBy { it.id }).mapKeys { it.key.lowercase() })