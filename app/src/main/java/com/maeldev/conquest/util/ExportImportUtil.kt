package com.maeldev.conquest.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.maeldev.conquest.components.IMAGE_STORAGE_DIR
import com.maeldev.conquest.data.classes.CosplayExportDto
import com.maeldev.conquest.data.classes.ExportDataDto
import com.maeldev.conquest.data.classes.toDto
import com.maeldev.conquest.data.classes.toEntity
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.CosplayElementDao
import com.maeldev.conquest.data.dao.CosplayPhotoDao
import com.maeldev.conquest.data.dao.CosplayTaskDao
import com.maeldev.conquest.data.dao.EventDao
import com.maeldev.conquest.data.dao.ProgressPhotoDao
import com.maeldev.conquest.data.entity.EventCosplayCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.coroutines.cancellation.CancellationException

object ExportImportUtil {
    private const val JSON_ENTRY_NAME = "data.json"
    private const val IMAGES_ENTRY_PREFIX = "$IMAGE_STORAGE_DIR/"

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Like [runCatching], but lets [CancellationException] propagate instead of reporting a
     * cancelled scope as an export/import failure.
     */
    @Suppress("TooGenericExceptionCaught") // Mirrors runCatching: any failure becomes Result.failure.
    private inline fun <T> runCatchingCancellable(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }

    /**
     * Resolves a ZIP entry name to a file inside the managed images directory.
     *
     * Entry names come from an untrusted archive, so a name containing traversal segments
     * (`images/../../databases/cosplays_database`) would otherwise write anywhere in the app
     * sandbox. Any entry that resolves outside the images directory is rejected.
     */
    private fun resolveImageEntryFile(
        context: Context,
        entryName: String,
    ): File {
        val imagesRoot = File(context.filesDir, IMAGE_STORAGE_DIR).canonicalFile
        val destination = File(context.filesDir, entryName).canonicalFile
        if (!destination.path.startsWith(imagesRoot.path + File.separator)) {
            error("Refusing to extract entry outside the images directory: $entryName")
        }
        return destination
    }

    suspend fun exportCosplays(
        context: Context,
        cosplayIds: Set<Int>,
        targetUri: Uri,
        cosplayDao: CosplayDao,
        elementDao: CosplayElementDao,
        taskDao: CosplayTaskDao,
        photoDao: CosplayPhotoDao,
        progressPhotoDao: ProgressPhotoDao,
        eventDao: EventDao,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatchingCancellable {
            val cosplays = cosplayDao.getCosplaysByIdsOnce(cosplayIds)
            val elements = elementDao.getElementsForCosplaysOnce(cosplayIds)
            val tasks = taskDao.getTasksForCosplaysOnce(cosplayIds)
            val photos = photoDao.getPhotosForCosplayOnce(cosplayIds)
            val progressPhotos = progressPhotoDao.getPhotosForCosplayOnce(cosplayIds)
            
            val eventCrossRefs = eventDao.getEventCrossRefsForCosplaysOnce(cosplayIds)
            val eventIds = eventCrossRefs.map { it.eventId }.toSet()
            val events = if (eventIds.isNotEmpty()) eventDao.getEventsByIdsOnce(eventIds) else emptyList()

            val exportCosplays = cosplays.map { cosplay ->
                val cosplayEventIds = eventCrossRefs
                    .filter { it.cosplayId == cosplay.uid }
                    .map { it.eventId }
                    .toSet()

                CosplayExportDto(
                    cosplay = cosplay.toDto(),
                    elements = elements.filter { it.cosplayId == cosplay.uid }.map { it.toDto() },
                    tasks = tasks.filter { it.cosplayId == cosplay.uid }.map { it.toDto() },
                    photos = photos.filter { it.cosplayId == cosplay.uid }.map { it.toDto() },
                    progressPhotos = progressPhotos
                        .filter { it.cosplayId == cosplay.uid }
                        .map { it.toDto() },
                    events = events.filter { it.id in cosplayEventIds }.map { it.toDto() },
                )
            }

            val exportData = ExportDataDto(cosplays = exportCosplays)
            val jsonString = json.encodeToString(exportData)

            val photoPathsToExport = mutableSetOf<String>()
            exportCosplays.forEach { dto ->
                dto.cosplay.cosplayPhotoPath?.takeIf { it.isNotBlank() }?.let { photoPathsToExport.add(it) }
                dto.elements.forEach { it.photoPath?.takeIf { p -> p.isNotBlank() }?.let { p -> photoPathsToExport.add(p) } }
                dto.photos.forEach { if (it.path.isNotBlank()) photoPathsToExport.add(it.path) }
                dto.progressPhotos.forEach { if (it.path.isNotBlank()) photoPathsToExport.add(it.path) }
            }

            context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zos ->
                    // 1. Write JSON
                    val jsonEntry = ZipEntry(JSON_ENTRY_NAME)
                    zos.putNextEntry(jsonEntry)
                    zos.write(jsonString.toByteArray())
                    zos.closeEntry()

                    // 2. Write Photos
                    photoPathsToExport.forEach { path ->
                        val file = File(context.filesDir, path)
                        if (file.exists()) {
                            val photoEntry = ZipEntry(path)
                            zos.putNextEntry(photoEntry)
                            FileInputStream(file).use { fis ->
                                fis.copyTo(zos)
                            }
                            zos.closeEntry()
                        } else {
                            Log.w("Export", "Photo not found: $path")
                        }
                    }
                }
            } ?: error("Could not open output stream")
        }
    }

    suspend fun importCosplays(
        context: Context,
        sourceUri: Uri,
        cosplayDao: CosplayDao,
        elementDao: CosplayElementDao,
        taskDao: CosplayTaskDao,
        photoDao: CosplayPhotoDao,
        progressPhotoDao: ProgressPhotoDao,
        eventDao: EventDao
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatchingCancellable {
            var jsonString: String? = null
            
            // Unzip to temp dir to read images, or stream them
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        if (entry.name == JSON_ENTRY_NAME) {
                            jsonString = zis.readBytes().toString(Charsets.UTF_8)
                        } else if (entry.name.startsWith(IMAGES_ENTRY_PREFIX)) {
                            val destFile = resolveImageEntryFile(context, entry.name)
                            destFile.parentFile?.mkdirs()
                            destFile.outputStream().use { fos ->
                                zis.copyTo(fos)
                            }
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            } ?: error("Could not open input stream")

            val payload = jsonString ?: error("$JSON_ENTRY_NAME not found in the ZIP archive")

            val exportData = json.decodeFromString<ExportDataDto>(payload)

            // Insert into DB
            exportData.cosplays.forEach { cosplayExport ->
                val newCosplayId = cosplayDao.insertCosplay(cosplayExport.cosplay.toEntity()).toInt()

                cosplayExport.elements.forEach { elementDao.insertElement(it.toEntity(newCosplayId)) }
                cosplayExport.tasks.forEach { taskDao.insertTask(it.toEntity(newCosplayId)) }
                cosplayExport.photos.forEach { photoDao.insertPhoto(it.toEntity(newCosplayId)) }
                cosplayExport.progressPhotos.forEach {
                    progressPhotoDao.insertPhoto(it.toEntity(newCosplayId))
                }

                cosplayExport.events.forEach { eventDto ->
                    val newEventId = eventDao.insertEvent(eventDto.toEntity()).toInt()
                    eventDao.insertEventCosplayCrossRefs(
                        listOf(EventCosplayCrossRef(eventId = newEventId, cosplayId = newCosplayId)),
                    )
                }
            }
        }
    }
}
