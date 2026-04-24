package com.example.conquest.screens

import com.example.conquest.data.classes.CosplaySortOrder
import com.example.conquest.data.classes.CosplaySortOption
import com.example.conquest.data.classes.CosplayStatusFilter
import com.example.conquest.data.entity.Cosplay
import java.util.Date

private fun compareStringsAlphabetical(first: String, second: String): Int {
    return first.lowercase().compareTo(second.lowercase())
}

private fun compareDates(first: Date?, second: Date?): Int {
    return when {
        first == null && second == null -> 0
        first == null -> 1
        second == null -> -1
        else -> first.compareTo(second)
    }
}

fun matchesMainScreenFilter(cosplay: Cosplay, filter: CosplayStatusFilter): Boolean {
    return when (filter) {
        CosplayStatusFilter.All -> true
        CosplayStatusFilter.Planned -> !cosplay.inProgress && !cosplay.finished
        CosplayStatusFilter.InProcess -> cosplay.inProgress && !cosplay.finished
        CosplayStatusFilter.Completed -> cosplay.finished
    }
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
        val baseCompare = when (sort) {
            CosplaySortOption.Character -> compareStringsAlphabetical(left.name, right.name)
            CosplaySortOption.Series -> compareStringsAlphabetical(left.series, right.series)
            CosplaySortOption.Tasks -> left.tasksCount.compareTo(right.tasksCount)
            CosplaySortOption.InitialDate -> left.initialDate.compareTo(right.initialDate)
            CosplaySortOption.EndDate -> {
                (left.initialDate.time + (left.totalTimeDays * 86_400_000L)).compareTo(
                    right.initialDate.time + (right.totalTimeDays * 86_400_000L)
                )
            }
            CosplaySortOption.DueDate -> compareDates(left.dueDate, right.dueDate)
            CosplaySortOption.Budget -> (left.budget ?: 0.0).compareTo(right.budget ?: 0.0)
            CosplaySortOption.TotalSpend -> left.totalSpend.compareTo(right.totalSpend)
            CosplaySortOption.TotalTime -> left.totalTimeDays.compareTo(right.totalTimeDays)
            CosplaySortOption.Events -> left.eventsCount.compareTo(right.eventsCount)
        }

        val compare = baseCompare * direction
        if (compare != 0) compare else compareStringsAlphabetical(left.name, right.name)
    }
}

