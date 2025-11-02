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

package dev.mtctx.zappy.processor

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import dev.mtctx.zappy.zpl.ZPLProvider

data class FunSpecAndOptInMarkers(val funSpec: FunSpec, val optInMarkers: Set<String>)

fun KSClassDeclaration.processAndReturnFunSpec(
    logger: KSPLogger,
    sourceFile: KSFile
): Pair<FunSpecAndOptInMarkers?, String> {
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

    val optInMarkerClassNames = mutableSetOf<String>()
    this.annotations.flatMapTo(optInMarkerClassNames) { it.toOptInMarkerClassNames() }
    this.primaryConstructor?.apply {
        annotations.flatMapTo(optInMarkerClassNames) { it.toOptInMarkerClassNames() }
        parameters.forEach { param ->
            param.annotations.flatMapTo(optInMarkerClassNames) { it.toOptInMarkerClassNames() }
        }
    }

    val companionKClass = declarations.filterIsInstance<KSClassDeclaration>().find { it.isCompanionObject }
    val companionKClassHasMockFunction =
        companionKClass?.declarations?.any { it is KSFunctionDeclaration && it.simpleName.asString() == "mock" } == true

    val funSpec = FunSpec.builder("mock_${formatQualifiedName(qualifiedName!!.asString())}_Class")
        .addParameter(
            ParameterSpec.builder(
                "customProviders",
                Collection::class.asClassName().parameterizedBy(ZPLProvider::class.asClassName().parameterizedBy(STAR)),
            ).defaultValue(CodeBlock.of("emptyList()")).build()
        )
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
    return FunSpecAndOptInMarkers(funSpec.build(), optInMarkerClassNames) to ""
}

private fun KSValueParameter.generateMockValueCode(logger: KSPLogger): CodeBlock {
    val type = type.resolve().toTypeName().toString()
    val customAnnotation = annotations.find { it.shortName.asString() == "Custom" }
    if (customAnnotation != null) {
        val pattern = customAnnotation.arguments.first { it.name?.asString() == "zpl" }.value as String
        return CodeBlock.of("%S.generateWithZPL<$type>(customProviders)", pattern)
    }

    val specificAnnotation =
        annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() in annotationClassNames }
    if (specificAnnotation != null) {
        val zpl = specificAnnotation.arguments.first { it.name?.asString() == "zpl" }.value as String
        return CodeBlock.of("%S.generateWithZPL<$type>(customProviders)", zpl)
    }

    return when (type) {
        "kotlin.String" -> CodeBlock.of("%S", "mock-${name?.asString()}")
        "kotlin.Int" -> CodeBlock.of("(0..100).random()")
        "kotlin.Long" -> CodeBlock.of("System.currentTimeMillis()")
        "kotlin.Boolean" -> CodeBlock.of("true")
        "kotlin.Double" -> CodeBlock.of("1.0")
        "kotlin.Float" -> CodeBlock.of("1.0f")
        else -> {
            logger.warn("Unsupported type $type for ${name?.asString()}")
            CodeBlock.of("%S", "mock-${name?.asString()}")
        }
    }
}

private fun KSAnnotation.toOptInMarkerClassNames(): List<String> {
    if (this.annotationType.resolve().declaration.qualifiedName?.asString() != "kotlin.OptIn") {
        return emptyList()
    }

    val classArguments = this.arguments.firstOrNull { it.name?.asString() == "markerClass" }?.value as? List<*>

    return classArguments
        ?.filterIsInstance<KSType>()
        ?.mapNotNull { it.declaration.qualifiedName?.asString() }
        ?.map { "$it::class" }
        ?: emptyList()
}