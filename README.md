# Zappy – Declarative Mock Data for Kotlin

[](https://www.gnu.org/licenses/gpl-3.0)
[](https://kotlinlang.org)
[](https://github.com/google/ksp)
[](https://central.sonatype.com/artifact/dev.mtctx.library/zappy-core)
[](https://central.sonatype.com/artifact/dev.mtctx.library/zappy-processor)

-----

## What is Zappy?

**Zappy** is a **KSP-powered**, **zero-reflection**, **type-safe** mock data generator for Kotlin.

Annotate your data classes with `@Mock` and fields with ZPL-aware annotations (`@Name`, `@Email`, etc.), then generate
fully-populated instances with a single call:

```kotlin
val user = mock<User>()
```

No boilerplate. No reflection. No runtime cost.

-----

## Modules

| Module              | Description                                                                            |
|:--------------------|:---------------------------------------------------------------------------------------|
| **zappy-core**      | Core ZPL engine, annotations, built-in providers, and `String.generateWithZPL()`       |
| **zappy-processor** | KSP annotation processor that generates `mock<T>()` and `mock_X_Y_Z_Class()` functions |
| **zappy-test**      | Example usage tests                                                                    |

-----

## Features

* **Annotation-Driven**: `@Mock` on classes, `@Name`, `@Email`, etc. on fields
* **ZPL (Zappy Pattern Language)**: Declarative fake data via `<pattern>` strings
* **Length Control**: `<username:5-12>`, `<numeric:1-100>`, etc.
* **Extensible**: Add custom `ZPLProvider`s and annotations
* **Compile-Time Code Gen**: No reflection, zero runtime overhead
* **Kotlin-First**: Pure Kotlin, no Java dependencies
* **GPL-3.0 Licensed** with full SPDX compliance

-----

## Installation

Zappy is published to **Maven Central**.

### Gradle (Kotlin DSL)

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.mtctx.library:zappy-core:1.2.0")
    ksp("dev.mtctx.library:zappy-processor:1.2.0")
}
```

> **Important**: KSP must be applied for the processor to run.

-----

## Example Usage

```kotlin
import dev.mtctx.zappy.mock

@Mock
data class User(
    @Name val username: String,
    @Email val email: String,
    @Numeric(":1-2") val age: Int // Min 1, Max 2 digits, e.g. 1, 2, ... 12, 35, ...
)

fun main() {
    val user = mock<User>()
    println(user)
    // → User(username=7g1iH7VlMBM2C, email=7g1iH7VlMBM2C@example.io, age=42)
}
```

> Output is **fully random** — no fixed examples like "alice@example.com".

-----

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
* Default ZPL strings are provided (e.g. `<name>`)
* Call `mock<Profile>()` anywhere

-----

### How to Create a Custom Annotation

```kotlin
@ZappyAnnotation
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class FullName(val zpl: String = "<name>_<numeric:1-99>")

@Mock
data class Person(
    @FullName val fullName: String
)
```

> `@ZappyAnnotation` enables KSP processing.
> Must have a `zpl: String` parameter.

-----

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

// Register when using mock (e.g., in main or Application class)
mock<Person>(CreditCardZPLProvider)
```

Now use: `<credit-card:19>` → `"1234-5678-9012-3456"`

-----

## Built-in ZPL Providers

| ID                       | Example Output                         |
|:-------------------------|:---------------------------------------|
| `<name>`                 | `k9PxM2vN`                             |
| `<email>`                | `Bc34QQ6grdHQ0ozz@2noJRHYKqklOFZ.com`  |
| `<domain>`               | `aa2fa5.net`                           |
| `<numeric>`              | `42`                                   |
| `<password>`             | `e*<PyXvp]B`                           |
| `<token>`                | `YWEeWGC3Fd4Fk-H`                      |
| `<uuid>`                 | `f47ac10b-58cc-4372-a567-0e02b2c3d479` |
| `<iso-date>` (Base Only) | `2025-10-29T14:30:22Z`                 |
| `<phone-number>`         | `+1017079879685250`                    |
| `<url>`                  | `http://2ikEu.io`                      |

> **Note**: `<iso-date>` requires a custom provider (or `ZPLDateProvider` implementation) to be functional.
> See [Custom ZPLProvider](#how-to-create-a-custom-zplprovider).

-----

## Documentation

Full API reference (Dokka):
[https://zappy.apidoc.mtctx.dev](https://zappy.apidoc.mtctx.dev)

-----

## Contributing

Contributions are **very welcome**\!
See [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE\_OF\_CONDUCT.md](CODE_OF_CONDUCT.md).

-----

## License

Zappy is **free software** under the **GNU GPL v3**.
You can use, modify, and distribute it — as long as it remains free.

Copyright (C) 2025 mtctx

-----

> **Zappy: Fake data, real fast.**