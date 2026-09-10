package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.data.classes.CosplaySortOrder
import com.maeldev.conquest.data.classes.EventSortOption
import com.maeldev.conquest.data.ReminderEntityType
import com.maeldev.conquest.data.ReminderScheduler
import com.maeldev.conquest.data.ReminderTarget
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.refreshStatsFor
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
            ReminderScheduler.syncReminder(
                context = getApplication(),
                target = ReminderTarget(ReminderEntityType.EVENT, eventId),
                alarm = event.alarm,
                date = event.eventDate,
                message = event.eventName,
            )
            replaceEventCosplayLinks(eventId = eventId, cosplayIds = cosplayIds)
            cosplayDao.refreshStatsFor(cosplayIds)
        }
    }

    fun updateEvent(event: Event, cosplayIds: Set<Int>? = null) {
        viewModelScope.launch {
            val existingCosplayIds = eventDao.getCosplayIdsForEventOnce(event.id).toSet()
            eventDao.updateEvent(event)
            ReminderScheduler.syncReminder(
                context = getApplication(),
                target = ReminderTarget(ReminderEntityType.EVENT, event.id),
                alarm = event.alarm,
                date = event.eventDate,
                message = event.eventName,
            )
            if (cosplayIds != null) {
                replaceEventCosplayLinks(eventId = event.id, cosplayIds = cosplayIds)
                cosplayDao.refreshStatsFor(existingCosplayIds + cosplayIds)
            } else {
                cosplayDao.refreshStatsFor(existingCosplayIds)
            }
        }
    }

    fun deleteEventsByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            ids.forEach { id -> ReminderScheduler.cancelReminder(context, ReminderTarget(ReminderEntityType.EVENT, id)) }
            val cosplayIds = eventDao.getCosplayIdsForEventIdsOnce(ids).toSet()
            eventDao.deleteEventsByIds(ids)
            cosplayDao.refreshStatsFor(cosplayIds)
        }
    }

    fun getEventById(id: Int): Flow<Event?> {
        return eventDao.getEventById(id)
    }

}
