package com.maeldev.conquest.components

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionStateTest {
    private fun selectionOf(
        vararg visible: Int,
        selected: Set<Int> = emptySet(),
    ): SelectionState {
        return SelectionState(selected).apply { onVisibleIdsChanged(visible.toSet()) }
    }

    @Test
    fun newSelection_isNotActive() {
        val selection = selectionOf(1, 2, 3)

        assertFalse(selection.isActive)
        assertEquals(0, selection.count)
    }

    @Test
    fun select_entersSelectionMode() {
        val selection = selectionOf(1, 2, 3)

        selection.select(2)

        assertTrue(selection.isActive)
        assertTrue(selection.isSelected(2))
        assertFalse(selection.isSelected(1))
    }

    @Test
    fun toggle_removingLastSelectedItem_leavesSelectionMode() {
        val selection = selectionOf(1, 2)

        selection.toggle(1)
        assertTrue(selection.isActive)

        selection.toggle(1)
        assertFalse("Removing the last selected item must exit selection mode", selection.isActive)
    }

    @Test
    fun selectAll_selectsOnlyVisibleItems() {
        // Mirrors a filtered or searched list: ids 3 and 4 exist but are not on screen.
        val selection = selectionOf(1, 2)

        selection.selectAll()

        assertEquals(setOf(1, 2), selection.selectedIds)
    }

    @Test
    fun selectAll_onEmptyList_staysInactive() {
        val selection = selectionOf()

        selection.selectAll()

        assertFalse(selection.isActive)
    }

    @Test
    fun visibleIdsChanging_dropsSelectedItemsThatDisappeared() {
        val selection = selectionOf(1, 2, 3)
        selection.selectAll()

        // Item 2 was deleted, or filtered out by a search.
        selection.onVisibleIdsChanged(setOf(1, 3))

        assertEquals(setOf(1, 3), selection.selectedIds)
    }

    @Test
    fun visibleIdsChanging_toNothing_leavesSelectionMode() {
        val selection = selectionOf(1, 2)
        selection.selectAll()

        selection.onVisibleIdsChanged(emptySet())

        assertFalse(selection.isActive)
        assertEquals(0, selection.count)
    }

    @Test
    fun clear_leavesSelectionMode() {
        val selection = selectionOf(1, 2)
        selection.selectAll()

        selection.clear()

        assertFalse(selection.isActive)
    }

    @Test
    fun saver_roundTripsTheSelection() {
        val selection = selectionOf(1, 2, 3)
        selection.select(1)
        selection.select(3)

        @Suppress("UNCHECKED_CAST")
        val saver = SelectionState.Saver as Saver<SelectionState, Any>
        val restored =
            with(saver) {
                val saved = SaverScope { true }.save(selection)
                restore(saved!!)
            }

        assertEquals(setOf(1, 3), restored!!.selectedIds)
        assertTrue("A restored selection must still be active", restored.isActive)
    }
}
