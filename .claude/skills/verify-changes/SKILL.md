---
name: verify-changes
description: Build, test and lint ConQuest before calling any change done. Use after editing any Kotlin file, when asked to "check", "verify", "run the tests", "does it build", "run detekt/ktlint", or before opening a PR. Mirrors the CI in .github/workflows/build_and_test.yml.
---

# Verify a change to ConQuest

Run from the repo root. Gradle wrapper is `./gradlew`; the project targets JDK 17.

## The gate

Run these in order and stop at the first failure:

```sh
./gradlew :app:assembleDebug                  # compiles main sources + KSP (Room)
./gradlew :app:testDebugUnitTest              # JVM/Robolectric tests in app/src/test
./gradlew :app:detekt :app:ktlintCheck        # exactly what CI runs
```

A single pass is fine once you know the change is close:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:detekt :app:ktlintCheck --continue
```

`--continue` reports every failure instead of only the first — useful on a wide change.

## Fixing lint

- Formatting only: `./gradlew :app:ktlintFormat` rewrites the files, then re-run `ktlintCheck`.
- Detekt findings are not auto-fixable. Fix the code.
- **Line length is 140 columns** in both tools. `config/detekt/detekt.yml` sets
  `style.MaxLineLength.maxLineLength: 140` so it agrees with ktlint's android mode.

## Baselines — do not regenerate

`config/detekt/baseline.xml` and `config/ktlint/baseline.xml` grandfather findings that predate
the current rules. Their whole purpose is that *new* findings fail the build.

Fix what you introduced. Never run `detektBaseline` / `ktlintGenerateBaseline` to make a failure
go away — that silently absolves every other outstanding finding too. If a baseline genuinely
needs to change, say so and let the user decide.

## Instrumented tests

`app/src/androidTest` (including `ConQuestDatabaseTest`, `MigrationTest`, `ExportImportTest`,
`MyInputFieldTest`) needs a connected device or running emulator:

```sh
adb devices                                   # confirm one is attached first
./gradlew :app:connectedDebugAndroidTest
```

If no device is available, **say the instrumented tests were not run** rather than reporting the
change as fully verified. This matters most after a Room schema change — see the
`room-schema-change` skill.

## Reporting

State what passed and what did not, with the failing output. "Builds, unit tests and lint pass;
instrumented tests not run (no emulator)" is an honest result. "Verified" is not, unless
everything relevant actually ran.
