package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.components.deleteStoredImageByPath
import com.maeldev.conquest.data.dao.CosplayPhotoDao
import com.maeldev.conquest.data.entity.CosplayPhoto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotoViewModel(
    application: Application,
    private val photoDao: CosplayPhotoDao
) : AndroidViewModel(application) {

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
            photoDao.insertPhoto(
                CosplayPhoto(
                    cosplayId = cosplayId,
                    path = path
                )
            )
        }
    }

    fun deletePhotosByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val toDelete = photoDao.getPhotosByIdsOnce(ids)
            photoDao.deletePhotosByIds(ids)
            toDelete.forEach { deleteManagedImageFile(it.path) }
        }
    }

    fun getPhotoById(id: Int): Flow<CosplayPhoto?> = photoDao.getPhotoById(id)

    fun updatePhoto(updated: CosplayPhoto, oldPathToDelete: String? = null) {
        viewModelScope.launch {
            photoDao.updatePhoto(updated)
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
