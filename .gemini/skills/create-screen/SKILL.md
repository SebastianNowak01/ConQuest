---
name: create-screen
description: Scaffold a new screen in ConQuest with ViewModel, MyOuterBox, type-safe navigation, and Drawer integration.
---

# Create Screen

Scaffold a new screen composable in the ConQuest app following the project's architecture and navigation conventions.

## Instructions

### 1. File Location & Package
- Place general screens in `app/src/main/java/com/maeldev/conquest/screens/`
- Place cosplay-specific screens in `app/src/main/java/com/maeldev/conquest/screens/cosplay/`
- Package statement must match the directory: `package com.maeldev.conquest.screens` or `package com.maeldev.conquest.screens.cosplay`

### 2. Define the Route
Define a `@Serializable` route at the top of the screen file:
- **No-arg route**: `@Serializable object MyScreen`
- **Parameterized route**: `@Serializable data class MyScreen(val id: Int)`

### 3. Screen Composable Structure
- Declare the composable function taking `navController: NavController` (and any route parameters).
- Wire up ViewModels using `viewModel(factory = AppViewModelProvider.Factory)`.
- Use `MyOuterBox` as the root layout container for the screen content.
- Never use inline imports (e.g. `androidx.compose.material3.Text`); always add proper top-level imports.
- Import `AppViewModelProvider` from `com.maeldev.conquest.AppViewModelProvider`.

### 4. Register in Navigation
Register the new route in `app/src/main/java/com/maeldev/conquest/components/Navigation.kt` inside the `NavHost` composable:
- **No arguments**:
  ```kotlin
  composable<MyScreen> {
      MyScreen(navController = navController)
  }
  ```
- **With arguments**:
  ```kotlin
  composable<MyScreen> { backStackEntry ->
      val args = backStackEntry.toRoute<MyScreen>()
      MyScreen(
          id = args.id,
          navController = navController,
      )
  }
  ```

### 5. Drawer Integration (If Applicable)
- **Top-level destination**: Add the route to `routes` in `Drawer.kt` and create a matching `NavigationItem` in `NavigationItem.kt`.
- **Form / Detail screen**: If the screen should not show the navigation drawer (e.g., edit or create forms), add its route pattern to `noDrawerRoutes` in `Drawer.kt`.

---

## Minimal Screen Template

### No-Argument Screen
```kotlin
package com.maeldev.conquest.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.viewmodel.CosplayViewModel
import kotlinx.serialization.Serializable

@Serializable
object MyScreen

@Composable
fun MyScreen(
    navController: NavController,
    cosplayViewModel: CosplayViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val cosplays by cosplayViewModel.allCosplays.collectAsState()

    MyOuterBox {
        Text(text = "My Screen Content")
    }
}
```

### Parameterized Screen
```kotlin
package com.maeldev.conquest.screens.cosplay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.viewmodel.CosplayViewModel
import kotlinx.serialization.Serializable

@Serializable
data class MyDetailScreen(val cosplayId: Int)

@Composable
fun MyDetailScreen(
    cosplayId: Int,
    navController: NavController,
    cosplayViewModel: CosplayViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val cosplay by cosplayViewModel.getCosplayById(cosplayId).collectAsState(initial = null)

    MyOuterBox {
        Text(text = "Cosplay ID: $cosplayId")
    }
}
```
