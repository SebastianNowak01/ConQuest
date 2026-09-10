package com.maeldev.conquest.data.classes

/**
 * Main-screen status filter. Every entry but [All] shows exactly one [CosplayStatus] and takes
 * its wording from it, so the filter menu and the rows it filters always agree.
 */
enum class CosplayStatusFilter(val status: CosplayStatus?) {
    All(null),
    Planned(CosplayStatus.Planned),
    InProcess(CosplayStatus.InProgress),
    Completed(CosplayStatus.Done),
    ;

    val label: String get() = status?.label ?: "All"
}
