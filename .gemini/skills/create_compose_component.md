# Skill: Create Compose Component

## Description
This skill provides a standardized way to create a new, reusable Jetpack Compose UI component for the ConQuest project, ensuring adherence to the project's architecture and styling guidelines.

## Instructions
When requested to create a new UI component, follow these exact steps:

1. **Verify Need**: Check the `app/src/main/java/com/example/conquest/components/` directory. Does a similar component already exist? If yes, inform the user and suggest modifying it instead.
2. **Naming Convention**: Name the file and the composable function starting with `My` (e.g., `MyCard.kt`, `MyDropdown.kt`).
3. **Location**: Always create the component inside the `app/src/main/java/com/example/conquest/components/` package.
4. **Implementation Rules**:
    - Use a `@Composable` function.
    - Make it as stateless as possible by passing in parameters for data and lambda functions for events (`onClick`, `onValueChange`).
    - Use proper imports at the top of the file. **NEVER use inline importing** (e.g., do not use `androidx.compose.material3.Text(...)` in the body, import it instead).
    - Provide a default `Modifier = Modifier` parameter to allow the caller to customize the component's layout behavior.

## Example Output Structure
```kotlin
package com.example.conquest.components

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
