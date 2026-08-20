# Tower Android

Android app for TOWER.

## Build instructions

First, install `gradlew` by running:

```shell
gradle :wrapper
```

With `gradlew` installed, you can build the project by running:

```shell
gradlew build
```

You can run Android lint locally with:

```shell
./gradlew lintDebug --no-daemon
```

The gradle version is pinned in `gradle/wrapper/gradle-wrapper.properties`, which is the only
wrapper file checked into the repository. The wrapper jar and the `gradlew` scripts are generated
locally and stay untracked, so change the gradle version by editing `distributionUrl` in that file.
Renovate proposes gradle updates the same way.

Gradle 9.5.x is the highest version the current build can use: the Android Gradle Plugin 8.x binds
to a gradle internal API that gradle removed in 9.6.0, and 8.13.2 is the last 8.x release, so
moving past gradle 9.5.x means migrating to the Android Gradle Plugin 9.

The gradle wrapper and the gradle version it uses will automatically be updated to the correct
version during each build, if outdated. However, if the version of gradle used during the last build
is too old to start the current build (for example because you haven't built the project for a
while), you might get an error about your gradle version being too old. In this case, just re-run
the first command to get the correct version.
