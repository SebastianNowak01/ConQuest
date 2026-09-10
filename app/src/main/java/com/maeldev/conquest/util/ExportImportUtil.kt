package com.maeldev.conquest.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.maeldev.conquest.components.IMAGE_STORAGE_DIR
import com.maeldev.conquest.data.classes.CosplayExportDto
import com.maeldev.conquest.data.classes.ExportDataDto
import com.maeldev.conquest.data.classes.toDto
import com.maeldev.conquest.data.classes.toEntity
import com.maeldev.conquest.data.dao.CosplayDaos
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

    private val json =
        Json {
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
        daos: CosplayDaos,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatchingCancellable {
                val exportCosplays = buildExportCosplays(cosplayIds, daos)
                val jsonString = json.encodeToString(ExportDataDto(cosplays = exportCosplays))

                context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                    ZipOutputStream(outputStream).use { zos ->
                        writeArchive(context, zos, jsonString, collectPhotoPaths(exportCosplays))
                    }
                } ?: error("Could not open output stream")
            }
        }

    /** Reads every cosplay in [cosplayIds] and its children into serializable DTOs. */
    private suspend fun buildExportCosplays(
        cosplayIds: Set<Int>,
        daos: CosplayDaos,
    ): List<CosplayExportDto> {
        val cosplays = daos.cosplayDao.getCosplaysByIdsOnce(cosplayIds)
        val elements = daos.elementDao.getElementsForCosplaysOnce(cosplayIds)
        val tasks = daos.taskDao.getTasksForCosplaysOnce(cosplayIds)
        val photos = daos.photoDao.getPhotosForCosplayOnce(cosplayIds)
        val progressPhotos = daos.progressPhotoDao.getPhotosForCosplayOnce(cosplayIds)

        val eventCrossRefs = daos.eventDao.getEventCrossRefsForCosplaysOnce(cosplayIds)
        val eventIds = eventCrossRefs.map { it.eventId }.toSet()
        val events = if (eventIds.isNotEmpty()) daos.eventDao.getEventsByIdsOnce(eventIds) else emptyList()

        return cosplays.map { cosplay ->
            val cosplayEventIds =
                eventCrossRefs
                    .filter { it.cosplayId == cosplay.uid }
                    .map { it.eventId }
                    .toSet()

            CosplayExportDto(
                cosplay = cosplay.toDto(),
                elements = elements.filter { it.cosplayId == cosplay.uid }.map { it.toDto() },
                tasks = tasks.filter { it.cosplayId == cosplay.uid }.map { it.toDto() },
                photos = photos.filter { it.cosplayId == cosplay.uid }.map { it.toDto() },
                progressPhotos =
                    progressPhotos
                        .filter { it.cosplayId == cosplay.uid }
                        .map { it.toDto() },
                events = events.filter { it.id in cosplayEventIds }.map { it.toDto() },
            )
        }
    }

    /** Every non-blank image path referenced anywhere in [exportCosplays], deduplicated. */
    private fun collectPhotoPaths(exportCosplays: List<CosplayExportDto>): Set<String> {
        val paths = mutableSetOf<String>()
        exportCosplays.forEach { dto ->
            dto.cosplay.cosplayPhotoPath?.takeIf { it.isNotBlank() }?.let { paths.add(it) }
            dto.elements.forEach { element ->
                element.photoPath?.takeIf { it.isNotBlank() }?.let { paths.add(it) }
            }
            dto.photos.forEach { if (it.path.isNotBlank()) paths.add(it.path) }
            dto.progressPhotos.forEach { if (it.path.isNotBlank()) paths.add(it.path) }
        }
        return paths
    }

    /** Writes the JSON payload followed by every photo that still exists on disk. */
    private fun writeArchive(
        context: Context,
        zos: ZipOutputStream,
        jsonString: String,
        photoPaths: Set<String>,
    ) {
        zos.putNextEntry(ZipEntry(JSON_ENTRY_NAME))
        zos.write(jsonString.toByteArray())
        zos.closeEntry()

        photoPaths.forEach { path ->
            val file = File(context.filesDir, path)
            if (!file.exists()) {
                Log.w("Export", "Photo not found: $path")
                return@forEach
            }
            zos.putNextEntry(ZipEntry(path))
            FileInputStream(file).use { fis -> fis.copyTo(zos) }
            zos.closeEntry()
        }
    }

    suspend fun importCosplays(
        context: Context,
        sourceUri: Uri,
        daos: CosplayDaos,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatchingCancellable {
                val jsonString = readArchive(context, sourceUri)
                val payload = jsonString ?: error("$JSON_ENTRY_NAME not found in the ZIP archive")

                val exportData = json.decodeFromString<ExportDataDto>(payload)
                exportData.cosplays.forEach { cosplayExport -> insertCosplay(cosplayExport, daos) }
            }
        }

    /**
     * Extracts the archive's images into the managed images directory and returns the JSON
     * payload, or null when the archive contains no [JSON_ENTRY_NAME] entry.
     */
    private fun readArchive(
        context: Context,
        sourceUri: Uri,
    ): String? {
        var jsonString: String? = null

        context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    jsonString = readEntry(context, zis, entry.name) ?: jsonString
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } ?: error("Could not open input stream")

        return jsonString
    }

    /**
     * Handles a single archive entry: extracts images to disk, and returns the JSON payload when
     * [entryName] is the data entry. Any other entry is ignored.
     */
    private fun readEntry(
        context: Context,
        zis: ZipInputStream,
        entryName: String,
    ): String? {
        if (entryName == JSON_ENTRY_NAME) {
            return zis.readBytes().toString(Charsets.UTF_8)
        }
        if (entryName.startsWith(IMAGES_ENTRY_PREFIX)) {
            val destFile = resolveImageEntryFile(context, entryName)
            destFile.parentFile?.mkdirs()
            destFile.outputStream().use { fos -> zis.copyTo(fos) }
        }
        return null
    }

    /** Inserts one imported cosplay and all of its children under freshly generated ids. */
    private suspend fun insertCosplay(
        cosplayExport: CosplayExportDto,
        daos: CosplayDaos,
    ) {
        val newCosplayId = daos.cosplayDao.insertCosplay(cosplayExport.cosplay.toEntity()).toInt()

        cosplayExport.elements.forEach { daos.elementDao.insertElement(it.toEntity(newCosplayId)) }
        cosplayExport.tasks.forEach { daos.taskDao.insertTask(it.toEntity(newCosplayId)) }
        cosplayExport.photos.forEach { daos.photoDao.insertPhoto(it.toEntity(newCosplayId)) }
        cosplayExport.progressPhotos.forEach {
            daos.progressPhotoDao.insertPhoto(it.toEntity(newCosplayId))
        }

        cosplayExport.events.forEach { eventDto ->
            val newEventId = daos.eventDao.insertEvent(eventDto.toEntity()).toInt()
            daos.eventDao.insertEventCosplayCrossRefs(
                listOf(EventCosplayCrossRef(eventId = newEventId, cosplayId = newCosplayId)),
            )
        }
    }
}
