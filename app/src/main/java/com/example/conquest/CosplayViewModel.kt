package com.example.conquest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import com.example.conquest.data.entity.Cosplay
import com.example.conquest.data.entity.CosplayElement
import com.example.conquest.data.entity.CosplayPhoto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CosplayViewModel(application: Application) : AndroidViewModel(application) {
    internal val dao = (application as ConQuestApplication).database.cosplayDao()
    internal val photoDao = (application as ConQuestApplication).database.cosplayPhotoDao()
    internal val elementDao = (application as ConQuestApplication).database.cosplayElementDao()

    val allCosplays =
        dao.getAllCosplays().stateIn(viewModelScope, SharingStarted.Lazily, emptyList<Cosplay>())

    fun insertCosplay(cosplay: Cosplay) {
        viewModelScope.launch {
            dao.insertCosplay(cosplay)
        }
    }

    fun deleteCosplaysByIds(cosplayIds: Set<Int>) {
        viewModelScope.launch {
            val photos = photoDao.getPhotosForCosplayOnce(cosplayIds)
            photoDao.deletePhotos(photos)
            photos.forEach { deleteFileByPath(it.path) }
            dao.deleteCosplaysByIds(cosplayIds)
        }
    }

    private val _cosplayId = MutableStateFlow<Int?>(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val photos: StateFlow<List<CosplayPhoto>> =
        _cosplayId.filterNotNull().flatMapLatest { id -> photoDao.getPhotosForCosplay(id) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setCosplayId(id: Int) {
        _cosplayId.value = id
    }

    fun addPhoto(cosplayId: Int, path: String) {
        viewModelScope.launch {
            photoDao.insertPhoto(CosplayPhoto(cosplayId = cosplayId, path = path))
        }
    }

    fun insertElement(element: CosplayElement) {
        viewModelScope.launch {
            elementDao.insertElement(element)
        }
    }

    private val _elementCosplayId = MutableStateFlow<Int?>(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val elements: StateFlow<List<CosplayElement>> =
        _elementCosplayId.filterNotNull()
            .flatMapLatest { id -> elementDao.getElementsForCosplay(id) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setElementCosplayId(id: Int) {
        _elementCosplayId.value = id
    }

    fun deleteElementsByIds(ids: Set<Int>) {
        viewModelScope.launch {
            elementDao.deleteElementsByIds(ids)

        }
    }
}

fun deleteFileByPath(path: String) {
    try {
        val file = java.io.File(path)
        if (file.exists()) file.delete()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}