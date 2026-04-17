package com.example.conquest.screens

import com.example.conquest.data.classes.CosplaySortOrder
import com.example.conquest.data.classes.CosplaySortOption
import com.example.conquest.data.classes.CosplayStatusFilter
import com.example.conquest.data.entity.Cosplay
import com.example.conquest.data.entity.CosplayElement
import com.example.conquest.data.entity.CosplayTask
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

fun buildTaskCountByCosplay(allTasks: List<CosplayTask>): Map<Int, Int> {
    return allTasks.groupingBy { it.cosplayId }.eachCount()
}

fun buildEndDateByCosplay(allTasks: List<CosplayTask>): Map<Int, Date?> {
    return allTasks
        .filter { it.date != null }
        .groupBy { it.cosplayId }
        .mapValues { (_, tasks) -> tasks.mapNotNull { it.date }.maxOrNull() }
}

fun buildTotalSpendByCosplay(allElements: List<CosplayElement>): Map<Int, Double> {
    return allElements
        .groupBy { it.cosplayId }
        .mapValues { (_, elements) ->
            elements.sumOf { element ->
                if (element.bought) {
                    element.cost ?: 0.0
                } else {
                    0.0
                }
            }
        }
}

fun buildTotalTimeDaysByCosplay(
    cosplays: List<Cosplay>,
    endDateByCosplay: Map<Int, Date?>,
): Map<Int, Long> {
    return cosplays.associate { cosplay ->
        val endDate = endDateByCosplay[cosplay.uid] ?: cosplay.dueDate ?: cosplay.initialDate
        val days = (endDate.time - cosplay.initialDate.time) / 86_400_000L
        cosplay.uid to days.coerceAtLeast(0L)
    }
}

fun sortMainScreenCosplays(
    cosplays: List<Cosplay>,
    sort: CosplaySortOption,
    order: CosplaySortOrder,
    taskCountByCosplay: Map<Int, Int>,
    endDateByCosplay: Map<Int, Date?>,
    totalSpendByCosplay: Map<Int, Double>,
    totalTimeDaysByCosplay: Map<Int, Long>,
    eventsCount: Int,
): List<Cosplay> {
    val direction = if (order == CosplaySortOrder.MostToLeast) -1 else 1

    return cosplays.sortedWith { left, right ->
        val baseCompare = when (sort) {
            CosplaySortOption.Character -> compareStringsAlphabetical(left.name, right.name)
            CosplaySortOption.Series -> compareStringsAlphabetical(left.series, right.series)
            CosplaySortOption.Tasks -> {
                (taskCountByCosplay[left.uid] ?: 0).compareTo(taskCountByCosplay[right.uid] ?: 0)
            }
            CosplaySortOption.InitialDate -> left.initialDate.compareTo(right.initialDate)
            CosplaySortOption.EndDate -> compareDates(endDateByCosplay[left.uid], endDateByCosplay[right.uid])
            CosplaySortOption.DueDate -> compareDates(left.dueDate, right.dueDate)
            CosplaySortOption.Budget -> (left.budget ?: 0.0).compareTo(right.budget ?: 0.0)
            CosplaySortOption.TotalSpend -> {
                (totalSpendByCosplay[left.uid] ?: 0.0).compareTo(totalSpendByCosplay[right.uid] ?: 0.0)
            }
            CosplaySortOption.TotalTime -> {
                (totalTimeDaysByCosplay[left.uid] ?: 0L).compareTo(totalTimeDaysByCosplay[right.uid] ?: 0L)
            }
            CosplaySortOption.Events -> eventsCount.compareTo(eventsCount)
        }

        val compare = baseCompare * direction

        if (compare != 0) {
            compare
        } else {
            compareStringsAlphabetical(left.name, right.name)
        }
    }
}

