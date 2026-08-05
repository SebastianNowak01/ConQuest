package com.maeldev.conquest.data.classes

import com.maeldev.conquest.data.entity.EventType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date

object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Date) = encoder.encodeLong(value.time)
    override fun deserialize(decoder: Decoder): Date = Date(decoder.decodeLong())
}

@Serializable
data class ExportDataDto(
    val version: Int = 1,
    val cosplays: List<CosplayExportDto>
)

@Serializable
data class CosplayExportDto(
    val cosplay: CosplayDto,
    val elements: List<CosplayElementDto>,
    val tasks: List<CosplayTaskDto>,
    val photos: List<CosplayPhotoDto>,
    val progressPhotos: List<ProgressPhotoDto>,
    val events: List<EventDto>
)

@Serializable
data class CosplayDto(
    val inProgress: Boolean,
    val finished: Boolean,
    val name: String,
    val series: String,
    @Serializable(with = DateSerializer::class) val initialDate: Date,
    @Serializable(with = DateSerializer::class) val dueDate: Date?,
    val budget: Double?,
    val overallPercentage: Int,
    val tasksCount: Int,
    val eventsCount: Int,
    val totalSpend: Double,
    val totalTimeDays: Long,
    val cosplayPhotoPath: String?
)

@Serializable
data class CosplayElementDto(
    val name: String,
    val cost: Double?,
    val ready: Boolean,
    val photoPath: String?,
    val highlight: Boolean,
    val bought: Boolean,
    val notes: String?
)

@Serializable
data class CosplayTaskDto(
    val taskName: String,
    val done: Boolean,
    val alarm: Boolean,
    val notes: String?,
    @Serializable(with = DateSerializer::class) val date: Date?
)

@Serializable
data class CosplayPhotoDto(
    val path: String,
    val notes: String?
)

@Serializable
data class ProgressPhotoDto(
    val path: String,
    val notes: String?,
    @Serializable(with = DateSerializer::class) val createdAt: Date
)

@Serializable
data class EventDto(
    val eventName: String,
    val eventLocation: String,
    val eventType: EventType,
    @Serializable(with = DateSerializer::class) val eventDate: Date,
    val description: String?,
    val alarm: Boolean
)
