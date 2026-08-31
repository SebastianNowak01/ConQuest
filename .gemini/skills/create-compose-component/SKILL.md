---
name: create-compose-component
description: >-
  Use this skill when the user asks to create a new reusable Jetpack Compose UI component.
  Guides naming conventions, file placement, and stateless design patterns for the ConQuest project.
---

# Skill: Create Compose Component

## Description
This skill provides a standardized way to create a new, reusable Jetpack Compose UI component for the ConQuest project, ensuring adherence to the project's architecture and styling guidelines.

## Instructions
When requested to create a new UI component, follow these exact steps:

1. **Verify Need**: Check the `app/src/main/java/com/maeldev/conquest/components/` directory first. Does a similar component already exist?
   - **Existing components list**: `MyInputField`, `MyLazyColumn`, `MyTopAppBar`, `MyImageBox`, `MyIcon`, `MyButton`, `MyColumn`, `MyOuterBox`, `MyAddFab`, `MyFloatingActionButton`, `MyHeaderText`, `MyConfirmationDialog`, `MyCosplayRow`, `MyPhotoGrid`, `MyPhotoPreview`, `MySaveCancelRow`, `MySelectionModeFabs`, `MyExportSelectionModeFabs`, `MySnackbarHost`, `MySwitchCard`, `MyBackgroundImage`, `SearchBar`, `DatePicker`.
   - If a similar component exists, inform the user and suggest reusing or modifying it instead.
2. **Naming Convention**: Name the file and the composable function starting with `My` (e.g., `MyCard.kt`, `MyDropdown.kt`).
3. **Location**: Always create the component inside `app/src/main/java/com/maeldev/conquest/components/`.
4. **Implementation Rules**:
   - Use a `@Composable` function.
   - Make it as stateless as possible by passing in parameters for data and lambda functions for events (`onClick`, `onValueChange`).
   - Use proper imports at the top of the file. **NEVER use inline importing** (e.g., do not use `androidx.compose.material3.Text(...)` in the body, import it instead).
   - Provide a default `Modifier = Modifier` parameter to allow the caller to customize the component's layout behavior.
   - Include a `@Preview` composable function at the bottom for easy layout verification in Android Studio.

## Example Output Structure
```kotlin
package com.maeldev.conquest.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MyNewComponent(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Implementation
}

@Preview(showBackground = true)
@Composable
fun MyNewComponentPreview() {
    MyNewComponent(text = "Preview Text")
}
```
