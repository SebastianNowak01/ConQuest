package com.maeldev.conquest.data.classes

import com.maeldev.conquest.data.entity.Cosplay

/**
 * Where a cosplay is in its life, as one value.
 *
 * [Cosplay] stores this as two independent booleans, which lets it express states that do not
 * exist (planned *and* finished) and previously let the New and Edit screens write a different
 * state from the same control. This enum is the single representation the UI works in; the
 * booleans are an encoding detail of the table, mapped in [fromEntity] and out via
 * [inProgressFlag] / [finishedFlag].
 *
 * The labels here are the app's whole status vocabulary — the form control, the cosplay row and
 * [CosplayStatusFilter] all read them from here so no screen invents its own wording.
 */
enum class CosplayStatus(val label: String) {
    Planned("Planned"),
    InProgress("In Progress"),
    Done("Done"),
    ;

    val inProgressFlag: Boolean get() = this == InProgress

    val finishedFlag: Boolean get() = this == Done

    companion object {
        /**
         * Reads the stored booleans. `finished` wins over `in_progress` so that a row already
         * carrying both — writable by older versions of the app — is read the same way
         * [com.maeldev.conquest.screens.matchesMainScreenFilter] has always read it.
         */
        fun fromEntity(cosplay: Cosplay): CosplayStatus =
            when {
                cosplay.finished -> Done
                cosplay.inProgress -> InProgress
                else -> Planned
            }
    }
}
