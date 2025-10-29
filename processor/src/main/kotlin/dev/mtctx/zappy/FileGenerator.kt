/*
 * Zappy (Zappy.processor.main): FileGenerator.kt
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

package dev.mtctx.zappy

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

fun KSClassDeclaration.processAndReturnFunSpec(
    logger: KSPLogger,
    sourceFile: KSFile
): Pair<FunSpec?, String> {
    if (isLocal() || Modifier.PRIVATE in modifiers) {
        logger.warn(
            "Skipping non-public (local or private) class $qualifiedName » cannot generate top-level mock",
            this
        )
        return null to ""
    }

    val packageName = packageName.asString()
    val className = simpleName.asString()
    val classTypeName = toClassName()

    logger.info("Processing mock class: $packageName.$className")

    val companionKClass = declarations.filterIsInstance<KSClassDeclaration>().find { it.isCompanionObject }
    val companionKClassHasMockFunction =
        companionKClass?.declarations?.any { it is KSFunctionDeclaration && it.simpleName.asString() == "mock" } == true

    val funSpec = FunSpec.builder("mock_${formatQualifiedName(qualifiedName!!.asString())}_Class")
        .returns(classTypeName)
        .addOriginatingKSFile(sourceFile)

    if (companionKClassHasMockFunction) {
        funSpec.addCode("return %T.mock()", classTypeName)
    } else {
        funSpec.addCode(buildCodeBlock {
            add("return %T(", classTypeName)
            val constructorParams = primaryConstructor?.parameters ?: emptyList()

            constructorParams.forEachIndexed { index, property ->
                val propertyName = property.name?.asString() ?: return@forEachIndexed
                val mockValueCode = property.generateMockValueCode(logger)
                val format = if (index < constructorParams.lastIndex) "%L = %L," else "%L = %L"
                add(format, propertyName, mockValueCode)
            }
            add(")")
        })
    }

    return funSpec.build() to ""
}

private fun KSValueParameter.generateMockValueCode(logger: KSPLogger): CodeBlock {
    val regexAnnotation = annotations.find { it.shortName.asString() == "WithRegex" }
    if (regexAnnotation != null) {
        val pattern = regexAnnotation.arguments.first { it.name?.asString() == "regex" }.value as String
        return CodeBlock.of("%S.generateWithZPL()", pattern)
    }

    val specificAnnotation =
        annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() in annotationClassNames }
    if (specificAnnotation != null) {
        val regex = specificAnnotation.arguments.first { it.name?.asString() == "zpl" }.value as String
        return CodeBlock.of("%S.generateWithZPL()", regex)
    }

    return when (type.resolve().toTypeName().toString()) {
        "kotlin.String" -> CodeBlock.of("%S", "mock-${name?.asString()}")
        "kotlin.Int" -> CodeBlock.of("(0..100).random()")
        "kotlin.Long" -> CodeBlock.of("System.currentTimeMillis()")
        "kotlin.Boolean" -> CodeBlock.of("true")
        "kotlin.Double" -> CodeBlock.of("1.0")
        "kotlin.Float" -> CodeBlock.of("1.0f")
        else -> {
            logger.warn("Unsupported type ${type.resolve().toTypeName()} for ${name?.asString()}")
            CodeBlock.of("%S", "mock-${name?.asString()}")
        }
    }
}