---
name: create-form-state
description: >-
  Use this skill when creating or updating form state data classes for entity creation and editing screens in ConQuest.
---

# Skill: Create Form State

## Description
This skill provides guidelines and patterns for implementing form state management data classes in ConQuest. Form state classes encapsulate UI input fields, validation rules, type conversions, and transformations to/from Room entities.

## Instructions

When creating or modifying a form state for an entity, follow these steps:

1. **Location & Naming**:
   - Directory: `app/src/main/java/com/maeldev/conquest/data/classes/`
   - File name: `<Entity>FormState.kt` (e.g., `EventFormState.kt`, `ElementFormState.kt`, `CosplayFormState.kt`, `TaskFormState.kt`)

2. **Structure & Template**:
   ```kotlin
   package com.maeldev.conquest.data.classes

   import com.maeldev.conquest.components.getCurrentDate
   import com.maeldev.conquest.data.entity.Entity
   import java.util.Date

   data class EntityFormState(
       val fieldName: String = "",
       val dateField: Date? = getCurrentDate(),
       val budget: String = "",
       // ... all form fields with sensible defaults
   ) {
       companion object {
           /**
            * Converts a Room entity into form state for edit screens.
            */
           fun fromEntity(entity: Entity): EntityFormState {
               return EntityFormState(
                   fieldName = entity.fieldName,
                   dateField = entity.dateField,
                   budget = entity.budget?.toString().orEmpty(),
                   // ... map all fields
               )
           }
       }

       /**
        * Computed helper for numeric fields stored as String in the form.
        */
       val budgetAmount: Double?
           get() = budget.takeIf { it.isNotBlank() }?.toDoubleOrNull()

       /**
        * Form validation check.
        */
       val isValid: Boolean
           get() = fieldName.isNotBlank() && dateField != null

       /**
        * Creates a new entity instance for create screens.
        */
       fun toEntity(id: Int = 0): Entity {
           return Entity(
               id = id,
               fieldName = fieldName.trim(),
               dateField = requireNotNull(dateField) { "Date field required" },
               budget = budgetAmount,
               // ... map remaining fields
           )
       }

       /**
        * Applies form field updates to an existing entity, preserving computed/untouched fields (e.g., stats).
        */
       fun toUpdatedEntity(current: Entity): Entity {
           return current.copy(
               fieldName = fieldName.trim(),
               dateField = requireNotNull(dateField) { "Date field required" },
               budget = budgetAmount,
               // ... update only form-controlled fields
           )
       }
   }
   ```

3. **Key Rules**:
   - **`fromEntity(entity)`**: Converts a Room entity into a form state instance (used when loading data in edit screens).
   - **`toEntity(id)`**: Creates a new entity from the form state (used when saving new items in create screens).
   - **`toUpdatedEntity(current)`**: Copies and updates only user-editable fields on an existing entity, preserving computed fields or fields not controlled by the form (like stats or completion timestamps).
   - **`isValid`**: A read-only boolean property defining validation logic (e.g. required non-blank strings, non-null dates).
   - **Numeric inputs as Strings**: Store numeric inputs (such as prices, budget, quantities) as `String` in the form state for fluid text entry, and expose a parsed computed property getter (e.g. `val budgetAmount: Double? get() = budget.takeIf { it.isNotBlank() }?.toDoubleOrNull()`).
   - **Date defaults**: Import `getCurrentDate` from `com.maeldev.conquest.components` for initial date values.
   - **String trimming**: Use `.trim()` and `.ifBlank { null }` where applicable when converting back to entity fields.

4. **Screen Integration Pattern**:
   - **Create Screen**:
     ```kotlin
     var form by remember { mutableStateOf(EntityFormState()) }
     
     // Save button
     MyButton(
         enabled = form.isValid,
         onClick = { viewModel.insert(form.toEntity()) }
     )
     ```
   - **Edit Screen**:
     ```kotlin
     var form by remember { mutableStateOf(EntityFormState()) }

     LaunchedEffect(loadedEntity) {
         loadedEntity?.let {
             form = EntityFormState.fromEntity(it)
         }
     }

     // Update button
     MyButton(
         enabled = form.isValid,
         onClick = {
             loadedEntity?.let { current ->
                 viewModel.update(form.toUpdatedEntity(current))
             }
         }
     )
     ```
