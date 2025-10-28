# Squishy – Unified Kotlin API for PaperMC & SpongeMC

[![License: GPL-3.0](https://img.shields.io/badge/License-GPL%203.0-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

---

## What is Squishy?

Squishy is a modular, Kotlin-based API designed to make Minecraft plugin development easier and more consistent across
**PaperMC** and **SpongeMC**.

It provides a **shared core** with flexible platform integrations, letting developers write powerful, maintainable
plugins with **less boilerplate** and **more control** — all while keeping clean separation between platforms.

---

## Modules

| Module     | Description                                                                                                      |
|------------|------------------------------------------------------------------------------------------------------------------|
| **Core**   | The foundation of Squishy — provides shared APIs, abstractions, and utilities used across all platforms.         |
| **Lumina** | Logging implementation for [**Lumina**](https://github.com/mtctx/Lumina) to use it directly inside your plugins. |
| **Paper**  | Squishy integration for **PaperMC** plugins, extending the core to work seamlessly in Bukkit-based environments. |
| **Sponge** | Squishy integration for **SpongeMC** plugins, built for flexibility and alignment with the Sponge API design.    |

---

## Features

* 🧩 Modular architecture (Core + platform-specific extensions)
* 🪶 Simple Kotlin-based API design
* ⚙️ Common abstractions for PaperMC and SpongeMC
* 📚 Dokka-based documentation for clean, readable API references
* 🧠 Built with developer experience and consistency in mind

---

## Installation

Squishy will be available via **Maven Central** once published.

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.mtctx.library:squishy-core:1.0.0")  // needed by every other library
    implementation("dev.mtctx.library:squishy-paper:1.0.0+mc1.21.8") // for PaperMC - MC 1.21.8 is the current supported MC Version for Paper
    implementation("dev.mtctx.library:squishy-sponge:1.0.0+api13.0") // for SpongeMC - API 13.0 is the current supported SpongeMC API
    implementation("dev.mtctx.library:squishy-lumina:4.0.0") // Lumina is my custom Logger, this module provides a direct implementation for it and exposes lumina api for further custom usage. The version is always the same as the lumina version used (e.g. Lumina 4.0.0 -> Module Version 4.0.0)
}
```

### Maven

```xml

<dependency>
    <groupId>dev.mtctx.library</groupId>
    <artifactId>squishy-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Example Usage

```kotlin
import mtctx.squishy.core.*

class ExampleFeature {
    fun register() {
        // Use shared Squishy abstractions here
        Squishy.log("Hello from Squishy!")
    }
}
```

For PaperMC or Sponge-specific code, simply use their API.

---

## Documentation

Full API reference is available at:
👉 [https://squishy.apidoc.mtctx.dev](https://squishy.apidoc.mtctx.dev)

---

## Contributing

Contributions are welcome!
See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for community
standards.

---

## License

Squishy is free software under the **GNU GPL v3**.
You can use it, modify it, and distribute it — as long as it remains free.