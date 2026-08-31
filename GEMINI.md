# ConQuest Android App - AI Assistant Guidelines

## Tech Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose
- **Database**: Room Database
- **Architecture**: MVVM (Model-View-ViewModel)

## Architectural Patterns
- **MVVM**: The app strictly follows the Model-View-ViewModel pattern.
  - **Models**: Located in `app/src/main/java/com/example/conquest/data/entity`.
  - **DAOs**: Located in `app/src/main/java/com/example/conquest/data/dao`.
  - **ViewModels**: Keep business logic inside ViewModels (e.g., `CosplayViewModel.kt`). State should be exposed as Flows or Compose State.
  - **Views (Screens)**: Located in `app/src/main/java/com/example/conquest/screens/`. Screens should be as stateless as possible, observing ViewModel state and delegating actions to the ViewModel.

## UI Components
- **Components Folder**: All reusable UI components are located in `app/src/main/java/com/example/conquest/components/`.
- **Golden Rule**: **Always check the `components` folder first** before creating a new UI element. Use available components (e.g., `MyInputField`, `MyLazyColumn`, `MyTopAppBar`, `MyImageBox`, `MyIcon`, etc.).
- **Creating Components**: If a required component does not exist in the `components` folder, you must create it there and design it to be reusable.

## Kotlin & Compose Best Practices
- Use modern Kotlin features (Coroutines, Flows, Data Classes, Extension Functions).
- Maintain unidirectional data flow in Compose: State flows down, events flow up.
- Use `remember` and `rememberSaveable` to cache expensive operations and maintain state across recompositions or configuration changes where appropriate.
- Avoid side effects inside composables. Use `LaunchedEffect`, `DisposableEffect`, or delegate to the ViewModel.
- Database operations (Room) should always be executed off the main thread using Kotlin Coroutines (e.g., `Dispatchers.IO`).
- Write clean, concise, and self-documenting code.
- **Never use inline importing** (e.g., `com.example.conquest.components.MyComponent()`). Always add the import statement at the top of the file and use the component/class directly.

## Project Structure Overview
- `app/src/main/java/com/example/conquest/components/` - Reusable Compose UI widgets and navigation helpers.
- `app/src/main/java/com/example/conquest/screens/` - Complete UI screens (e.g., `MainScreen.kt`, `SettingsScreen.kt`, and the `cosplay` subfolder).
- `app/src/main/java/com/example/conquest/data/` - Room database setup (`database/`), DAOs (`dao/`), Entities (`entity/`), and utility classes.
- `app/src/main/java/com/example/conquest/ui/` - Theming, typography, and color definitions.
- `CosplayViewModel.kt` - Example of a central ViewModel managing application state.

## Core Directives for the AI
1. **Reuse First**: Look for `My[Component].kt` before making raw Compose elements.
2. **Strict MVVM**: Never put database calls or complex business logic directly in Compose screens.
3. **Keep it Kotlin**: Use idiomatic Kotlin styles and standard library functions.
