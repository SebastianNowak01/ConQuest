package com.maeldev.conquest.data.classes

import com.maeldev.conquest.data.daysUntil
import com.maeldev.conquest.data.entity.Cosplay
import java.util.Date

/**
 * Days spent on this cosplay: from its initial date up to the day it was marked Done, or up to
 * today while it is still unfinished.
 *
 * This used to be read straight from the cached `total_time_days` column, which measures
 * something else entirely — the span from the initial date to the last dated task or the due
 * date. That is a *plan*, fixed the moment the dates are entered, so the card sat on the same
 * number for the whole life of a cosplay no matter how long it actually took.
 *
 * The value is computed on read rather than cached because the unfinished case has to move on its
 * own every midnight, and no write happens to refresh a column on those days.
 *
 * Rows finished before `finished_date` existed have no stamp to read. Rather than show nothing,
 * they keep the old planned-span estimate — the best evidence left about how long they ran.
 *
 * Both ends are counted, so a cosplay started and finished on one day reads as 1 day rather than
 * 0: the question the card answers is how many days were spent on it, not how far apart two dates
 * are. That also means the number is never 0 — every cosplay has at least the day it started.
 */
fun Cosplay.daysWorked(today: Date = Date()): Long {
    val span =
        when {
            finishedDate != null -> initialDate.daysUntil(finishedDate)
            finished -> totalTimeDays
            else -> initialDate.daysUntil(today)
        }
    return span.coerceAtLeast(0L) + 1
}
