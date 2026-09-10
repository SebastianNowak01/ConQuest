// A mapper file holds one toDto/toEntity pair per entity, so the count scales with the
// number of entities rather than with complexity.
@file:Suppress("TooManyFunctions")

package com.maeldev.conquest.data.classes

import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayElement
import com.maeldev.conquest.data.entity.CosplayPhoto
import com.maeldev.conquest.data.entity.CosplayTask
import com.maeldev.conquest.data.entity.Event
import com.maeldev.conquest.data.entity.ProgressPhoto

/**
 * Conversions between the Room entities and their serialized export counterparts.
 *
 * Keeping both directions next to each other makes it obvious when a new entity field has been
 * added without a matching export field — previously these were two ~100-line blocks of inline
 * construction at opposite ends of ExportImportUtil, where a field could easily be exported but
 * never imported back.
 *
 * Entity ids and foreign keys are deliberately not carried across: an import always inserts new
 * rows, so ids are assigned by the database and parents are supplied by the caller.
 */

fun Cosplay.toDto(): CosplayDto = CosplayDto(
    inProgress = inProgress,
    finished = finished,
    name = name,
    series = series,
    initialDate = initialDate,
    dueDate = dueDate,
    budget = budget,
    overallPercentage = overallPercentage,
    tasksCount = tasksCount,
    eventsCount = eventsCount,
    totalSpend = totalSpend,
    totalTimeDays = totalTimeDays,
    cosplayPhotoPath = cosplayPhotoPath,
)

fun CosplayDto.toEntity(): Cosplay = Cosplay(
    uid = 0,
    inProgress = inProgress,
    finished = finished,
    name = name,
    series = series,
    initialDate = initialDate,
    dueDate = dueDate,
    budget = budget,
    overallPercentage = overallPercentage,
    tasksCount = tasksCount,
    eventsCount = eventsCount,
    totalSpend = totalSpend,
    totalTimeDays = totalTimeDays,
    cosplayPhotoPath = cosplayPhotoPath,
)

fun CosplayElement.toDto(): CosplayElementDto = CosplayElementDto(
    name = name,
    cost = cost,
    ready = ready,
    photoPath = photoPath,
    highlight = highlight,
    bought = bought,
    notes = notes,
)

fun CosplayElementDto.toEntity(cosplayId: Int): CosplayElement = CosplayElement(
    id = 0,
    cosplayId = cosplayId,
    name = name,
    cost = cost,
    ready = ready,
    photoPath = photoPath,
    highlight = highlight,
    bought = bought,
    notes = notes,
)

fun CosplayTask.toDto(): CosplayTaskDto = CosplayTaskDto(
    taskName = taskName,
    done = done,
    alarm = alarm,
    notes = notes,
    date = date,
)

fun CosplayTaskDto.toEntity(cosplayId: Int): CosplayTask = CosplayTask(
    id = 0,
    cosplayId = cosplayId,
    taskName = taskName,
    done = done,
    alarm = alarm,
    notes = notes,
    date = date,
)

fun CosplayPhoto.toDto(): CosplayPhotoDto = CosplayPhotoDto(
    path = path,
    notes = notes,
)

fun CosplayPhotoDto.toEntity(cosplayId: Int): CosplayPhoto = CosplayPhoto(
    id = 0,
    cosplayId = cosplayId,
    path = path,
    notes = notes,
)

fun ProgressPhoto.toDto(): ProgressPhotoDto = ProgressPhotoDto(
    path = path,
    notes = notes,
    createdAt = createdAt,
)

fun ProgressPhotoDto.toEntity(cosplayId: Int): ProgressPhoto = ProgressPhoto(
    id = 0,
    cosplayId = cosplayId,
    path = path,
    notes = notes,
    createdAt = createdAt,
)

fun Event.toDto(): EventDto = EventDto(
    eventName = eventName,
    eventLocation = eventLocation,
    eventType = eventType,
    eventDate = eventDate,
    description = description,
    alarm = alarm,
)

fun EventDto.toEntity(): Event = Event(
    id = 0,
    eventName = eventName,
    eventLocation = eventLocation,
    eventType = eventType,
    eventDate = eventDate,
    description = description,
    alarm = alarm,
)
