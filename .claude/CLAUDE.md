# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a monorepo of 59+ Android/Kotlin Multiplatform (KMP) support libraries published under `org.ccci.gto.android`. Modules are standalone and can be depended on individually.

## Build Commands

```bash
# Build all modules
./gradlew assemble

# Run unit tests
./gradlew test jvmTest

# Run iOS tests (KMP modules with iOS targets)
./gradlew iosX64Test

# Run JavaScript tests
./gradlew jsBrowserTest jsNodeTest

# Code coverage report
./gradlew koverXmlReport

# Kotlin style (ktlint)
./gradlew :build-logic:ktlintCheck ktlintCheck

# Java style (checkstyle)
./gradlew checkstyle

# Android lint
./gradlew lint

# Run a single module's tests
./gradlew :gto-support-<module>:test

# Run tests with sharding (CI pattern)
./gradlew test -PtestShard=1 -PtestTotalShards=2

# Release build
./gradlew assemble -PreleaseBuild=true

# Publish to Artifactory
./gradlew publish
```

## Architecture

### Module Categories

- **`gto-support-androidx-*`** — Thin wrapper/extension libraries for AndroidX components (activity, collection, compose, core, fragment, lifecycle, room, viewpager2, etc.)
- **Core**: `gto-support-base`, `gto-support-core`, `gto-support-compat`, `gto-support-util`, `gto-support-dagger`, `gto-support-api-base`, `gto-support-db`
- **Networking**: `gto-support-api-okhttp3`, `gto-support-okhttp3`, `gto-support-retrofit2`, `gto-support-jsonapi*`, `gto-support-scarlet*`
- **UI**: `gto-support-compose` (KMP), `gto-support-androidx-compose`, `gto-support-circuit`
- **Serialization**: `gto-support-moshi`, `gto-support-parcelize`
- **Third-party integrations**: firebase-crashlytics, okta, picasso, lottie, realm, facebook, kermit, eventbus
- **Testing utilities**: `testing/gto-support-dagger`, `testing/gto-support-picasso`, `testing/gto-support-timber`

### KMP Targets

Multiplatform modules support combinations of:
- **Android** — primary target, publishes all library variants
- **iOS** — arm64, x64, simulatorArm64
- **JVM** — for non-Android JVM contexts
- **JS** — browser + Node targets for select modules

### Build Logic (`/build-logic/`)

Convention plugins centralize all Gradle configuration. Apply these instead of configuring directly:

| Plugin ID | Use for |
|-----------|---------|
| `gto-support.android-conventions` | Android library modules |
| `gto-support.android-testing-conventions` | Android testing utilities |
| `gto-support.java-conventions` | Java/JVM-only modules |
| `gto-support.multiplatform-conventions` | KMP modules (no Android target) |
| `gto-support.multiplatform-android-conventions` | KMP modules with Android target |

These plugins configure: compile SDK 36 / min SDK 23 / target SDK 36, Java toolchain 17, 2GB test heap, Robolectric, ktlint, Kover, and publishing.

### Publishing

- Group ID: `org.ccci.gto.android` (testing modules: `org.ccci.gto.android.testing`)
- Target: JFrog Artifactory (snapshots or releases based on version suffix)
- Version managed in `gradle.properties`

### Dependency Management

All versions are centralized in `/gradle/libs.versions.toml`. Key versions:
- Kotlin 2.3.10, AGP 8.13.2, Coroutines 1.10.2
- OkHttp3 5.3.2, Dagger 2.58, Moshi 1.15.2
- Compose 1.10.4, Circuit 0.33.1, Turbine 1.2.1

The root `build.gradle.kts` forces resolution of several dependencies (androidx.annotation, appcompat, core, sqlite, kotlin.coroutines, okio) to prevent version conflicts across modules.

### Adding a New Module

1. Create the module directory with a `build.gradle.kts` applying the appropriate convention plugin
2. Add the module to `settings.gradle.kts`
3. Use `libs.*` version catalog aliases for all dependencies
