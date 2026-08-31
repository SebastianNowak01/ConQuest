package com.maeldev.conquest.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.CosplayElementDao
import com.maeldev.conquest.data.dao.CosplayPhotoDao
import com.maeldev.conquest.data.dao.CosplayTaskDao
import com.maeldev.conquest.data.dao.EventDao
import com.maeldev.conquest.data.dao.ProgressPhotoDao
import com.maeldev.conquest.util.ExportImportUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ExportImportState {
    object Idle : ExportImportState()
    object Loading : ExportImportState()
    object Success : ExportImportState()
    data class Error(val message: String) : ExportImportState()
}

class ExportImportViewModel(
    application: Application,
    private val cosplayDao: CosplayDao,
    private val elementDao: CosplayElementDao,
    private val taskDao: CosplayTaskDao,
    private val photoDao: CosplayPhotoDao,
    private val progressPhotoDao: ProgressPhotoDao,
    private val eventDao: EventDao
) : AndroidViewModel(application) {

    private val _exportImportState = MutableStateFlow<ExportImportState>(ExportImportState.Idle)
    val exportImportState: StateFlow<ExportImportState> = _exportImportState.asStateFlow()

    fun exportCosplays(cosplayIds: Set<Int>, targetUri: Uri) {
        viewModelScope.launch {
            _exportImportState.value = ExportImportState.Loading
            val result = ExportImportUtil.exportCosplays(
                context = getApplication(),
                cosplayIds = cosplayIds,
                targetUri = targetUri,
                cosplayDao = cosplayDao,
                elementDao = elementDao,
                taskDao = taskDao,
                photoDao = photoDao,
                progressPhotoDao = progressPhotoDao,
                eventDao = eventDao
            )
            if (result.isSuccess) {
                _exportImportState.value = ExportImportState.Success
            } else {
                _exportImportState.value = ExportImportState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun importCosplays(sourceUri: Uri) {
        viewModelScope.launch {
            _exportImportState.value = ExportImportState.Loading
            val result = ExportImportUtil.importCosplays(
                context = getApplication(),
                sourceUri = sourceUri,
                cosplayDao = cosplayDao,
                elementDao = elementDao,
                taskDao = taskDao,
                photoDao = photoDao,
                progressPhotoDao = progressPhotoDao,
                eventDao = eventDao
            )
            if (result.isSuccess) {
                _exportImportState.value = ExportImportState.Success
            } else {
                _exportImportState.value = ExportImportState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _exportImportState.value = ExportImportState.Idle
    }
}
