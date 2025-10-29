# Contributing to Zappy

Hey there!

We’re **thrilled** you want to contribute to **Zappy** — the fast, type-safe, KSP-powered mock data generator for
Kotlin.  
Your help makes it better for everyone. By contributing, you agree to follow our **[Code of Conduct](CODE_OF_CONDUCT.md)
** — in short: **be kind, be respectful, be awesome**.

---

## How You Can Help

Even small contributions go a long way:

* **Report bugs** – Found a weird mock? Let us know!
* **Submit code** – Add new ZPL providers, fix bugs, improve performance
* **Improve docs** – Make the README, examples, or API clearer
* **Add examples** – Show off cool use cases in `zappy-test`
* **Suggest features** – Want `<hex-color>`? `<iban>`? Open an issue!

---

## Getting Started

### Set Up Your Development Environment

1. **Install prerequisites**
    - **JDK 17+** (Zappy uses Kotlin 1.9+)
    - **IntelliJ IDEA** (recommended) or any IDE with Kotlin + Gradle support

2. **Clone the repo**

   ```bash
   git clone https://github.com/mtctx/zappy.git
   cd zappy
   ```

3. **Open in your IDE**
    - Open the root `build.gradle.kts`
    - Let Gradle sync (it will download KSP, KotlinPoet, etc.)

4. **Build & Test**

   ```bash
   ./gradlew build
   ```

   This compiles all modules and runs the KSP processor.

5. **Run the example**

   ```bash
   ./gradlew :zappy-test:run
   ```

   You’ll see:
   ```
   Test(username=7g1iH7VlMBM2C)
   ```

---

## Reporting a Bug

Help us fix it fast! When opening an issue on [GitHub Issues](https://github.com/mtctx/zappy/issues):

* **Clear title** – e.g., `mock<User>() generates invalid email with @Name(":")`
* **Steps to reproduce** – Code snippet + build command
* **Expected vs Actual** – What should happen? What did?
* **Zappy version** – `1.0.0`, `main`, or commit SHA
* **Logs/screenshots** – KSP warnings, stack traces, etc.

---

## Submitting Code Changes

We **love PRs**! Here’s the flow:

### Pull Request Process

1. **Fork** the repo on GitHub
2. **Create a branch**:
   ```bash
   git checkout -b feature/hex-color-provider
   # or
   git checkout -b fix/email-validation
   ```
3. **Make your changes**
    - Add tests in `zappy-test` if possible
    - Update docs if adding public APIs
4. **Commit clearly**:
   ```bash
   git commit -m "feat: add <hex-color> ZPL provider"
   ```
5. **Push & open PR** to `main`
6. **Link issues**: Use `Closes #123` if applicable

---

### Code Style

* Follow **Kotlin Coding Conventions**
* Use **4-space indentation**
* Format with **IntelliJ’s default Kotlin formatter**
* Keep functions small and focused
* Add KDocs for public APIs

> No linter config needed — just keep it clean and consistent.

---

## Project Structure (Quick Guide)

```
zappy/
├── zappy-core/          → ZPL engine, annotations, providers
├── zappy-processor/     → KSP code generation
├── zappy-test/          → Example usage + integration tests
└── build.gradle.kts     → Multi-module config
```

---

## Testing Your Changes

```bash
# Full build + KSP + tests
./gradlew build

# Just run the example
./gradlew :zappy-test:run
```

The KSP processor will regenerate `MockProviders.kt` on every build.

---

## Need Help?

* **Stuck on setup?** → Open an issue
* **Idea to discuss?** → Start a [Discussion](https://github.com/mtctx/zappy/discussions)
* **Want to pair?** → Ping `@mtctx` on GitHub

---

## Thank You

Every contribution — from a typo fix to a new provider — makes Zappy better.  
**You’re making Kotlin testing smoother for everyone.**

Let’s build something awesome together!