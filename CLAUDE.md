# ConQuest — working agreement

Kotlin · Jetpack Compose · Room · MVVM. Module `:app`, package `com.maeldev.conquest`,
min SDK 34 / target 35, JDK 17.

## Skills

Detail lives in `.claude/skills/`. Use them rather than re-deriving the conventions:

| Skill | When |
| --- | --- |
| `verify-changes` | After **any** Kotlin edit — build, unit tests, detekt, ktlint |
| `room-schema-change` | Touching `data/entity/`, `data/dao/`, `CosplayDatabase.kt`, `Migrations.kt` |
| `compose-screen` | Screens, navigation routes, forms, `My*` components |
| `conquest-viewmodel` | Anything in `viewmodel/`, plus its test |
| `sonar-triage` | Sonar issues, quality gate, security hotspots |

## Layer boundaries

```
screens/      stateless Compose; observes state, delegates events
components/   reusable My* widgets + Navigation
viewmodel/    all business logic; AndroidViewModel + injected DAOs
data/         entity/ dao/ database/ classes/ — Room and form state
theme/        colours, type, UIConsts dimension tokens
```

- No database calls or business logic in a composable.
- Every ViewModel must be registered in `AppViewModelProvider.kt`.
- DB work runs in `viewModelScope.launch` off the main thread.

## Non-negotiables

- **Reuse before building.** Read `components/` before writing a raw Compose element. New widgets
  go in `components/` as reusable `My*` composables.
- **Dimensions come from `theme/UIConsts.kt`.** No literal `.dp` / `.sp` outside that file.
- **Explicit imports at the top.** Never a fully-qualified inline call.
- **140-column limit**, enforced by both detekt and ktlint.
- **Never regenerate a lint baseline** to clear a failure, and never add
  `fallbackToDestructiveMigration` to clear a schema failure.

## Comments

Comment only where the reasoning is not recoverable from the code: heavy or non-obvious logic,
and architecture decisions — why this shape, what broke under the previous one. `Migrations.kt`,
`MainDispatcherRule.kt` and `FormActions.kt` are the house style.

Do not narrate self-evident code, restate a function name as a KDoc, or leave step-by-step
running commentary. When in doubt, leave it out.

## Verification

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:detekt :app:ktlintCheck
```

Instrumented tests (`app/src/androidTest`) need an emulator; if none is attached, say they were
not run.

`GEMINI.md` covers the same ground for a different tool — keep the two from contradicting each
other if you change one.
