# Zappy – Declarative Mock Data for Kotlin

[![License: GPL-3.0](https://img.shields.io/badge/License-GPL%203.0-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg)](https://kotlinlang.org)
[![KSP](https://img.shields.io/badge/KSP-2.3.0-orange.svg)](https://github.com/google/ksp)

---

## What is Zappy?

**Zappy** is a **KSP-powered**, **zero-reflection**, **type-safe** mock data generator for Kotlin.

Annotate your data classes with `@Mock` and fields with ZPL-aware annotations (`@Name`, `@Email`, etc.), then generate
fully-populated instances with a single call:

```kotlin
val user = mock<User>()
```

No boilerplate. No reflection. No runtime cost.

---

## Modules

| Module              | Description                                                                            |
|---------------------|----------------------------------------------------------------------------------------|
| **zappy-core**      | Core ZPL engine, annotations, built-in providers, and `String.generateWithZPL()`       |
| **zappy-processor** | KSP annotation processor that generates `mock<T>()` and `mock_X_Y_Z_Class()` functions |
| **zappy-test**      | Example usage tests                                                                    |

---

## Features

* **Annotation-Driven**: `@Mock` on classes, `@Name`, `@Email`, etc. on fields
* **ZPL (Zappy Pattern Language)**: Declarative fake data via `<pattern>` strings
* **Length Control**: `<username:5-12>`, `<numeric:1-100>`, etc.
* **Extensible**: Add custom `ZPLProvider`s and annotations
* **Compile-Time Code Gen**: No reflection, zero runtime overhead
* **Kotlin-First**: Pure Kotlin, no Java dependencies
* **GPL-3.0 Licensed** with full SPDX compliance

---

## Installation

Zappy will be published to **Maven Central** at `v1.0.0`.

### Gradle (Kotlin DSL)

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.mtctx.zappy:zappy-core:1.0.0")
    ksp("dev.mtctx.zappy:zappy-processor:1.0.0")
}
```

> **Important**: KSP must be applied for the processor to run.

---

## Example Usage

```kotlin
import dev.mtctx.zappy.mock

@Mock
data class User(
    @Name val username: String,
    @Email val email: String,
    @Numeric(":1-2") val age: Int // 1-2 digits, e.g. 1, 2, ... 12, 35, ...
)

fun main() {
    val user = mock<User>()
    println(user)
    // → User(username=7g1iH7VlMBM2C, email=7g1iH7VlMBM2C@example.io, age=42)
}
```

> Output is **fully random** — no fixed examples like "alice@example.com".

---

## Documentation

### How to Create a Mockable Class

```kotlin
@Mock
data class Profile(
    @Name val username: String,
    @Email val email: String,
    @ISODate val createdAt: String,
)
```

* Use `@Mock` on the class
* Use ZPL annotations on fields
* Default ZPL strings are provided (e.g. `<username>`)
* Call `mock<Profile>()` anywhere

> **Important**: Every type has to be a String, or else the processor won't work. Will be fixed in the future.

---

### How to Create a Custom Annotation

```kotlin
@ZappyAnnotation
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class FullName(val zpl: String = "<username>_<numeric:1-99>")

@Mock
data class Person(
    @FullName val fullName: String
)

// register it at startup (e.g. in main or Application class)
dev.mtctx.zappy.processor.registerNewZPLAnnotation(FullName::class)
```

> `@ZappyAnnotation` enables KSP processing.  
> Must have a `zpl: String` parameter.

---

### How to Create a Custom ZPLProvider

```kotlin
object CreditCardZPLProvider : ZPLProvider() {
    override val id: String = "credit-card" // will be used as "<credit-card>"
    override val characterList = ('0'..'9').toList()

    override fun generate(minLength: Int, maxLength: Int): String {
        val digits = length(minLength, maxLength).map { characterList.random() }.joinToString("")
        return digits.chunked(4).joinToString("-")
    }
}

// Register at startup (e.g. in main or Application class)
dev.mtctx.zappy.processor.registerNewZPLProvider(CreditCardZPLProvider)
// or
dev.mtctx.zappy.processor.registerNewZPLProvider(CreditCardZPLProvider.mapEntry())
```

Now use: `<credit-card:19>` → `"1234-5678-9012-3456"`

---

### How to Use ZPL Directly

```kotlin
val pattern = "<username:5-10>@<domain>"
val email = pattern.generateWithZPL()

println(email) // → "a1b2c@example.com"
```

* Use any registered provider
* Combine providers: `<email>`, `<uuid>`, custom ones
* Length control: `:min-max`, `:min`, `:-max`

---

## Built-in ZPL Providers

| ID               | Example Output                         |
|------------------|----------------------------------------|
| `<name>`         | `k9PxM2vN`                             |
| `<email>`        | `Bc34QQ6grdHQ0ozz@2noJRHYKqklOFZ.com`  |
| `<domain>`       | `aa2fa5.net`                           |
| `<numeric>`      | `42`                                   |
| `<password>`     | `e*<PyXvp]B`                           |
| `<token>`        | `YWEeWGC3Fd4Fk-H`                      |
| `<uuid>`         | `f47ac10b-58cc-4372-a567-0e02b2c3d479` |
| `<iso-date>`     | `2025-10-29T14:30:22Z`                 |
| `<phone-number>` | `+1017079879685250`                    |
| `<url>`          | `http://2ikEu.io`                      |

> **Note**: `<iso-date>` requires a custom provider. See [Custom ZPLProvider](#how-to-create-a-custom-zplprovider).

---

## Documentation

Full API reference (Dokka):  
[https://zappy.apidoc.mtctx.dev](https://zappy.apidoc.mtctx.dev)

---

## Contributing

Contributions are **very welcome**!  
See [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

---

## License

Zappy is **free software** under the **GNU GPL v3**.  
You can use, modify, and distribute it — as long as it remains free.

Copyright (C) 2025 mtctx

---

> **Zappy: Fake data, real fast.**