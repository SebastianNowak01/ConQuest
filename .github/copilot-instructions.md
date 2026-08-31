# Copilot Instructions - ConQuest Android App

## Project Overview
ConQuest is a Kotlin + Jetpack Compose Android app.
It is a cosplay management tracker using Room, a single shared ViewModel, type-safe
Compose Navigation, and a custom reusable component library.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material3)
- **Navigation**: Navigation Compose with type-safe serializable routes
- **Database**: Room (with `DateConverter` type converter)
- **Architecture**: MVVM with one `CosplayViewModel : AndroidViewModel`
- **Async**: Kotlin Coroutines + `viewModelScope`
- **State**: `StateFlow`, `MutableStateFlow`, `collectAsState`

## Package Structure
```text
com.example.conquest
|- components/         # Reusable UI components
|- data/
|  |- classes/         # Form state data classes (XxxFormState)
|  |- dao/             # Room DAOs
|  |- database/        # CosplayDatabase (RoomDatabase)
|  `- entity/          # Room entity data classes
|- screens/
|  |- cosplay/         # Cosplay-scoped screens
|  |- MainScreen.kt
|  `- SettingsScreen.kt
|- ui/theme/           # Color.kt, Theme.kt, Type.kt, UIConsts
|- Application.kt      # ConQuestApplication (owns database instance)
|- CosplayViewModel.kt # Single ViewModel for data operations
`- MainActivity.kt     # Entry point
```

## Import Rules (Critical)
- **Never use wildcard imports** (for example, `import androidx.compose.material3.*`).
- Every import must be explicit.

Correct:
```kotlin
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
```

Incorrect:
```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
```

## Naming Conventions
| Category | Pattern | Example |
| --- | --- | --- |
| Reusable components | `My` prefix | `MyColumn`, `MyInputField` |
| DAOs | `<Entity>Dao` | `CosplayDao`, `EventDao` |
| Entities | PascalCase plural | `Cosplays`, `CosplayElements` |
| Form states | `<Entity>FormState` | `ElementFormState` |
| Screen composables | Match route class name | `NewElement`, `EditElement` |
| One-shot DAO queries | `Once` suffix | `getPhotosForCosplayOnce` |

## Navigation (Type-Safe)
- Define each screen route as its own `@Serializable` data class at the top of that screen file.
- Register routes centrally in `components/Navigation.kt`.
- Navigate with `navController.navigate(RouteClass(param))`.
- Return with `navController.popBackStack()`.

```kotlin
@Serializable
data class NewElement(val cosplayId: Int)

@Composable
fun NewElement(cosplayId: Int, navController: NavController) { ... }
```

## Screen/Form Layout Pattern
Every create/edit form screen should follow this structure:

```kotlin
MyOuterBox {
    MyColumn {
        MyHeaderText(text = "Screen Title")

        MyInputField(
            value = form.fieldName,
            onValueChange = { form = form.copy(fieldName = it) },
            label = "Field Label",
            singleLine = true,
        )

        MySwitchCard(
            label = "Toggle Label",
            checked = form.boolField,
            onCheckedChange = { form = form.copy(boolField = it) },
        )

        MyImageBox(
            photoPath = form.photoPath,
            contentDescription = "Description",
            size = UIConsts.imageSizeS,
            clickable = true,
            onClick = { launcher.launch("image/*") },
        )
    }

    MySaveCancelRow(
        snackbarHostState = snackbarHostState,
        isValid = form.isValid,
        onCancel = { navController.popBackStack() },
        onCommit = { /* call viewModel insert/update */ },
        postCommit = { navController.popBackStack() },
    )

    MySnackbarHost(hostState = snackbarHostState)
}
```

## Form State Pattern
Form states live in `data/classes/`. Each should be a data class that includes:

| Member | Purpose |
| --- | --- |
| `val isValid: Boolean` | Computed validation state |
| `fun toEntity(...)` | Convert form to Room entity for insert |
| `fun toUpdatedEntity(existing)` | Merge form into an existing entity for update |
| `companion object { fun fromEntity(e): FormState }` | Convert entity to form state |

Example usage:
```kotlin
var form by remember { mutableStateOf(ElementFormState()) }

// Field update
form = form.copy(name = it)

// Load for edit
LaunchedEffect(element?.id) {
    element?.let { form = ElementFormState.fromEntity(it) }
}

// Save
cosplayViewModel.insertElement(form.toEntity(cosplayId = cosplayId, id = 0))

// Update
cosplayViewModel.updateElement(form.toUpdatedEntity(element ?: return@MySaveCancelRow))
```

## ViewModel (CosplayViewModel)
- Use exactly one `AndroidViewModel` (`CosplayViewModel`). Do not add additional ViewModels.
- Access DAOs via `(application as ConQuestApplication).database.<dao>()`.
- Expose list state as `StateFlow` using `stateIn(viewModelScope, SharingStarted.Lazily, emptyList())`.
- For cosplay-scoped streams, use `MutableStateFlow<Int?>` + `filterNotNull()` + `flatMapLatest`.
- Use `viewModelScope.launch { ... }` for all writes.
- Use top-level `deleteFileByPath(path: String)` for file deletion.

```kotlin
private val _cosplayId = MutableStateFlow<Int?>(0)

val photos: StateFlow<List<CosplayPhoto>> =
    _cosplayId
        .filterNotNull()
        .flatMapLatest { id -> photoDao.getPhotosForCosplay(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

fun setCosplayId(id: Int) {
    _cosplayId.value = id
}
```

## Database (Room)
- Use one `CosplayDatabase : RoomDatabase()` from `ConQuestApplication.database`.
- Apply `@TypeConverters(DateConverter::class)` at the database level.
- Keep DAOs in `data/dao/` with `@Dao`.
- Keep entities in `data/entity/` with `@Entity`.
- Primary key convention: `@PrimaryKey(autoGenerate = true) val id: Int = 0`.
- Reactive queries should return `Flow<...>`.
- One-shot queries should be `suspend` and use the `Once` suffix.

## Custom Component Reference
Always use project components instead of raw Material3 widgets in screens.

| Component | Purpose |
| --- | --- |
| `MyOuterBox` | Root container for each screen |
| `MyColumn` | Scrollable column with standard spacing/padding |
| `MyHeaderText` | Screen or section heading |
| `MyInputField` | Styled input field (optional decimal filter) |
| `MySaveCancelRow` | Save/cancel row with snackbar integration |
| `MySnackbarHost` | Snackbar host (bottom of `MyOuterBox`) |
| `MySwitchCard` | Labeled card switch |
| `MyImageBox` | Image preview with placeholder and click support |
| `MyLazyColumn` | Standard lazy list |
| `MyAddFab` | Add FAB |
| `MyDeleteFab` | Delete FAB |
| `MyTopAppBar` | Top app bar with search/menu/back |
| `MyFloatingActionButton` | Base FAB wrapper |
| `MyIcon` | Standard icon wrapper |
| `Drawer` | Navigation drawer |
| `SearchBar` | Top bar search input |

## UIConsts
Never hardcode `dp` values in screens. Use `UIConsts` values:

```kotlin
UIConsts.cornerRadiusL
UIConsts.cornerRadiusM
UIConsts.spacingS
UIConsts.spacingM
UIConsts.imageSizeS
UIConsts.imageSizeM
UIConsts.heightM
```

## Image Handling
- Pick images with `rememberLauncherForActivityResult(ActivityResultContracts.GetContent())`.
- Save with `saveImageUriToInternalStorage(context, uri, fileNamePrefix)`.
- Store only the file path (`String`) in entities.
- Always handle both `.onSuccess { ... }` and `.onFailure { ... }`.
- When replacing/deleting an image, call `deleteFileByPath(oldPath)`.

```kotlin
val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
) { uri: Uri? ->
    uri?.let {
        saveImageUriToInternalStorage(context, it, "prefix")
            .onSuccess { path ->
                form = form.copy(photoPath = path)
            }
            .onFailure { e ->
                scope.launch {
                    snackbarHostState.showSnackbar("Failed: ${e.localizedMessage}")
                }
            }
    }
}
```

## Theme and Colors
- Wrap app content in `ConQuestTheme` in `MainActivity`.
- Resolve theme mode via `rememberThemePreference(context)`.
- Use `MaterialTheme.colorScheme.*` inside components.
- Keep color/typography/shape definitions in `ui/theme/` only.
- Keep content centered with `widthIn(max = 600.dp)` for tablet support.

## State and Side Effects
| Pattern | Use case |
| --- | --- |
| `remember { mutableStateOf(...) }` | Local transient UI state |
| `rememberSaveable { mutableStateOf(...) }` | State across configuration changes |
| `collectAsState(initial = null)` | Collect `Flow` or `StateFlow` in composables |
| `LaunchedEffect(key)` | Keyed side effects (for example, load entity into form) |
| `rememberCoroutineScope()` | Launch coroutines from callbacks (for example, snackbar) |

## Error Handling and Feedback
- Use `SnackbarHostState` + `MySnackbarHost` for user-facing messages.
- Use `rememberCoroutineScope()` for `showSnackbar(...)` calls from callbacks.
- Enforce `form.isValid` before save; `MySaveCancelRow` should disable save if invalid.

## Code Style
- Composable function names are PascalCase.
- Callback names use `on<Action>` (for example, `onCancel`, `onCommit`, `onSearchQueryChange`).
- Use `postCommit` in `MySaveCancelRow` for post-save navigation.
- Obtain ViewModel in composables as: `val vm: CosplayViewModel = viewModel()`.
- Prefer delegated state (`by`) with `remember`/`collectAsState` over direct `.value` access.
- Keep screens thin: business logic in `CosplayViewModel`, UI logic in components.
