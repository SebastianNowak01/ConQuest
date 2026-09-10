package com.maeldev.conquest.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.data.dao.CosplayDaos
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
    private val daos: CosplayDaos,
) : AndroidViewModel(application) {
    private val _exportImportState = MutableStateFlow<ExportImportState>(ExportImportState.Idle)
    val exportImportState: StateFlow<ExportImportState> = _exportImportState.asStateFlow()

    fun exportCosplays(
        cosplayIds: Set<Int>,
        targetUri: Uri,
    ) {
        viewModelScope.launch {
            _exportImportState.value = ExportImportState.Loading
            val result =
                ExportImportUtil.exportCosplays(
                    context = getApplication(),
                    cosplayIds = cosplayIds,
                    targetUri = targetUri,
                    daos = daos,
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
            val result =
                ExportImportUtil.importCosplays(
                    context = getApplication(),
                    sourceUri = sourceUri,
                    daos = daos,
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
