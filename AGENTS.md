# AGENTS.md – Tower Android

## Project Overview

- This is a single-module Android app for TOWER.
- The Gradle root project is `Tower_Android`; the only module is `:app`.
- App package and namespace: `media.valo.tower_android`.
- Main entry points are `app/src/main/java/media/valo/tower_android/MainActivity.kt`,
  `TowerAndroidApplication.kt`, and `ui/TowerApp.kt`.
- The UI is Jetpack Compose with Navigation Compose and Material 3.
- Dependency injection uses Hilt.
- Local persistence uses AndroidX DataStore under `data/local/preferences`.
- Remote API access uses Ktor under `data/remote`.
- Calling/video features use Azure Communication Calling and Android camera APIs.

## Build and Verification

- If `gradlew` is missing, run `gradle wrapper` first.
  The wrapper files are ignored by git in this repository.
- Use `./gradlew build` for a full local build when the wrapper exists.
- Use `./gradlew testDebugUnitTest --no-daemon` for the unit-test gate.
  CI runs the same task with Gradle 8.13 and JDK 17.
- Use `./gradlew :app:lintDebug` for Android lint when lint feedback is relevant.
- Instrumented tests live under `app/src/androidTest` and require a device or emulator.

## Source Layout

- Production Kotlin source lives under `app/src/main/java/media/valo/tower_android`.
- Unit tests live under `app/src/test/java/media/valo/tower_android`.
- Android resources live under `app/src/main/res`.
- Version catalog dependencies live in `gradle/libs.versions.toml`.
- App configuration is in `app/build.gradle.kts`.

## Coding Conventions

- Kotlin uses the official Kotlin style configured in `gradle.properties`.
- Target Java/Kotlin bytecode is JVM 11.
- Keep the existing file header style when editing Kotlin, XML, Gradle, or config files.
- Prefer small, focused Compose functions and typed navigation routes.
- Keep Hilt modules close to the data source or Android service they provide.
- Do not hand-edit generated build output, `.gradle`, `build`, `app/build`, or `local.properties`.

## Dependencies and Generated Work

- Add or update dependency versions in `gradle/libs.versions.toml`,
  then reference them from Gradle build files through `libs` aliases.
- KSP is used for Hilt code generation.
  Fix generated-code errors by changing source annotations or modules, not generated files.
- Do not commit local Gradle wrapper output unless the repository policy changes;
  `/gradlew`, `/gradlew.bat`, and `/gradle/wrapper` are currently ignored.

## Before Finishing Changes

- Run the smallest relevant Gradle task you can run in the current environment.
- If Gradle or the Android SDK is unavailable, state that clearly and perform direct inspection instead.
- Include verification details in the handoff.
