---
name: add-export-import-support
description: Guide for extending the ConQuest ZIP export and import system for new or updated entities.
---

# Add Export/Import Support

Guide to extending ZIP archive export and import functionality for entities in ConQuest (`com.maeldev.conquest`).

---

## 1. Create or Update DTOs

File path: `app/src/main/java/com/maeldev/conquest/data/classes/CosplayExportDto.kt`

Entities serialized for export/import should use dedicated `@Serializable` data classes to decouple Room schema from serialization format.

### Step A: Define the Entity DTO
```kotlin
@Serializable
data class CosplayItemDto(
    val name: String,
    val notes: String? = null,
    val photoPath: String? = null,
    @Serializable(with = DateSerializer::class) val createdAt: Date? = null
)
```
> [!NOTE]
> For `Date` fields, always annotate with `@Serializable(with = DateSerializer::class)`.

### Step B: Add to `CosplayExportDto`
Provide a default value (`= emptyList()`) for backward compatibility with archives created before this entity existed:
```kotlin
@Serializable
data class CosplayExportDto(
    val cosplay: CosplayDto,
    val elements: List<CosplayElementDto>,
    val tasks: List<CosplayTaskDto>,
    val photos: List<CosplayPhotoDto>,
    val progressPhotos: List<ProgressPhotoDto>,
    val events: List<EventDto>,
    val items: List<CosplayItemDto> = emptyList() // Added with default
)
```

---

## 2. Update `ExportImportUtil.kt`

File path: `app/src/main/java/com/maeldev/conquest/util/ExportImportUtil.kt`

### Step A: Export Logic
1. Accept the new DAO in `exportCosplays(...)`.
2. Fetch items for the given cosplays: `val items = itemDao.getItemsForCosplaysOnce(cosplayIds)`.
3. Map entities to DTOs in `CosplayExportDto`.
4. Collect any associated image relative paths into `photoPathsToExport`:
   ```kotlin
   dto.items.forEach { it.photoPath?.takeIf { p -> p.isNotBlank() }?.let { p -> photoPathsToExport.add(p) } }
   ```
   Images will be written directly into the ZIP archive under their relative path (e.g., `images/...`).

### Step B: Import Logic
1. Accept the new DAO in `importCosplays(...)`.
2. As the ZIP stream processes entries, `images/*` files are extracted to `context.filesDir`.
3. After parsing `data.json` and inserting the parent `Cosplay` to obtain `newCosplayId`, map DTOs to Room entities and insert them:
   ```kotlin
   cosplayExport.items.forEach { itemDto ->
       itemDao.insertItem(
           CosplayItem(
               id = 0, // Auto-generate primary key
               cosplayId = newCosplayId, // Remapped foreign key
               name = itemDto.name,
               notes = itemDto.notes,
               photoPath = itemDto.photoPath,
               createdAt = itemDto.createdAt
           )
       )
   }
   ```

---

## 3. Update `ExportImportViewModel.kt`

File path: `app/src/main/java/com/maeldev/conquest/viewmodel/ExportImportViewModel.kt`

1. Add the new DAO to `ExportImportViewModel` constructor:
   ```kotlin
   class ExportImportViewModel(
       application: Application,
       private val cosplayDao: CosplayDao,
       private val elementDao: CosplayElementDao,
       private val taskDao: CosplayTaskDao,
       private val photoDao: CosplayPhotoDao,
       private val progressPhotoDao: ProgressPhotoDao,
       private val eventDao: EventDao,
       private val itemDao: CosplayItemDao // New DAO
   ) : AndroidViewModel(application)
   ```
2. Pass `itemDao` in `exportCosplays(...)` and `importCosplays(...)` calls to `ExportImportUtil`.

---

## 4. Register in `AppViewModelProvider.kt`

File path: `app/src/main/java/com/maeldev/conquest/AppViewModelProvider.kt`

Pass the new DAO from `db` into `ExportImportViewModel`:
```kotlin
modelClass.isAssignableFrom(com.maeldev.conquest.viewmodel.ExportImportViewModel::class.java) -> {
    com.maeldev.conquest.viewmodel.ExportImportViewModel(
        application = application,
        cosplayDao = db.cosplayDao(),
        elementDao = db.cosplayElementDao(),
        taskDao = db.cosplayTaskDao(),
        photoDao = db.cosplayPhotoDao(),
        progressPhotoDao = db.progressPhotoDao(),
        eventDao = db.eventDao(),
        itemDao = db.cosplayItemDao() // Added DAO
    ) as T
}
```

---

## 5. Backward Compatibility & Image Path Rules

1. **Missing JSON Fields**: Always assign default values (`emptyList()`, `null`, or sensible defaults) in DTOs. `Json { ignoreUnknownKeys = true }` ignores unexpected keys when importing from newer formats.
2. **Image Paths**: Store relative paths (e.g., `images/uuid.jpg`) in the database. During export, files are read from `File(context.filesDir, path)`. During import, entries starting with `images/` are written directly into `File(context.filesDir, entry.name)`.

---

## Checklist
- [ ] DTO created with `@Serializable` and `@Serializable(with = DateSerializer::class)` for `Date` fields
- [ ] Added to `CosplayExportDto` with default value for backwards compatibility
- [ ] `ExportImportUtil.exportCosplays` queries DAO, populates DTO, and collects photo paths
- [ ] `ExportImportUtil.importCosplays` inserts new records with remapped `newCosplayId`
- [ ] `ExportImportViewModel` constructor and calls updated with new DAO
- [ ] `AppViewModelProvider` updated to inject the new DAO into `ExportImportViewModel`
