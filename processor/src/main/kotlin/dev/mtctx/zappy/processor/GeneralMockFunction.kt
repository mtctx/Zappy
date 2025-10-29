/*
 * Zappy (Zappy.processor.main): GeneralMockFunction.kt
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

package dev.mtctx.zappy.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*

fun buildGeneralMockFunctionAndReturnFileSpec(
    logger: KSPLogger,
    classes: List<KSClassDeclaration>,
    funSpecs: List<FunSpec>
): FileSpec {
    logger.info("Generating general mock function...")

    val typeVariable = TypeVariableName("T", ANY).copy(reified = true)

    val funSpec = FunSpec.builder("mock")
        .addAnnotation(AnnotationSpec.builder(Throws::class).addMember("IllegalArgumentException::class").build())
        .addModifiers(KModifier.INLINE)
        .addTypeVariable(typeVariable)
        .returns(typeVariable)
        .addCode(buildCodeBlock {
            /**
             * @Throws(IllegalArgumentException::class)
             * inline fun <reified T> function(): T = when (T::class) {
             *     CoolData::class -> Cool.function() as T
             *     else -> throw IllegalArgumentException("${T::class.simpleName} is not annotated with @Mock!")
             * }
             */

            add("return when (T::class) {\n")
            classes.map { it.qualifiedName!!.asString() }
                .forEach { qualifiedName ->
                    add(
                        "${qualifiedName}::class -> mock_${formatQualifiedName(qualifiedName)}_Class() as T\n"
                    )
                }
            add($$"else -> throw IllegalArgumentException(\"${T::class.simpleName} is not annotated with @Mock!\")\n")
            add("}\n")
        })
        .addKdoc("Generates a mock of the given type. Throws an exception if the type is not annotated with [dev.mtctx.zappy.annotation.Mock].")
        .build()

    return FileSpec.builder("dev.mtctx.zappy", "MockProviders")
        .addFunction(funSpec)
        .addFunctions(funSpecs)
        .addImport("dev.mtctx.zappy.zpl", "generateWithZPL")
        .build().also { logger.info("Generated general mock function!") }
}