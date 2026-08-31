---
name: create-viewmodel
description: Guide for creating and registering new AndroidViewModels in the ConQuest project with Room DAOs and AppViewModelProvider.
---

# Create ViewModel Skill

This skill explains how to implement and register a new ViewModel in ConQuest following project-specific architectural patterns.

## ViewModel Conventions

1. **Location**: Place all ViewModels in `app/src/main/java/com/maeldev/conquest/viewmodel/`.
2. **Inheritance**: Always extend `AndroidViewModel(application)` — do NOT extend plain `ViewModel`.
3. **Constructor**:
   - `application: Application` as the first parameter.
   - Required DAOs passed as subsequent parameters (e.g., `private val itemDao: ItemDao`, `private val cosplayDao: CosplayDao`).

## Data Exposure Patterns

- **List Queries**: Expose Room DAO lists as `StateFlow`:
  ```kotlin
  val allItems: StateFlow<List<Item>> =
      itemDao.getAllItems().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
  ```
- **Single-Item Queries**: Expose as `Flow`:
  ```kotlin
  fun getItemById(id: Int): Flow<Item?> = itemDao.getItemById(id)
  ```
- **UI State**: Use `MutableStateFlow` for UI state (e.g., sort, filters):
  ```kotlin
  private val _filter = MutableStateFlow(FilterOption.ALL)
  val filter: StateFlow<FilterOption> = _filter.asStateFlow()
  fun setFilter(option: FilterOption) { _filter.value = option }
  ```
- **Cosplay Child Entities**: Use the `flatMapLatest` pattern:
  ```kotlin
  private val _cosplayId = MutableStateFlow<Int?>(null)

  @OptIn(ExperimentalCoroutinesApi::class)
  val items: StateFlow<List<Item>> = _cosplayId.filterNotNull()
      .flatMapLatest { id -> itemDao.getItemsForCosplay(id) }
      .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  fun setCosplayId(id: Int) {
      _cosplayId.value = id
  }
  ```

## Mutations & Side Effects

- **Coroutine Scope**: Run mutations in `viewModelScope.launch { ... }` (fire-and-forget).
- **Cosplay Stats Recomputation**: If entity changes affect cosplay stats (cost, task counts, etc.), call `cosplayDao.recomputeStatsForCosplay(cosplayId)` or `cosplayDao.recomputeStatsForCosplays(cosplayIds)` after mutations.
- **Image Cleanup**: When updating/deleting entities with stored image paths, call `deleteStoredImageByPath(getApplication(), path)` from `com.maeldev.conquest.components`.

## Factory Registration

Register the new ViewModel in `app/src/main/java/com/maeldev/conquest/AppViewModelProvider.kt`:
1. Import the ViewModel class.
2. Add a branch in `when`:
   ```kotlin
   modelClass.isAssignableFrom(ItemViewModel::class.java) -> {
       ItemViewModel(
           application = application,
           itemDao = db.itemDao(),
           cosplayDao = db.cosplayDao()
       ) as T
   }
   ```

## Minimal Example

```kotlin
package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.components.deleteStoredImageByPath
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.ItemDao
import com.maeldev.conquest.data.entity.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemViewModel(
    application: Application,
    private val itemDao: ItemDao,
    private val cosplayDao: CosplayDao
) : AndroidViewModel(application) {

    private val _cosplayId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: StateFlow<List<Item>> = _cosplayId.filterNotNull()
        .flatMapLatest { id -> itemDao.getItemsForCosplay(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allItems: StateFlow<List<Item>> =
        itemDao.getAllItems().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setCosplayId(id: Int) {
        _cosplayId.value = id
    }

    fun getItemById(id: Int): Flow<Item?> = itemDao.getItemById(id)

    fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insertItem(item)
            cosplayDao.recomputeStatsForCosplay(item.cosplayId)
        }
    }

    fun updateItem(item: Item, oldImagePath: String? = null) {
        viewModelScope.launch {
            itemDao.updateItem(item)
            cosplayDao.recomputeStatsForCosplay(item.cosplayId)
            oldImagePath?.let { deleteStoredImageByPath(getApplication(), it) }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.deleteItem(item)
            cosplayDao.recomputeStatsForCosplay(item.cosplayId)
            item.imagePath?.let { deleteStoredImageByPath(getApplication(), it) }
        }
    }
}
```
