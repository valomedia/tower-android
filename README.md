# Tower Android

Android app for TOWER.

## Build instructions

First, install `gradlew` by running:

```shell
gradle wrapper
```

With `gradlew` installed, you can build the project by running:

```shell
gradlew build
```

You can run Android lint locally with:

```shell
./gradlew lintDebug --no-daemon
```

The gradle wrapper and the gradle version it uses will automatically be updated to the correct
version during each build, if outdated. However, if the version of gradle used during the last build
is too old to start the current build (for example because you haven't built the project for a
while), you might get an error about your gradle version being too old. In this case, just re-run
the first command to get the correct version.
