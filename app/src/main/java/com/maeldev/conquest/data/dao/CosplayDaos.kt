package com.maeldev.conquest.data.dao

/**
 * Export and import both need every one of them, and passing them individually meant a long
 * positional parameter list that had to be repeated at each call site.
 */
data class CosplayDaos(
    val cosplayDao: CosplayDao,
    val elementDao: CosplayElementDao,
    val taskDao: CosplayTaskDao,
    val photoDao: CosplayPhotoDao,
    val progressPhotoDao: ProgressPhotoDao,
    val eventDao: EventDao,
)
