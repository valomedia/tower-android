# AGENTS.md – Tower Android

## Scope

These instructions apply to the whole repository.

## Project shape

- This is a single-module Android app.
  The only Gradle module is `:app`.
- Application code lives under `app/src/main/java/media/valo/tower_android`.
  Keep the existing package name with the underscore.
- `MainActivity.kt` and `TowerAndroidApplication.kt` are the Android entry points.
  `ui/TowerApp.kt` wires the Compose navigation graph.
- UI code uses Jetpack Compose,
  Navigation Compose,
  and Material 3.
  Screens live under `ui/routes/<route>`,
  shared UI elements live under `ui/elements`,
  and theme files live under `ui/theme`.
- Data access is split between `data/local` and `data/remote`.
  Repositories wrap data sources,
  and Hilt modules bind concrete implementations.
- Local persistence uses AndroidX DataStore.
  Remote API access uses Ktor clients.
  Calling and video features use Azure Communication Calling and Android camera APIs.
- Unit tests live under `app/src/test`.
  Instrumented Android tests live under `app/src/androidTest`.

## Build and verification

- CI uses JDK 17,
  Gradle 8.13,
  the Android SDK,
  and runs:

  ```shell
  gradle testDebugUnitTest --no-daemon
  ```

- The Gradle wrapper output is intentionally ignored by git:
  `/gradle/wrapper`,
  `/gradlew`,
  and `/gradlew.bat` are in `.gitignore`.
- If `./gradlew` is missing locally,
  bootstrap it with:

  ```shell
  gradle wrapper
  ```

  Then prefer `./gradlew` for follow-up commands.
- Useful verification commands are:

  ```shell
  ./gradlew testDebugUnitTest --no-daemon
  ./gradlew lintDebug --no-daemon
  ./gradlew build --no-daemon
  ```

- `./gradlew connectedDebugAndroidTest --no-daemon` requires a connected device or emulator.
  Do not treat it as a routine local check unless that dependency is available.
- `:app:preBuild` depends on `:wrapper`,
  so normal app builds refresh the wrapper to the Gradle version configured in the root build file.

## Kotlin and Compose conventions

- The project uses official Kotlin code style.
  Keep four-space indentation and follow the formatting already present in nearby files.
- Preserve the repository's file headers in Kotlin,
  Gradle,
  XML,
  and properties files.
  Add a matching header for new source/config files when it fits the surrounding pattern.
- Use `@Serializable` route objects for Navigation Compose destinations,
  matching the existing route files.
- Compose screens should expose preview functions when practical.
  Existing previews use dummy data sources and `AppBarPreview` helpers.
- Keep UI text consistent with the app's current German user-facing copy.
- Prefer small route-specific `ViewModel` classes in the same route package.
  Inject dependencies with Hilt instead of constructing production data sources in UI code.

## Dependency injection and generated code

- Hilt is configured through the Android Gradle plugin and KSP.
  Add or update `@Module`,
  `@InstallIn`,
  `@Provides`,
  and qualifier annotations in source files;
  never hand-edit generated Hilt or KSP output.
- Build output,
  generated sources,
  Gradle caches,
  local SDK paths,
  Android Studio settings,
  and release artifacts are ignored.
  Do not commit files from `.gradle/`,
  `build/`,
  `app/build/`,
  `local.properties`,
  `.idea/`,
  or `app/release/`.

## Testing notes

- Unit tests use JUnit 4.
  Follow the existing backtick test-name style for behavior descriptions.
- Prefer fake or dummy data sources for tests and previews.
  Do not make tests depend on live backend services,
  camera hardware,
  location services,
  or Azure Communication Calling unless the test is explicitly instrumented for that dependency.
