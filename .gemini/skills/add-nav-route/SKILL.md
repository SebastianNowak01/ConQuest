---
name: add-nav-route
description: Add a type-safe navigation route to ConQuest, including NavHost registration, Drawer setup, and parameter handling.
---

# Add Navigation Route

Add and configure type-safe navigation routes using Jetpack Navigation with Kotlin Serialization in the ConQuest app.

## Instructions

### 1. Define the Route
Define the `@Serializable` route inside the corresponding screen file (`screens/` or `screens/cosplay/`):
- **No arguments**:
  ```kotlin
  @Serializable
  object MyScreen
  ```
- **With arguments**:
  ```kotlin
  @Serializable
  data class MyDetailScreen(val cosplayId: Int)
  ```

### 2. Register Route in NavHost
In `app/src/main/java/com/maeldev/conquest/components/Navigation.kt`, import the screen and route, then add a `composable` destination inside `NavHost`:

- **No arguments**:
  ```kotlin
  composable<MyScreen> {
      MyScreen(navController = navController)
  }
  ```

- **With arguments**:
  ```kotlin
  composable<MyDetailScreen> { backStackEntry ->
      val args = backStackEntry.toRoute<MyDetailScreen>()
      MyDetailScreen(
          cosplayId = args.cosplayId,
          navController = navController,
      )
  }
  ```

### 3. Top-Level Drawer Destinations
If the screen is a top-level destination accessible from the navigation drawer:

1. **Add to `routes` list** in `app/src/main/java/com/maeldev/conquest/components/Drawer.kt`:
   ```kotlin
   val routes = listOf(
       MainScreen,
       SettingsScreenParams,
       Events,
       MyScreen, // Add here
   )
   ```
2. **Define `NavigationItem`** in `app/src/main/java/com/maeldev/conquest/components/NavigationItem.kt`:
   ```kotlin
   val navigationItems = listOf(
       // ... existing items
       NavigationItem(
           title = "My Screen",
           selectedIcon = Icons.Filled.Star,
           unselectedIcon = Icons.Outlined.Star,
       ),
   )
   ```

### 4. Hide Drawer for Child / Form Screens
If the screen should **not** display the navigation drawer (e.g. create/edit forms or sub-details), add the full route signature string to `noDrawerRoutes` in `app/src/main/java/com/maeldev/conquest/components/Drawer.kt`:

```kotlin
val noDrawerRoutes = listOf(
    "com.maeldev.conquest.screens.cosplay.NewCosplay",
    "com.maeldev.conquest.screens.cosplay.EditEvent/{eventId}",
    "com.maeldev.conquest.screens.cosplay.MyDetailScreen/{cosplayId}", // Qualified class name + path params
)
```

### 5. Trigger Navigation
Navigate to the route from any composable using the `NavController`:
- **No arguments**: `navController.navigate(MyScreen)`
- **With arguments**: `navController.navigate(MyDetailScreen(cosplayId = 123))`
- **Pop back stack**: `navController.popBackStack()`
