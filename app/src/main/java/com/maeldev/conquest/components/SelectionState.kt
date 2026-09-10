package com.maeldev.conquest.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Multi-select state for a list or grid screen.
 *
 * "Selection mode" is not stored separately. A screen is in selection mode exactly when
 * something is selected ([isActive]) — an invariant each screen previously re-established by
 * hand at five different call sites, which is easy to get subtly wrong.
 *
 * Obtain one with [rememberSelectionState], which also keeps it in step with what is on screen.
 */
@Stable
class SelectionState internal constructor(initialSelectedIds: Set<Int>) {
    var selectedIds: Set<Int> by mutableStateOf(initialSelectedIds)
        private set

    private var visibleIds: Set<Int> by mutableStateOf(emptySet())

    /** True when the screen should show its selection UI instead of its normal actions. */
    val isActive: Boolean get() = selectedIds.isNotEmpty()

    val count: Int get() = selectedIds.size

    fun isSelected(id: Int): Boolean = id in selectedIds

    /** Adds [id] to the selection, entering selection mode if it was not already active. */
    fun select(id: Int) {
        selectedIds = selectedIds + id
    }

    /** Adds or removes [id]; removing the last one leaves selection mode. */
    fun toggle(id: Int) {
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
    }

    /** Selects everything currently on screen — filtered and searched items only. */
    fun selectAll() {
        selectedIds = visibleIds
    }

    fun clear() {
        selectedIds = emptySet()
    }

    /**
     * Records what is on screen and drops any selected id that is no longer there, so a delete,
     * a filter change or a search cannot leave invisible items selected.
     */
    internal fun onVisibleIdsChanged(ids: Set<Int>) {
        visibleIds = ids
        if (selectedIds.isNotEmpty()) {
            selectedIds = selectedIds intersect ids
        }
    }

    companion object {
        val Saver: Saver<SelectionState, *> =
            listSaver(
                save = { it.selectedIds.toList() },
                restore = { SelectionState(it.toSet()) },
            )
    }
}

/**
 * Remembers a [SelectionState] for [items], surviving configuration changes and process death.
 *
 * [id] extracts the stable identity of an item. The returned state tracks [items], so callers
 * cannot forget to prune a stale selection.
 */
@Composable
fun <T> rememberSelectionState(
    items: List<T>,
    id: (T) -> Int,
): SelectionState {
    val selection = rememberSaveable(saver = SelectionState.Saver) { SelectionState(emptySet()) }
    val visibleIds = remember(items) { items.map(id).toSet() }

    LaunchedEffect(visibleIds) {
        selection.onVisibleIdsChanged(visibleIds)
    }

    return selection
}
