package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.components.deleteStoredImageByPath
import com.maeldev.conquest.data.dao.ProgressPhotoDao
import com.maeldev.conquest.data.entity.ProgressPhoto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgressPhotoViewModel(
    application: Application,
    private val progressPhotoDao: ProgressPhotoDao
) : AndroidViewModel(application) {

    private val _progressCosplayId = MutableStateFlow<Int?>(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val progressPhotos: StateFlow<List<ProgressPhoto>> =
        _progressCosplayId.filterNotNull().flatMapLatest { id -> progressPhotoDao.getPhotosForCosplay(id) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setProgressCosplayId(id: Int) {
        _progressCosplayId.value = id
    }

    fun addProgressPhoto(cosplayId: Int, path: String) {
        viewModelScope.launch {
            progressPhotoDao.insertPhoto(
                ProgressPhoto(
                    cosplayId = cosplayId,
                    path = path
                )
            )
        }
    }

    fun deleteProgressPhotosByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val toDelete = progressPhotoDao.getPhotosByIdsOnce(ids)
            progressPhotoDao.deletePhotosByIds(ids)
            toDelete.forEach { deleteManagedImageFile(it.path) }
        }
    }

    fun getProgressPhotoById(id: Int, cosplayId: Int): Flow<ProgressPhoto?> {
        return progressPhotoDao.getPhotoById(id, cosplayId)
    }

    fun updateProgressPhoto(photo: ProgressPhoto) {
        viewModelScope.launch {
            progressPhotoDao.updatePhoto(photo)
        }
    }

    private fun deleteManagedImageFile(path: String?) {
        deleteStoredImageByPath(
            getApplication(),
            path.orEmpty()
        )
    }
}
