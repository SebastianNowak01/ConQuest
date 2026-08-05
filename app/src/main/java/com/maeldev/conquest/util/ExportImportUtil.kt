package com.maeldev.conquest.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.maeldev.conquest.data.classes.CosplayDto
import com.maeldev.conquest.data.classes.CosplayElementDto
import com.maeldev.conquest.data.classes.CosplayExportDto
import com.maeldev.conquest.data.classes.CosplayPhotoDto
import com.maeldev.conquest.data.classes.CosplayTaskDto
import com.maeldev.conquest.data.classes.EventDto
import com.maeldev.conquest.data.classes.ExportDataDto
import com.maeldev.conquest.data.classes.ProgressPhotoDto
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.CosplayElementDao
import com.maeldev.conquest.data.dao.CosplayPhotoDao
import com.maeldev.conquest.data.dao.CosplayTaskDao
import com.maeldev.conquest.data.dao.EventDao
import com.maeldev.conquest.data.dao.ProgressPhotoDao
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayElement
import com.maeldev.conquest.data.entity.CosplayPhoto
import com.maeldev.conquest.data.entity.CosplayTask
import com.maeldev.conquest.data.entity.Event
import com.maeldev.conquest.data.entity.EventCosplayCrossRef
import com.maeldev.conquest.data.entity.ProgressPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ExportImportUtil {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportCosplays(
        context: Context,
        cosplayIds: Set<Int>,
        targetUri: Uri,
        cosplayDao: CosplayDao,
        elementDao: CosplayElementDao,
        taskDao: CosplayTaskDao,
        photoDao: CosplayPhotoDao,
        progressPhotoDao: ProgressPhotoDao,
        eventDao: EventDao
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val cosplays = cosplayDao.getCosplaysByIdsOnce(cosplayIds)
            val elements = elementDao.getElementsForCosplaysOnce(cosplayIds)
            val tasks = taskDao.getTasksForCosplaysOnce(cosplayIds)
            val photos = photoDao.getPhotosForCosplayOnce(cosplayIds)
            val progressPhotos = progressPhotoDao.getPhotosForCosplayOnce(cosplayIds)
            
            val eventCrossRefs = eventDao.getEventCrossRefsForCosplaysOnce(cosplayIds)
            val eventIds = eventCrossRefs.map { it.eventId }.toSet()
            val events = if (eventIds.isNotEmpty()) eventDao.getEventsByIdsOnce(eventIds) else emptyList()

            val exportCosplays = cosplays.map { cosplay ->
                val cosplayElements = elements.filter { it.cosplayId == cosplay.uid }
                val cosplayTasks = tasks.filter { it.cosplayId == cosplay.uid }
                val cosplayPhotos = photos.filter { it.cosplayId == cosplay.uid }
                val cosplayProgressPhotos = progressPhotos.filter { it.cosplayId == cosplay.uid }
                
                val cosplayEventIds = eventCrossRefs.filter { it.cosplayId == cosplay.uid }.map { it.eventId }.toSet()
                val cosplayEvents = events.filter { cosplayEventIds.contains(it.id) }

                CosplayExportDto(
                    cosplay = CosplayDto(
                        inProgress = cosplay.inProgress,
                        finished = cosplay.finished,
                        name = cosplay.name,
                        series = cosplay.series,
                        initialDate = cosplay.initialDate,
                        dueDate = cosplay.dueDate,
                        budget = cosplay.budget,
                        overallPercentage = cosplay.overallPercentage,
                        tasksCount = cosplay.tasksCount,
                        eventsCount = cosplay.eventsCount,
                        totalSpend = cosplay.totalSpend,
                        totalTimeDays = cosplay.totalTimeDays,
                        cosplayPhotoPath = cosplay.cosplayPhotoPath
                    ),
                    elements = cosplayElements.map {
                        CosplayElementDto(
                            name = it.name,
                            cost = it.cost,
                            ready = it.ready,
                            photoPath = it.photoPath,
                            highlight = it.highlight,
                            bought = it.bought,
                            notes = it.notes
                        )
                    },
                    tasks = cosplayTasks.map {
                        CosplayTaskDto(
                            taskName = it.taskName,
                            done = it.done,
                            alarm = it.alarm,
                            notes = it.notes,
                            date = it.date
                        )
                    },
                    photos = cosplayPhotos.map {
                        CosplayPhotoDto(
                            path = it.path,
                            notes = it.notes
                        )
                    },
                    progressPhotos = cosplayProgressPhotos.map {
                        ProgressPhotoDto(
                            path = it.path,
                            notes = it.notes,
                            createdAt = it.createdAt
                        )
                    },
                    events = cosplayEvents.map {
                        EventDto(
                            eventName = it.eventName,
                            eventLocation = it.eventLocation,
                            eventType = it.eventType,
                            eventDate = it.eventDate,
                            description = it.description,
                            alarm = it.alarm
                        )
                    }
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
                    val jsonEntry = ZipEntry("data.json")
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
        runCatching {
            var jsonString: String? = null
            
            // Unzip to temp dir to read images, or stream them
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        if (entry.name == "data.json") {
                            jsonString = zis.readBytes().toString(Charsets.UTF_8)
                        } else if (entry.name.startsWith("images/")) {
                            val destFile = File(context.filesDir, entry.name)
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

            if (jsonString == null) {
                error("data.json not found in the ZIP archive")
            }

            val exportData = json.decodeFromString<ExportDataDto>(jsonString!!)

            // Insert into DB
            exportData.cosplays.forEach { cosplayExport ->
                val newCosplay = Cosplay(
                    uid = 0, // auto generate
                    inProgress = cosplayExport.cosplay.inProgress,
                    finished = cosplayExport.cosplay.finished,
                    name = cosplayExport.cosplay.name,
                    series = cosplayExport.cosplay.series,
                    initialDate = cosplayExport.cosplay.initialDate,
                    dueDate = cosplayExport.cosplay.dueDate,
                    budget = cosplayExport.cosplay.budget,
                    overallPercentage = cosplayExport.cosplay.overallPercentage,
                    tasksCount = cosplayExport.cosplay.tasksCount,
                    eventsCount = cosplayExport.cosplay.eventsCount,
                    totalSpend = cosplayExport.cosplay.totalSpend,
                    totalTimeDays = cosplayExport.cosplay.totalTimeDays,
                    cosplayPhotoPath = cosplayExport.cosplay.cosplayPhotoPath
                )
                val newCosplayId = cosplayDao.insertCosplay(newCosplay).toInt()

                cosplayExport.elements.forEach { elem ->
                    elementDao.insertElement(
                        CosplayElement(
                            id = 0,
                            cosplayId = newCosplayId,
                            name = elem.name,
                            cost = elem.cost,
                            ready = elem.ready,
                            photoPath = elem.photoPath,
                            highlight = elem.highlight,
                            bought = elem.bought,
                            notes = elem.notes
                        )
                    )
                }

                cosplayExport.tasks.forEach { task ->
                    taskDao.insertTask(
                        CosplayTask(
                            id = 0,
                            cosplayId = newCosplayId,
                            taskName = task.taskName,
                            done = task.done,
                            alarm = task.alarm,
                            notes = task.notes,
                            date = task.date
                        )
                    )
                }

                cosplayExport.photos.forEach { photo ->
                    photoDao.insertPhoto(
                        CosplayPhoto(
                            id = 0,
                            cosplayId = newCosplayId,
                            path = photo.path,
                            notes = photo.notes
                        )
                    )
                }

                cosplayExport.progressPhotos.forEach { pp ->
                    progressPhotoDao.insertPhoto(
                        ProgressPhoto(
                            id = 0,
                            cosplayId = newCosplayId,
                            path = pp.path,
                            notes = pp.notes,
                            createdAt = pp.createdAt
                        )
                    )
                }
                
                cosplayExport.events.forEach { eventDto ->
                    val newEvent = Event(
                        id = 0,
                        eventName = eventDto.eventName,
                        eventLocation = eventDto.eventLocation,
                        eventType = eventDto.eventType,
                        eventDate = eventDto.eventDate,
                        description = eventDto.description,
                        alarm = eventDto.alarm
                    )
                    val newEventId = eventDao.insertEvent(newEvent).toInt()
                    
                    eventDao.insertEventCosplayCrossRefs(
                        listOf(
                            EventCosplayCrossRef(
                                eventId = newEventId,
                                cosplayId = newCosplayId
                            )
                        )
                    )
                }
            }
        }
    }
}
