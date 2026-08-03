package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.components.deleteStoredImageByPath
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.CosplayElementDao
import com.maeldev.conquest.data.entity.CosplayElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ElementViewModel(
    application: Application,
    private val elementDao: CosplayElementDao,
    private val cosplayDao: CosplayDao
) : AndroidViewModel(application) {

    private val _elementCosplayId = MutableStateFlow<Int?>(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val elements: StateFlow<List<CosplayElement>> = _elementCosplayId.filterNotNull()
        .flatMapLatest { id -> elementDao.getElementsForCosplay(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allElements: StateFlow<List<CosplayElement>> =
        elementDao.getAllElements().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setElementCosplayId(id: Int) {
        _elementCosplayId.value = id
    }

    private suspend fun refreshCosplayStats(cosplayId: Int) {
        cosplayDao.recomputeStatsForCosplay(cosplayId)
    }

    private suspend fun refreshCosplayStats(cosplayIds: Set<Int>) {
        if (cosplayIds.isNotEmpty()) {
            cosplayDao.recomputeStatsForCosplays(cosplayIds)
        }
    }

    fun insertElement(element: CosplayElement) {
        viewModelScope.launch {
            elementDao.insertElement(element)
            refreshCosplayStats(element.cosplayId)
        }
    }

    fun deleteElementsByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val cosplayIds = elementDao.getCosplayIdsForElementIdsOnce(ids).toSet()
            val photoPaths = elementDao.getPhotoPathsForElementIdsOnce(ids)
            elementDao.deleteElementsByIds(ids)
            photoPaths.forEach { deleteManagedImageFile(it) }
            refreshCosplayStats(cosplayIds)
        }
    }

    fun getElementById(id: Int): Flow<CosplayElement?> {
        return elementDao.getElementById(id)
    }

    fun updateElement(cosplayElement: CosplayElement, oldPathToDelete: String? = null) {
        viewModelScope.launch {
            elementDao.updateElement(cosplayElement)
            refreshCosplayStats(cosplayElement.cosplayId)
            deleteManagedImageFile(oldPathToDelete)
        }
    }

    private fun deleteManagedImageFile(path: String?) {
        deleteStoredImageByPath(
            getApplication(),
            path.orEmpty()
        )
    }
}
