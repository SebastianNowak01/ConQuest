package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.data.classes.CosplaySortOrder
import com.maeldev.conquest.data.classes.EventSortOption
import com.maeldev.conquest.data.ReminderEntityType
import com.maeldev.conquest.data.ReminderScheduler
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.EventDao
import com.maeldev.conquest.data.entity.Event
import com.maeldev.conquest.data.entity.EventCosplayCrossRef
import com.maeldev.conquest.data.entity.EventType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventViewModel(
    application: Application,
    private val eventDao: EventDao,
    private val cosplayDao: CosplayDao
) : AndroidViewModel(application) {

    private val _eventsFilterType = MutableStateFlow<EventType?>(null)
    val eventsFilterType: StateFlow<EventType?> = _eventsFilterType

    private val _eventsSortOrder = MutableStateFlow(CosplaySortOrder.LeastToMost)
    val eventsSortOrder: StateFlow<CosplaySortOrder> = _eventsSortOrder

    private val _eventsSortOption = MutableStateFlow(EventSortOption.Date)
    val eventsSortOption: StateFlow<EventSortOption> = _eventsSortOption

    fun setEventsFilterType(filterType: EventType?) {
        _eventsFilterType.value = filterType
    }

    fun setEventsSortOrder(order: CosplaySortOrder) {
        _eventsSortOrder.value = order
    }

    fun setEventsSortOption(sortOption: EventSortOption) {
        _eventsSortOption.value = sortOption
    }

    val events: StateFlow<List<Event>> =
        eventDao.getAllEvents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private suspend fun refreshCosplayStats(cosplayIds: Set<Int>) {
        if (cosplayIds.isNotEmpty()) {
            cosplayDao.recomputeStatsForCosplays(cosplayIds)
        }
    }

    private suspend fun replaceEventCosplayLinks(eventId: Int, cosplayIds: Set<Int>) {
        eventDao.deleteEventCosplayCrossRefsForEvent(eventId)
        if (cosplayIds.isNotEmpty()) {
            eventDao.insertEventCosplayCrossRefs(
                cosplayIds.map { cosplayId ->
                    EventCosplayCrossRef(
                        eventId = eventId,
                        cosplayId = cosplayId
                    )
                }
            )
        }
    }

    fun insertEvent(event: Event, cosplayIds: Set<Int> = emptySet()) {
        viewModelScope.launch {
            val eventId = eventDao.insertEvent(event).toInt()
            handleReminderScheduling(ReminderEntityType.EVENT, eventId, event.alarm, event.eventDate, "Event Reminder", event.eventName)
            replaceEventCosplayLinks(eventId = eventId, cosplayIds = cosplayIds)
            refreshCosplayStats(cosplayIds)
        }
    }

    fun updateEvent(event: Event, cosplayIds: Set<Int>? = null) {
        viewModelScope.launch {
            val existingCosplayIds = eventDao.getCosplayIdsForEventOnce(event.id).toSet()
            eventDao.updateEvent(event)
            handleReminderScheduling(ReminderEntityType.EVENT, event.id, event.alarm, event.eventDate, "Event Reminder", event.eventName)
            if (cosplayIds != null) {
                replaceEventCosplayLinks(eventId = event.id, cosplayIds = cosplayIds)
                refreshCosplayStats(existingCosplayIds + cosplayIds)
            } else {
                refreshCosplayStats(existingCosplayIds)
            }
        }
    }

    fun deleteEventsByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            ids.forEach { id -> ReminderScheduler.cancelReminder(context, ReminderEntityType.EVENT, id) }
            val cosplayIds = eventDao.getCosplayIdsForEventIdsOnce(ids).toSet()
            eventDao.deleteEventsByIds(ids)
            refreshCosplayStats(cosplayIds)
        }
    }

    fun getEventById(id: Int): Flow<Event?> {
        return eventDao.getEventById(id)
    }

    private fun handleReminderScheduling(
        entityType: ReminderEntityType,
        entityId: Int,
        alarm: Boolean,
        date: java.util.Date?,
        title: String,
        message: String,
    ) {
        val context = getApplication<Application>()
        if (alarm && date != null) {
            val cal = java.util.Calendar.getInstance().apply {
                time = date
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            ReminderScheduler.scheduleReminder(
                context = context,
                entityType = entityType,
                entityId = entityId,
                triggerAtMillis = cal.timeInMillis,
                title = title,
                message = message,
            )
        } else {
            ReminderScheduler.cancelReminder(context, entityType, entityId)
        }
    }
}
