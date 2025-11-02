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
import kotlin.reflect.KClass
import kotlin.uuid.ExperimentalUuidApi

/**
 * ZPL stands for Zappy Pattern Language
 */
abstract class ZPLProvider<R : Any> {
    abstract val id: String
    abstract val characterList: Collection<Char>
    abstract val returnType: KClass<R>

    open val defaultMinLength: Int = 1
    open val defaultMaxLength: Int = 20

    open fun generate(minLength: Int = defaultMinLength, maxLength: Int = defaultMaxLength): R {
        require(minLength <= maxLength) { "minLength must be less than maxLength." }

        return toType(
            length(minLength, maxLength)
                .map { characterList.random() }
                .joinToString("")
        )
    }

    open fun toType(generated: String): R = generalToType(generated, returnType)

    protected fun length(minLength: Int, maxLength: Int): IntRange = minLength..((minLength..maxLength).random())
}

@Suppress("UNCHECKED_CAST")
fun <R : Any> generalToType(input: String, returnType: KClass<R>): R {
    return when (returnType) {
        String::class -> input as R
        Int::class -> input.toInt() as R
        Long::class -> input.toLong() as R
        Boolean::class -> input.toBoolean() as R
        Double::class -> input.toDouble() as R
        Float::class -> input.toFloat() as R
        Char::class -> input.single() as R
        Short::class -> input.toShort() as R
        UShort::class -> input.toUShort() as R
        UInt::class -> input.toUInt() as R
        ULong::class -> input.toULong() as R
        UByte::class -> input.toUByte() as R
        else -> input as R
    }
}

@OptIn(ExperimentalUuidApi::class)
val defaultZPLProviders = setOf(
    DomainZPLProvider,
    EmailZPLProvider,
    NameZPLProvider,
    NumericZPLProvider,
    PasswordZPLProvider,
    PhoneNumberZPLProvider,
    TokenZPLProvider,
    URLZPLProvider,
    UUIDZPLProvider,
)

inline fun <reified R : Any> String.generateWithZPL(customProviders: Collection<ZPLProvider<*>> = emptyList()): R {
    val providers = defaultZPLProviders + customProviders
    val providerMap = providers.associateBy { it.id.lowercase() }

    val regex = "<([^:]+)(?::([0-9]+)-([0-9]+)|:([0-9]+)|:-([0-9]+))?>".toRegex()

    val replaced = regex.replace(this) { match ->
        val tagId = match.groups[1]?.value?.lowercase() ?: return@replace match.value

        val provider = providerMap[tagId] ?: return@replace match.value

        val rangeMin = match.groups[2]?.value?.toIntOrNull()
        val rangeMax = match.groups[3]?.value?.toIntOrNull()
        val minOnly = match.groups[4]?.value?.toIntOrNull()
        val maxOnly = match.groups[5]?.value?.toIntOrNull()

        when {
            rangeMin != null && rangeMax != null -> provider.generate(rangeMin, rangeMax) // <tag:X-Y>
            minOnly != null -> provider.generate(minLength = minOnly) // <tag:X>
            maxOnly != null -> provider.generate(maxLength = maxOnly) // <tag:-Y>
            else -> provider.generate() // <tag>
        }.toString()
    }

    return generalToType(replaced, R::class)
}