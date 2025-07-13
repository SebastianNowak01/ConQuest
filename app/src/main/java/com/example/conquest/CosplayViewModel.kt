package com.example.conquest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import com.example.conquest.data.entity.Cosplay
import kotlinx.coroutines.launch

class CosplayViewModel(application: Application) : AndroidViewModel(application) {
    internal val dao = (application as ConQuestApplication).database.cosplayDao()

    val allCosplays = dao.getAllCosplays()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList<Cosplay>())

    fun insertCosplay(cosplay: Cosplay) {
        viewModelScope.launch {
            dao.insertCosplay(cosplay)
        }
    }

    fun deleteCosplay(cosplay: Cosplay) {
        viewModelScope.launch {
            dao.delete(cosplay)
        }
    }
}