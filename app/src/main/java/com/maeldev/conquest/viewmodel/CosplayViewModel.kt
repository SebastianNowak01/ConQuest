package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.components.deleteStoredImageByPath
import com.maeldev.conquest.data.classes.CosplaySortOrder
import com.maeldev.conquest.data.classes.CosplaySortOption
import com.maeldev.conquest.data.classes.CosplayStatusFilter
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.CosplayPhotoDao
import com.maeldev.conquest.data.dao.ProgressPhotoDao
import com.maeldev.conquest.data.entity.Cosplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CosplayViewModel(
    application: Application,
    private val dao: CosplayDao,
    private val photoDao: CosplayPhotoDao,
    private val progressPhotoDao: ProgressPhotoDao
) : AndroidViewModel(application) {

    val allCosplays =
        dao.getAllCosplays().stateIn(viewModelScope, SharingStarted.Lazily, emptyList<Cosplay>())

    private val _mainScreenFilter = MutableStateFlow(CosplayStatusFilter.All)
    val mainScreenFilter: StateFlow<CosplayStatusFilter> = _mainScreenFilter

    private val _mainScreenSort = MutableStateFlow(CosplaySortOption.Character)
    val mainScreenSort: StateFlow<CosplaySortOption> = _mainScreenSort

    private val _mainScreenSortOrder = MutableStateFlow(CosplaySortOrder.LeastToMost)
    val mainScreenSortOrder: StateFlow<CosplaySortOrder> = _mainScreenSortOrder

    fun setMainScreenFilter(filter: CosplayStatusFilter) {
        _mainScreenFilter.value = filter
    }

    fun setMainScreenSort(sort: CosplaySortOption) {
        _mainScreenSort.value = sort
    }

    fun setMainScreenSortOrder(order: CosplaySortOrder) {
        _mainScreenSortOrder.value = order
    }

    private suspend fun refreshCosplayStats(cosplayId: Int) {
        dao.recomputeStatsForCosplay(cosplayId)
    }

    private suspend fun refreshCosplayStats(cosplayIds: Set<Int>) {
        if (cosplayIds.isNotEmpty()) {
            dao.recomputeStatsForCosplays(cosplayIds)
        }
    }

    fun insertCosplay(cosplay: Cosplay) {
        viewModelScope.launch {
            val cosplayId = dao.insertCosplay(cosplay).toInt()
            refreshCosplayStats(cosplayId)
        }
    }

    fun getCosplayById(cosplayId: Int): Flow<Cosplay?> {
        return dao.getCosplayById(cosplayId)
    }

    fun updateCosplay(cosplay: Cosplay, oldPathToDelete: String? = null) {
        viewModelScope.launch {
            dao.updateCosplay(cosplay)
            refreshCosplayStats(cosplay.uid)
            deleteManagedImageFile(oldPathToDelete)
        }
    }

    fun deleteCosplaysByIds(cosplayIds: Set<Int>) {
        viewModelScope.launch {
            val cosplayCovers = dao.getCosplayPhotoPathsByIdsOnce(cosplayIds)
            val photos = photoDao.getPhotosForCosplayOnce(cosplayIds)
            photoDao.deletePhotos(photos)
            photos.forEach { deleteManagedImageFile(it.path) }
            val progressPhotos = progressPhotoDao.getPhotosForCosplayOnce(cosplayIds)
            progressPhotoDao.deletePhotos(progressPhotos)
            progressPhotos.forEach { deleteManagedImageFile(it.path) }
            dao.deleteCosplaysByIds(cosplayIds)
            cosplayCovers.forEach { deleteManagedImageFile(it) }
        }
    }

    private fun deleteManagedImageFile(path: String?) {
        deleteStoredImageByPath(
            getApplication(),
            path.orEmpty()
        )
    }
}
