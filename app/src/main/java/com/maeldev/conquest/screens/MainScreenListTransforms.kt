package com.maeldev.conquest.screens

import com.maeldev.conquest.data.classes.CosplaySortOption
import com.maeldev.conquest.data.classes.CosplaySortOrder
import com.maeldev.conquest.data.classes.CosplayStatus
import com.maeldev.conquest.data.classes.CosplayStatusFilter
import com.maeldev.conquest.data.classes.daysWorked
import com.maeldev.conquest.data.entity.Cosplay
import java.util.Date

private const val MILLIS_PER_DAY = 86_400_000L

private fun compareStringsAlphabetical(
    first: String,
    second: String,
): Int {
    return first.lowercase().compareTo(second.lowercase())
}

private fun compareDates(
    first: Date?,
    second: Date?,
): Int {
    return when {
        first == null && second == null -> 0
        first == null -> 1
        second == null -> -1
        else -> first.compareTo(second)
    }
}

fun matchesMainScreenFilter(
    cosplay: Cosplay,
    filter: CosplayStatusFilter,
): Boolean {
    val wanted = filter.status ?: return true
    return CosplayStatus.fromEntity(cosplay) == wanted
}

fun filterMainScreenCosplays(
    cosplays: List<Cosplay>,
    searchQuery: String,
    selectedFilter: CosplayStatusFilter,
): List<Cosplay> {
    return cosplays
        .filter { cosplay -> matchesMainScreenFilter(cosplay, selectedFilter) }
        .filter { cosplay ->
            searchQuery.isBlank() || cosplay.name.contains(searchQuery, ignoreCase = true)
        }
}

fun sortMainScreenCosplays(
    cosplays: List<Cosplay>,
    sort: CosplaySortOption,
    order: CosplaySortOrder,
): List<Cosplay> {
    val direction = if (order == CosplaySortOrder.MostToLeast) -1 else 1

    return cosplays.sortedWith { left, right ->
        val baseCompare =
            when (sort) {
                CosplaySortOption.Character -> compareStringsAlphabetical(left.name, right.name)
                CosplaySortOption.Series -> compareStringsAlphabetical(left.series, right.series)
                CosplaySortOption.Tasks -> left.tasksCount.compareTo(right.tasksCount)
                CosplaySortOption.InitialDate -> left.initialDate.compareTo(right.initialDate)
                CosplaySortOption.EndDate -> {
                    (left.initialDate.time + (left.daysWorked() * MILLIS_PER_DAY)).compareTo(
                        right.initialDate.time + (right.daysWorked() * MILLIS_PER_DAY),
                    )
                }
                CosplaySortOption.DueDate -> compareDates(left.dueDate, right.dueDate)
                CosplaySortOption.Budget -> (left.budget ?: 0.0).compareTo(right.budget ?: 0.0)
                CosplaySortOption.TotalSpend -> left.totalSpend.compareTo(right.totalSpend)
                CosplaySortOption.TotalTime -> left.daysWorked().compareTo(right.daysWorked())
                CosplaySortOption.Events -> left.eventsCount.compareTo(right.eventsCount)
            }

        val compare = baseCompare * direction
        if (compare != 0) compare else compareStringsAlphabetical(left.name, right.name)
    }
}
