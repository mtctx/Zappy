/*
 * Zappy (Zappy.processor.main): MockProcessor.kt
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
@file:Suppress("Unused")

package dev.mtctx.zappy.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.writeTo
import dev.mtctx.zappy.annotation.*

internal val annotationClasses = mutableSetOf(
    Custom::class,
    Domain::class,
    Email::class,
    ISODate::class,
    Name::class,
    Numeric::class,
    Password::class,
    PhoneNumber::class,
    Token::class,
    URL::class,
    UUID::class,
)

internal val externalAnnotationClassNames = mutableSetOf<String>()

internal val annotationClassNames: Set<String>
    get() = annotationClasses.mapNotNull { it.qualifiedName }.toSet() + externalAnnotationClassNames

internal var errorOccurred = false

class MockProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(ZappyAnnotation::class.qualifiedName!!).filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() && it.isValidZappyAnnotation(logger) }.map { it.qualifiedName!!.asString() }
            .also { externalAnnotationClassNames.addAll(it) }

        if (errorOccurred) return emptyList()

        val symbols = resolver
            .getSymbolsWithAnnotation(Mock::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }

        val funSpecs = mutableListOf<FunSpecAndOptInMarkers>()
        symbols.forEach { classDeclaration ->
            val (funSpec, error) = classDeclaration.processAndReturnFunSpec(logger, classDeclaration.containingFile!!)
            if (error.isNotEmpty()) {
                logger.error(error, classDeclaration)
                return@forEach
            }
            funSpec?.let { funSpecs.add(it) }
        }

        val symbolList = symbols.toList()
        if (symbolList.isNotEmpty()) {
            logger.info("Generated ${symbolList.size} mock functions!")

            logger.info("Writing general mock function...")
            val fileSpec = buildGeneralMockFunctionAndReturnFileSpec(logger, symbolList, funSpecs)
            fileSpec.writeTo(codeGenerator, false)
            logger.info("Generated general mock function.")
        }

        return symbols.filterNot { it.validate() }.toList()
    }
}

class MockProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        environment.logger.info("MockProcessorProvider created")
        return MockProcessor(environment.codeGenerator, environment.logger)
    }
}

fun formatQualifiedName(qualifiedName: String): String = qualifiedName.split('.').filter { it.isNotEmpty() }
    .joinToString("_") { it.replaceFirstChar { ch -> ch.uppercaseChar() } }

internal fun KSClassDeclaration.isValidZappyAnnotation(logger: KSPLogger): Boolean {
    val constructor = primaryConstructor

    if (constructor == null) {
        logger.error(
            "Annotation class '${simpleName.asString()}' is annotated with @ZappyAnnotation but must have a primary constructor.",
            this
        )
        errorOccurred = true
        return false
    }

    val zplParameter = constructor.parameters.find { it.name?.asString() == "zpl" }

    if (zplParameter == null) {
        logger.error(
            "Annotation class '${simpleName.asString()}' must have a primary constructor parameter named 'zpl'.",
            this
        )
        errorOccurred = true
        return false
    }

    val typeName = zplParameter.type.resolve().declaration.qualifiedName?.asString()

    if (typeName != "kotlin.String") {
        logger.error(
            "The 'zpl' parameter in '${simpleName.asString()}' must be of type String. Found: $typeName.",
            this
        )
        errorOccurred = true
        return false
    }

    if (zplParameter.isVal.not() && zplParameter.isVar.not()) {
        logger.error("The 'zpl' parameter in '${simpleName.asString()}' must be declared with 'val' or 'var'.", this)
        errorOccurred = true
        return false
    }

    return true
}