---
name: compose-screen
description: ConQuest's Jetpack Compose conventions — adding or editing a screen, wiring a navigation route, building a form, and reusing the My* component library. Use when asked to add a screen, add a page, change the UI, add a form or dialog, add a navigation destination, or build any Compose widget in this project.
---

# Compose screens and components in ConQuest

Package root: `app/src/main/java/com/maeldev/conquest/`.

## Golden rule: reuse before you build

`components/` already holds the widget vocabulary. **Read the folder before writing any raw
Compose element**: `MyInputField`, `MyButton`, `MyColumn`, `MyLazyColumn`, `MyOuterBox`,
`MyTopAppBar`, `MyHeaderText`, `MySectionLabel`, `MyIcon`, `MyImageBox`, `MyPhotoGrid`,
`MyPhotoPreview`, `MyEmptyState`, `MyStatusChip`, `MyStatusSegmentedRow`, `MySwitchCard`,
`MyDropdownMenu`, `MyConfirmationDialog`, `MySaveCancelRow`, `MySnackbarHost`,
`MyFloatingActionButton`, `MyAddFab`, `MySelectionModeFabs`, `MySelectionCountLabel`,
`MyBackgroundImage`, `DatePicker`, `SearchBar`, `Drawer`.

If the widget does not exist, create it in `components/` as a reusable `My*` composable — never
inline a one-off in a screen file.

## Screens

Screens live in `screens/` (`screens/cosplay/` for the cosplay flow). Keep them stateless: read
ViewModel state, hand events back up. No database calls, no business logic.

```kotlin
val taskViewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
```

Always pass `AppViewModelProvider.Factory` — plain `viewModel()` will not resolve these
constructors.

## Navigation

Routes are type-safe `kotlinx.serialization` classes declared **in the screen's own file**:

```kotlin
@Serializable
data class NewTask(val cosplayId: Int)
```

Then wire it in `components/Navigation.kt`:

```kotlin
composable<NewTask> { backStackEntry ->
    val args = backStackEntry.toRoute<NewTask>()
    NewTask(cosplayId = args.cosplayId, navController = navController)
}
```

Argument-free routes skip the `toRoute` line. A route class with no `composable<>` entry compiles
fine and fails at runtime — add both. Drawer/bottom entries also need an item in
`components/NavigationItem.kt`.

## Forms

Form screens share one pattern (`screens/cosplay/NewTask.kt` and `EditTask.kt` are the templates):

- A `*FormState` data class in `data/classes/` owns the fields and the validation:
  `fromEntity(entity)`, `val isValid`, `toEntity(...)` for create, `toUpdatedEntity(current)` for
  edit. Keep trimming and `requireNotNull` checks in there, not in the screen.
- The screen holds `val baseline = remember { ... }` and `var form by remember { mutableStateOf(baseline) }`;
  dirtiness is `form != baseline`.
- Save/cancel plumbing goes in one `FormActions(snackbarHostState, isDirty, onCancel, onCommit, postCommit)`
  (`data/classes/FormActions.kt`) passed to the matching `*FormContent` composable in `components/`.
- Cancel on a dirty form goes through `rememberDiscardChangesGuard` (`components/MyDiscardChangesGuard.kt`).

## Style rules

- **Every dimension comes from `theme/UIConsts.kt`** (`paddingM`, `cornerRadiusL`, `imageSizeM`,
  `formBottomInset`, …). No literal `.dp` or `.sp` in screens or components; add a named token if
  one is missing. Colours come from `MaterialTheme.colorScheme` / `theme/Color.kt`.
- **Explicit imports at the top of the file.** Never call something fully-qualified inline
  (`com.maeldev.conquest.components.MyButton()`).
- No side effects in composable bodies — use `LaunchedEffect` / `DisposableEffect`, or move it to
  the ViewModel. `remember` / `rememberSaveable` for state that must survive recomposition.
- `@Composable` functions are PascalCase; detekt and ktlint are configured to allow it.
- Keep lines under 140 columns.

## Then

Run the `verify-changes` gate. Compose-heavy changes are worth a manual look too — the `run`
skill launches the app.
